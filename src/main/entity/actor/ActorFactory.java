package main.entity.actor;

import java.text.ParseException;

import main.entity.item.ItemType;
import main.entity.item.equipment.BasicEquipmentImpl;
import main.entity.item.equipment.EquipmentType;
import main.entity.save.EntityMap;
import main.logic.AI.AiType;
import main.presentation.Logger;

public class ActorFactory
{
	private ActorFactory()
	{
	}

	public static Actor generateNewActor(ActorType actorType)
	{
		// TODO: give the creatures different attributes

		switch (actorType)
		{
		case PLAYER:
			return ActorBuilder.generateActor(ActorType.PLAYER).setHP(25).setIcon('@').setColor(15).setEquipmentType(EquipmentType.BASIC)
					.setAI(AiType.HUMAN_CONTROLLED).setGender(GenderType.PLAYER).setUnique(true).build();
		case HUMAN:
			return ActorBuilder.generateActor(actorType).setName("human").setGender(GenderType.MALE).setHP(8).setIcon('H').setColor(12)
					.setAI(AiType.RAND_MOVE).build();
		case ROGUE:
			return ActorBuilder.generateActor(actorType).setName("rogue").setGender(GenderType.MALE).setHP(8).setIcon('H').setColor(4)
					.setEquipmentType(EquipmentType.BASIC).equip(ItemType.DAGGER, BasicEquipmentImpl.RHAND_INDEX)
					.equip(ItemType.QUILTED_SHIRT, BasicEquipmentImpl.ARMOR_INDEX).build();
		case RAT:
			return ActorBuilder.generateActor(actorType).setName("rat").setHP(5).setIcon('r').setColor(6).setDamage("1D2").build();
		case RATTLESNAKE:
			return ActorBuilder.generateActor(actorType).setName("rattlesnake").setHP(6).setIcon('s').setColor(6).setDamage("1D3").build();
		case SCORPION:
			return ActorBuilder.generateActor(actorType).setName("scorpion").setHP(4).setIcon('s').setColor(8).setDamage("1D4").build();
		case WOLF:
			return ActorBuilder.generateActor(actorType).setName("wolf").setHP(15).setIcon('w').setColor(7).setDamage("2D2").build();
		case BEAR:
			return ActorBuilder.generateActor(actorType).setName("bear").setHP(25).setIcon('B').setColor(8).setDamage("2D3").build();
		case FOX:
			return ActorBuilder.generateActor(actorType).setName("fox").setHP(10).setIcon('f').setColor(12).setDamage("1D4").build();
		case BANDIT:
			return ActorBuilder.generateActor(actorType).setName("bandit").setGender(GenderType.MALE).setHP(12).setIcon('H').setColor(3)
					.setEquipmentType(EquipmentType.BASIC).equip(ItemType.SHORT_SWORD, BasicEquipmentImpl.RHAND_INDEX)
					.equip(ItemType.SOFT_LEATHER_VEST, BasicEquipmentImpl.ARMOR_INDEX).build();
		case OGRE:
			return ActorBuilder.generateActor(actorType).setName("ogre").setHP(30).setIcon('O').setColor(10)
					.setEquipmentType(EquipmentType.BASIC).equip(ItemType.HEAVY_CLUB, BasicEquipmentImpl.RHAND_INDEX).build();
		case BOSS1:
			return ActorBuilder.generateActor(actorType).setName("shadow knight").setHP(50).setIcon('S').setColor(8).setDamage("3D3")
					.setArmor(1).build();
		case NO_TYPE:
			return ActorBuilder.generateActor(actorType).setName("generic actor").setIcon('0').setColor(7).build();
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
