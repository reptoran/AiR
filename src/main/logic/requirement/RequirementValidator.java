package main.logic.requirement;

import org.apache.commons.lang3.StringUtils;

import main.data.Data;
import main.entity.CompareOperator;
import main.entity.actor.Actor;
import main.entity.actor.ActorType;
import main.entity.item.ItemType;

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
		ActorType actorType = ActorType.fromString(actorTypeString);
		Actor actor = data.getFirstActorOfType(actorType);
		ItemType itemType = ItemType.fromString(itemTypeString);
		return getValueToCheckForActorHasItem(actor, itemType);
	}
	
	public String getValueToCheckForActorHasItem(Actor actor, ItemType itemType)
	{
		int itemCount =  actor.getTotalItemCount(itemType);
		return String.valueOf(itemCount);
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
