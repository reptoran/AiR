package main.data;

import main.entity.actor.Actor;
import main.entity.zone.Zone;

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
}
