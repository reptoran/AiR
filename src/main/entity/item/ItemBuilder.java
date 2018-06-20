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
	
	public Item build()
	{
		return item;
	}
	
	public ItemBuilder setName(String name)
	{
		item.setName(name);
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
	
	public ItemBuilder setDR(int dr)
	{
		item.setDR(dr);
		return this;
	}
}
