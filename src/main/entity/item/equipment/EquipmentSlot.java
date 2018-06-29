package main.entity.item.equipment;

import main.entity.item.Item;

public class EquipmentSlot
{
	private String name;
	private String shortName;
	private EquipmentSlotType type;
	private Item item = null;
	
	public EquipmentSlot(String name, EquipmentSlotType type)
	{
		this(name, name, type);
	}
	
	public EquipmentSlot(String name, String shortName, EquipmentSlotType type)
	{
		this.name = name;
		this.shortName = shortName.substring(0, 5);
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

	public String getShortName()
	{
		return shortName;
	}

	public EquipmentSlotType getType()
	{
		return type;
	}
	
	public Item getItem()
	{
		return item;
	}
	
	public Item removeItem()
	{
		Item itemToRemove = item;
		item = null;
		return itemToRemove;
	}
	
	@Override
	public EquipmentSlot clone()
	{
		EquipmentSlot slot = new EquipmentSlot(name, type);
		
		if (item != null)
			slot.setItem(item.clone());
		
		return slot;
	}

	@Override
	public int hashCode()
	{
		final int prime = 97;
		int result = 1;
		result = prime * result + ((item == null) ? 0 : item.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((shortName == null) ? 0 : shortName.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EquipmentSlot other = (EquipmentSlot) obj;
		if (item == null)
		{
			if (other.item != null)
				return false;
		} else if (!item.equals(other.item))
			return false;
		if (name == null)
		{
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (shortName == null)
		{
			if (other.shortName != null)
				return false;
		} else if (!shortName.equals(other.shortName))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
}
