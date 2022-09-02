package main.data.event.environment;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import main.data.DataAccessor;
import main.data.event.InternalEvent;
import main.entity.actor.Actor;
import main.entity.item.ItemFactory;
import main.entity.item.ItemType;

public class CreateItemEvent extends AbstractEnvironmentEvent
{
	public CreateItemEvent(Actor receiver, ItemType itemType, int quantity, EnvironmentEventQueue eventQueue)
	{
		this.actor = receiver;
		this.item = ItemFactory.generateNewItem(itemType);
		this.values[0] = quantity;
		this.eventQueue = eventQueue;
	}
	
	public CreateItemEvent(Point targetCoords, ItemType itemType, int quantity, EnvironmentEventQueue eventQueue)
	{
		this.item = ItemFactory.generateNewItem(itemType);
		this.values[0] = quantity;
		this.values[1] = targetCoords.x;
		this.values[2] = targetCoords.y;
		this.eventQueue = eventQueue;
	}
	
	@Override
	public List<InternalEvent> trigger()
	{
		List<InternalEvent> eventList = new ArrayList<InternalEvent>();
		
		if (actor != null)
		{
			Point targetCoords = DataAccessor.getInstance().getCurrentZone().getCoordsOfActor(actor);
			values[1] = targetCoords.x;
			values[2] = targetCoords.y;
		}
		
		if (actor != null && actor.hasSpaceForItem(item))
		{
			int actorIndex = DataAccessor.getInstance().getIndexOfActor(actor);
			eventList.add(InternalEvent.createItemForActorInternalEvent(actorIndex, item.getType(), values[0]));
		}
		else
		{
			eventList.add(InternalEvent.createItemOnGroundInternalEvent(item.getType(), new Point(values[1], values[2]), values[0]));
		}
		
		return eventList;
	}

	@Override
	public EnvironmentEventType getType()
	{
		return EnvironmentEventType.CREATE_ITEM;
	}

}
