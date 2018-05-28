package main.entity.actor;

public class ActorBuilder
{
	private static final int TOTAL_ATTRIBUTES = 5;
	
	private int[] attributes = new int[TOTAL_ATTRIBUTES];
	
	private Actor actor = null;
	
	private ActorBuilder()
	{
		for (int i = 0; i < TOTAL_ATTRIBUTES; i++)
		{
			this.attributes[i] = 0;
		}
	}
	
	public ActorBuilder(ActorType actorType)
	{
		this();
		actor = new Actor(actorType, "unbuilt actor", '%', 8, attributes);
	}
	
	public static ActorBuilder generateActor(ActorType actorType)
	{
		return new ActorBuilder(actorType);
	}
	
	public Actor build()
	{
		return actor;
	}
	
	public ActorBuilder setName(String name)
	{
		actor.setName(name);
		return this;
	}
	
	public ActorBuilder setIcon(char icon)
	{
		actor.setIcon(icon);
		return this;
	}
	
	public ActorBuilder setColor(int color)
	{
		actor.setColor(color);
		return this;
	}
	
	public ActorBuilder setStrength(int attributeValue)
	{
		actor.setAttribute(Actor.ATT_STR, attributeValue);
		return this;
	}
}
