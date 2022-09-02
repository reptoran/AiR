package main.data.event.environment;

import java.util.ArrayList;
import java.util.List;

import main.data.event.InternalEvent;
import main.entity.actor.Actor;
import main.entity.item.Item;

public class ConsumeInventoryItemEvent extends AbstractInventoryItemEvent
{
	public ConsumeInventoryItemEvent(Actor owner, Item inventoryItem, int amount, EnvironmentEventQueue eventQueue)
	{
		super(owner, inventoryItem, amount, eventQueue);
	}
	
	@Override
	public List<InternalEvent> trigger()
	{
		List<InternalEvent> eventList = new ArrayList<InternalEvent>();
		
		InternalEvent event = InternalEvent.deleteInventoryItemInternalEvent(actorIndex, itemSource, itemIndex, amount);
		eventList.add(event);
		
		return eventList;
	}

	@Override
	public EnvironmentEventType getType()
	{
		return EnvironmentEventType.CONSUME_ITEM;
	}
}
