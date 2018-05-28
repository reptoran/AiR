package main.logic.AI;

import main.entity.actor.Actor;
import main.logic.RPGlib;

public class MoveRandomAI extends ActorAI
{
	public String getNextCommand(Actor theActor)
	{
		String moveTo[] = {"DIRNW", "DIRN", "DIRNE", "DIRW", "DIRE", "DIRSW", "DIRS", "DIRSE"};
	    return moveTo[RPGlib.Randint(0, 7)];
	}
}
