package main.presentation.curses;

import java.awt.event.KeyEvent;

import main.presentation.curses.terminal.CursesTerminal;

public abstract class CursesGuiUtil
{
	protected CursesTerminal terminal;
	
	protected static final int COLOR_BLACK = 0;
	protected static final int COLOR_DARK_BLUE = 1;
	protected static final int COLOR_DARK_GREEN = 2;
	protected static final int COLOR_DARK_CYAN = 3;
	protected static final int COLOR_DARK_RED = 4;
	protected static final int COLOR_DARK_MAGENTA = 5;
	protected static final int COLOR_BROWN = 6;
	protected static final int COLOR_LIGHT_GREY = 7;
	protected static final int COLOR_DARK_GREY = 8;
	protected static final int COLOR_LIGHT_BLUE = 9;
	protected static final int COLOR_LIGHT_GREEN = 10;
	protected static final int COLOR_LIGHT_CYAN = 11;
	protected static final int COLOR_LIGHT_RED = 12;
	protected static final int COLOR_LIGHT_MAGENTA = 13;
	protected static final int COLOR_YELLOW = 14;
	protected static final int COLOR_WHITE = 15;
	
	public CursesGuiUtil(CursesTerminal csi)
	{
		this.terminal = csi;
	}
	
	public abstract void refresh();
	public abstract void handleKeyEvent(KeyEvent ke);
}
