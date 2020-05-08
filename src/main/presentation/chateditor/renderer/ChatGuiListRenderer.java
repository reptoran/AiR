package main.presentation.chateditor.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public abstract class ChatGuiListRenderer<E> extends JLabel implements ListCellRenderer<E>
{
	private static final long serialVersionUID = -2124670356285539998L;
	
	protected String textString = "";

	public ChatGuiListRenderer()
	{
		setOpaque(true);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends E> list, E value, int index, boolean isSelected,
			boolean cellHasFocus)
	{
		updateText(value);
		setText(textString);

		Color background = Color.WHITE;
		Color foreground = Color.BLACK;

		if (isSelected)
			background = Color.GRAY;
		
		setBackground(background);
		setForeground(foreground);

		return this;
	}
	
	protected abstract void updateText(E value);
}
