package main.logic.AI;

import main.data.event.ActorCommand;
import main.entity.actor.Actor;
import main.entity.zone.Zone;
import main.logic.Direction;
import main.logic.AI.faction.CoalignedFactionAi;

public class FrozenCoalignedAI extends CoalignedFactionAi
{
	@Override
	public ActorCommand getNextCommand(Zone zone, Actor actor)
	{
		return ActorCommand.move(Direction.DIRNONE);
	}
}
