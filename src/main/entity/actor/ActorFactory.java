package main.entity.actor;

import java.text.ParseException;

import main.entity.item.ItemType;
import main.entity.item.equipment.BasicEquipmentImpl;
import main.entity.item.equipment.EquipmentType;
import main.entity.save.EntityMap;
import main.logic.AI.AiType;
import main.presentation.Logger;
import main.presentation.curses.CursesGuiScreen;

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
			return ActorBuilder.generateActor(actorType).setHP(20).setIcon('@').setColor(CursesGuiScreen.COLOR_LIGHT_BLUE)
					.setEquipmentType(EquipmentType.BASIC).setAI(AiType.HUMAN_CONTROLLED).setGender(GenderType.PLAYER).setUnique(true)
					.carry(ItemType.DEBUG_GEM_UP).carry(ItemType.DEBUG_GEM_DOWN).build();
		case PC_ELDER:
			return ActorBuilder.generateActor(actorType).setName("village elder").setHP(20).setIcon('@')
					.setColor(CursesGuiScreen.COLOR_DARK_MAGENTA).setEquipmentType(EquipmentType.BASIC).setAI(AiType.COALIGNED)
					.setGender(GenderType.FEMALE).setUnique(true).build();
		case PC_PHYSICIAN:
			return ActorBuilder.generateActor(actorType).setName("physician").setHP(15).setIcon('@').setColor(CursesGuiScreen.COLOR_WHITE)
					.setEquipmentType(EquipmentType.BASIC).equip(ItemType.KNIFE, BasicEquipmentImpl.RHAND_INDEX)
					.equip(ItemType.QUILTED_SHIRT, BasicEquipmentImpl.ARMOR_INDEX).addTrait(ActorTraitType.HP_REGEN).setAI(AiType.PHYSICIAN)
					.carry(ItemType.HEALING_SALVE).setGender(GenderType.MALE).setUnique(true)
					.setSkill(SkillType.HERBALISM, 2).build();
		case PC_SMITH:
			return ActorBuilder.generateActor(actorType).setName("blacksmith").setHP(25).setIcon('@')
					.setColor(CursesGuiScreen.COLOR_DARK_GREY).setEquipmentType(EquipmentType.BASIC)
					.equip(ItemType.HAMMER, BasicEquipmentImpl.RHAND_INDEX)
					.equip(ItemType.HARD_LEATHER_VEST, BasicEquipmentImpl.ARMOR_INDEX).addTrait(ActorTraitType.DURABLE_EQ)
					.setAI(AiType.BLACKSMITH).setGender(GenderType.MALE).setUnique(true).setDamage("1D3").build();
		case BOUND_ARCHEO:
			return ActorBuilder.generateActor(actorType).setName("bound archaeologist").setHP(1).setIcon('@')
					.setColor(CursesGuiScreen.COLOR_BROWN).setAI(AiType.FROZEN_CA).setGender(GenderType.FEMALE).setUnique(true).build();
		case ARCHAEOLOGIST:
			return ActorBuilder.generateActor(actorType).setName("archaeologist").setHP(10).setIcon('@')
					.setColor(CursesGuiScreen.COLOR_BROWN).setAI(AiType.COALIGNED).setGender(GenderType.FEMALE).setUnique(true).build();
		case WEAPONSMITH:
			return ActorBuilder.generateActor(actorType).setName("weaponsmith").setHP(30).setIcon('@')
					.setColor(CursesGuiScreen.COLOR_LIGHT_RED).setEquipmentType(EquipmentType.BASIC).setAI(AiType.COALIGNED)
					.setGender(GenderType.FEMALE).setUnique(true).setDamage("2D4").build();
		case COMMANDER:
			return ActorBuilder.generateActor(actorType).setName("commander").setHP(50).setIcon('@')
					.setColor(CursesGuiScreen.COLOR_LIGHT_MAGENTA).setEquipmentType(EquipmentType.BASIC).setAI(AiType.COALIGNED)
					.setGender(GenderType.MALE).setUnique(true).setDamage("3D3").build();

		case SQUAD_LEADER:
			return ActorBuilder.generateActor(actorType).setName("squad leader").setHP(100).setCurHP(10).setIcon('@')
					.setColor(CursesGuiScreen.COLOR_LIGHT_BLUE).setEquipmentType(EquipmentType.BASIC).setAI(AiType.COALIGNED)
					.setGender(GenderType.MALE).setUnique(true).setDamage("3D4").build();
		case COOK:
			return ActorBuilder.generateActor(actorType).setName("cook").setHP(20).setIcon('@').setColor(CursesGuiScreen.COLOR_YELLOW)
					.setEquipmentType(EquipmentType.BASIC).setAI(AiType.COALIGNED).setGender(GenderType.MALE).setUnique(true)
					.setDamage("2D3").build();
		case CHAPLAIN:
			return ActorBuilder.generateActor(actorType).setName("chaplain").setHP(15).setIcon('@').setColor(CursesGuiScreen.COLOR_LIGHT_GREY)
					.setEquipmentType(EquipmentType.BASIC).setAI(AiType.COALIGNED).setGender(GenderType.MALE).setUnique(true).build();
		case HUMAN:
			return ActorBuilder.generateActor(actorType).setName("human").setGender(GenderType.MALE).setHP(8).setIcon('H')
					.setColor(CursesGuiScreen.COLOR_LIGHT_RED).setAI(AiType.RAND_MOVE).build();
		case ROGUE:
			return ActorBuilder.generateActor(actorType).setName("rogue").setGender(GenderType.MALE).setHP(8).setIcon('H')
					.setColor(CursesGuiScreen.COLOR_DARK_RED).setEquipmentType(EquipmentType.BASIC)
					.equip(ItemType.KNIFE, BasicEquipmentImpl.RHAND_INDEX).equip(ItemType.QUILTED_SHIRT, BasicEquipmentImpl.ARMOR_INDEX)
					.build();
		case RAT:
			return ActorBuilder.generateActor(actorType).setName("rat").setHP(5).setIcon('r').setColor(CursesGuiScreen.COLOR_BROWN)
					.setDamage("1D2").build();
		case GIANT_RAT:
			return ActorBuilder.generateActor(actorType).setName("giant rat").setHP(10).setIcon('r').setColor(CursesGuiScreen.COLOR_DARK_GREY)
					.setDamage("1D4").build();
		case BAT:
			return ActorBuilder.generateActor(actorType).setName("bat").setHP(3).setIcon('b').setColor(CursesGuiScreen.COLOR_BROWN)
					.setDamage("1D3").build();
		case GIANT_BAT:
			return ActorBuilder.generateActor(actorType).setName("giant bat").setHP(7).setIcon('b').setColor(CursesGuiScreen.COLOR_DARK_GREY)
					.setDamage("1D6").build();
		case RATTLESNAKE:
			return ActorBuilder.generateActor(actorType).setName("rattlesnake").setHP(6).setIcon('s').setColor(CursesGuiScreen.COLOR_BROWN)
					.setDamage("1D3").build();
		case SCORPION:
			return ActorBuilder.generateActor(actorType).setName("scorpion").setHP(4).setIcon('s').setColor(CursesGuiScreen.COLOR_DARK_GREY)
					.setDamage("1D4").build();
		case WOLF:
			return ActorBuilder.generateActor(actorType).setName("wolf").setHP(10).setIcon('w').setColor(CursesGuiScreen.COLOR_LIGHT_GREY)
					.setDamage("2D2").build();
		case BEAR:
			return ActorBuilder.generateActor(actorType).setName("bear").setHP(20).setIcon('B').setColor(CursesGuiScreen.COLOR_DARK_GREY)
					.setDamage("2D3").build();
		case FOX:
			return ActorBuilder.generateActor(actorType).setName("fox").setHP(8).setIcon('f').setColor(CursesGuiScreen.COLOR_LIGHT_RED)
					.setDamage("1D4").build();
		case WILDMAN:
			return ActorBuilder.generateActor(actorType).setName("wildman").setGender(GenderType.MALE).setHP(20).setIcon('H')
					.setColor(CursesGuiScreen.COLOR_BROWN).setDamage("1D7")
					.equip(ItemType.CLUB, BasicEquipmentImpl.RHAND_INDEX)
					.build();
		case BANDIT:
			return ActorBuilder.generateActor(actorType).setName("bandit").setGender(GenderType.MALE).setHP(12).setIcon('H')
					.setColor(CursesGuiScreen.COLOR_DARK_CYAN).setEquipmentType(EquipmentType.BASIC)
					.equip(ItemType.SWORD, BasicEquipmentImpl.RHAND_INDEX)
					.equip(ItemType.SOFT_LEATHER_VEST, BasicEquipmentImpl.ARMOR_INDEX).build();
		case SKELETON:
			return ActorBuilder.generateActor(actorType).setName("skeleton").setHP(25).setIcon('z').setColor(15).setDamage("1D4").build();
		case ZOMBIE:
			return ActorBuilder.generateActor(actorType).setName("zombie").setHP(30).setIcon('z').setColor(CursesGuiScreen.COLOR_LIGHT_RED)
					.setDamage("1D5").build();
		case OGRE:
			return ActorBuilder.generateActor(actorType).setName("ogre").setHP(30).setIcon('O').setColor(CursesGuiScreen.COLOR_LIGHT_GREEN)
					.setEquipmentType(EquipmentType.BASIC).equip(ItemType.HEAVY_CLUB, BasicEquipmentImpl.RHAND_INDEX).build();
		case BOSS1:
			return ActorBuilder.generateActor(actorType).setName("shadow knight").setHP(50).setIcon('S')
					.setColor(CursesGuiScreen.COLOR_DARK_GREY).setDamage("3D3").setArmor(1).build();
		case NO_TYPE:
			return ActorBuilder.generateActor(actorType).setName("generic actor").setIcon('0').setColor(CursesGuiScreen.COLOR_LIGHT_GREY)
					.build();
		case ANY_ACTOR:
		default:
			Logger.warn("No definition for actor type " + actorType.name() + ".  Generating an actor with no type.");
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
