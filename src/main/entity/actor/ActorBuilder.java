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
	
	public ActorBuilder setDamage(String damage)
	{
		actor.setDefaultDamage(damage);
		return this;
	}
	
	public ActorBuilder setArmor(int armor)
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
	
	public ActorBuilder addTrait(ActorTraitType trait)
	{
		actor.addTrait(trait);
		return this;
	}
}
