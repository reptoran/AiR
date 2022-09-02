package main.data.event.environment;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import main.entity.actor.Actor;
import main.entity.item.Item;
import main.entity.item.ItemSource;
import main.entity.save.EntityMap;
import main.presentation.Logger;

public class EnvironmentEventFactory
{
	private static Map<Integer, EnvironmentEventQueue> queueMappings = new HashMap<Integer, EnvironmentEventQueue>();
	
	private EnvironmentEventFactory() {}
	
	private static EnvironmentEvent generateNewEvent(EnvironmentEventQueue queue, EnvironmentEventType type, Actor actor)
	{
		return generateNewEvent(queue, type, actor, null, null, 0, 0, 0);
	}
	
	private static EnvironmentEvent generateNewEvent(EnvironmentEventQueue queue, EnvironmentEventType type, Actor actor, Actor secondaryActor, Item item, int value, int secondaryValue, int tertiaryValue)
	{
		switch (type)
		{
		case HP_REGEN:
			return new HitpointRegenEvent(actor, queue);
			
		case HP_CHANGE:
			return new HitpointChangeEvent(actor, value, queue);
			
		case ACTOR_TURN:
			return new ActorTurnEvent(actor, queue);
			
		case GIVE_ITEM:
			return new GiveItemEvent(actor, secondaryActor, ItemSource.fromInt(value), secondaryValue, tertiaryValue, queue);
		
		case CONSUME_ITEM:
			return new ConsumeInventoryItemEvent(actor, item, value, queue);
			
		default:
			Logger.warn("Generating a null environment event because behavior for the type [" + type + "] is not yet defined.");
			return null;
		}
	}
	
	public static EnvironmentEvent loadAndMapEventFromSaveString(String saveString)
	{
		EnvironmentEvent event = null;
		
		try
		{
			SaveableEnvironmentEvent saveableEvent = new SaveableEnvironmentEvent();
			String key = saveableEvent.loadFromText(saveString);
			EnvironmentEventQueue queue = getQueue(saveableEvent.getQueueHash());
			event = generateNewEvent(queue, saveableEvent.getType(), saveableEvent.getActor());
			EntityMap.put(key, event);
		} catch (ParseException e)
		{
			Logger.warn("EnvironmentEventFactory - " + e.getMessage());
		}
		
		return event;
	}
	
	private static EnvironmentEventQueue getQueue(int queueHash)
	{
		if (queueHash == -1)
			throw new IllegalArgumentException("Queue hash of -1 received, meaning hash was not loaded properly from event file.");
		
		Logger.debug("EnvironmentEventFactory - getting queue for hash " + queueHash);
		if (queueMappings.containsKey(queueHash))
			return queueMappings.get(queueHash);
		
		EnvironmentEventQueue queue = new EnvironmentEventQueue();
		queueMappings.put(queueHash, queue);
		return queue;
	}
}
