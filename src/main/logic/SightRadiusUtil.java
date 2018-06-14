package main.logic;

import java.awt.Point;
import java.util.List;

import main.entity.tile.Tile;
import main.entity.zone.Zone;

public class SightRadiusUtil
{
	public static boolean losExists(Zone zone, Point origin, Point target, int radius)
	{
		Line visionRay = new Line(origin.x, origin.y, target.x, target.y);
		List<Point> rayPoints = visionRay.getPoints();
		boolean previousPointObstructed = false;
		int pointsVisited = 0;
		
		for (Point point : rayPoints)
		{
			//If the obstruction is the last point of the line, then LOS still exists.  By getting here, we know it wasn't.
			if (previousPointObstructed || pointsVisited > radius)
				return false;
			
			if (zone.getTile(point.x, point.y).obstructsSight())
				previousPointObstructed = true;
			
			pointsVisited++;		//TODO: may be an off-by-one error, since the first point visited is the origin. 
		}
		
		return true;
	}
	
	public static void updateFieldOfView(Zone zone, Point origin, int radius)
	{
		if (zone == null || !zone.containsPoint(origin))
			return;
		
		int min = -1 * radius;
		int max = radius;
		
		for (int i = min; i <= max; i++)
		{
			for (int j = min; j <= max; j++)
			{
				//only going around the border
				if (i > min && i < max && j > min && j < max)
					continue;
				
				scanRay(zone, origin, new Line(0, 0, i, j), radius);
			}
		}
	}

	private static void scanRay(Zone zone, Point origin, Line visionRay, int maxDistance)
	{
		List<Point> rayPoints = visionRay.getPoints();
		
		for (Point point : rayPoints)
		{
			int zoneRow = point.x + origin.x;
			int zoneCol = point.y + origin.y;
			
			if (!zone.containsPoint(new Point(zoneRow, zoneCol)))
				return;
			
			if (outsideFovRange(point.x, point.y, maxDistance))
				return;
			
			Tile tile = zone.getTile(zoneRow, zoneCol);
			
			tile.setVisible(true);
			tile.setSeen(true);
			
			if (tile.obstructsSight())
				return;
		}
	}

	private static boolean outsideFovRange(int xChange, int yChange, int range)
	{
		 int longerDistance = Math.abs(xChange);
		 int shorterDistance = Math.abs(yChange);
		 
         if (longerDistance < shorterDistance)
         {
             int temp = shorterDistance; 
        	 shorterDistance = longerDistance;
             longerDistance = temp;
         }
         
         if ((int)((longerDistance - shorterDistance) + (1.5 * shorterDistance)) <= range)
        	 return false;
         
         return true;
	}
}
