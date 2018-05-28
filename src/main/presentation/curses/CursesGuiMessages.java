package main.presentation.curses;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import main.presentation.curses.terminal.CursesTerminal;

public class CursesGuiMessages extends CursesGuiUtil
{
	private static final Color DEFAULT_MESSAGE_COLOR = Color.LIGHT_GRAY;
	
	private int messageStartRow = 1;
	private int messageStartCol = 1;
	private int messageHeight = 2;
	private int messageWidth = 78;
	
	private List<String> messageLines;
	
	private CursesGui parentGui;
	
	public CursesGuiMessages(CursesGui parentGui, CursesTerminal terminal)
	{
		super(terminal);
		
		this.parentGui = parentGui;
		messageLines = new ArrayList<String>();
	}

	
	protected void clearMessageArea()
	{
		for (int i = messageStartRow; i < messageStartRow + messageHeight; i++)
		{
			for (int j = messageStartCol; j < messageStartCol + messageWidth; j++)
			{
				// make sure we're in the window
				if (i < 0 || j < 0 || i > 24 || j > 79)
					continue;
				
				terminal.print(j, i, " ", Color.BLACK);
			}
		}
		
		terminal.refresh();
	}
	
	protected void displayNextMessages()
	{
		for (int i = messageStartRow; i < messageStartRow + messageHeight; i++)
		{
			if (!messageLines.isEmpty())
			{
				String messageLine = messageLines.remove(0);
				terminal.print(messageStartCol, i, messageLine, DEFAULT_MESSAGE_COLOR);
			}
		}
		
		if (messageLines.isEmpty())
		{
			parentGui.setCurrentState(CursesGuiState.STATE_NONE);
		}
	}
	
	protected void parseMessageBuffer(String bufferedMessages)
	{
		Scanner splitter = new Scanner(bufferedMessages);
		
		String currentLine = "";
		String toAdd = "";
		
		while (splitter.hasNext())
		{
			toAdd = splitter.next();
			int newLength = toAdd.length() + currentLine.length();
			
			if (newLength > messageWidth - 2)
			{
				messageLines.add(currentLine);
				currentLine = toAdd + " ";
			}
			else
			{
				currentLine = currentLine + toAdd + " ";
			}
		}
		
		splitter.close();
		
		if (currentLine.length() > 0)
			messageLines.add(currentLine);
		
		if (!messageLines.isEmpty())
			parentGui.setCurrentState(CursesGuiState.STATE_MESSAGE);
	}
}
