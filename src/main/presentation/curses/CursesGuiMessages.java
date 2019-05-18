package main.presentation.curses;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import main.presentation.GuiState;
import main.presentation.Logger;
import main.presentation.curses.terminal.CursesTerminal;
import main.presentation.message.MessageBuffer;

public class CursesGuiMessages extends CursesGuiUtil
{
	private static final Color DEFAULT_MESSAGE_COLOR = Color.LIGHT_GRAY;
	
	private int messageStartRow;
	private int messageStartCol;
	private int messageHeight;
	private int messageWidth;
	
	private CursesGui parentGui;
	private GuiState refreshState;
	private GuiState defaultState;
	
	private List<String> messageLines = new ArrayList<String>();
	
	public CursesGuiMessages(CursesGui parentGui, Rectangle messageDisplayBox, CursesTerminal terminal, GuiState refreshState, GuiState defaultState)
	{
		super(terminal);
		
		messageStartRow = messageDisplayBox.x;
		messageStartCol = messageDisplayBox.y;
		messageHeight = messageDisplayBox.height;
		messageWidth = messageDisplayBox.width;
		
		this.parentGui = parentGui;
		this.refreshState = refreshState;
		this.defaultState = defaultState;
	}

	@Override
	public void refresh()
	{
		messageLines.addAll(MessageBuffer.parseMessageBuffer(messageWidth, messageHeight));
		
		if (!messageLines.isEmpty())
		{
			clearMessageArea();
			displayNextMessages();
		}
		
		updateState();		
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
	
	protected void updateState()
	{
		if (!messageLines.isEmpty())
			parentGui.setCurrentState(refreshState);
		else
			parentGui.setCurrentState(defaultState);
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
	}

	@Override
	public void handleKeyEvent(KeyEvent ke)
	{
		Logger.debug("Key event received in CursesGuiMessages");
		refresh();
	}
}
