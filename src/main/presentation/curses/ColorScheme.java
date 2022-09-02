package main.presentation.curses;

public class ColorScheme
{
	private int borderColor;
	private int titleColor;
	private int textColor;
	private int shadeColor;
	private int highlightColor;

	public static ColorScheme woodenScheme()
	{
		return new ColorScheme(CursesGuiScreen.COLOR_BROWN, CursesGuiScreen.COLOR_YELLOW, CursesGuiScreen.COLOR_LIGHT_GREY,
				CursesGuiScreen.COLOR_DARK_GREY, CursesGuiScreen.COLOR_WHITE);
	}

	public static ColorScheme specialWoodenScheme()
	{
		return new ColorScheme(CursesGuiScreen.COLOR_DARK_MAGENTA, CursesGuiScreen.COLOR_YELLOW, CursesGuiScreen.COLOR_LIGHT_GREY,
				CursesGuiScreen.COLOR_DARK_GREY, CursesGuiScreen.COLOR_WHITE);
	}

	public static ColorScheme monochromeScheme()
	{
		return new ColorScheme(CursesGuiScreen.COLOR_LIGHT_GREY, CursesGuiScreen.COLOR_LIGHT_GREY, CursesGuiScreen.COLOR_LIGHT_GREY,
				CursesGuiScreen.COLOR_LIGHT_GREY, CursesGuiScreen.COLOR_LIGHT_GREY);
	}

	private ColorScheme(int borderColor, int titleColor, int textColor, int shadeColor, int highlightColor)
	{
		this.borderColor = borderColor;
		this.titleColor = titleColor;
		this.textColor = textColor;
		this.shadeColor = shadeColor;
		this.highlightColor = highlightColor;
	}

	public int getBorderColor()
	{
		return borderColor;
	}

	public int getTitleColor()
	{
		return titleColor;
	}

	public int getTextColor()
	{
		return textColor;
	}

	public int getShadeColor()
	{
		return shadeColor;
	}

	public int getHighlightColor()
	{
		return highlightColor;
	}
}
