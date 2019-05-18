package main.entity.zone.generator.dungeon.classic;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import main.logic.RPGlib;

public class NormalRoom implements DungeonElement
{
	private int height;
	private int width;
	
	private List<Point> edges = new ArrayList<Point>();
	
	public NormalRoom(int height, int width)
	{
		this.height = height;
		this.width = width;
		
		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				if (i != 0 && j != 0 && i != height - 1 && j != width - 1)
					continue;
				
				edges.add(new Point(i, j));
			}
		}
	}
	
	@Override
	public List<Point> getDeadEnds()
	{
		return new ArrayList<Point>();
	}

	@Override
	public boolean hasDeadEnds()
	{
		return false;
	}

	@Override
	public int getHeight()
	{
		return height;
	}

	@Override
	public int getWidth()
	{
		return width;
	}

	@Override
	public Point getNextPointToDigFrom()
	{
		if (edges.isEmpty())
			return null;
		
		int pointIndex = RPGlib.randInt(0, edges.size() - 1);
		return edges.remove(pointIndex);
	}

	@Override
	public DungeonElementCategory getType()
	{
		return DungeonElementCategory.ROOM;
	}

	@Override
	public char[][] getTileMap()
	{
		char[][] map = new char[height][width];
		
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++)
				map[i][j] = '.';
		
		return map;
	}
}
