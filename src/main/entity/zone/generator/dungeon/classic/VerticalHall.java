package main.entity.zone.generator.dungeon.classic;

import java.awt.Point;

public class VerticalHall extends NormalHall
{

	public VerticalHall(Point origin, int height, Direction direction)
	{
		super(origin, height, 1);
		edges.clear();
		
		if (direction == Direction.NORTH)
			edges.add(new Point(0, 0));
		if (direction == Direction.SOUTH)
			edges.add(new Point(height - 1, 0));
	}
}
