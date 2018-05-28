package main.presentation.curses;

import main.presentation.curses.terminal.CursesTerminal;

public abstract class CursesGuiUtil
{
	protected CursesTerminal terminal;
	
	public CursesGuiUtil(CursesTerminal csi)
	{
		this.terminal = csi;
	}
}
