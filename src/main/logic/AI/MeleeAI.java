package main.logic.AI;

import java.awt.Point;
import java.util.List;

import main.entity.actor.Actor;
import main.entity.zone.Zone;
import main.logic.RPGlib;

public class MeleeAI extends ActorAI
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
	protected List<AiType> getEnemyAiTypes()
	{
		return generateAiList(AiType.HUMAN_CONTROLLED);
	}
}
