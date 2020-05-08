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
	protected Actor secondaryActor = null;
	
	protected int value = 0;
	protected int secondaryValue = 0;
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
	public Actor getSecondaryActor()
	{
		return secondaryActor;
	}
	
	@Override
	public int getValue()
	{
		return value;
	}
	
	@Override
	public int getSecondaryValue()
	{
		return secondaryValue;
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
		event.secondaryActor = secondaryActor;
		event.value = value;
		event.secondaryValue = secondaryValue;
		
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
		ssb.addToken(new SaveToken(SaveTokenTag.E_VAL, String.valueOf(value)));
		ssb.addToken(new SaveToken(SaveTokenTag.E_VA2, String.valueOf(secondaryValue)));
		
		savePrimaryActor(ssb);
		saveSecondaryActor(ssb);
		
		return ssb.getSaveString();
	}
	
	private void savePrimaryActor(SaveStringBuilder ssb)
	{
		if (actor == null)
			return;
		
		String actorUid = actor.getUniqueId();
		
		if (EntityMap.getActor(actorUid) == null)
			actorUid = EntityMap.put(actorUid, actor);
		else
			actorUid = EntityMap.getSimpleKey(actorUid);
		
		ssb.addToken(new SaveToken(SaveTokenTag.E_ACT, actorUid.substring(1)));
	}
	
	private void saveSecondaryActor(SaveStringBuilder ssb)
	{
		if (secondaryActor == null)
			return;
		
		String secondaryActorUid = secondaryActor.getUniqueId();
		
		if (EntityMap.getActor(secondaryActorUid) == null)
			secondaryActorUid = EntityMap.put(secondaryActorUid, actor);
		else
			secondaryActorUid = EntityMap.getSimpleKey(secondaryActorUid);
		
		ssb.addToken(new SaveToken(SaveTokenTag.E_AC2, secondaryActorUid.substring(1)));
	}

	@Override
	public String loadFromText(String text) throws ParseException
	{
		SaveStringBuilder ssb = new SaveStringBuilder(EntityType.EVENT, text);
		
		String toRet = getContentsForTag(ssb, SaveTokenTag.E_UID);	//assumed to be defined
		
		setMember(ssb, SaveTokenTag.E_TYP);
		setMember(ssb, SaveTokenTag.E_TIC);
		setMember(ssb, SaveTokenTag.E_ACT);
		setMember(ssb, SaveTokenTag.E_AC2);
		setMember(ssb, SaveTokenTag.E_VAL);
		setMember(ssb, SaveTokenTag.E_VA2);
		
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

		case E_AC2:
			saveToken = ssb.getToken(saveTokenTag);
			referenceKey = "A" + saveToken.getContents();
			Actor loadedSecondaryActor = EntityMap.getActor(referenceKey);
			this.secondaryActor = loadedSecondaryActor;
			break;

		case E_VAL:
			saveToken = ssb.getToken(saveTokenTag);
			this.value = Integer.parseInt(saveToken.getContents());
			break;

		case E_VA2:
			saveToken = ssb.getToken(saveTokenTag);
			this.secondaryValue = Integer.parseInt(saveToken.getContents());
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
		
		if (type != event.type || ticksBeforeActing != event.ticksBeforeActing || value != event.value || secondaryValue != event.secondaryValue)
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
		
		if (secondaryActor == null)
		{
			if (event.secondaryActor != null)
				return false;
		} else if (!secondaryActor.equals(event.secondaryActor))
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
		hash = prime * hash + ((secondaryActor == null) ? 0 : secondaryActor.hashCode());
		hash = prime * hash + ((eventQueue == null) ? 0 : eventQueue.hashCode());
		hash = prime * hash + ticksBeforeActing;
		hash = prime * hash + value;
		hash = prime * hash + secondaryValue;
		
		return hash;
	}
}
