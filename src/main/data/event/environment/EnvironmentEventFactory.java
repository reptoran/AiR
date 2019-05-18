package main.data.event.environment;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import main.entity.actor.Actor;
import main.entity.save.EntityMap;
import main.presentation.Logger;

public class EnvironmentEventFactory
{
	private static Map<Integer, EnvironmentEventQueue> queueMappings = new HashMap<Integer, EnvironmentEventQueue>();
	
	private EnvironmentEventFactory() {}
	
	public static EnvironmentEvent generateNewEvent(EnvironmentEventQueue queue, EnvironmentEventType type)
	{
		return generateNewEvent(queue, type, null);
	}
	
	public static EnvironmentEvent generateNewEvent(EnvironmentEventQueue queue, EnvironmentEventType type, Actor actor)
	{
		switch (type)
		{
		case HP_REGEN:
			return new HitpointRegenEvent(actor, queue);
			
		case ACTOR_TURN:
			return new ActorTurnEvent(actor, queue);

		default:
			Logger.warn("Generating a null environment event because the type [" + type + "] is not yet defined.");
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
