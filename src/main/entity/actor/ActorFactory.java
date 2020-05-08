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
			return ActorBuilder.generateActor(actorType).setHP(20).setIcon('@').setColor(9).setEquipmentType(EquipmentType.BASIC)
					.setAI(AiType.HUMAN_CONTROLLED).setGender(GenderType.PLAYER).setUnique(true).build();
		case PC_ELDER:
			return ActorBuilder.generateActor(actorType).setName("village elder").setHP(20).setIcon('@').setColor(5)
					.setEquipmentType(EquipmentType.BASIC).setAI(AiType.COALIGNED).setGender(GenderType.FEMALE).setUnique(true).build();
		case PC_PHYSICIAN:
			return ActorBuilder.generateActor(actorType).setName("physician").setHP(15).setIcon('@').setColor(15)
					.setEquipmentType(EquipmentType.BASIC).equip(ItemType.KNIFE, BasicEquipmentImpl.RHAND_INDEX)
					.equip(ItemType.QUILTED_SHIRT, BasicEquipmentImpl.ARMOR_INDEX).addTrait(ActorTraitType.HP_REGEN).setAI(AiType.COALIGNED)
					.carry(ItemType.HEALING_SALVE).carry(ItemType.HEALING_SALVE)
					.setGender(GenderType.MALE).setUnique(true).build();
		case PC_SMITH:
			return ActorBuilder.generateActor(actorType).setName("blacksmith").setHP(25).setIcon('@').setColor(8)
					.setEquipmentType(EquipmentType.BASIC).equip(ItemType.HAMMER, BasicEquipmentImpl.RHAND_INDEX)
					.equip(ItemType.HARD_LEATHER_VEST, BasicEquipmentImpl.ARMOR_INDEX).addTrait(ActorTraitType.DURABLE_EQ)
					.setAI(AiType.COALIGNED).setGender(GenderType.MALE).setUnique(true).setDamage("1D3").build();
		case WEAPONSMITH:
			return ActorBuilder.generateActor(actorType).setName("weaponsmith").setHP(30).setIcon('@').setColor(12)
					.setEquipmentType(EquipmentType.BASIC).setAI(AiType.COALIGNED).setGender(GenderType.FEMALE).setUnique(true)
					.setDamage("2D4").build();
		case COMMANDER:
			return ActorBuilder.generateActor(actorType).setName("commander").setHP(50).setIcon('@').setColor(13)
					.setEquipmentType(EquipmentType.BASIC).setAI(AiType.COALIGNED).setGender(GenderType.MALE).setUnique(true)
					.setDamage("3D3").build();
		case HUMAN:
			return ActorBuilder.generateActor(actorType).setName("human").setGender(GenderType.MALE).setHP(8).setIcon('H').setColor(12)
					.setAI(AiType.RAND_MOVE).build();
		case ROGUE:
			return ActorBuilder.generateActor(actorType).setName("rogue").setGender(GenderType.MALE).setHP(8).setIcon('H').setColor(4)
					.setEquipmentType(EquipmentType.BASIC).equip(ItemType.DAGGER, BasicEquipmentImpl.RHAND_INDEX)
					.equip(ItemType.QUILTED_SHIRT, BasicEquipmentImpl.ARMOR_INDEX).build();
		case RAT:
			return ActorBuilder.generateActor(actorType).setName("rat").setHP(5).setIcon('r').setColor(6).setDamage("1D2").build();
		case GIANT_RAT:
			return ActorBuilder.generateActor(actorType).setName("giant rat").setHP(10).setIcon('r').setColor(8).setDamage("1D5").build();
		case BAT:
			return ActorBuilder.generateActor(actorType).setName("bat").setHP(3).setIcon('b').setColor(6).setDamage("1D3").build();
		case GIANT_BAT:
			return ActorBuilder.generateActor(actorType).setName("giant bat").setHP(8).setIcon('b').setColor(8).setDamage("1D6").build();
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
		case WILDMAN:
			return ActorBuilder.generateActor(actorType).setName("wildman").setGender(GenderType.MALE).setHP(20).setIcon('H').setColor(6)
					.setDamage("1D7").build();
		case BANDIT:
			return ActorBuilder.generateActor(actorType).setName("bandit").setGender(GenderType.MALE).setHP(12).setIcon('H').setColor(3)
					.setEquipmentType(EquipmentType.BASIC).equip(ItemType.SHORT_SWORD, BasicEquipmentImpl.RHAND_INDEX)
					.equip(ItemType.SOFT_LEATHER_VEST, BasicEquipmentImpl.ARMOR_INDEX).build();
		case SKELETON:
			return ActorBuilder.generateActor(actorType).setName("skeleton").setHP(25).setIcon('z').setColor(15).setDamage("1D4").build();
		case ZOMBIE:
			return ActorBuilder.generateActor(actorType).setName("zombie").setHP(30).setIcon('z').setColor(12).setDamage("1D5").build();
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
