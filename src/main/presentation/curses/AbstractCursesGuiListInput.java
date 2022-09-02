package main.presentation.curses;

import java.util.List;

import main.presentation.Logger;

public abstract class AbstractCursesGuiListInput extends ColorSchemeCursesGuiUtil
{
	private int elementCount = 0;

	protected AbstractCursesGuiListInput(ColorScheme colorScheme)
	{
		super(colorScheme);
	}
	
	protected void printList(List<String> elements)
	{
		elementCount = 0;
		
		for (String element : elements)
		{
			elementCount++;
			addText(elementCount, 1, (char)(96 + elementCount) + element, getTextColor());
		}
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
