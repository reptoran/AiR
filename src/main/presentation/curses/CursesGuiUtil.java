package main.presentation.curses;

import main.presentation.curses.terminal.CursesTerminal;

public abstract class CursesGuiUtil
{
	protected CursesTerminal terminal;
	
	protected static final int COLOR_LIGHT_GREY = 7;
	
	public CursesGuiUtil(CursesTerminal csi)
	{
		this.terminal = csi;
	}
	
	public abstract void refresh();
}
