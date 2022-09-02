package main.entity.zone.generator.dungeon.classic;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import main.logic.RPGlib;

public class NormalRoom implements DungeonElement
{
	protected int height;
	protected int width;
	protected Point origin;
	
	protected List<Point> edges = new ArrayList<Point>();
	
	public NormalRoom(Point origin, int height, int width)
	{
		this.origin = new Point(origin.x, origin.y);
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
	public Point getOrigin()
	{
		return origin;
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
		Point toDigFrom = edges.remove(pointIndex);
		
		return new Point(origin.x + toDigFrom.x, origin.y + toDigFrom.y);
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
	
	@Override
	public String toString()
	{
		return "<" + getType().name() + ": Origin is " + origin + ">";
	}

	@Override
	public void addEdge(Point edge)
	{
		Point adjustedEdge = new Point(edge.x - origin.x, edge.y - origin.y);
		
		for (Point point : edges)
		{
			if (point.x == adjustedEdge.x && point.y == adjustedEdge.y)
				return;
		}
		
		edges.add(adjustedEdge);
	}
}
