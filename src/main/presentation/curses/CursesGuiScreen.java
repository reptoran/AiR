package main.presentation.curses;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public abstract class CursesGuiScreen
{
	protected CursesGuiMessages messageHandler = null;
	protected Map<Point, DisplayTile> characterMap;
	
	public CursesGuiScreen()
	{
		this.characterMap = new HashMap<Point, DisplayTile>();
	}
	
	public abstract void refresh();
	protected abstract void handleKey(int code, char keyChar);
	
	public void clearScreen()
	{
		characterMap.clear();
	}
	
	protected void addText(int row, int col, String text, int foreground)
	{
		addText(row, col, text, foreground, Colors.BLACK);
	}
	
	protected void addText(int row, int col, String text, int foreground, int background)
	{
		for (int i = 0; i < text.length(); i++)
			addCharacter(row, col + i, new DisplayTile(text.charAt(i), foreground, background));
	}
	
	protected void addCharacter(int row, int col, DisplayTile tile)
	{
		Point point = new Point(row, col);
		characterMap.put(point, tile);
	}
	
	protected void clearCharacter(int row, int col)
	{
		Point point = new Point(row, col);
		characterMap.remove(point);
	}
	
	public DisplayTile getCharacter(Point point)
	{
		return characterMap.get(point);
	}

	public CursesGuiScreen getMessageHandler()
	{
		return messageHandler;
	}
	
	public boolean delegatesKeyEventsToNextLayer()
	{
		return false;
	}
	
	public void handleKeyEvent(KeyEvent ke)
	{
		int code = ke.getKeyCode();
		char keyChar = ke.getKeyChar();
		
		if (messageHandler == null)
		{
			handleKey(code, keyChar);
			return;
		}
		
		if (messageHandler.shouldMessagesBlockGameInput())
			messageHandler.handleKey(code, keyChar);
		else
			handleKey(code, keyChar);
	}
}
