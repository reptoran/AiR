package main.data.event.environment;

import java.util.ArrayList;
import java.util.List;

import main.data.event.InternalEvent;
import main.entity.actor.Actor;
import main.entity.item.InventorySelectionKey;

public class ConsumeSingleInventoryItemEvent extends AbstractInventorySingleItemEvent
{
	public ConsumeSingleInventoryItemEvent(Actor owner, InventorySelectionKey slot, int itemAmount, EnvironmentEventQueue eventQueue)
	{
		super(owner, slot, itemAmount, eventQueue);
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
		return EnvironmentEventType.CONSUME_SINGLE_ITEM;
	}
}
