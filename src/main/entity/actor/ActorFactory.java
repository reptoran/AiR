package main.entity.actor;

import java.text.ParseException;

import main.entity.save.EntityMap;
import main.logic.AI.AiType;
import main.presentation.Logger;


public class ActorFactory
{
	private ActorFactory() {}
	
	public static Actor generateNewActor(ActorType actorType)
	{
		//TODO: give the creatures different attributes
		
		switch (actorType)
		{
			case PLAYER:
				return ActorBuilder.generateActor(ActorType.NO_TYPE).setHP(10).setIcon('@').setColor(15).setAI(AiType.HUMAN_CONTROLLED).setGender(GenderType.PLAYER).setUnique(true).build();
			case HUMAN:
				return ActorBuilder.generateActor(ActorType.HUMAN).setName("human").setHP(8).setIcon('H').setColor(12).build();
			case RAT:
				return ActorBuilder.generateActor(ActorType.RAT).setName("rat").setHP(5).setIcon('r').setColor(6).build();
			case RATTLESNAKE:
				return ActorBuilder.generateActor(ActorType.RATTLESNAKE).setName("rattlesnake").setHP(6).setIcon('s').setColor(6).build();
			case SCORPION:
				return ActorBuilder.generateActor(ActorType.SCORPION).setName("scorpion").setHP(4).setIcon('s').setColor(8).build();
			case WOLF:
				return ActorBuilder.generateActor(ActorType.WOLF).setName("wolf").setHP(15).setIcon('w').setColor(7).build();
			case BEAR:
				return ActorBuilder.generateActor(ActorType.BEAR).setName("bear").setHP(25).setIcon('B').setColor(8).build();
			case FOX:
				return ActorBuilder.generateActor(ActorType.FOX).setName("fox").setHP(10).setIcon('f').setColor(12).build();
			case BANDIT:
				return ActorBuilder.generateActor(ActorType.BANDIT).setName("bandit").setHP(12).setIcon('H').setColor(3).build();
			case OGRE:
				return ActorBuilder.generateActor(ActorType.OGRE).setName("ogre").setHP(30).setIcon('O').setColor(10).build();
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
