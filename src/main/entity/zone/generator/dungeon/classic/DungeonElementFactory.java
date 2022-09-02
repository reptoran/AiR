package main.entity.zone.generator.dungeon.classic;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import main.logic.RPGlib;

public class DungeonElementFactory
{
	private static DungeonElementFactory instance = null;
	
	private List<DungeonElementType> halls;
	private List<DungeonElementType> rooms;
	
	private DungeonElementFactory()
	{
		populateHalls();
		populateRooms();
	}
	
	public static DungeonElementFactory getInstance()
	{
		if (instance == null)
			instance = new DungeonElementFactory();
		
		return instance;
	}
	
	public DungeonElement generateHorizontalHall(Point origin, int length, Direction direction)
	{
		return new HorizontalHall(origin, length, direction);
	}
	
	public DungeonElement generateVerticalHall(Point origin, int length, Direction direction)
	{
		return new VerticalHall(origin, length, direction);
	}
	
	public DungeonElement generateRoom(Point origin, int height, int width)
	{
		DungeonElementType type = RPGlib.getRandomListEntry(rooms);
		
		switch (type)
		{
		case NORMAL_ROOM:
			return new NormalRoom(origin, height, width);
		}
		
		return null;
	}
	
	private void populateHalls()
	{
		halls = new ArrayList<DungeonElementType>();
	}
	
	private void populateRooms()
	{
		rooms = new ArrayList<DungeonElementType>();
		rooms.add(DungeonElementType.NORMAL_ROOM);
	}
}
