package main.presentation.curses;

import java.util.List;

import main.presentation.Logger;
import main.presentation.curses.terminal.CursesTerminal;

public abstract class AbtractCursesGuiListInput extends CursesGuiUtil
{
	private int elementCount = 0;

	protected AbtractCursesGuiListInput(CursesTerminal csi)
	{
		super(csi);
	}
	
	protected void printList(List<String> elements)
	{
		elementCount = 0;
		
		for (String element : elements)
		{
			elementCount++;
			terminal.print(1, elementCount, (char)(96 + elementCount) + element, COLOR_LIGHT_GREY);
		}
		
		terminal.refresh();
	}
	
	protected int getElementCount()
	{
		return elementCount;
	}
	
	protected int getSelectedIndex(char keyChar)
	{
		char lowercaseChar = Character.toLowerCase(keyChar);
		int index = (int)(lowercaseChar) - 97;
		Logger.info("Key " + keyChar + " pressed; this translates to an item index of " + index + ".");
		return index;
	}
}
