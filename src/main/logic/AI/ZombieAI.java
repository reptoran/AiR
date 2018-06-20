package main.logic.AI;

import java.awt.Point;
import java.util.List;

import main.entity.actor.Actor;
import main.entity.zone.Zone;
import main.logic.Line;
import main.logic.RPGlib;

public class ZombieAI extends ActorAI
{
	@Override
	public String getNextCommand(Zone zone, Actor actor)
	{
		setActorTargets(zone, actor);
		
		if (nearestEnemy == null)
			return getRandomLegalMove(zone, actor);
		
		Point origin = zone.getCoordsOfActor(actor);
		Point target = zone.getCoordsOfActor(nearestEnemy);
		Point nextMove = nextPointToApproachTarget(zone, actor, target);
		
		return RPGlib.convertCoordChangeToDirection(nextMove.x - origin.x, nextMove.y - origin.y);
	}
	
	@Override
	protected Point nextPointToApproachTarget(Zone zone, Actor actor, Point target)
	{
		Point origin = zone.getCoordsOfActor(actor);
		Line line = new Line(origin.x, origin.y, target.x, target.y);
		
		if (line.getLength() > 1)
			return line.getPoint(1);
				
		return origin;
	}

	@Override
	protected List<AiType> getEnemyAiTypes()
	{
		return generateAiList(AiType.HUMAN_CONTROLLED);
	}
}
