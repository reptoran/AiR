package main.data.event.environment;

import java.util.Map;

import main.entity.actor.Actor;
import main.entity.item.InventorySelectionKey;
import main.entity.item.Item;

public class ConsumeInventoryItemEvent extends AbstractInventoryMultiItemEvent
{
	public ConsumeInventoryItemEvent(Actor owner, Item inventoryItem, int amount, EnvironmentEventQueue eventQueue)
	{
		super(owner, inventoryItem, amount, eventQueue);
		Map<InventorySelectionKey, Integer> itemsToConsume = getSlotMapping();
		
		for (InventorySelectionKey key : itemsToConsume.keySet())
		{
			int quantity = itemsToConsume.get(key);
			addInventoryItemEvent(new ConsumeSingleInventoryItemEvent(owner, key, quantity, eventQueue));
		}
	}
	
	@Override
	public EnvironmentEventType getType()
	{
		return EnvironmentEventType.CONSUME_ITEM;
	}

}
