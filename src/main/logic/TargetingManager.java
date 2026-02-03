package main.logic;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import main.entity.actor.Actor;
import main.entity.zone.Zone;
import main.presentation.curses.Colors;

public class TargetingManager
{
	public static final int RETICLE_TARGET_COLOR = Colors.LIGHT_RED;
	public static final int RETICLE_TARGET_CRITICAL_COLOR = Colors.DARK_RED;
	public static final char RETICLE_ICON = 'X';
	
	private static TargetingManager instance = null;
	
	private SnapTargets snapTargets;
	private Point currentCoords;
	private Point maxBounds;
	private boolean onTarget;
	private boolean active;
	
	private TargetingManager()
	{
		snapTargets = new SnapTargets();
		currentCoords = new Point(-1, -1);
		maxBounds = new Point(0, 0);
		onTarget = false;
		active = false;
	}
	
	public static TargetingManager getInstance()
	{
		if (instance == null)
			instance = new TargetingManager();
		
		return instance;
	}
	
	//TODO: It might be worth remembering the last target, if it proves better for throwing items/firing missles (since the interface would go away after the shot) 
	public void activateTargeting(Actor source, Zone currentZone)
	{
		maxBounds = new Point(currentZone.getHeight() - 1, currentZone.getWidth() - 1);
		snapTargets.initialize(currentZone.getCoordsOfActor(source));
		
		for (Actor target : currentZone.getActors())
		{
			if (target == source)
				continue;
			
			AwarenessStatus awareness = new AwarenessStatus(currentZone, source, target);
			
			if (awareness.isTargetActorVisible())
				snapTargets.addTarget(currentZone.getCoordsOfActor(target));
		}
		
		snapTargets.sort();
		currentCoords = snapTargets.nextTarget();
		
		if (currentCoords == null)
			currentCoords = currentZone.getCoordsOfActor(source);
		
		if (snapTargets.isEmpty())
			onTarget = false;
		else
			onTarget = true;
		
		active = true;
	}
	
	public void deactivateTargeting()
	{
		snapTargets.reset();
		active = false;
	}
	
	public boolean isActive()
	{
		return active;
	}
	
	public Point getReticleCoords()
	{
		return currentCoords;
	}
	
	public void moveReticle(Direction direction)
	{
		Point coordChange = direction.getCoordChange();
		Point newCoords = new Point(currentCoords.x + coordChange.x, currentCoords.y + coordChange.y);
		
		if (newCoords.x < 0 || newCoords.y < 0 || newCoords.x > maxBounds.x || newCoords.y > maxBounds.y)
			return;
		
		currentCoords = newCoords;
		onTarget = snapTargets.syncTarget(currentCoords);
	}
	
	public void nextTarget()
	{
		Point nextCoords;
		
		if (onTarget)
			nextCoords = snapTargets.nextTarget();
		else
			nextCoords = snapTargets.closestTarget(currentCoords);
		
		if (nextCoords != null)
		{
			currentCoords = new Point(nextCoords.x, nextCoords.y);
			onTarget = true;
		}
	}
	
	public void lastTarget()
	{
		Point lastCoords;
		
		if (onTarget)
			lastCoords = snapTargets.lastTarget();
		else
			lastCoords = snapTargets.closestTarget(currentCoords);
		
		if (lastCoords != null)
		{
			currentCoords = new Point(lastCoords.x, lastCoords.y);
			onTarget = true;
		}
	}
	
	private class SnapTargets
	{
		private PointComparator comparator;
		private List<Point> targets;
		private Point sourceCoords;
		private int selectedIndex;
		
		public SnapTargets()
		{
			comparator = new PointComparator();
			targets = new ArrayList<Point>();
			sourceCoords = null;
			selectedIndex = -1;
		}
		
		public void initialize(Point source)
		{
			targets.clear();
			sourceCoords = new Point(source.x, source.y);
		}
		
		public void reset()
		{
			targets.clear();
			sourceCoords = null;
		}
		
		public void addTarget(Point targetCoords)
		{
			if (targetCoords != null)
				targets.add(new Point(targetCoords.x, targetCoords.y));
		}
		
		public void sort()
		{
			if (sourceCoords == null)
				return;
			
			targets.sort(comparator);
			selectedIndex = -1;
		}
		
		public boolean isEmpty()
		{
			return targets.isEmpty();
		}
		
		public boolean syncTarget(Point coords)
		{
			if (coords == null)
				return false;
			
			for (int i = 0; i < targets.size(); i++)
			{
				Point targetCoords = targets.get(i);
				
				if (coords.x == targetCoords.x && coords.y == targetCoords.y)
				{
					selectedIndex = i;
					return true;
				}
			}
			
			selectedIndex = -1;
			return false;
		}
		
		public Point nextTarget()
		{
			if (targets.isEmpty())
				return null;
			
			if (targets.size() == 1)
				return targets.getFirst();
			
			selectedIndex++;
			
			if (selectedIndex >= targets.size())
				selectedIndex = 0;
			
			return targets.get(selectedIndex);
		}
		
		public Point lastTarget()
		{
			if (targets.isEmpty())
				return null;
			
			if (targets.size() == 1)
				return targets.getFirst();
			
			selectedIndex--;
			
			if (selectedIndex < 0)
				selectedIndex = targets.size() - 1;
			
			return targets.get(selectedIndex);
		}
		
		public Point closestTarget(Point source)
		{
			if (targets.isEmpty())
				return null;
			
			if (targets.size() == 1)
				return targets.getFirst();
			
			selectedIndex = -1;
			double shortestDistance = Double.MAX_VALUE;
			
			for (int i = 0; i < targets.size(); i++)
			{
				double newDistance = RPGlib.trueDistance(source, targets.get(i));
				
				if (newDistance < shortestDistance)
				{
					shortestDistance = newDistance;
					selectedIndex = i;
				}
			}
			
			return targets.get(selectedIndex);
		}
		
		private class PointComparator implements Comparator<Point>
		{
			@Override
			public int compare(Point point1, Point point2)
			{
				double distance1 = RPGlib.trueDistance(sourceCoords, point1);
				double distance2 = RPGlib.trueDistance(sourceCoords, point2);
				
				if (distance1 < distance2)
					return -1;
				
				if (distance2 < distance1)
					return 1;
				
				return 0;
			}
			
		}
		
		@Override
		public String toString()
		{
			return targets.toString();
		}
	}
}
