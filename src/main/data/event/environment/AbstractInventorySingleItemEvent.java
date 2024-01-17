package main.data.event.environment;

import main.data.DataAccessor;
import main.entity.actor.Actor;
import main.entity.item.InventorySelectionKey;
import main.entity.item.Item;
import main.entity.item.ItemSource;
import main.presentation.Logger;

public abstract class AbstractInventorySingleItemEvent extends AbstractEnvironmentEvent
{
	protected ItemSource itemSource;
	protected int amount;
	protected int itemIndex;
	protected int actorIndex;
	
	protected AbstractInventorySingleItemEvent(Actor owner, InventorySelectionKey slot, int itemAmount, EnvironmentEventQueue eventQueue)
	{
		setFields(owner, slot, itemAmount, eventQueue);
	}
	
	protected AbstractInventorySingleItemEvent(Actor owner, Item inventoryItem, int itemAmount, EnvironmentEventQueue eventQueue)
	{
		ItemSource source = null;
		int index = -1;
		
		if (actor.getIndexOfMaterial(inventoryItem) != -1)
		{
			source = ItemSource.MATERIAL;
			index = actor.getIndexOfMaterial(inventoryItem);
		}
		else if (actor.getIndexOfMagicItem(inventoryItem) != -1)
		{
			source = ItemSource.MAGIC;
			index = actor.getIndexOfMagicItem(inventoryItem);
		}
		else if (actor.getIndexOfStoredItem(inventoryItem) != -1)
		{
			source = ItemSource.PACK;
			index = actor.getIndexOfStoredItem(inventoryItem);
		}
		else if (actor.getIndexOfEquippedItem(inventoryItem) != -1)
		{
			source = ItemSource.EQUIPMENT;
			index = actor.getIndexOfEquippedItem(inventoryItem);
		}
		else if (DataAccessor.getInstance().getCurrentZone().getTile(actor).getItemHere() == inventoryItem)
		{
			source = ItemSource.GROUND;
			index = 0;
		}
		else
		{
			Logger.error("Cannot locate item for environment event type [" + getType() + "]; it may not be owned by the actor.  Event may not trigger correctly.  Item: " + inventoryItem);
		}
		
		InventorySelectionKey slot = new InventorySelectionKey(source, index);
		setFields(owner, slot, itemAmount, eventQueue);
	}
	
	private void setFields(Actor owner, InventorySelectionKey slot, int itemAmount, EnvironmentEventQueue eventQueue)
	{
		this.actor = owner;
		this.actorIndex = DataAccessor.getInstance().getIndexOfActor(actor);
		this.item = actor.getItem(slot);
		this.itemSource = slot.getItemSource();
		this.itemIndex = slot.getItemIndex();
		this.amount = itemAmount;
		this.values[0] = itemSource.intValue();
		this.values[1] = itemIndex;
		this.values[2] = amount;
		this.eventQueue = eventQueue;
	}
}
