package main.logic.AI;

import main.data.event.ActorCommand;
import main.entity.actor.Actor;
import main.entity.zone.Zone;
import main.logic.AI.faction.NoneFactionAi;

public class MoveRandomAI extends NoneFactionAi
{
	@Override
	public ActorCommand getNextCommand(Zone zone, Actor actor)
	{
		return getRandomLegalMoveCommand(zone, actor);
	}
}
