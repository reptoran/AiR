package main.presentation.curses;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import main.data.Data;
import main.data.event.InternalEvent;
import main.presentation.GuiState;
import main.presentation.Logger;
import main.presentation.message.MessageBuffer;

public class CursesGuiProfessionSelect extends AbstractCursesGuiListInput
{
	private CursesGui parentGui;
	private Data gameData;
	
	private boolean professionChosen = false;
	
	protected CursesGuiProfessionSelect(CursesGui parentGui, Data gameData)
	{
		super(ColorScheme.monochromeScheme());
		
		this.parentGui = parentGui;
		this.gameData = gameData;
		this.messageHandler = new CursesGuiMessages(parentGui, new Rectangle(0, 0, 80, 25));
	}

	@Override
	public void refresh()
	{
		characterMap.clear();
		addText(0, 0, "Choose a profession for your character:", getTextColor());
		
		List<String> formattedProfessions = new ArrayList<String>();
		
		for (String profession : gameData.getAvailableProfessions())
			formattedProfessions.add(") " + profession);
		
		printList(formattedProfessions);
	}

	@Override
	protected void handleKey(int code, char keyChar)
	{
		if (professionChosen)
		{
			advanceToNextScreen();
			return;
		}
		
		Logger.debug("Key event received in CursesGuiProfessionSelect");
		if (code == KeyEvent.VK_ESCAPE)
			gameData.receiveInternalEvent(InternalEvent.exitInternalEvent());
		
		int professionIndex = getSelectedIndex(keyChar);
		
		if (professionIndex < 0 || professionIndex > (getElementCount() - 1))
			return;
		
		Logger.debug("Profession selected, index is " + professionIndex);
		
		gameData.setPlayerProfession(professionIndex);
		professionChosen = true;
		
		if (!MessageBuffer.hasMessages())
			advanceToNextScreen();
		
		parentGui.refreshInterface();
	}
	
	private void advanceToNextScreen()
	{
		parentGui.initializeNewGame();
		parentGui.setSingleLayer(GuiState.GAME_START);
	}
}
