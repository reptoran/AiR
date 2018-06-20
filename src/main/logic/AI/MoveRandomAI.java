package main.logic.AI;

import java.util.List;

import main.entity.actor.Actor;
import main.entity.zone.Zone;

public class MoveRandomAI extends ActorAI
{
	@Override
	public String getNextCommand(Zone zone, Actor actor)
	{
		return getRandomLegalMove(zone, actor);
	}

	@Override
	protected List<AiType> getEnemyAiTypes()
	{
		return generateAiList();
	}
}
