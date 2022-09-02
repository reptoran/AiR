package main.data.event.environment;

import main.data.DataAccessor;
import main.entity.actor.Actor;
import main.entity.item.Item;
import main.entity.item.ItemSource;
import main.presentation.Logger;

public abstract class AbstractInventoryItemEvent extends AbstractEnvironmentEvent
{
	protected ItemSource itemSource;
	protected int amount;
	protected int itemIndex;
	protected int actorIndex;
	
	protected AbstractInventoryItemEvent(Actor owner, Item inventoryItem, int itemAmount, EnvironmentEventQueue eventQueue)
	{
		this.actor = owner;
		this.item = inventoryItem;
		this.values[2] = itemAmount;
		this.eventQueue = eventQueue;
		
		if (actor.getIndexOfMaterial(inventoryItem) != -1)
		{
			values[0] = ItemSource.MATERIAL.intValue();
			values[1] = actor.getIndexOfMaterial(inventoryItem);
		}
		else if (actor.getIndexOfMagicItem(inventoryItem) != -1)
		{
			values[0] = ItemSource.MAGIC.intValue();
			values[1] = actor.getIndexOfMagicItem(inventoryItem);
		}
		else if (actor.getIndexOfStoredItem(inventoryItem) != -1)
		{
			values[0] = ItemSource.PACK.intValue();
			values[1] = actor.getIndexOfStoredItem(inventoryItem);
		}
		else if (actor.getIndexOfEquippedItem(inventoryItem) != -1)
		{
			values[0] = ItemSource.EQUIPMENT.intValue();
			values[1] = actor.getIndexOfEquippedItem(inventoryItem);
		}
		else if (DataAccessor.getInstance().getCurrentZone().getTile(actor).getItemHere() == inventoryItem)
		{
			values[0] = ItemSource.GROUND.intValue();
			values[1] = 0;
		}
		else
		{
			Logger.error("Cannot locate item for environment event type [" + getType() + "]; it may not be owned by the actor.  Event may not trigger correctly.  Item: " + inventoryItem);
		}
		
		itemSource = ItemSource.fromInt(values[0]);
		amount = itemAmount;
		itemIndex = values[1];
		actorIndex = DataAccessor.getInstance().getIndexOfActor(actor);
	}
}
