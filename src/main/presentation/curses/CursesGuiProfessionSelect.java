package main.presentation.curses;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import main.data.Data;
import main.data.event.InternalEvent;
import main.presentation.GuiState;
import main.presentation.Logger;
import main.presentation.curses.terminal.CursesTerminal;

public class CursesGuiProfessionSelect extends AbtractCursesGuiListInput
{
	private CursesGui parentGui;
	private Data gameData;
	
	protected CursesGuiProfessionSelect(CursesGui parentGui, Data gameData, CursesTerminal terminal)
	{
		super(terminal);
		
		this.parentGui = parentGui;
		this.gameData = gameData;
	}

	@Override
	public void refresh()
	{
		terminal.clear();
		terminal.print(0, 0, "Choose a profession for your character:", COLOR_LIGHT_GREY);
		
		List<String> formattedProfessions = new ArrayList<String>();
		
		for (String profession : gameData.getAvailableProfessions())
			formattedProfessions.add(") " + profession);
		
		printList(formattedProfessions);
	}

	@Override
	public void handleKeyEvent(KeyEvent ke)
	{
		int code = ke.getKeyCode();
		
		if (code == KeyEvent.VK_ESCAPE)
			gameData.receiveInternalEvent(InternalEvent.exitInternalEvent());
		
		int professionIndex = getSelectedIndex(ke.getKeyChar());
		
		if (professionIndex < 0 || professionIndex > (getElementCount() - 1))
			return;
		
		Logger.debug("Profession selected, index is " + professionIndex);
		
		gameData.setPlayerProfession(professionIndex);
		parentGui.initializeNewGame();
		parentGui.setCurrentState(GuiState.PROFESSION_DESCRIPTION);
	}
}
