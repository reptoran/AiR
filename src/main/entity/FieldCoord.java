package main.entity;

import java.text.ParseException;

import main.entity.save.SaveStringBuilder;
import main.entity.save.SaveTokenTag;

public class FieldCoord extends SaveableEntity
{
	protected String name;
	protected char icon;
	protected int color; // TODO: this should be a Color
	protected boolean obstructsSight;
	protected boolean obstructsMotion;

	protected boolean visible;
	protected boolean seen;

	protected double moveCostModifier;
	protected String blockedMessage;

	public FieldCoord(String name, char icon, int color, boolean obstructsSight, boolean obstructsMotion, double moveCostModifier,
			String blockedMessage)
	{
		this.name = name;
		this.icon = icon;
		this.color = color;
		this.obstructsSight = obstructsSight;
		this.obstructsMotion = obstructsMotion;
		this.moveCostModifier = moveCostModifier;
		this.blockedMessage = blockedMessage;
		this.visible = false;
		this.seen = false;
	}

	public FieldCoord()
	{
		this("", ' ', 7, false, false, 1, "");
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public char getIcon()
	{
		return icon;
	}

	public void setIcon(char icon)
	{
		this.icon = icon;
	}

	public int getColor()
	{
		return color;
	}

	public void setColor(int color)
	{
		this.color = color;
	}

	public boolean obstructsSight()
	{
		return obstructsSight;
	}

	public void setObstructsSight(boolean obstructsSight)
	{
		this.obstructsSight = obstructsSight;
	}

	public boolean obstructsMotion()
	{
		return obstructsMotion;
	}

	public void setObstructsMotion(boolean obstructsMotion)
	{
		this.obstructsMotion = obstructsMotion;
	}

	public boolean isVisible()
	{
		return visible;
	}

	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}

	public boolean isSeen()
	{
		return seen;
	}

	public void setSeen(boolean seen)
	{
		this.seen = seen;
	}

	public double getMoveCostModifier()
	{
		return moveCostModifier;
	}

	public void setMoveCostModifier(double moveCostModifier)
	{
		this.moveCostModifier = moveCostModifier;
	}

	public String getBlockedMessage()
	{
		return blockedMessage;
	}

	public void setBlockedMessage(String blockedMessage)
	{
		this.blockedMessage = blockedMessage;
	}

	@Override
	public boolean equals(Object obj)
	{
		FieldCoord fieldCoord;

		if (obj instanceof FieldCoord)
			fieldCoord = (FieldCoord) obj;
		else
			return false;

		if (!name.equals(fieldCoord.name) || icon != fieldCoord.icon || color != fieldCoord.color
				|| obstructsSight != fieldCoord.obstructsSight || obstructsMotion != fieldCoord.obstructsMotion || seen != fieldCoord.seen
				|| visible != fieldCoord.visible)
			return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = 7;

		hash = 31 * hash + name.hashCode();
		hash = 31 * hash + (int) icon;
		hash = 31 * hash + color;
		hash = 31 * hash + (obstructsSight ? 1 : 0);
		hash = 31 * hash + (obstructsMotion ? 1 : 0);
		hash = 31 * hash + (seen ? 1 : 0);
		hash = 31 * hash + (visible ? 1 : 0);

		return hash;
	}

	@Override
	public String saveAsText()
	{
		throw new UnsupportedOperationException("FieldCoord should not be instantiated outside of a builder.");
	}

	@Override
	public String loadFromText(String text) throws ParseException
	{
		throw new UnsupportedOperationException("FieldCoord should not be instantiated outside of a builder.");
	}

	@Override
	public String getUniqueId()
	{
		throw new UnsupportedOperationException("FieldCoord should not be instantiated outside of a builder.");
	}

	@Override
	protected void setMember(SaveStringBuilder saveStringBuilder, SaveTokenTag saveTokenTag)
	{
		throw new UnsupportedOperationException("FieldCoord should not be instantiated outside of a builder.");
	}
}
