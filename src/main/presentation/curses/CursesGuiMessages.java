package main.presentation.curses;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import main.presentation.Logger;
import main.presentation.message.MessageBuffer;

public class CursesGuiMessages extends CursesGuiScreen
{
	private static final int DEFAULT_MESSAGE_COLOR = COLOR_LIGHT_GREY;
	
	private int messageStartRow;
	private int messageStartCol;
	private int messageHeight;
	private int messageWidth;
	
	private List<String> messageLines = new ArrayList<String>();
	
	private CursesGui parentGui;
	
	public CursesGuiMessages(CursesGui parentGui, Rectangle messageDisplayBox)
	{		
		messageStartRow = messageDisplayBox.x;
		messageStartCol = messageDisplayBox.y;
		messageHeight = messageDisplayBox.height;
		messageWidth = messageDisplayBox.width;
		
		this.parentGui = parentGui;
	}

	@Override
	public void refresh()
	{
		clearScreen();
		
		messageLines.addAll(MessageBuffer.parseMessageBuffer(messageWidth, messageHeight));
		
		displayNextMessages();
	}
	
	protected void displayNextMessages()
	{
		for (int i = messageStartRow; i < messageStartRow + messageHeight; i++)
		{
			if (!messageLines.isEmpty())
			{
				String messageLine = messageLines.remove(0);
				
				for (int j = messageStartCol; j < messageWidth; j++)
				{
					int index = j - messageStartCol;
					char symbol = ' ';
					
					if (index < messageLine.length())
						symbol = messageLine.charAt(index);
					
					addCharacter(i, j, new DisplayTile(symbol, DEFAULT_MESSAGE_COLOR, COLOR_BLACK));
				}
			}
		}
	}
	
	public boolean shouldMessagesBlockGameInput()
	{
		if (messageLines.isEmpty())
			return false;
		
		return true;
	}
	
	@Override
	public boolean delegatesKeyEventsToNextLayer()
	{
		return true;
	}

	@Override
	protected void handleKey(int code, char keyChar)
	{
		Logger.debug("Key event received in CursesGuiMessages");
		parentGui.refreshInterface();
	}
}
