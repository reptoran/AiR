package main.entity.zone.generator.dungeon.classic;

import java.awt.Point;
import java.util.List;

public abstract class NormalHall extends NormalRoom
{
	public NormalHall(Point origin, int height, int width)
	{
		super(origin, height, width);
	}
	
	@Override
	public List<Point> getDeadEnds()
	{
		return edges;
	}

	@Override
	public boolean hasDeadEnds()
	{
		return !edges.isEmpty();
	}

	@Override
	public DungeonElementCategory getType()
	{
		return DungeonElementCategory.HALL;
	}

}
