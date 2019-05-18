package main.entity.zone.generator.dungeon.classic;

import java.awt.Point;
import java.util.List;

public interface DungeonElement
{
	public List<Point> getDeadEnds();
	public boolean hasDeadEnds();
	public int getHeight();
	public int getWidth();
	public Point getNextPointToDigFrom();
	public DungeonElementCategory getType();
	public char[][] getTileMap();
}
