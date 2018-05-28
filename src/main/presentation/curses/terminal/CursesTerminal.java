package main.presentation.curses.terminal;

import java.awt.Color;
import java.awt.event.KeyListener;

public interface CursesTerminal
{
	public void setTitle(String title);
	public void addKeyListener(KeyListener kl);
	public void print(int row, int column, String text, Color color);
	public void print(int row, int column, String text, int color);
	public void print(int row, int column, String text, Color foreground, Color background);
	public void print(int row, int column, String text, int foreground, int background);
	public void refresh();
	public void close();
}
