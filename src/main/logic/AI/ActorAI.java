package main.logic.AI;

import main.entity.actor.Actor;

public abstract class ActorAI
{
	public ActorAI()
	{
		//
	}
	
	public abstract String getNextCommand(Actor theActor);	//maybe return something better than a String later
}
