package main.entity.item.recipe;

import java.util.HashMap;
import java.util.Map;

import main.entity.actor.Actor;
import main.entity.actor.SkillType;
import main.entity.item.ItemFactory;
import main.entity.item.ItemType;
import main.logic.requirement.RequirementValidator;

public class Recipe
{
	private Map<ItemType, Integer> components;
	private Map<SkillType, Integer> requiredSkills;
	private ItemType resultingItem;
	
	public Recipe()
	{
		components = new HashMap<ItemType, Integer>();
		requiredSkills = new HashMap<SkillType, Integer>();
		resultingItem = ItemType.NO_TYPE;
	}
	
	public ItemType getResultingItem()
	{
		return resultingItem;
	}
	
	public void setResultingItem(ItemType resultingItem)
	{
		this.resultingItem = resultingItem;
	}
	
	public void addComponent(ItemType component, int quantity)
	{
		if (quantity > 0)
			components.put(component, quantity);
	}
	
	public Map<ItemType, Integer> getComponents()
	{
		return components;
	}
	
	public Map<SkillType, Integer> getRequiredSkills()
	{
		return requiredSkills;
	}
	
	public int getTotalSkillRanksRequired()
	{
		int totalSkillRanks = 0;
		
		for (Integer rank : requiredSkills.values())
		{
			totalSkillRanks += rank;
		}
		
		return totalSkillRanks;
	}
	
	public boolean sufficientSkills(Actor actor)
	{
		for (SkillType requiredSkill : requiredSkills.keySet())
		{
			int requiredLevel = requiredSkills.get(requiredSkill);
			
			if (!actor.hasSkill(requiredSkill, requiredLevel))
				return false;
		}
		
		return true;
	}
	
	public boolean sufficientComponents(Actor actor)
	{
		for (ItemType component : components.keySet())
		{
			int quantity = components.get(component);
			
			if (!RequirementValidator.getInstance().doesActorHaveItem(actor, component, quantity))
				return false;
		}
		
		return true;
	}
	
	@Override
	public String toString()
	{
		String toRet = ItemFactory.generateNewItem(resultingItem).getName() + ": ";
		
		for (ItemType component : components.keySet())
		{
			int quantity = components.get(component);
			toRet = toRet + quantity + "x " + ItemFactory.generateNewItem(component).getName() + ", "; 
		}
		
		return toRet.substring(0, toRet.length() - 2);
	}
}
