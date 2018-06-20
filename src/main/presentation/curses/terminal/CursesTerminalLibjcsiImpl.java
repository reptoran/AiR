package main.presentation.curses.terminal;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyListener;
import java.lang.reflect.Field;

import net.slashie.libjcsi.CSIColor;
import net.slashie.libjcsi.wswing.SwingConsoleFrame;
import net.slashie.libjcsi.wswing.WSwingConsoleInterface;

public class CursesTerminalLibjcsiImpl implements CursesTerminal
{
	private WSwingConsoleInterface csi;
	
	public CursesTerminalLibjcsiImpl()
	{
		csi = new WSwingConsoleInterface("", new Font("Font", Font.BOLD, 8));
		csi.setAutoRefresh(false);
	}
	
	public CursesTerminalLibjcsiImpl(String title)
	{
		csi = new WSwingConsoleInterface(title, new Font("Font", Font.BOLD, 8));
		csi.setAutoRefresh(false);
	}
	
	@Override
	public void setTitle(String title)
	{
		try
		{
			Field field = csi.getClass().getDeclaredField("targetFrame");
			field.setAccessible(true);
			SwingConsoleFrame frame = (SwingConsoleFrame) field.get(csi);
			frame.setTitle(title);
		} catch (NoSuchFieldException e)
		{
			throw new UnsupportedOperationException("Cannot set the title of a WSwingConsoleInterface after initializing it.");
		} catch (SecurityException e)
		{
			throw new UnsupportedOperationException("Cannot set the title of a WSwingConsoleInterface after initializing it.");
		} catch (IllegalArgumentException e)
		{
			throw new UnsupportedOperationException("Cannot set the title of a WSwingConsoleInterface after initializing it.");
		} catch (IllegalAccessException e)
		{
			throw new UnsupportedOperationException("Cannot set the title of a WSwingConsoleInterface after initializing it.");
		}
	}

	@Override
	public void addKeyListener(KeyListener kl)
	{
		csi.addKeyListener(kl);
	}

	@Override
	public void print(int row, int column, String text, Color color)
	{
		csi.print(row, column, text, convertToCSIColor(color));
	}

	@Override
	public void print(int row, int column, String text, int color)
	{
		csi.print(row, column, text, asciiColor(color));
	}

	@Override
	public void print(int row, int column, String text, Color foreground, Color background)
	{
		csi.print(row, column, text, convertToCSIColor(foreground), convertToCSIColor(background));
	}

	@Override
	public void print(int row, int column, String text, int foreground, int background)
	{
		csi.print(row, column, text, asciiColor(foreground), asciiColor(background));
	}

	@Override
	public void clear()
	{
		csi.cls();
		csi.refresh();
	}

	@Override
	public void refresh()
	{
		csi.refresh();
	}

	@Override
	public void close()
	{
		csi.close();
	}
	
	protected CSIColor convertToCSIColor(Color color)
	{
		return new CSIColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}
	
	protected CSIColor asciiColor(int color)
	{
		switch (color)
		{
		case 0:
			return CSIColor.BLACK;
		case 1:
			return CSIColor.DARK_BLUE;
		case 2:
			return CSIColor.GREEN;
		case 3:
			return CSIColor.TEAL;
		case 4:
			return CSIColor.DARK_RED;
		case 5:
			return CSIColor.PURPLE;
		case 6:
			return CSIColor.BROWN;
		case 7:
			return CSIColor.LIGHT_GRAY;
		case 8:
			return CSIColor.GRAY;
		case 9:
			return CSIColor.BLUE;
		case 10:
			return CSIColor.BRIGHT_GREEN;
		case 11:
			return CSIColor.CYAN;
		case 12:
			return CSIColor.RED;
		case 13:
			return CSIColor.MAGENTA;
		case 14:
			return CSIColor.YELLOW;
		case 15:
			return CSIColor.WHITE;
		default:
			throw new IllegalArgumentException("Invalid color value of " + color + ".  Color value must be an integer from 0 to 15.");
		}
	}
}
