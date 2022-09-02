package main.entity.item.recipe;

import main.entity.actor.SkillType;
import main.entity.item.ItemType;

public class RecipeBuilder
{
	private static Recipe recipe;
	
	public RecipeBuilder()
	{
		recipe = new Recipe();
	}
	
	public Recipe build()
	{
		return recipe;
	}
	
	public RecipeBuilder setResultingItem(ItemType resultingItem)
	{
		recipe.setResultingItem(resultingItem);
		return this;
	}
	
	public RecipeBuilder addComponent(ItemType component)
	{
		return addComponent(component, 1);
	}
	
	public RecipeBuilder addComponent(ItemType component, int quantity)
	{
		recipe.addComponent(component, quantity);
		return this;
	}
	
	public RecipeBuilder addRequiredSkill(SkillType skill, int requiredLevel)
	{
		recipe.getRequiredSkills().put(skill, requiredLevel);
		return this;
	}
}
