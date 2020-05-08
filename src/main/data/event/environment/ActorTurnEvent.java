package main.data.event.environment;

import java.util.List;

import main.data.event.InternalEvent;
import main.entity.actor.Actor;
import main.logic.AI.AiType;

public class ActorTurnEvent extends RecurringEnvironmentEvent
{
	public ActorTurnEvent(Actor actor, EnvironmentEventQueue eventQueue)
	{
		this.actor = actor;
		this.eventQueue = eventQueue;
	}
	
	public boolean isHuman()
	{
		return (actor.getAI() == AiType.HUMAN_CONTROLLED);
	}
	
	@Override
	public List<InternalEvent> trigger()
	{
		throw new UnsupportedOperationException("This is an ActorTurnEvent with no defined logic.  Call getActor() instead to access the actor.");
	}

	@Override
	public EnvironmentEventType getType()
	{
		return EnvironmentEventType.ACTOR_TURN;
	}
}
