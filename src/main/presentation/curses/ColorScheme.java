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
		return new ColorScheme(CursesGuiUtil.COLOR_BROWN, CursesGuiUtil.COLOR_YELLOW, CursesGuiUtil.COLOR_LIGHT_GREY,
				CursesGuiUtil.COLOR_DARK_GREY, CursesGuiUtil.COLOR_WHITE);
	}

	public static ColorScheme monochromeScheme()
	{
		return new ColorScheme(CursesGuiUtil.COLOR_LIGHT_GREY, CursesGuiUtil.COLOR_LIGHT_GREY, CursesGuiUtil.COLOR_LIGHT_GREY,
				CursesGuiUtil.COLOR_LIGHT_GREY, CursesGuiUtil.COLOR_LIGHT_GREY);
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
