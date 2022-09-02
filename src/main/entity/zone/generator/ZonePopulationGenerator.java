package main.entity.zone.generator;

import java.util.ArrayList;
import java.util.List;

import main.entity.actor.Actor;
import main.entity.actor.ActorFactory;
import main.entity.actor.ActorType;
import main.logic.RPGlib;
import main.presentation.Logger;

public class ZonePopulationGenerator
{
	private static List<List<ActorType>> actorsForLevels = null;
	
	public static Actor generateMonster(int level)
	{
		if (actorsForLevels == null)
			populateActorsForLevels();
		
		List<ActorType> actorsForCurrentLevel = actorsForLevels.get(level);
		
		int totalPossibleActorsToGenerate = actorsForCurrentLevel.size();
		
		if (totalPossibleActorsToGenerate == 0)
			return null;
		
		int indexOfActorToGenerate = RPGlib.randInt(0, totalPossibleActorsToGenerate - 1);
		
		return ActorFactory.generateNewActor(actorsForCurrentLevel.get(indexOfActorToGenerate));
	}

	private static void populateActorsForLevels()
	{
		actorsForLevels = new ArrayList<List<ActorType>>();
		
		for (int i = 0; i <= 100; i++)
		{
			actorsForLevels.add(new ArrayList<ActorType>());
		}
		
		for (ActorType actorType : ActorType.values())
		{
			for (int i = actorType.getMinDepth(); i <= actorType.getMaxDepth(); i++)
			{
				try
				{
					actorsForLevels.get(i).add(actorType);
				} catch (ArrayIndexOutOfBoundsException aioobe) 
				{
					Logger.debug("Actor type " + actorType + " could not be included to be generated on level " + i + ".");
					break;
				}
			}
		}
	}
}
