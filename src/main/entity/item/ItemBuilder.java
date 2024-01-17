package main.entity.item;

import main.entity.item.equipment.EquipmentSlotType;

public class ItemBuilder
{
	private static Item item;
	
	private ItemBuilder(ItemType type)
	{
		item = new Item(type);
	}
	
	public static ItemBuilder generateItem(ItemType type)
	{
		return new ItemBuilder(type);
	}
	
	public static ItemBuilder generateArmor(ItemType type)
	{
		return new ItemBuilder(type).setSlot(EquipmentSlotType.ARMOR).setUpgradedBy(ItemType.METAL_SHARD).setIcon('[');
	}
	
	public static ItemBuilder generateShield(ItemType type)
	{
		return new ItemBuilder(type).setSlot(EquipmentSlotType.ARMAMENT).setUpgradedBy(ItemType.METAL_SHARD).setIcon('(');
	}
	
	public static ItemBuilder generateWeapon(ItemType type, String damage)
	{
		return new ItemBuilder(type).setSlot(EquipmentSlotType.ARMAMENT).setUpgradedBy(ItemType.METAL_SHARD).setIcon('/').setDamage(damage);
	}
	
	public static ItemBuilder generateMaterial(ItemType type)
	{
		return new ItemBuilder(type).setSlot(EquipmentSlotType.MATERIAL);
	}
	
	public static ItemBuilder generateMagicItem(ItemType type)
	{
		return new ItemBuilder(type).setSlot(EquipmentSlotType.MAGIC);
	}
	
	public Item build()
	{
		return item;
	}
	
	public ItemBuilder setName(String name)
	{
		item.setName(name);
		return this;
	}
	
	public ItemBuilder setNames(String name)
	{
		item.setName(name);
		item.setPlural(name + "s");
		return this;
	}
	
	public ItemBuilder setPlural(String plural)
	{
		item.setPlural(plural);
		return this;
	}
	
	public ItemBuilder setIcon(char icon)
	{
		item.setIcon(icon);
		return this;
	}
	
	public ItemBuilder setColor(int color)
	{
		item.setColor(color);
		return this;
	}
	
	public ItemBuilder setDamage(String damage)
	{
		item.setDamage(damage);
		return this;
	}
	
	public ItemBuilder setSize(int size)
	{
		item.setSize(size);
		return this;
	}
	
	public ItemBuilder setSlot(EquipmentSlotType slot)
	{
		item.setInventorySlot(slot);
		return this;
	}
	
	public ItemBuilder setHP(int HP)
	{
		item.setMaxHp(HP);
		item.setCurHp(HP);
		return this;
	}
	
	public ItemBuilder setCR(int cr)
	{
		item.setCR(cr);
		return this;
	}
	
	public ItemBuilder setAR(int ar)
	{
		item.setAR(ar);
		return this;
	}
	
	public ItemBuilder setMaterial(ItemMaterial material)
	{
		item.setMaterial(material);
		return this;
	}
	
	public ItemBuilder setUpgraded(boolean upgraded)
	{
		if (upgraded && !item.isUpgraded())
			item.upgrade();
		
		if (!upgraded && item.isUpgraded())
			item.downgrade();
		
		return this;
	}
	
	public ItemBuilder setUpgradedBy(ItemType upgradedBy)
	{
		item.setUpgradedBy(upgradedBy);
		return this;
	}
	
	public ItemBuilder addTrait(ItemTrait trait)
	{
		item.addTrait(trait);
		return this;
	}
}
