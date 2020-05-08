package main.logic;

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
}
