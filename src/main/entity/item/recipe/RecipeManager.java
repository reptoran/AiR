package main.entity.item.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.entity.actor.Actor;
import main.entity.actor.SkillType;
import main.entity.item.ItemType;

public class RecipeManager
{
	private List<Recipe> allRecipes = null;
	private Map<ItemType, Recipe> recipesByResult = null;		//assumes each item only has one way to make it; fair enough for now
	private static RecipeManager instance = null;
	
	public static RecipeManager getInstance()
	{
		if (instance == null)
			instance = new RecipeManager();
		
		return instance;
	}
	
	private RecipeManager()
	{
		loadAllRecipes();
		mapRecipesByResultingItem();
	}
	
	//TODO: right now this is going to be extremely inefficient once there are hundreds of recipes, but it'll do for now
	//		also, right now this assumes there are no "hidden" recipes - once you have the level, you can make everything associated with that level (OSRS style)
	public List<Recipe> getKnownRecipes(Actor actor)
	{
		List<Recipe> knownRecipes = new ArrayList<Recipe>();
		
		for (Recipe recipe : allRecipes)
		{
			if (recipe.sufficientSkills(actor))
				knownRecipes.add(recipe);
		}
		
		return knownRecipes;
	}
	
	public Recipe getRecipeForItem(ItemType item)
	{
		return recipesByResult.get(item);
	}
	
	private void mapRecipesByResultingItem()
	{
		recipesByResult = new HashMap<ItemType, Recipe>();
		
		for (Recipe recipe : allRecipes)
		{
			recipesByResult.put(recipe.getResultingItem(), recipe);
		}
	}
	
	private void loadAllRecipes()
	{
		allRecipes = new ArrayList<Recipe>();
		
		allRecipes.add(new RecipeBuilder().setResultingItem(ItemType.HEALING_SALVE).addComponent(ItemType.MEDICINAL_FUNGUS, 2).addRequiredSkill(SkillType.HERBALISM, 2).build());
	}
}
