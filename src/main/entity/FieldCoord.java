package main.entity;


public abstract class FieldCoord extends SaveableEntity
{
	protected String name;
	protected char icon;
	protected int color;	//TODO: this should be a Color
	protected boolean obstructsSight;	
	protected boolean obstructsMotion;
	
	protected int moveCost;
	protected String blockedMessage;
	
	public FieldCoord(String name, char icon, int color, boolean obstructsSight, boolean obstructsMotion, int moveCost, String blockedMessage)
	{
		this.name = name;
		this.icon = icon;
		this.color = color;
		this.obstructsSight = obstructsSight;
		this.obstructsMotion = obstructsMotion;
		this.moveCost = moveCost;
		this.blockedMessage = blockedMessage;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getColor()
	{
		return color;
	}
	
	public char getIcon()
	{
		return icon;
	}

	public boolean obstructsSight()
	{
		return obstructsSight;
	}

	public boolean obstructsMotion()
	{
		return obstructsMotion;
	} 
	
	public int getMoveCost()
	{
		return moveCost;
	}

	public String getBlockedMessage()
	{
		return blockedMessage;
	}

	@Override
	public boolean equals(Object obj)
	{
		FieldCoord fieldCoord;
		
		if (obj instanceof FieldCoord)
			fieldCoord = (FieldCoord)obj;
		else
			return false;
		
		if (!name.equals(fieldCoord.name) || icon != fieldCoord.icon || color != fieldCoord.color ||
				obstructsSight != fieldCoord.obstructsSight || obstructsMotion != fieldCoord.obstructsMotion)
			return false;
		
		return true;
	}
	
	@Override
	public int hashCode()
	{
		int hash = 7;
				
		hash = 31 * hash + name.hashCode();
		hash = 31 * hash + (int)icon;
		hash = 31 * hash + color;
		hash = 31 * hash + (obstructsSight ? 1 : 0);
		hash = 31 * hash + (obstructsMotion ? 1 : 0);
		
		return hash;
	}
}
