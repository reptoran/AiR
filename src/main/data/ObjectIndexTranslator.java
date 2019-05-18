package main.data;

import main.entity.actor.Actor;

public class ObjectIndexTranslator
{
	private static ObjectIndexTranslator instance = null;
	private Data data = null;
	
	private ObjectIndexTranslator() {}
	
	public static ObjectIndexTranslator getInstance()
	{
		if (instance == null)
			instance = new ObjectIndexTranslator();
		
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
}
