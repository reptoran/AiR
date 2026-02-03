package main.logic;

import java.awt.Point;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.entity.actor.Actor;
import main.entity.actor.SkillRank;
import main.entity.actor.SkillType;
import main.entity.tile.Tile;
import main.entity.zone.Zone;
import main.presentation.Logger;

public class ActorSightUtil
{
	private Map<SkillRank, Integer> sightRadius;
	private Map<SkillRank, Integer> hearingRadius;
	private Map<SkillRank, Integer> stealthNoiseModifier;	//There's probably a better place for this than the SIGHT util
	
	private static ActorSightUtil instance = null;
	
	public static ActorSightUtil getInstance()
	{
		if (instance == null)
			instance = new ActorSightUtil();
		
		return instance;
	}
	
	private ActorSightUtil()
	{
		sightRadius = new HashMap<SkillRank, Integer>();
		sightRadius.put(SkillRank.UNKNOWN, 5);
		sightRadius.put(SkillRank.UNSKILLED, 6);
		sightRadius.put(SkillRank.NOVICE, 7);
		sightRadius.put(SkillRank.ADEPT, 8);
		sightRadius.put(SkillRank.EXPERT, 9);
		sightRadius.put(SkillRank.MASTER, 10);
		
		hearingRadius = new HashMap<SkillRank, Integer>();
		hearingRadius.put(SkillRank.UNKNOWN, -4);
		hearingRadius.put(SkillRank.UNSKILLED, -3);
		hearingRadius.put(SkillRank.NOVICE, -2);
		hearingRadius.put(SkillRank.ADEPT, -1);
		hearingRadius.put(SkillRank.EXPERT, 0);
		hearingRadius.put(SkillRank.MASTER, 1);
		
		stealthNoiseModifier = new HashMap<SkillRank, Integer>();
		stealthNoiseModifier.put(SkillRank.UNKNOWN, 1);
		stealthNoiseModifier.put(SkillRank.UNSKILLED, 0);
		stealthNoiseModifier.put(SkillRank.NOVICE, -1);
		stealthNoiseModifier.put(SkillRank.ADEPT, -2);
		stealthNoiseModifier.put(SkillRank.EXPERT, -3);
		stealthNoiseModifier.put(SkillRank.MASTER, -4);
	}
	
	public int getSightRadius(SkillRank awarenessRank)
	{
		return sightRadius.get(awarenessRank);
	}
	
	public int getHearingRadius(SkillRank awarenessRank)
	{
		return hearingRadius.get(awarenessRank);
	}
	
	public int getStealthNoiseModifier(SkillRank stealthRank)
	{
		return stealthNoiseModifier.get(stealthRank);
	}
	
	public void updateFieldOfView(Zone zone, Actor actor)
	{
		if (actor == null)
		{
			Logger.warn("Cannot update FOV for null actor.");
			return;
		}
		
		Point origin = zone.getCoordsOfActor(actor);
		
		if (origin == null)
		{
			Logger.warn("Actor [" + actor.getName() + "] is not present in Zone [" + zone.getName() + "]; FOV will not be updated.");
			return;
		}
		
		updateFieldOfView(zone, origin, actor.getFacing(), actor.getSkillRank(SkillType.AWARENESS));
	}
	
	public void updateFieldOfView(Zone zone, Point origin, Direction facing, SkillRank awarenessRank)
	{
		if (zone == null || !zone.containsPoint(origin))
			return;
		
		int radius = getSightRadius(awarenessRank);
		
		int min = -1 * radius;
		int max = radius;
		
		for (int i = min; i <= max; i++)
		{
			for (int j = min; j <= max; j++)
			{
				//only going around the border
				if (i > min && i < max && j > min && j < max)
					continue;
				
				Line visionRay = new Line(0, 0, i, j);
				scanRay(zone, origin, visionRay, radius, facing, awarenessRank);
			}
		}
		
		//the player's current tile can always been seen
		Tile tile = zone.getTile(origin);
		tile.setVisible(true);
		tile.setSeen(true);
	}

	private void scanRay(Zone zone, Point origin, Line visionRay, int maxDistance, Direction facing, SkillRank awarenessRank)
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
			
			if (tile.obstructsSight())
			{
				if (!isOutsidePerceptionFov(awarenessRank, point, facing))
				{
					tile.setVisible(true);
					tile.setSeen(true);
				}
				
				return;
			}
			
			if (isOutsidePerceptionFov(awarenessRank, point, facing))
				continue;
			
			tile.setVisible(true);
			tile.setSeen(true);
		}
	}

	public boolean isOutsidePerceptionFov(SkillRank perception, Point point, Direction facing)
	{
		if (point.x == 0 && point.y == 0)
			return false;		//an actor can always see its own tile
		
		switch (perception)
		{
		case UNKNOWN:
			return isNotFrontQuadrant(point.x, point.y, facing);
		case UNSKILLED:
			return isRearHalf(point.x, point.y, facing);
		case NOVICE:
		case ADEPT:
			return isRearQuadrant(point.x, point.y, facing);
		case EXPERT:
			return isDirectlyBehind(point.x, point.y, facing);
		case MASTER:
			return false;
		default:
			return true;
		}
	}

	public boolean outsideFovRange(int xChange, int yChange, int range)
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
	
	private boolean isNotFrontQuadrant(int i, int j, Direction facing)
	{
		if (facing == Direction.DIRNONE)
			return false;
		
		Point coordChange = facing.getOppositeDirection().getCoordChange();
		
		if (facing.isDiagonal() && (valuesSharePositivity(i, coordChange.x, false) || valuesSharePositivity(j, coordChange.y, false)))
			return true;
		
		if (!facing.isOrthagonal())
			return false;
		
		if (coordChange.x == 0)
			return (valuesSharePositivity(j, coordChange.y, false) || Math.abs(j) < Math.abs(i));
		
		if (coordChange.y == 0)
			return (valuesSharePositivity(i, coordChange.x, false) || Math.abs(i) < Math.abs(j));
		
		return false;
	}
	
	private boolean isRearQuadrant(int i, int j, Direction facing)
	{
		if (facing == Direction.DIRNONE)
			return false;
		
		Point coordChange = facing.getOppositeDirection().getCoordChange();
		
		if (facing.isDiagonal() && (valuesSharePositivity(i, coordChange.x, true) && valuesSharePositivity(j, coordChange.y, true)))
			return true;
		
		if (!facing.isOrthagonal())
			return false;
		
		if (coordChange.x == 0)
			return (valuesSharePositivity(j, coordChange.y, false) && Math.abs(j) >= Math.abs(i));
		
		if (coordChange.y == 0)
			return (valuesSharePositivity(i, coordChange.x, false) && Math.abs(i) >= Math.abs(j));
		
		return false;
	}
	
	private boolean isRearHalf(int i, int j, Direction facing)
	{
		if (facing == Direction.DIRNONE)
			return false;
		
		if (facing.isOrthagonal())
			return isOrthagonalRearHalf(i, j, facing);
		
		return isDiagonalRearHalf(i, j, facing);
	}
	
	private boolean isOrthagonalRearHalf(int i, int j, Direction facing)
	{
		Point coordChange = facing.getOppositeDirection().getCoordChange();
		
		if (coordChange.x == 0)
			return valuesSharePositivity(j, coordChange.y, false);
		
		if (coordChange.y == 0)
			return valuesSharePositivity(i, coordChange.x, false);
		
		return false;
	}
	
	private boolean isDiagonalRearHalf(int i, int j, Direction facing)
	{
		Point coordChange = facing.getOppositeDirection().getCoordChange();
		
		if (valuesSharePositivity(i, coordChange.x, true) && valuesSharePositivity(j, coordChange.y, true))
			return true;
		
		if (valuesSharePositivity(i, coordChange.x, true))
			return Math.abs(j) < Math.abs(i);
		
		if (valuesSharePositivity(j, coordChange.y, true))
			return Math.abs(i) < Math.abs(j);
		
		return false;
	}

	private boolean isDirectlyBehind(int i, int j, Direction facing)
	{
		int singleUnitI = i == 0 ? 0 : i / Math.abs(i);
		int singleUnitJ = j == 0 ? 0 : j / Math.abs(j);
		
		Point coordChange = facing.getOppositeDirection().getCoordChange();
		
		if (facing.isOrthagonal() && coordChange.x == singleUnitI && coordChange.y == singleUnitJ)
			return true;
		
		if (!facing.isDiagonal())
			return false;
		
		return (Math.abs(i) == Math.abs(j) && coordChange.x == singleUnitI && coordChange.y == singleUnitJ);
	}
	
	private boolean valuesSharePositivity(int val1, int val2, boolean includeZero)
	{
		if (includeZero)
			return (val1 <= 0 && val2 <= 0) || (val1 >= 0 && val2 >= 0);
		
		return (val1 < 0 && val2 < 0) || (val1 > 0 && val2 > 0);
	}
}
