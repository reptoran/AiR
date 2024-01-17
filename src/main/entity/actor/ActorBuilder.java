package main.entity.actor;

import main.entity.item.Item;
import main.entity.item.ItemFactory;
import main.entity.item.ItemType;
import main.entity.item.equipment.EquipmentType;
import main.logic.AI.AiType;

public class ActorBuilder
{
	private static final int TOTAL_ATTRIBUTES = 5;
	
	private int[] attributes = new int[TOTAL_ATTRIBUTES];
	
	private Actor actor = null;
	
	public ActorBuilder(ActorType actorType)
	{
		for (int i = 0; i < TOTAL_ATTRIBUTES; i++)
		{
			this.attributes[i] = 0;
		}
		
		actor = new Actor(actorType, actorType.name() + " (unbuilt)", '%', 8, attributes);
	}
	
	public static ActorBuilder generateActor(ActorType actorType)
	{
		return new ActorBuilder(actorType);
	}
	
	public static ActorBuilder generateCoaligned(ActorType actorType)
	{
		return new ActorBuilder(actorType).setAI(AiType.COALIGNED);
	}
	
	public static ActorBuilder generateWild(ActorType actorType)
	{
		return new ActorBuilder(actorType).setAI(AiType.WILD);
	}
	
	public static ActorBuilder generateEvil(ActorType actorType)
	{
		return new ActorBuilder(actorType).setAI(AiType.EVIL);
	}
	
	public static ActorBuilder generateShadow(ActorType actorType)
	{
		return new ActorBuilder(actorType).setAI(AiType.SHADOW);
	}
	
	public static ActorBuilder generateUnaligned(ActorType actorType)
	{
		return new ActorBuilder(actorType).setAI(AiType.UNALIGNED);
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
	
	public ActorBuilder setGender(GenderType gender)
	{
		actor.setGender(gender);
		return this;
	}
	
	public ActorBuilder addNaturalWeapon(NaturalWeapon weapon)
	{
		actor.addNaturalWeapon(weapon);
		return this;
	}
	
	public ActorBuilder setNaturalArmor(int armor)
	{
		actor.setDefaultArmor(armor);
		return this;
	}
	
	public ActorBuilder setEquipmentType(EquipmentType equipment)
	{
		actor.setEquipment(equipment);
		return this;
	}
	
	public ActorBuilder equip(ItemType itemType, int slotIndex)
	{
		Item item = ItemFactory.generateNewItem(itemType);
		actor.equipItem(item, slotIndex);
		return this;
	}
	
	public ActorBuilder carry(ItemType itemType)
	{
		Item item = ItemFactory.generateNewItem(itemType);
		actor.receiveItem(item);
		return this;
	}
	
	public ActorBuilder setUnique(boolean unique)
	{
		actor.setUnique(unique);
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
	
	public ActorBuilder setAI(AiType AI)
	{
		actor.setAI(AI);
		return this;
	}
	
	public ActorBuilder setHP(int hp)
	{
		actor.setMaxHp(hp);
		actor.setCurHp(hp);
		return this;
	}
	
	public ActorBuilder setCurHP(int curHp)
	{
		actor.setCurHp(curHp);
		return this;
	}
	
	public ActorBuilder setStrength(int attributeValue)
	{
		actor.setAttribute(Actor.ATT_STR, attributeValue);
		return this;
	}
	
	public ActorBuilder addTrait(ActorTrait trait)
	{
		actor.addTrait(trait);
		return this;
	}
	
	public ActorBuilder setSkill(SkillType skill, int level)
	{
		actor.setSkill(skill, level);
		return this;
	}
}
