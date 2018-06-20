package main.presentation.curses;

import java.awt.Color;
import java.util.List;

import main.presentation.GuiState;
import main.presentation.curses.terminal.CursesTerminal;
import main.presentation.message.MessageBuffer;

public class CursesGuiMessages extends CursesGuiUtil
{
	private static final Color DEFAULT_MESSAGE_COLOR = Color.LIGHT_GRAY;
	
	private int messageStartRow = 0;
	private int messageStartCol = 0;
	private int messageHeight = 2;
	private int messageWidth = 80;
	
	private CursesGui parentGui;
	
	public CursesGuiMessages(CursesGui parentGui, CursesTerminal terminal)
	{
		super(terminal);
		
		this.parentGui = parentGui;
	}

	@Override
	public void refresh()
	{
		clearMessageArea();
		displayNextMessages();
		terminal.refresh();
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
	}
	
	protected void displayNextMessages()
	{
		List<String> messageLines = MessageBuffer.parseMessageBuffer(messageWidth);
		
		for (int i = messageStartRow; i < messageStartRow + messageHeight; i++)
		{
			if (!messageLines.isEmpty())
			{
				String messageLine = messageLines.remove(0);
				terminal.print(messageStartCol, i, messageLine, DEFAULT_MESSAGE_COLOR);
			}
		}
		
		if (!messageLines.isEmpty())
			parentGui.setCurrentState(GuiState.MESSAGE);
		else
			parentGui.setCurrentState(GuiState.NONE);
	}
}
