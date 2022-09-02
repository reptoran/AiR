package main.presentation.curses;

public abstract class ColorSchemeCursesGuiUtil extends CursesGuiScreen
{
	private ColorScheme colorScheme;
	
	public ColorSchemeCursesGuiUtil(ColorScheme colorScheme)
	{
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
