package main.execute;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;

import main.data.Data;
import main.data.DataAccessor;
import main.logic.Engine;
import main.presentation.GuiState;
import main.presentation.Logger;
import main.presentation.UiManager;
import main.presentation.curses.CursesGui;

public class GameRunner
{
	private static Data dataLayer;
	private static Engine logicLayer;

	public static void main(String[] args)
	{
		Logger.setLogLevel(Logger.WARN);
//		Logger.setLogLevel(Logger.DEBUG);
//		Logger.setLogLevel(Logger.INFO);
		String playerName = "";

		while (playerName.length() == 0)
		{
			playerName = JOptionPane.showInputDialog("What is your name?");
			if (StringUtils.isEmpty(playerName))
				return;
		}

		dataLayer = new Data();
		logicLayer = new Engine(dataLayer);
		
		DataAccessor.getInstance().setData(dataLayer);
		CursesGui gui = new CursesGui(logicLayer, GuiState.SELECT_PROFESSION);
		UiManager.getInstance().setGui(gui);

		if (dataLayer.setPlayerNameAndLoadGame(playerName))
		{
			gui.setSingleLayer(GuiState.MAIN_GAME);
			logicLayer.beginGame();
		}
	}
}
