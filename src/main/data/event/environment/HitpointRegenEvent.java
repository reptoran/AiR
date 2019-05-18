package main.data.event.environment;

import main.data.ObjectIndexTranslator;
import main.data.event.InternalEvent;
import main.entity.actor.Actor;

public class HitpointRegenEvent extends RecurringEnvironmentEvent
{	
	public HitpointRegenEvent(Actor actor, EnvironmentEventQueue eventQueue)
	{
		this.actor = actor;
		this.eventQueue = eventQueue;
	}
	
	@Override
	public InternalEvent trigger()
	{
		recur(100);	//TODO: base on actor's toughness
		
		if (actor.getCurHp() >= actor.getMaxHp())
			return null;
			
		return InternalEvent.attackInternalEvent(-1, ObjectIndexTranslator.getInstance().getIndexOfActor(actor), -1, 0);
	}

	@Override
	public EnvironmentEventType getType()
	{
		return EnvironmentEventType.HP_REGEN;
	}
}
