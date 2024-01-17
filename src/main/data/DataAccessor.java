package main.data;

import main.data.event.InternalEvent;
import main.entity.actor.Actor;
import main.entity.zone.Zone;
import main.logic.AI.ActorAI;
import main.logic.AI.AiType;

public class DataAccessor
{
	private static DataAccessor instance = null;
	private Data data = null;
	
	private DataAccessor() {}
	
	public static DataAccessor getInstance()
	{
		if (instance == null)
			instance = new DataAccessor();
		
		return instance;
	}
	
	public void setData(Data data)
	{
		this.data = data;
	}
	
	public ActorAI getActorAi(AiType aiType)
	{
		return data.getAI(aiType);
	}
	
	public Actor getActorFromIndex(int actorIndex)
	{
		return data.getActor(actorIndex);
	}
	
	public int getIndexOfActor(Actor actor)
	{
		return data.getActorIndex(actor);
	}
	
	public Zone getCurrentZone()
	{
		return data.getCurrentZone();
	}
	
	public DataSaveUtils getZoneCacheUtility()
	{
		return data.getZoneCacheUtility();
	}
	
	public void sendInternalEvent(InternalEvent event)
	{
		data.receiveInternalEvent(event);
	}
}
