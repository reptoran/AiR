package main.presentation.curses;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public abstract class CursesGuiScreen
{
	protected CursesGuiMessages messageHandler = null;
	protected Map<Point, DisplayTile> characterMap;
	
	public static final int COLOR_BLACK = 0;
	public static final int COLOR_DARK_BLUE = 1;
	public static final int COLOR_DARK_GREEN = 2;
	public static final int COLOR_DARK_CYAN = 3;
	public static final int COLOR_DARK_RED = 4;
	public static final int COLOR_DARK_MAGENTA = 5;
	public static final int COLOR_BROWN = 6;
	public static final int COLOR_LIGHT_GREY = 7;
	public static final int COLOR_DARK_GREY = 8;
	public static final int COLOR_LIGHT_BLUE = 9;
	public static final int COLOR_LIGHT_GREEN = 10;
	public static final int COLOR_LIGHT_CYAN = 11;
	public static final int COLOR_LIGHT_RED = 12;
	public static final int COLOR_LIGHT_MAGENTA = 13;
	public static final int COLOR_YELLOW = 14;
	public static final int COLOR_WHITE = 15;
	
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
		addText(row, col, text, foreground, COLOR_BLACK);
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
