package main.execute;

import javax.swing.JOptionPane;

import main.data.Data;
import main.logic.Engine;
import main.presentation.Logger;
import main.presentation.UiManager;
import main.presentation.curses.CursesGui;

public class GameRunner
{
	private static Data dataLayer;
	private static Engine logicLayer;

	public static void main(String[] args)
	{
		Logger.setLogLevel(Logger.DEBUG);
		String playerName = "";

		while (playerName.length() == 0)
		{
			playerName = JOptionPane.showInputDialog("What is your name?");
			if (playerName == null)		//TODO: check for whitespace
				return;
		}

		dataLayer = new Data();
		dataLayer.begin(playerName);

		logicLayer = new Engine(dataLayer);

		UiManager.getInstance().setGui(new CursesGui(logicLayer));

		logicLayer.beginGame();
	}
}
