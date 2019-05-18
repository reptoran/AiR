package main.data.event.environment;

import java.text.ParseException;

import main.entity.EntityType;
import main.entity.SaveableEntity;
import main.entity.actor.Actor;
import main.entity.save.EntityMap;
import main.entity.save.SaveStringBuilder;
import main.entity.save.SaveToken;
import main.entity.save.SaveTokenTag;

public abstract class AbstractEnvironmentEvent extends SaveableEntity implements EnvironmentEvent 
{
	protected EnvironmentEventQueue eventQueue;
	protected int ticksBeforeActing = 0;
	protected EnvironmentEventType type;		//needed for loading from a file
	
	protected Actor actor = null;
	//TODO: tiles, features, items, etc. as events are created that act on them; remember to also add getters
	
	@Override
	public void setEventQueue(EnvironmentEventQueue eventQueue)
	{
		this.eventQueue = eventQueue;
	}
	
	@Override
	public EnvironmentEventQueue getEventQueue()
	{
		return eventQueue;
	}
	
	@Override
	public Actor getActor()
	{
		return actor;
	}

	@Override
	public void reduceTicksLeftBeforeActing(int amount)
	{
		ticksBeforeActing -= amount;
	}

	@Override
	public void increaseTicksLeftBeforeActing(int amount)
	{
		ticksBeforeActing += amount;
	}

	@Override
	public int getTicksLeftBeforeActing()
	{
		return ticksBeforeActing;
	}
	
	@Override
	public SaveableEnvironmentEvent asSaveableEvent()
	{
		SaveableEnvironmentEvent event = new SaveableEnvironmentEvent();
		
		event.eventQueue = eventQueue;
		event.type = getType();
		event.ticksBeforeActing = ticksBeforeActing;
		event.actor = actor;
		
		return event;
	}
	
	@Override
	public String saveAsText()
	{
		SaveStringBuilder ssb = new SaveStringBuilder(EntityType.EVENT);
		
		String eventUid = getUniqueId();
		
		if (EntityMap.getEvent(eventUid) == null)
			eventUid = EntityMap.put(eventUid, this);
		else
			eventUid = EntityMap.getSimpleKey(eventUid);
		
		//all events should be unique, so save everything every time
		ssb.addToken(new SaveToken(SaveTokenTag.E_UID, eventUid));
		ssb.addToken(new SaveToken(SaveTokenTag.E_TYP, getType().toString()));
		ssb.addToken(new SaveToken(SaveTokenTag.E_TIC, String.valueOf(ticksBeforeActing)));
		ssb.addToken(new SaveToken(SaveTokenTag.E_QUE, String.valueOf(eventQueue.hashCode())));
		
		String actorUid = actor.getUniqueId();
		
		if (EntityMap.getActor(actorUid) == null)
			actorUid = EntityMap.put(actorUid, actor);
		else
			actorUid = EntityMap.getSimpleKey(actorUid);
		
		ssb.addToken(new SaveToken(SaveTokenTag.E_ACT, actorUid.substring(1)));
		
		return ssb.getSaveString();
	}
	
	@Override
	public String loadFromText(String text) throws ParseException
	{
		SaveStringBuilder ssb = new SaveStringBuilder(EntityType.EVENT, text);
		
		String toRet = getContentsForTag(ssb, SaveTokenTag.E_UID);	//assumed to be defined
		
		setMember(ssb, SaveTokenTag.E_TYP);
		setMember(ssb, SaveTokenTag.E_TIC);
		setMember(ssb, SaveTokenTag.E_ACT);
		
		return toRet;
	}
	
	@Override
	protected void setMember(SaveStringBuilder ssb, SaveTokenTag saveTokenTag)
	{
		String contents = getContentsForTag(ssb, saveTokenTag);
		SaveToken saveToken = null;
		String referenceKey = "";
		
		if (contents.equals(""))
			return;

		switch (saveTokenTag)
		{	
		case E_TYP:
			saveToken = ssb.getToken(saveTokenTag);
			String typeString = saveToken.getContents();
			this.type = EnvironmentEventType.valueOf(typeString);
			break;

		case E_TIC:
			saveToken = ssb.getToken(saveTokenTag);
			this.ticksBeforeActing = Integer.parseInt(saveToken.getContents());
			break;

		case E_ACT:
			saveToken = ssb.getToken(saveTokenTag);
			referenceKey = "A" + saveToken.getContents();
			Actor loadedActor = EntityMap.getActor(referenceKey);
			this.actor = loadedActor;
			break;
			
		default:
			throw new IllegalArgumentException("EnvironmentEvent - Unhandled token: " + saveTokenTag.toString());
		}

		return;
	}
	
	@Override
	public String getUniqueId()
	{
		return EntityType.EVENT.toString() + String.valueOf(Math.abs(hashCode()));
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		AbstractEnvironmentEvent event = (AbstractEnvironmentEvent) obj;
		
		if (type != event.type || ticksBeforeActing != event.ticksBeforeActing)
			return false;
		
		if (eventQueue == null)
		{
			if (event.eventQueue != null)
				return false;
		} else if (eventQueue.hashCode() != event.eventQueue.hashCode())	//checking hashes because otherwise there would be circular equals checking
			return false;
		
		if (actor == null)
		{
			if (event.actor != null)
				return false;
		} else if (!actor.equals(event.actor))
			return false;
		
		return true;
	}

	@Override
	public int hashCode()
	{
		final int prime = 271;
		int hash = 1;

		hash = prime * hash + ((type == null) ? 0 : type.toString().hashCode());
		hash = prime * hash + ((actor == null) ? 0 : actor.hashCode());
		hash = prime * hash + ((eventQueue == null) ? 0 : eventQueue.hashCode());
		hash = prime * hash + ticksBeforeActing;
		
		return hash;
	}
}
