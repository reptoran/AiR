package main.logic.AI;

import java.awt.Point;
import java.util.List;

import main.data.event.ActorCommand;
import main.entity.actor.Actor;
import main.entity.zone.Zone;

public class MeleeAI extends ActorAI
{
	@Override
	public ActorCommand getNextCommand(Zone zone, Actor actor)
	{
		setActorTargets(zone, actor);
		
		if (nearestEnemy == null)
			return getRandomLegalMoveCommand(zone, actor);
		
		Point target = zone.getCoordsOfActor(nearestEnemy);
		return moveTowardPoint(zone, actor, target);
	}

	@Override
	protected List<AiType> getEnemyAiTypes()
	{
		return generateAiList(AiType.HUMAN_CONTROLLED, AiType.REPEAT_LAST_MOVE);
	}
}
