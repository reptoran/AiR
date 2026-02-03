package main.logic;

import java.awt.Point;

public enum Direction
{
	DIRNW("northwest", (char)218),
	DIRN("north", '^'),
	DIRNE("northeast", (char)191),
	DIRW("west", '<'),
	DIRNONE("no direction", (char)233),
	DIRE("east", '>'),
	DIRSW("southwest", (char)192),
	DIRS("south", 'v'),
	DIRSE("southeast", (char)217);
	
	private String stringValue;
	private char facingIcon;
	
	private Direction(String stringValue, char facingIcon)
	{
		this.stringValue = stringValue;
		this.facingIcon = facingIcon;
	}
	
	public String getStringValue()
	{
		return stringValue;
	}
	
	public char getFacingIcon()
	{
		return facingIcon;
	}
	
	public static Direction fromString(String string)
	{
		return Direction.valueOf(string.toUpperCase());
	}
	
	public static Direction fromCoords(Point coords)
	{
		return fromCoords(coords.x, coords.y);
	}
	
	public static Direction fromCoords(int rowChange, int colChange)
	{
		if (rowChange == 0 && colChange == 0)
			return Direction.DIRNONE;
		
		String directionString = "DIR";
		
		if (rowChange == -1)
			directionString = directionString + "N";
		if (rowChange == 1)
			directionString = directionString + "S";
		if (colChange == -1)
			directionString = directionString + "W";
		if (colChange == 1)
			directionString = directionString + "E";
		
		return fromString(directionString);
	}
	
	public Point getCoordChange()
	{
		switch (this)
		{
		case DIRE:
			return new Point(0, 1);
		case DIRN:
			return new Point(-1, 0);
		case DIRNE:
			return new Point(-1, 1);
		case DIRNONE:
			return new Point(0, 0);
		case DIRNW:
			return new Point(-1, -1);
		case DIRS:
			return new Point(1, 0);
		case DIRSE:
			return new Point(1, 1);
		case DIRSW:
			return new Point(1, -1);
		case DIRW:
			return new Point(0, -1);
		default:
			return null;
		}
	}
	
	public Direction getOppositeDirection()
	{
		switch (this)
		{
		case DIRE:
			return DIRW;
		case DIRN:
			return DIRS;
		case DIRNE:
			return DIRSW;
		case DIRNONE:
			return DIRNONE;
		case DIRNW:
			return DIRSE;
		case DIRS:
			return DIRN;
		case DIRSE:
			return DIRNW;
		case DIRSW:
			return DIRNE;
		case DIRW:
			return DIRE;
		default:
			return null;
		}
	}
	
	public boolean isDiagonal()
	{
		return this.name().length() == 5;
	}
	
	public boolean isOrthagonal()
	{
		return this.name().length() == 4;
	}
}
