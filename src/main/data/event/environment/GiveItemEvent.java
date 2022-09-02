package main.data.event.environment;

import java.util.ArrayList;
import java.util.List;

import main.data.DataAccessor;
import main.data.event.InternalEvent;
import main.entity.actor.Actor;
import main.entity.item.ItemSource;

public class GiveItemEvent extends AbstractEnvironmentEvent
{
	public GiveItemEvent(Actor giver, Actor receiver, ItemSource itemSource, int itemIndex, int quantity, EnvironmentEventQueue eventQueue)
	{
		this.actor = giver;
		this.secondaryActor = receiver;
		this.values[0] = itemSource.intValue();
		this.values[1] = itemIndex;
		this.values[2] = quantity;
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
		
		
		ItemSource itemSource = ItemSource.fromInt(values[0]);
		int itemIndex = values[1];
		int quantity = values[2];
		
		InternalEvent event = InternalEvent.giveItemInternalEvent(giverActorIndex, itemSource, itemIndex, quantity, receiverActorIndex);
		eventList.add(event);
		
		return eventList;
	}

	@Override
	public EnvironmentEventType getType()
	{
		return EnvironmentEventType.GIVE_ITEM;
	}
}
