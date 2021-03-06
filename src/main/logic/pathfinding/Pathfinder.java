package main.logic.pathfinding;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import main.entity.tile.Tile;
import main.entity.zone.Zone;

public class Pathfinder
{
	private static final int SECTION_PADDING = 2;
	
	public static List<Point> findPath(Zone zone, Point origin, Point destination)
	{
		int topBorder = Math.min(origin.x, destination.x);
		int bottomBorder = Math.max(origin.x, destination.x);
		int leftBorder = Math.min(origin.y, destination.y);
		int rightBorder = Math.max(origin.y, destination.y);
		
		Point topLeftCorner = new Point(topBorder - SECTION_PADDING, leftBorder - SECTION_PADDING);
		Point bottomRightCorner = new Point(bottomBorder + SECTION_PADDING, rightBorder + SECTION_PADDING);
		
		PathfindingMap obstructionMap = createObstructionMap(zone, topLeftCorner, bottomRightCorner);
		PathfindingMap distanceMap = obstructionMap.createRawDistanceMap();
		distanceMap.setCell(destination.x, destination.y, 0);
		
		distanceMap = updateDistanceAroundPoint(obstructionMap, distanceMap, destination);
//		distanceMap.printMap();
		
		return getAndAddNearestPointToDestination(new ArrayList<Point>(), distanceMap, origin, destination);
	}
	
	private static List<Point> getAndAddNearestPointToDestination(List<Point> travelledPoints, PathfindingMap distanceMap, Point currentLocation, Point destination)
	{
		int x = currentLocation.x;
		int y = currentLocation.y;
		int distanceAtPoint = distanceMap.getCell(x, y);
		
		List<Point> potentialNextLocationCoords = new ArrayList<Point>();
		
		for (int i = -1; i <= 1; i++)
		{
			for (int j = -1; j <= 1; j++)
			{
				if (i == 0 && j == 0)
					continue;
				
				if (!distanceMap.containsCell(x + i, y + j))
					continue;
				
				int newPointDistance = distanceMap.getCell(x + i, y + j);
				
				if (newPointDistance < distanceAtPoint)
				{
					potentialNextLocationCoords.clear();
					distanceAtPoint = newPointDistance;
				}
				
				if (newPointDistance == distanceAtPoint)
					potentialNextLocationCoords.add(new Point(x + i, y + j));
			}
		}
		
		if (potentialNextLocationCoords.isEmpty())
			return travelledPoints;
		
//		Point nextPoint = potentialNextLocationCoords.get(0);
		Point nextPoint = getNextPointFavoringCardinalDirection(potentialNextLocationCoords, destination);
		
		travelledPoints.add(nextPoint);
		return getAndAddNearestPointToDestination(travelledPoints, distanceMap, nextPoint, destination);
	}
	
	private static Point getNextPointFavoringCardinalDirection(List<Point> potentialPoints, Point destination)
	{
		int absoluteCoordSum = Integer.MAX_VALUE;
		Point bestPoint = null;
		
		for (Point point : potentialPoints)
		{
			int newCoordSum = Math.abs(point.x - destination.x) + Math.abs(point.y - destination.y);
			if (newCoordSum < absoluteCoordSum)
			{
				bestPoint = point;
				absoluteCoordSum = newCoordSum;
			}
		}
		
		return bestPoint;
	}
	
	private static PathfindingMap updateDistanceAroundPoint(PathfindingMap obstructionMap, PathfindingMap distanceMap, Point point)
	{
		List<Point> updatedPoints = new ArrayList<Point>();
		
		int x = point.x;
		int y = point.y;
		int distanceAtPoint = distanceMap.getCell(x, y);
		
		for (int i = -1; i <= 1; i++)
		{
			for (int j = -1; j <= 1; j++)
			{
				int row = x + i;
				int col = y + j;
				
				if (!distanceMap.containsCell(row, col))
					continue;
				
				if (obstructionMap.isBlocked(row, col))
					continue;
				
				//ignore any cells that wouldn't benefit from setting the value to (thisDist + 1); this includes the center point
				if (distanceMap.getCell(row, col) <= distanceAtPoint + 1)
					continue;
				
				distanceMap.setCell(row, col, distanceAtPoint + 1);
				updatedPoints.add(new Point(row, col));
			}
		}
		
		for (Point pointToUpdate : updatedPoints)
		{
			distanceMap = updateDistanceAroundPoint(obstructionMap, distanceMap, pointToUpdate);
		}
		
		return distanceMap;
	}
	
	public static PathfindingMap createObstructionMap(Zone zone, Point topLeftCorner, Point bottomRightCorner)
	{
		int topBorder = topLeftCorner.x - 1;
		int bottomBorder = bottomRightCorner.x + 1;
		int leftBorder = topLeftCorner.y - 1;
		int rightBorder = bottomRightCorner.y + 1;
		
		PathfindingMap obstructionMap = new PathfindingMap(zone.getHeight(), zone.getWidth());
		
		for (int i = topBorder; i <= bottomBorder; i++)
		{
			for (int j = leftBorder; j <= rightBorder; j++)
			{
				if (!obstructionMap.containsCell(i, j))
					continue;
				
				//put a "wall" around the area we want to pathfind in
				if (i == topBorder || i == bottomBorder || j == leftBorder || j == rightBorder)
				{
					obstructionMap.setBlocked(i, j, true);
					continue;
				}
				
				boolean blocked = false;
				
				Tile tile = zone.getTile(i, j);
				
				if (tile.obstructsMotion() || tile.getActorHere() != null)
					blocked = true;
				
				obstructionMap.setBlocked(i, j, blocked);
			}
		}
		
		return obstructionMap;
	}
}
