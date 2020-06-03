package main.entity.item.equipment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import main.entity.item.Item;
import main.entity.item.ItemType;

public abstract class AbstractEquipment implements Equipment
{
	protected final int TOTAL_SLOTS;
	protected EquipmentSlot[] equipmentSlots;
	
	protected AbstractEquipment(int totalEquipmentSlots)
	{
		TOTAL_SLOTS = totalEquipmentSlots;
		equipmentSlots = new EquipmentSlot[TOTAL_SLOTS];
	}

	@Override
	public Item getItem(int slotIndex)
	{
		return equipmentSlots[slotIndex].getItem();
	}

	@Override
	public Item removeItem(int slotIndex)
	{
		Item item = equipmentSlots[slotIndex].getItem();
		equipmentSlots[slotIndex].setItem(null);
		return item;
	}

	@Override
	public void equipItem(Item item, int slotIndex)
	{
		equipmentSlots[slotIndex].setItem(item);
	}

	@Override
	public int getIndexOfItem(Item item)
	{
		if (item == null)
			return -1;
		
		for (int i = 0; i < TOTAL_SLOTS; i++)
		{
			//the idea of using == instead of equals() is that if we're requesting a specific item from the equipment,
			//we care about THAT item exactly, not the first instance that matches
			if (item == equipmentSlots[i].getItem())
				return i;
		}
		
		return -1;
	}

	@Override
	public List<EquipmentSlot> getEquipmentSlots()
	{
		List<EquipmentSlot> slots = new ArrayList<EquipmentSlot>();
		
		for (int i = 0; i < TOTAL_SLOTS; i++)
			slots.add(equipmentSlots[i]);
		
		return slots;
	}

	@Override
	public boolean isEmpty()
	{
		boolean empty = true;
		
		for (int i = 0; i < TOTAL_SLOTS; i++)
		{
			if (equipmentSlots[i].getItem() != null)
				empty = false;
		}
		
		return empty;
	}
	
	@Override
	public boolean hasEmptySlotAvailable(EquipmentSlotType slotType)
	{
		return getIndexOfFirstSlotAvailable(slotType) != -1;
	}
	
	@Override
	public int getIndexOfFirstSlotAvailable(EquipmentSlotType slotType)
	{
		for (int i = 0; i < TOTAL_SLOTS; i++)
		{
			if (equipmentSlots[i].getItem() != null)
				continue;
			
			if (slotType.equals(equipmentSlots[i].getType()))
				return i;
		}
		
		return -1;
	}

	@Override
	public int getTotalItemsOfType(ItemType type)
	{
		int count = 0;
		
		for (int i = 0; i < TOTAL_SLOTS; i++)
		{
			Item item = equipmentSlots[i].getItem();
			
			if (item == null)
				continue;
			
			if (item.getType() != type)
				continue;
			
			count = count + item.getAmount();
		}
		
		return count;
	}

	@Override
	public Item getFirstItemOfType(ItemType type)
	{
		for (int i = 0; i < TOTAL_SLOTS; i++)
		{
			Item item = equipmentSlots[i].getItem();
			
			if (item == null)
				continue;
			
			if (item.getType() == type)
				return item;
		}
		
		return null;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(equipmentSlots);
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
		AbstractEquipment other = (AbstractEquipment) obj;
		if (!Arrays.equals(equipmentSlots, other.equipmentSlots))
			return false;
		return true;
	}
	
	@Override
	public abstract AbstractEquipment clone();

	public AbstractEquipment copyEquipmentSlots(AbstractEquipment equipment)
	{
		if (equipment.TOTAL_SLOTS != TOTAL_SLOTS)
			throw new IllegalArgumentException("Cannot copy equipment slots; argument has " + equipment.TOTAL_SLOTS + " slots instead of the expected " + TOTAL_SLOTS + ".");
		
		for (int i = 0; i < TOTAL_SLOTS; i++)
			equipment.equipmentSlots[i] = equipmentSlots[i].clone();
		
		return equipment;
	}
}
