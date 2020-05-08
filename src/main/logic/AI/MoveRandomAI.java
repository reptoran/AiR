package main.logic.AI;

import java.util.List;

import main.data.event.ActorCommand;
import main.entity.actor.Actor;
import main.entity.zone.Zone;

public class MoveRandomAI extends ActorAI
{
	@Override
	public ActorCommand getNextCommand(Zone zone, Actor actor)
	{
		return getRandomLegalMoveCommand(zone, actor);
	}

	@Override
	protected List<AiType> getEnemyAiTypes()
	{
		return generateAiList();
	}
}
