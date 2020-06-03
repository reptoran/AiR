package main.data.event.environment;

import java.util.ArrayList;
import java.util.List;

import main.data.DataAccessor;
import main.data.event.InternalEvent;
import main.entity.actor.Actor;
import main.entity.item.ItemSource;

public class GiveItemEvent extends AbstractEnvironmentEvent
{
	public GiveItemEvent(Actor giver, Actor receiver, ItemSource itemSource, int itemIndex, EnvironmentEventQueue eventQueue)
	{
		this.actor = giver;
		this.secondaryActor = receiver;
		this.value = itemSource.intValue();
		this.secondaryValue = itemIndex;
		this.eventQueue = eventQueue;
	}
	
	@Override
	public List<InternalEvent> trigger()
	{
		List<InternalEvent> eventList = new ArrayList<InternalEvent>();
		
		if (actor == null || secondaryActor == null)
			return eventList;
		
		int giverActorIndex = DataAccessor.getInstance().getIndexOfActor(actor);
		int receiverActorIndex = DataAccessor.getInstance().getIndexOfActor(secondaryActor);
		
		
		ItemSource itemSource = ItemSource.fromInt(value);
		int itemIndex = secondaryValue;
		
		InternalEvent event = InternalEvent.giveItemInternalEvent(giverActorIndex, itemSource, itemIndex, receiverActorIndex);
		eventList.add(event);
		
		return eventList;
	}

	@Override
	public EnvironmentEventType getType()
	{
		return EnvironmentEventType.GIVE_ITEM;
	}
}
