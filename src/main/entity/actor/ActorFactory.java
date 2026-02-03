package main.entity.actor;

import java.text.ParseException;

import main.entity.item.ItemType;
import main.entity.item.equipment.BasicEquipmentImpl;
import main.entity.item.equipment.EquipmentType;
import main.entity.save.EntityMap;
import main.logic.AI.AiType;
import main.presentation.Logger;
import main.presentation.curses.Colors;

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
			return ActorBuilder.generateActor(actorType).setCategory(ActorCategory.SENTIENT).setHP(20).setIcon('@')
					.setColor(Colors.LIGHT_BLUE).setEquipmentType(EquipmentType.BASIC).setAI(AiType.HUMAN_CONTROLLED)
					.setGender(GenderType.PLAYER).setUnique(true).carry(ItemType.DEBUG_GEM_UP).carry(ItemType.DEBUG_GEM_DOWN).build();
		case PC_ELDER:
			return ActorBuilder.generateCoaligned(actorType).setCategory(ActorCategory.SENTIENT).setName("village elder").setHP(20)
					.setIcon('@').setColor(Colors.DARK_MAGENTA).setEquipmentType(EquipmentType.BASIC)
					.setGender(GenderType.FEMALE).setUnique(true).build();
		case PC_PHYSICIAN:
			return ActorBuilder.generateActor(actorType).setCategory(ActorCategory.SENTIENT).setName("physician").setHP(15).setIcon('@')
					.setColor(Colors.WHITE).setEquipmentType(EquipmentType.BASIC)
					.equip(ItemType.KNIFE, BasicEquipmentImpl.RHAND_INDEX).equip(ItemType.QUILTED_SHIRT, BasicEquipmentImpl.ARMOR_INDEX)
					.setAI(AiType.PHYSICIAN).carry(ItemType.HEALING_SALVE).setGender(GenderType.MALE)
					.setSkill(SkillType.ALCHEMY, SkillRank.NOVICE)
					.setSkill(SkillType.HEALING, SkillRank.NOVICE)
					.setSkill(SkillType.AWARENESS, SkillRank.UNSKILLED)
					.setSkill(SkillType.ARMOR_USE, SkillRank.UNSKILLED)
					.setUnique(true).build();
		case PC_SMITH:
			return ActorBuilder.generateActor(actorType).setCategory(ActorCategory.SENTIENT).setName("blacksmith").setHP(25).setIcon('@')
					.setColor(Colors.DARK_GREY).setEquipmentType(EquipmentType.BASIC)
					.equip(ItemType.HAMMER, BasicEquipmentImpl.RHAND_INDEX)
					.equip(ItemType.HARD_LEATHER_VEST, BasicEquipmentImpl.ARMOR_INDEX).addTrait(ActorTrait.DURABLE_EQ)
					.setAI(AiType.BLACKSMITH).setGender(GenderType.MALE).addNaturalWeapon(NaturalWeapon.hit())
					.setSkill(SkillType.ARMOR_USE, SkillRank.NOVICE)
					.setSkill(SkillType.SMITHING, SkillRank.NOVICE)
					.setSkill(SkillType.AWARENESS, SkillRank.UNSKILLED)
					.setSkill(SkillType.STEALTH, SkillRank.UNSKILLED)
					.setUnique(true).build();
		case PC_HUNTRESS:
			return ActorBuilder.generateActor(actorType).setCategory(ActorCategory.SENTIENT).setName("huntress").setHP(15).setIcon('@')
					.setColor(Colors.DARK_GREEN).setEquipmentType(EquipmentType.BASIC)
					.equip(ItemType.SWORD, BasicEquipmentImpl.RHAND_INDEX)
					.equip(ItemType.SOFT_LEATHER_VEST, BasicEquipmentImpl.ARMOR_INDEX)
					.give(ItemType.AMMO, 20)
					.setAI(AiType.COALIGNED).setGender(GenderType.FEMALE)
					.setSkill(SkillType.AWARENESS, SkillRank.NOVICE)
					.setSkill(SkillType.STEALTH, SkillRank.NOVICE)
					.setSkill(SkillType.ALCHEMY, SkillRank.UNSKILLED)
					.setSkill(SkillType.HEALING, SkillRank.UNSKILLED)
					.setUnique(true).build();
		case BOUND_ARCHEO:
			return ActorBuilder.generateActor(actorType).setCategory(ActorCategory.SENTIENT).setName("bound archaeologist").setHP(1)
					.setIcon('@').setColor(Colors.BROWN).setAI(AiType.FROZEN_CA).setGender(GenderType.FEMALE).setUnique(true)
					.build();
		case ARCHAEOLOGIST:
			return ActorBuilder.generateCoaligned(actorType).setCategory(ActorCategory.SENTIENT).setName("archaeologist").setHP(10)
					.setIcon('@').setColor(Colors.BROWN).setGender(GenderType.FEMALE).setUnique(true)
					.carry(ItemType.ANCIENT_TABLET).build();
		case WEAPONSMITH:
			return ActorBuilder.generateCoaligned(actorType).setCategory(ActorCategory.SENTIENT).setName("weaponsmith").setHP(30)
					.setIcon('@').setColor(Colors.LIGHT_RED).setEquipmentType(EquipmentType.BASIC)
					.setGender(GenderType.FEMALE).setUnique(true).addNaturalWeapon(NaturalWeapon.strike()).build();
		case COMMANDER:
			return ActorBuilder.generateCoaligned(actorType).setCategory(ActorCategory.SENTIENT).setName("commander").setHP(50).setIcon('@')
					.setColor(Colors.LIGHT_MAGENTA).setEquipmentType(EquipmentType.BASIC).setGender(GenderType.MALE)
					.setUnique(true).addNaturalWeapon(NaturalWeapon.strike()).build();

		case SQUAD_LEADER:
			return ActorBuilder.generateCoaligned(actorType).setCategory(ActorCategory.SENTIENT).setName("squad leader").setHP(100)
					.setCurHP(10).setIcon('@').setColor(Colors.LIGHT_GREEN).setEquipmentType(EquipmentType.BASIC)
					.setGender(GenderType.MALE).setUnique(true).addNaturalWeapon(NaturalWeapon.hit()).addNaturalWeapon(NaturalWeapon.hit())
					.addNaturalWeapon(NaturalWeapon.hit()).build();
		case COOK:
			return ActorBuilder.generateCoaligned(actorType).setCategory(ActorCategory.SENTIENT).setName("cook").setHP(20).setIcon('@')
					.setColor(Colors.YELLOW).setEquipmentType(EquipmentType.BASIC).setGender(GenderType.MALE).setUnique(true)
					.addNaturalWeapon(NaturalWeapon.hit()).build();
		case CHAPLAIN:
			return ActorBuilder.generateCoaligned(actorType).setCategory(ActorCategory.SENTIENT).setName("chaplain").setHP(15).setIcon('@')
					.setColor(Colors.LIGHT_GREY).setEquipmentType(EquipmentType.BASIC).setGender(GenderType.MALE)
					.setUnique(true).build();
		case HUMAN:
			return ActorBuilder.generateActor(actorType).setCategory(ActorCategory.SENTIENT).setName("human").setGender(GenderType.MALE)
					.setHP(8).setIcon('H').setColor(Colors.LIGHT_RED).setAI(AiType.RAND_MOVE).build();
		case ROGUE:
			return ActorBuilder.generateEvil(actorType).setCategory(ActorCategory.SENTIENT).setName("rogue").setGender(GenderType.MALE)
					.setHP(8).setIcon('H').setColor(Colors.DARK_RED).setEquipmentType(EquipmentType.BASIC)
					.equip(ItemType.KNIFE, BasicEquipmentImpl.RHAND_INDEX).equip(ItemType.THICK_SHIRT, BasicEquipmentImpl.ARMOR_INDEX)
					.build();
		case RAT:
			return ActorBuilder.generateWild(actorType).setCategory(ActorCategory.NATURAL).setName("rat").setHP(5).setIcon('r')
					.setColor(Colors.BROWN).addNaturalWeapon(NaturalWeapon.gnaw()).build();
		case GIANT_RAT:
			return ActorBuilder.generateWild(actorType).setCategory(ActorCategory.NATURAL).setName("giant rat").setHP(10).setIcon('r')
					.setColor(Colors.DARK_GREY).addNaturalWeapon(NaturalWeapon.claw()).build();
		case RAT_KING:
			return ActorBuilder.generateActor(actorType).setCategory(ActorCategory.NATURAL).setName("rat king").setHP(50).setIcon('r')
					.setColor(Colors.DARK_RED).addNaturalWeapon(NaturalWeapon.bite()).setAI(AiType.RAT_KING)
					.setSkill(SkillType.AWARENESS, SkillRank.EXPERT).build();
		case BAT:
			return ActorBuilder.generateWild(actorType).setCategory(ActorCategory.NATURAL).setName("bat").setHP(3).setIcon('b')
					.setColor(Colors.BROWN).addNaturalWeapon(NaturalWeapon.bite()).build();
		case GIANT_BAT:
			return ActorBuilder.generateWild(actorType).setCategory(ActorCategory.NATURAL).setName("giant bat").setHP(7).setIcon('b')
					.setColor(Colors.DARK_GREY).addNaturalWeapon(NaturalWeapon.pummel()).build();
		case RATTLESNAKE:
			return ActorBuilder.generateWild(actorType).setCategory(ActorCategory.NATURAL).setName("rattlesnake").setHP(6).setIcon('s')
					.setColor(Colors.BROWN).addNaturalWeapon(NaturalWeapon.bite()).build();
		case SCORPION:
			return ActorBuilder.generateWild(actorType).setCategory(ActorCategory.NATURAL).setName("scorpion").setHP(4).setIcon('s')
					.setColor(Colors.DARK_GREY).addNaturalWeapon(NaturalWeapon.sting()).build();
		case WOLF:
			return ActorBuilder.generateWild(actorType).setCategory(ActorCategory.NATURAL).setName("wolf").setHP(10).setIcon('w')
					.setColor(Colors.LIGHT_GREY).addNaturalWeapon(NaturalWeapon.bite())
					.addNaturalWeapon(NaturalWeapon.claw()).build();
		case BEAR:
			return ActorBuilder.generateWild(actorType).setCategory(ActorCategory.NATURAL).setName("bear").setHP(20).setIcon('B')
					.setColor(Colors.DARK_GREY).addNaturalWeapon(NaturalWeapon.maul()).build();
		case FOX:
			return ActorBuilder.generateWild(actorType).setCategory(ActorCategory.NATURAL).setName("fox").setHP(8).setIcon('f')
					.setColor(Colors.LIGHT_RED).addNaturalWeapon(NaturalWeapon.claw()).build();
		case WILDMAN:
			return ActorBuilder.generateUnaligned(actorType).setCategory(ActorCategory.SENTIENT).setName("wildman")
					.setGender(GenderType.MALE).setHP(20).setIcon('H').setColor(Colors.BROWN)
					.addNaturalWeapon(NaturalWeapon.pummel()).equip(ItemType.CLUB, BasicEquipmentImpl.RHAND_INDEX).build();
		case GIANT_BEETLE:
			return ActorBuilder.generateWild(actorType).setCategory(ActorCategory.NATURAL).setName("giant beetle").setHP(10).setIcon('i')
					.setColor(Colors.DARK_GREY).addNaturalWeapon(NaturalWeapon.bite())
					.addNaturalWeapon(NaturalWeapon.sting()).setNaturalArmor(2).build();
		case LIZARD:
			return ActorBuilder.generateWild(actorType).setCategory(ActorCategory.NATURAL).setName("lizard").setHP(12).setIcon('l')
					.setColor(Colors.DARK_GREEN).addNaturalWeapon(NaturalWeapon.bite())
					.addNaturalWeapon(NaturalWeapon.claw()).setNaturalArmor(1).build();
		case BANDIT:
			return ActorBuilder.generateEvil(actorType).setCategory(ActorCategory.SENTIENT).setName("bandit").setGender(GenderType.MALE)
					.setHP(12).setIcon('H').setColor(Colors.DARK_CYAN).setEquipmentType(EquipmentType.BASIC)
					.equip(ItemType.CLUB, BasicEquipmentImpl.RHAND_INDEX).equip(ItemType.SOFT_LEATHER_VEST, BasicEquipmentImpl.ARMOR_INDEX)
					.build();
		case MANIAC:
			return ActorBuilder.generateUnaligned(actorType).setCategory(ActorCategory.SENTIENT).setName("maniac")
					.setGender(GenderType.MALE).setHP(5).setIcon('H').setColor(Colors.YELLOW)
					.addNaturalWeapon(NaturalWeapon.bite()).addNaturalWeapon(NaturalWeapon.bite()).addNaturalWeapon(NaturalWeapon.pummel())
					.build();
		case GOBLIN:
			return ActorBuilder.generateEvil(actorType).setCategory(ActorCategory.SENTIENT).setName("goblin").setGender(GenderType.MALE)
					.setHP(5).setIcon('g').setColor(Colors.LIGHT_BLUE).setEquipmentType(EquipmentType.BASIC)
					.equip(ItemType.CLUB, BasicEquipmentImpl.RHAND_INDEX).equip(ItemType.BUCKLER, BasicEquipmentImpl.LHAND_INDEX)
					.equip(ItemType.SOFT_LEATHER_VEST, BasicEquipmentImpl.ARMOR_INDEX).build();
		case WORM_MASS:
			return ActorBuilder.generateWild(actorType).setCategory(ActorCategory.MUTATATED).setName("worm mass").setHP(12).setIcon('W')
					.setColor(Colors.WHITE).addNaturalWeapon(NaturalWeapon.gnaw()).setAI(AiType.RAT_KING).build();
		case GOBLIN_CARVER:
			return ActorBuilder.generateEvil(actorType).setCategory(ActorCategory.SENTIENT).setName("goblin carver")
					.setGender(GenderType.MALE).setHP(8).setIcon('g').setColor(Colors.DARK_RED)
					.setEquipmentType(EquipmentType.BASIC).equip(ItemType.KNIFE, BasicEquipmentImpl.RHAND_INDEX)
					.equip(ItemType.SWORD, BasicEquipmentImpl.LHAND_INDEX).build();
		case CAVE_CRAWLER:
			return ActorBuilder.generateWild(actorType).setCategory(ActorCategory.NATURAL).setName("cave crawler").setHP(15).setIcon('i')
					.setColor(Colors.DARK_BLUE).addNaturalWeapon(NaturalWeapon.claw()).addNaturalWeapon(NaturalWeapon.claw())
					.setNaturalArmor(2).build();
		case DESERTER:
			return ActorBuilder.generateUnaligned(actorType).setCategory(ActorCategory.SENTIENT).setName("deserter").setHP(15).setIcon('H')
					.setColor(Colors.DARK_GREEN).setEquipmentType(EquipmentType.BASIC).setGender(GenderType.MALE)
					.equip(ItemType.SWORD, BasicEquipmentImpl.LHAND_INDEX).equip(ItemType.HARD_LEATHER_VEST, BasicEquipmentImpl.ARMOR_INDEX)
					.build();
		case SKELETON:
			return ActorBuilder.generateShadow(actorType).setCategory(ActorCategory.UNDEAD).setName("skeleton").setHP(25).setIcon('z')
					.setColor(Colors.WHITE).addNaturalWeapon(NaturalWeapon.hit()).setNaturalArmor(4).build();
		case SHADOW_SLAVE:
			return ActorBuilder.generateShadow(actorType).setCategory(ActorCategory.SHADOW).setName("shadow slave").setHP(30).setIcon('H')
					.setColor(Colors.DARK_GREY).addNaturalWeapon(NaturalWeapon.slam()).setNaturalArmor(0).build();
		case ZOMBIE:
			return ActorBuilder.generateShadow(actorType).setCategory(ActorCategory.UNDEAD).setName("zombie").setHP(30).setIcon('z')
					.setColor(Colors.LIGHT_RED).addNaturalWeapon(NaturalWeapon.pummel()).setNaturalArmor(2).build();
		case OGRE:
			return ActorBuilder.generateEvil(actorType).setCategory(ActorCategory.SENTIENT).setName("ogre").setHP(30).setIcon('O')
					.setColor(Colors.LIGHT_GREEN).setEquipmentType(EquipmentType.BASIC)
					.equip(ItemType.HEAVY_CLUB, BasicEquipmentImpl.RHAND_INDEX).build();
		case BOSS1:
			return ActorBuilder.generateShadow(actorType).setCategory(ActorCategory.SHADOW).setName("shadow knight").setHP(50).setIcon('S')
					.setColor(Colors.DARK_GREY).addNaturalWeapon(NaturalWeapon.smite()).setNaturalArmor(1).build();
		case NO_TYPE:
			return ActorBuilder.generateActor(actorType).setCategory(ActorCategory.NATURAL).setName("generic actor").setIcon('0')
					.setColor(Colors.LIGHT_GREY).build();
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
