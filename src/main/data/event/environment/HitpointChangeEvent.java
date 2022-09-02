package main.data.event.environment;

import java.util.ArrayList;
import java.util.List;

import main.data.DataAccessor;
import main.data.event.InternalEvent;
import main.entity.actor.Actor;

public class HitpointChangeEvent extends AbstractEnvironmentEvent
{
	public HitpointChangeEvent(Actor actor, int value, EnvironmentEventQueue eventQueue)
	{
		this.actor = actor;
		this.values[0] = value;
		this.eventQueue = eventQueue;
	}
	
	@Override
	public List<InternalEvent> trigger()
	{
		List<InternalEvent> eventList = new ArrayList<InternalEvent>();
		
		if (values[0] == 0)
			return eventList;
		
		
		int curHp = actor.getCurHp();
		int correctedAmount = values[0];
		
		if (curHp + values[0] > actor.getMaxHp())
			correctedAmount = actor.getMaxHp() - curHp;
		//no check for below zero because it's fun to see how badly you were smacked by an attack, so negative HP is okay
		
		InternalEvent event = InternalEvent.changeHpInternalEvent(DataAccessor.getInstance().getIndexOfActor(actor), correctedAmount);
		eventList.add(event);
		
		if (curHp + values[0] <= 0)
			eventList.add((InternalEvent.deathInternalEvent(DataAccessor.getInstance().getIndexOfActor(actor))));
		
		return eventList;
	}

	@Override
	public EnvironmentEventType getType()
	{
		return EnvironmentEventType.HP_CHANGE;
	}
}
