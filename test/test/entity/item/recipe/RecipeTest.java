package test.entity.item.recipe;

import org.junit.Test;

import main.entity.item.ItemType;
import main.entity.item.recipe.Recipe;
import main.entity.item.recipe.RecipeBuilder;

public class RecipeTest
{
	@Test
	public void toStringTest()
	{
		Recipe recipe = new RecipeBuilder().addComponent(ItemType.BUCKLER, 2).addComponent(ItemType.HEALING_SALVE, 1).setResultingItem(ItemType.DEBUG_GEM_UP).build();
		System.out.println(recipe);
	}
}
