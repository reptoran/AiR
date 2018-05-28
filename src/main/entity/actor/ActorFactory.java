package main.entity.actor;

import java.text.ParseException;

import main.entity.save.EntityMap;
import main.presentation.Logger;


public class ActorFactory
{
	private ActorFactory() {}
	
	public static Actor generateNewActor(ActorType actorType)
	{
		//TODO: give the creatures different attributes
		
		switch (actorType)
		{
			case HUMAN:
				return ActorBuilder.generateActor(ActorType.HUMAN).setName("human").setIcon('H').setColor(12).build();
			case RAT:
				return ActorBuilder.generateActor(ActorType.RAT).setName("rat").setIcon('r').setColor(6).build();
			case RATTLESNAKE:
				return ActorBuilder.generateActor(ActorType.RATTLESNAKE).setName("rattlesnake").setIcon('s').setColor(6).build();
			case SCORPION:
				return ActorBuilder.generateActor(ActorType.SCORPION).setName("scorpion").setIcon('s').setColor(8).build();
			case WOLF:
				return ActorBuilder.generateActor(ActorType.WOLF).setName("wolf").setIcon('w').setColor(7).build();
			case BEAR:
				return ActorBuilder.generateActor(ActorType.BEAR).setName("bear").setIcon('B').setColor(8).build();
			case FOX:
				return ActorBuilder.generateActor(ActorType.FOX).setName("fox").setIcon('f').setColor(12).build();
			case BANDIT:
				return ActorBuilder.generateActor(ActorType.BANDIT).setName("bandit").setIcon('H').setColor(3).build();
			case NO_TYPE:
				return ActorBuilder.generateActor(ActorType.NO_TYPE).setName("generic actor").setIcon('0').setColor(7).build();
			default:
				Logger.warn("Generating an actor for a type not yet defined.");
				return new ActorBuilder(ActorType.NO_TYPE).build();
			
		}
	}
	
	public static Actor loadAndMapActorFromSaveString(String saveString)
	{
		Actor actor = null;
		
		try
		{
			actor = new Actor();
			String key = actor.loadFromText(saveString);
			EntityMap.put(key, actor);
		} catch (ParseException e)
		{
			System.out.println("ActorFactory - " + e.getMessage());
		}
		
		return actor;
	}
}
