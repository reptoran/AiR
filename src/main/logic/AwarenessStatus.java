package main.logic;

import java.awt.Point;
import java.util.List;

import main.entity.actor.Actor;
import main.entity.actor.SkillRank;
import main.entity.actor.SkillType;
import main.entity.zone.Zone;

public class AwarenessStatus
{
	private Actor source = null;
	private Actor target = null;
	private Point targetCoords = null;
	private boolean targetCoordsInLos = true;
	private boolean targetActorVisible = true;		//This specifically refers to invisibility effects.  A tile might be visible, but its actor might be invisible.
	private boolean targetActorHeard = true;
	
	public AwarenessStatus(Zone zone, Actor sourceActor, Actor targetActor)
	{
		Point targetCoordinates = zone.getCoordsOfActor(targetActor);
		buildAwarenessStatus(zone, sourceActor, targetActor, targetCoordinates);
	}
	
	public AwarenessStatus(Zone zone, Actor sourceActor, Point targetCoordinates)
	{
		Actor targetActor = zone.getActorAtCoords(targetCoordinates);
		buildAwarenessStatus(zone, sourceActor, targetActor, targetCoordinates);
	}
	
	public void buildAwarenessStatus(Zone zone, Actor sourceActor, Actor targetActor, Point targetCoordinates)
	{
		if (sourceActor == null || targetCoordinates == null)
			return;
		
		this.source = sourceActor;
		this.targetCoords = new Point(targetCoordinates.x, targetCoordinates.y);
		this.target = targetActor;
		
		Point sourceCoords = zone.getCoordsOfActor(source);
		
		if (sourceCoords == null)
			return;
		
		ActorSightUtil actorSightUtil = ActorSightUtil.getInstance();
		SkillRank sourceAwareness = SkillRank.rankName(source.getSkillLevel(SkillType.AWARENESS));
		
		int sightRadius = actorSightUtil.getSightRadius(sourceAwareness);
		
		Line visionRay = new Line(sourceCoords.x, sourceCoords.y, targetCoords.x, targetCoords.y);
		List<Point> rayPoints = visionRay.getPoints();
		
		if (rayPoints.size() < 2)	//if the only point is the origin, leave everything true, since everything there can be seen/heard (and the targetActor is the source)
			return;
		
		boolean previousPointObstructed = false;
		int pointsVisited = 0;
		int noiseDistance = 0;
		
		for (Point point : rayPoints)
		{
			//everything is guaranteed out of sight/earshot once you're outside the range
			if (pointsVisited > sightRadius)
			{
				targetCoordsInLos = false;
				targetActorVisible = false;
				targetActorHeard = false;
				return;
			}
			
			Point relativePoint = new Point(point.x - sourceCoords.x, point.y - sourceCoords.y);
			
			//If the obstruction is the last point of the line, then LOS still exists.  By getting here, we know it wasn't, though we'll still check for hearing.
			//Also, check to see if the source Actor's facing keeps the target out of sight.  THere's also a check for whether the coords have already been
			//	calculated to be out of the FOV, so we don't have to keep calculating that.
			if (previousPointObstructed || !targetCoordsInLos || actorSightUtil.isOutsidePerceptionFov(sourceAwareness, relativePoint, source.getFacing()))
			{
				targetCoordsInLos = false;
				targetActorVisible = false;
			}
			
			//Once the tile is out of sight, we can leave right away if there's no actor there.  Otherise, continue calculating hearing.
			if (!targetCoordsInLos && target == null)
				break;
			
			//Walls make things harder to hear, too.
			if (zone.getTile(point.x, point.y).obstructsSight())
			{
				previousPointObstructed = true;
				noiseDistance++;
			}
			
			pointsVisited++;
			noiseDistance++;
		}
		
		if (target == null)
		{
			targetActorVisible = false;
			targetActorHeard = false;
			return;
		}
		
		SkillRank targetStealth = SkillRank.rankName(target.getSkillLevel(SkillType.STEALTH));
		int totalNoise = target.getNoiseMadeLastTurn() + actorSightUtil.getStealthNoiseModifier(targetStealth);
		int hearingRange = noiseDistance - actorSightUtil.getHearingRadius(sourceAwareness);
		
		if (totalNoise < hearingRange)
			targetActorHeard = false;
	}

	public Actor getSource()
	{
		return source;
	}

	public Actor getTarget()
	{
		return target;
	}

	public Point getTargetCoords()
	{
		return targetCoords;
	}

	public boolean isTargetCoordsInLos()
	{
		return targetCoordsInLos;
	}

	public boolean isTargetActorVisible()
	{
		return targetActorVisible;
	}

	public boolean isTargetActorHeard()
	{
		return targetActorHeard;
	}
}
