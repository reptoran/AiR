package main.entity.item.equipment;

import main.entity.item.Item;

public class EquipmentSlot
{
	private String name;
	private EquipmentSlotType type;
	private Item item = null;
	
	public EquipmentSlot(String name, EquipmentSlotType type)
	{
		this.name = name;
		this.type = type;
	}
	
	public void setItem(Item item)
	{
		this.item = item;
	}

	public String getName()
	{
		return name;
	}

	public EquipmentSlotType getType()
	{
		return type;
	}
	
	public Item getItem()
	{
		return item;
	}
	
	@Override
	public EquipmentSlot clone()
	{
		EquipmentSlot slot = new EquipmentSlot(name, type);
		
		if (item != null)
			slot.setItem(item.clone());
		
		return slot;
	}
}
