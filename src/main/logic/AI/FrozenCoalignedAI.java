package main.logic.AI;

import java.util.ArrayList;
import java.util.List;

import main.data.event.ActorCommand;
import main.entity.actor.Actor;
import main.entity.zone.Zone;
import main.logic.Direction;

public class FrozenCoalignedAI extends ActorAI
{
	@Override
	public ActorCommand getNextCommand(Zone zone, Actor actor)
	{
		return ActorCommand.move(Direction.DIRNONE);
	}

	@Override
	protected List<AiType> getEnemyAiTypes()
	{
		return new ArrayList<AiType>();
	}
}
