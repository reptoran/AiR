package main.data.event.environment;

import java.util.ArrayList;
import java.util.List;

import main.data.event.Queue;
import main.entity.Queueable;
import main.entity.actor.Actor;
import main.entity.actor.ActorTraitType;

public class EnvironmentEventQueue extends Queue {
	private static int instatiatedQueues = 0;
	private final int queueIndex;
	
	public EnvironmentEventQueue()
	{
		queueIndex = instatiatedQueues++;
	}
	
	public EnvironmentEvent getNextEvent() {
		return (EnvironmentEvent) getNextElement();
	}
	
	public EnvironmentEvent popNextEvent() {
		EnvironmentEvent nextEvent = getNextEvent();
		elements.remove(0);
		return nextEvent;
	}
	
	public boolean nextEventIsHumanTurn() {
		EnvironmentEvent event = getNextEvent();
		if (event.getType() != EnvironmentEventType.ACTOR_TURN)
			return false;
		
		ActorTurnEvent turnEvent = (ActorTurnEvent) event;
		return turnEvent.isHuman();
	}
	
	public void add(Actor actor)
	{
		super.add(new ActorTurnEvent(actor, this));
		
		if (actor.hasTrait(ActorTraitType.HP_REGEN))
			super.add(new HitpointRegenEvent(actor, this));
	}
	
	public void remove(Actor actor)
	{
		//remove all events associate with this actor (turn events, hp regeneration events, etc.)
		for (Queueable event : elements)
		{
			AbstractEnvironmentEvent eventWithActor = (AbstractEnvironmentEvent) event;
			if (eventWithActor.getActor() == actor)
			{
				elements.remove(event);
				return;
			}
		}
	}
	
	public List<EnvironmentEvent> getQueueContents()
	{
		List<EnvironmentEvent> eventList = new ArrayList<EnvironmentEvent>();
		
		for (Queueable element : elements)
		{
			eventList.add((EnvironmentEvent) element);
		}
		
		return eventList;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EnvironmentEventQueue other = (EnvironmentEventQueue) obj;
		
		return (queueIndex == other.queueIndex);
	}
	
	@Override
	public int hashCode()
	{
		return queueIndex;
	}
}