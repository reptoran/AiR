package main.presentation.curses;

import main.logic.Engine;
import main.presentation.GuiState;

public class GameStartHelper extends CursesGuiScreen
{
	private CursesGui parentGui;
	private Engine engine;
	
	public GameStartHelper(CursesGui parentGui, Engine engine)
	{
		this.parentGui = parentGui;
		this.engine = engine;
	}
	
	@Override
	public void refresh()
	{
		parentGui.setSingleLayer(GuiState.MAIN_GAME);
		engine.beginGame();
	}

	@Override
	protected void handleKey(int code, char keyChar)
	{
		// this screen should trigger the game start and be updated to another one instantly, with no chance to press keys
	}
}
