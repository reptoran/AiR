package main.entity.zone.generator.dungeon.rifts;

import java.awt.Point;
import java.awt.Rectangle;

public class EmptySquareRoom extends Room
{
	public EmptySquareRoom(Node node, Rectangle area)
	{
		super(node, area);
	}

	@Override
	void generateRoom()
	{
		int maxX = area.x + area.width - 1;
		int maxY = area.y + area.height - 1;
		
		for (int i = area.x; i <= maxX; i++)
		{
			for (int j = area.y; j <= maxY; j++)
			{
//				if (!(i == area.x || i == maxX || j == area.y || j == maxY))
//					continue;
//				
//				Line ray = new Line(coords.x, coords.y, i, j);
//				int distance = RPGlib.randInt(ray.getLength() / 2, ray.getLength());
//				
//				for (int k = 0; k < distance; k++)
//					tiles.add(ray.getPoint(k));
					
				tiles.add(new Point(i, j));
			}
		}
	}
}
