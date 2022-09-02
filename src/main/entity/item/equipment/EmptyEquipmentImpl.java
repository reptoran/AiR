package main.entity.item.equipment;

import java.util.ArrayList;
import java.util.List;

import main.entity.item.Item;
import main.entity.item.ItemType;

public class EmptyEquipmentImpl implements Equipment
{
	public EmptyEquipmentImpl() {}

	@Override
	public Item getItem(int slotIndex)
	{
		return null;
	}

	@Override
	public Item removeItem(int slotIndex)
	{
		return null;
	}

	@Override
	public EquipmentSlotType getTypeOfSlot(int slotIndex)
	{
		return null;
	}

	@Override
	public void equipItem(Item item, int slotIndex)
	{
		return;
	}

	@Override
	public int getIndexOfItem(Item item)
	{
		return -1;
	}
	
	@Override
	public List<Item> getWeapons()
	{
		return new ArrayList<Item>();
	}

	@Override
	public List<Item> getShields()
	{
		return new ArrayList<Item>();
	}

	@Override
	public List<Item> getArmor()
	{
		return new ArrayList<Item>();
	}

	@Override
	public List<EquipmentSlot> getEquipmentSlots()
	{
		return new ArrayList<EquipmentSlot>();
	}
	
	@Override
	public EquipmentSlot getEquipmentSlot(int slotIndex)
	{
		return null;
	}

	@Override
	public boolean isEmpty()
	{
		return true;
	}

	@Override
	public boolean hasEmptySlotAvailable(EquipmentSlotType slotType)
	{
		return false;
	}

	@Override
	public int getIndexOfFirstSlotAvailable(EquipmentSlotType slotType)
	{
		return -1;
	}

	@Override
	public int getTotalItemsOfType(ItemType type)
	{
		return 0;
	}

	@Override
	public Item getFirstItemOfType(ItemType type)
	{
		return null;
	}
	
	@Override
	public EmptyEquipmentImpl clone()
	{
		return new EmptyEquipmentImpl();
	}
	
	@Override
	public int hashCode()
	{
		return -191;
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

		return true;
	}
}
