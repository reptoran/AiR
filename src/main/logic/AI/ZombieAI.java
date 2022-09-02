package main.logic.AI;

import java.awt.Point;
import java.util.List;

import main.data.event.ActorCommand;
import main.entity.actor.Actor;
import main.entity.zone.Zone;
import main.logic.Direction;
import main.logic.Line;
import main.logic.RPGlib;

public class ZombieAI extends ActorAI
{
	@Override
	public ActorCommand getNextCommand(Zone zone, Actor actor)
	{
		setActorTargets(zone, actor);
		
		if (nearestEnemy == null)
			return getRandomLegalMoveCommand(zone, actor);
		
		Point origin = zone.getCoordsOfActor(actor);
		Point target = zone.getCoordsOfActor(nearestEnemy);
		Point nextMove = nextPointToApproachTarget(zone, actor, target);
		
		Direction direction = RPGlib.convertCoordChangeToDirection(nextMove.x - origin.x, nextMove.y - origin.y);
		return ActorCommand.move(direction);
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
		return generateAiList(AiType.HUMAN_CONTROLLED, AiType.REPEAT_LAST_MOVE);
	}
}
