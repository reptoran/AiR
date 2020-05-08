package main.data.event.environment;

import java.util.ArrayList;
import java.util.List;

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
	public List<InternalEvent> trigger()
	{
		recur(100);	//TODO: base on actor's toughness
		
		List<InternalEvent> eventList = new ArrayList<InternalEvent>();
		
		if (actor.getCurHp() >= actor.getMaxHp())
			return eventList;
			
		InternalEvent event = InternalEvent.attackInternalEvent(-1, ObjectIndexTranslator.getInstance().getIndexOfActor(actor), -1, 0);
		eventList.add(event);
		return eventList;
	}

	@Override
	public EnvironmentEventType getType()
	{
		return EnvironmentEventType.HP_REGEN;
	}
}
