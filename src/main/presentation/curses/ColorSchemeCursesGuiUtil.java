package main.presentation.curses;

import main.presentation.curses.terminal.CursesTerminal;

public abstract class ColorSchemeCursesGuiUtil extends CursesGuiUtil
{
	private ColorScheme colorScheme;
	
	public ColorSchemeCursesGuiUtil(CursesTerminal csi, ColorScheme colorScheme)
	{
		super(csi);
		this.colorScheme = colorScheme;
	}
	
	public int getBorderColor() {
		return colorScheme.getBorderColor();
	}

	public int getTitleColor()
	{
		return colorScheme.getTitleColor();
	}

	public int getTextColor()
	{
		return colorScheme.getTextColor();
	}

	public int getShadeColor()
	{
		return colorScheme.getShadeColor();
	}

	public int getHighlightColor()
	{
		return colorScheme.getHighlightColor();
	}
}
