package main.logic.requirement;

import org.apache.commons.lang3.StringUtils;

import main.data.Data;
import main.entity.CompareOperator;
import main.entity.actor.Actor;
import main.entity.actor.ActorType;
import main.entity.item.ItemType;
import main.entity.zone.predefined.PredefinedZoneLoader;
import main.presentation.Logger;

public class RequirementValidator
{
	private static RequirementValidator instance = null;

	private Data data = null;
	
	private RequirementValidator() {}
	
	public static RequirementValidator getInstance()
	{
		if (instance == null)
			instance = new RequirementValidator();
		
		return instance;
	}

	public void setData(Data data)
	{
		this.data = data;
	}
	
	public String getValueToCheckForActorHasItem(String actorTypeString, String itemTypeString)
	{
		Logger.debug("Checking how many items of type [" + itemTypeString + "] actor [" + actorTypeString + "] has.");
		ActorType actorType = ActorType.fromString(actorTypeString);
		Actor actor = data.getFirstActorOfType(actorType);
		ItemType itemType = ItemType.fromString(itemTypeString);
		return getValueToCheckForActorHasItem(actor, itemType);
	}
	
	public String getValueToCheckForActorHasItem(Actor actor, ItemType itemType)
	{
		int itemCount =  actor.getTotalItemCount(itemType);
		Logger.debug("Item count is " + itemCount);
		return String.valueOf(itemCount);
	}
	
	//note that this automatically fails (returns -1) if the requested zone isn't the current zone
	public String getValueToCheckForActorCountInZone(String zoneId, String actorTypeString)
	{
		ActorType actorType = ActorType.fromString(actorTypeString);
		String cacheName = PredefinedZoneLoader.getInstance().getCacheNameOfZone(zoneId);
		Logger.debug("Checking how many actors of type [" + actorTypeString + "] zone [" + zoneId + "](" + cacheName + ") has.");
		Logger.debug("Current zone is [" + data.getCurrentZone().getName() + "].");
		int totalActorsOfType = data.getCountOfActorOfType(cacheName, actorType);
		Logger.debug("Actor count is " + totalActorsOfType);
		return String.valueOf(totalActorsOfType);
	}
	
	public boolean checkRequirement(CompareOperator operator, String requiredValue, String valueToCheck)
	{
		try
		{
			int requirementAsInt = Integer.parseInt(requiredValue);
			int valueAsInt = Integer.parseInt(valueToCheck);
			return checkIntegerRequirement(operator, requirementAsInt, valueAsInt);
		} catch (NumberFormatException nfe)
		{
			return checkStringRequirement(operator, requiredValue, valueToCheck);
		}
	}

	public boolean checkIntegerRequirement(CompareOperator operator, int requirement, int value)
	{
		if (operator.equals(CompareOperator.EQUAL))
			return value == requirement;
		else if (operator.equals(CompareOperator.NOT_EQUAL))
			return value != requirement;
		else if (operator.equals(CompareOperator.LESS_THAN))
			return value < requirement;
		else if (operator.equals(CompareOperator.GREATER_THAN))
			return value > requirement;
		else if (operator.equals(CompareOperator.LESS_THAN_OR_EQUAL))
			return value <= requirement;
		else if (operator.equals(CompareOperator.GREATER_THAN_OR_EQUAL))
			return value >= requirement;
		
		return false;
	}
	
	public boolean checkStringRequirement(CompareOperator operator, String requirement, String valueToCheck)
	{
		if (operator.equals(CompareOperator.EQUAL))
			return StringUtils.equalsIgnoreCase(requirement, valueToCheck);
		else if (operator.equals(CompareOperator.NOT_EQUAL))
			return !StringUtils.equalsIgnoreCase(requirement, valueToCheck);
		
		return false;
	}
	
	public boolean doesActorHaveItem(Actor actor, ItemType itemType, int quantity)
	{
		int availableItems = Integer.parseInt(getValueToCheckForActorHasItem(actor, itemType));
		return checkIntegerRequirement(CompareOperator.GREATER_THAN_OR_EQUAL, quantity, availableItems);
	}
}
