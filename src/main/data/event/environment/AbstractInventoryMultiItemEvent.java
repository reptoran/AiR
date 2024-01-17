package main.data.event.environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.data.event.InternalEvent;
import main.entity.actor.Actor;
import main.entity.item.InventorySelectionKey;
import main.entity.item.Item;

public abstract class AbstractInventoryMultiItemEvent extends AbstractEnvironmentEvent
{
	private List<AbstractInventorySingleItemEvent> events = new ArrayList<AbstractInventorySingleItemEvent>();
	private Map<InventorySelectionKey, Integer> slotMapping = new HashMap<InventorySelectionKey, Integer>();
	
	protected AbstractInventoryMultiItemEvent(Actor owner, Item inventoryItem, int itemAmount, EnvironmentEventQueue eventQueue)
	{
		this.actor = owner;
		this.item = inventoryItem;
		this.eventQueue = eventQueue;
		
		slotMapping = actor.getSlotsOfItemsToMeetQuantityRequirement(inventoryItem.getType(), itemAmount);
	}
	
	protected void addInventoryItemEvent(AbstractInventorySingleItemEvent event)
	{
		events.add(event);
	}
	
	@Override
	public List<InternalEvent> trigger()
	{
		List<InternalEvent> internalEvents = new ArrayList<InternalEvent>();
		
		for (AbstractInventorySingleItemEvent event : events)
		{
			internalEvents.addAll(event.trigger());
		}
		
		return internalEvents;
	}
	
	protected Map<InventorySelectionKey, Integer> getSlotMapping()
	{
		return slotMapping;
	}
}
