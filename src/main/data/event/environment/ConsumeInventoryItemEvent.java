package main.data.event.environment;

import java.util.ArrayList;
import java.util.List;

import main.data.DataAccessor;
import main.data.event.InternalEvent;
import main.entity.actor.Actor;
import main.entity.item.Item;
import main.entity.item.ItemSource;
import main.presentation.Logger;

public class ConsumeInventoryItemEvent extends AbstractEnvironmentEvent
{
	public ConsumeInventoryItemEvent(Actor owner, Item inventoryItem, int amount, EnvironmentEventQueue eventQueue)
	{
		this.actor = owner;
		this.item = inventoryItem;
		this.tertiaryValue = amount;
		this.eventQueue = eventQueue;
		
		if (actor.getIndexOfMaterial(inventoryItem) != -1)
		{
			value = ItemSource.MATERIAL.intValue();
			secondaryValue = actor.getIndexOfMaterial(inventoryItem);
		}
		else if (actor.getIndexOfMagicItem(inventoryItem) != -1)
		{
			value = ItemSource.MAGIC.intValue();
			secondaryValue = actor.getIndexOfMagicItem(inventoryItem);
		}
		else if (actor.getIndexOfStoredItem(inventoryItem) != -1)
		{
			value = ItemSource.PACK.intValue();
			secondaryValue = actor.getIndexOfStoredItem(inventoryItem);
		}
		else if (actor.getIndexOfEquippedItem(inventoryItem) != -1)
		{
			value = ItemSource.EQUIPMENT.intValue();
			secondaryValue = actor.getIndexOfEquippedItem(inventoryItem);
		}
		else
		{
			Logger.error("Cannot locate item to consume; it may not be owned by the actor.  Item will not be consumed.  Item: " + inventoryItem);
		}
	}
	
	@Override
	public List<InternalEvent> trigger()
	{
		List<InternalEvent> eventList = new ArrayList<InternalEvent>();
		
		if (actor == null || value == 0)	//the value is the ascii value of the item source character, so 0 would never be one
			return eventList;
		
		int actorIndex = DataAccessor.getInstance().getIndexOfActor(actor);
		
		ItemSource itemSource = ItemSource.fromInt(value);
		int itemIndex = secondaryValue;
		
		InternalEvent event = InternalEvent.deleteInventoryItemInternalEvent(actorIndex, itemSource, itemIndex, tertiaryValue);
		eventList.add(event);
		
		return eventList;
	}

	@Override
	public EnvironmentEventType getType()
	{
		return EnvironmentEventType.CONSUME_ITEM;
	}
}
