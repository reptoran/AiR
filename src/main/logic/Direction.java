package main.logic;

import java.awt.Point;

public enum Direction
{
	DIRNW,
	DIRN,
	DIRNE,
	DIRW,
	DIRNONE,
	DIRE,
	DIRSW,
	DIRS,
	DIRSE;
	
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
		Direction direction = Direction.fromString(this.name());		//holy cow this is clunky
		
		switch (direction)
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
}
