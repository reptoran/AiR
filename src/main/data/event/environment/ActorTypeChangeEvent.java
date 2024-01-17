package main.data.event.environment;

import java.util.ArrayList;
import java.util.List;

import main.data.DataAccessor;
import main.data.event.InternalEvent;
import main.entity.actor.Actor;
import main.entity.actor.ActorType;

public class ActorTypeChangeEvent extends AbstractEnvironmentEvent
{
	ActorType currentType = null;
	ActorType newType = null;
	
	public ActorTypeChangeEvent(Actor actor, String currentType, String newType)
	{
		this.actor = actor;
		this.currentType = ActorType.fromString(currentType);
		this.newType = ActorType.fromString(newType);
	}
	
	
	@Override
	public List<InternalEvent> trigger()
	{
		List<InternalEvent> eventList = new ArrayList<InternalEvent>();
		
		if (actor.getType() != currentType)
			return eventList;
		
		eventList.add(InternalEvent.changeActorTypeInternalEvent(DataAccessor.getInstance().getIndexOfActor(actor), newType));
		
		return eventList;
	}

	@Override
	public EnvironmentEventType getType()
	{
		return EnvironmentEventType.ACTOR_TYPE_CHANGE;
	}

}
