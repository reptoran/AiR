package main.entity.zone.generator.dungeon.classic;

import java.awt.Point;

public class HorizontalHall extends NormalHall
{

	public HorizontalHall(Point origin, int width, Direction direction)
	{
		super(origin, 1, width);
		edges.clear();
		
		if (direction == Direction.WEST)
			edges.add(new Point(0, 0));
		if (direction == Direction.EAST)
			edges.add(new Point(0, width - 1));
	}
}
