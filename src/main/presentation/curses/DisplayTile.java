package main.presentation.curses;

public class DisplayTile
{
	private char icon;
	private int foregroundColor;
	private int backgroundColor;
	
	public DisplayTile(char icon)
	{
		this(icon, CursesGuiScreen.COLOR_LIGHT_GREY);
	}

	public DisplayTile(char icon, int foregroundColor)
	{
		this(icon, foregroundColor, CursesGuiScreen.COLOR_BLACK);
	}

	public DisplayTile(char icon, int foregroundColor, int backgroundColor)
	{
		this.icon = icon;
		this.foregroundColor = foregroundColor;
		this.backgroundColor = backgroundColor;
	}
	
	public char getIcon()
	{
		return icon;
	}
	
	public int getForegroundColor()
	{
		return foregroundColor;
	}
	
	public int getBackgroundColor()
	{
		return backgroundColor;
	}
}