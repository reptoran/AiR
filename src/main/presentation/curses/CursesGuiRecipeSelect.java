package main.presentation.curses;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import main.data.event.ActorCommand;
import main.entity.item.recipe.Recipe;
import main.entity.item.recipe.RecipeManager;
import main.logic.Engine;
import main.presentation.GuiState;

public class CursesGuiRecipeSelect extends AbstractCursesGuiListInput
{
	private CursesGui parentGui;
	private Engine engine;
	
	private List<Recipe> recipes = null;
	
	protected CursesGuiRecipeSelect(CursesGui parentGui, Engine engine)
	{
		super(ColorScheme.monochromeScheme());
		
		this.parentGui = parentGui;
		this.engine = engine;
	}

	@Override
	public void refresh()
	{
		clearScreen();
		addText(0, 0, "Choose a recipe to make:", getTextColor());
		
		List<String> formattedRecipes = new ArrayList<String>();
		
		if (recipes == null)
			recipes =  RecipeManager.getInstance().getKnownRecipes(engine.getData().getPlayer());
		
		for (Recipe recipe : recipes)
			formattedRecipes.add(") " + recipe);
		
		printList(formattedRecipes);
	}

	@Override
	protected void handleKey(int code, char keyChar)
	{
		if (code == KeyEvent.VK_ESCAPE)
		{
			recipes = null;
			parentGui.setSingleLayer(GuiState.MAIN_GAME);
			return;
		}
		
		int recipeIndex = getSelectedIndex(keyChar);
		
		if (recipeIndex < 0 || recipeIndex > (getElementCount() - 1))
			return;
		
		Recipe recipe = recipes.get(recipeIndex);
		recipes = null;
		engine.receiveCommand(ActorCommand.recipe(recipe));
		parentGui.setSingleLayer(GuiState.MAIN_GAME);
	}
}
