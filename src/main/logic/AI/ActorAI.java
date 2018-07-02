package main.logic.AI;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import main.entity.actor.Actor;
import main.entity.zone.Zone;
import main.logic.ActorSightUtil;
import main.logic.Engine;
import main.logic.RPGlib;
import main.logic.pathfinding.Pathfinder;

public abstract class ActorAI
{
	protected List<Actor> visibleActors = new ArrayList<Actor>();
	protected Actor nearestActor = null;
	protected Actor nearestEnemy = null;
	
	public abstract String getNextCommand(Zone zone, Actor actor);	//maybe return something better than a String later
	protected abstract List<AiType> getEnemyAiTypes();

	//TODO: eventually remember the last location of the last target, so they can chase around corners, etc.
	protected void setActorTargets(Zone zone, Actor actor)
	{
		Point origin = zone.getCoordsOfActor(actor);
		int distanceOfNearestActor = 999;
		int distanceOfNearestEnemy = 999;
		
		visibleActors.clear();
		nearestActor = null;
		nearestEnemy = null;
		
		List<Actor> zoneActors = zone.getActors();
		
		for (Actor actorToCheck : zoneActors)
		{
			Point target = zone.getCoordsOfActor(actorToCheck);
			if (ActorSightUtil.losExists(zone, origin, target, Engine.ACTOR_SIGHT_RADIUS))
			{
				visibleActors.add(actorToCheck);
				
				int distanceToActor = RPGlib.distance(origin.x, origin.y, target.x, target.y);
				
				if (distanceToActor < distanceOfNearestActor)
				{
					nearestActor = actorToCheck;
					distanceOfNearestActor = distanceToActor;
				}
				
				if (getEnemyAiTypes().contains(actorToCheck.getAI()) && distanceToActor < distanceOfNearestEnemy)
				{
					nearestEnemy = actorToCheck;
					distanceOfNearestEnemy = distanceToActor;
				}
			}
		}
	}
	
	protected Point nextPointToApproachTarget(Zone zone, Actor actor, Point target)
	{
		Point origin = zone.getCoordsOfActor(actor);
		List<Point> line = Pathfinder.findPath(zone, origin, target);
		
		if (line.isEmpty())
			return origin;
				
		return line.get(0);
	}
	
	protected String getRandomLegalMove(Zone zone, Actor actor)
	{
		//TODO: check for walls
		String moveTo[] = {"DIRNW", "DIRN", "DIRNE", "DIRW", "DIRE", "DIRSW", "DIRS", "DIRSE"};
	    return moveTo[RPGlib.randInt(0, 7)];
	}
	
	protected List<AiType> generateAiList(AiType... types)
	{
		List<AiType> aiList = new ArrayList<AiType>();
		
		for (AiType ai : types)
			aiList.add(ai);
		
		return aiList;
	}
}
