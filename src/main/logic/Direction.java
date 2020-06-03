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
