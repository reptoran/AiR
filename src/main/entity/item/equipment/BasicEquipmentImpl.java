package main.entity.item.equipment;

import java.util.ArrayList;
import java.util.List;

import main.entity.item.Item;

public class BasicEquipmentImpl implements Equipment
{
	private EquipmentSlot[] equipmentSlots = new EquipmentSlot[3];
	
	private static final int TOTAL_SLOTS = 3;	
	private static final int ARMOR_INDEX = 0;
	private static final int RHAND_INDEX = 1;
	private static final int LHAND_INDEX = 2;

	public BasicEquipmentImpl()
	{
		equipmentSlots[0] = new EquipmentSlot("Armor", EquipmentSlotType.ARMOR);
		equipmentSlots[1] = new EquipmentSlot("Right Hand", EquipmentSlotType.ANY);
		equipmentSlots[2] = new EquipmentSlot("Left Hand", EquipmentSlotType.ANY);
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
	
	private boolean isEquipmentSlotType(Item item, EquipmentSlotType type)
	{
		return item != null && item.getInventorySlot() == type;
	}
	
	@Override
	public List<Item> getWeapons()
	{
		List<Item> weapons = new ArrayList<Item>();
		
		Item item = equipmentSlots[RHAND_INDEX].getItem();
		if (isEquipmentSlotType(item, EquipmentSlotType.WEAPON))
			weapons.add(item);
		
		item = equipmentSlots[LHAND_INDEX].getItem();
		if (isEquipmentSlotType(item, EquipmentSlotType.WEAPON))
			weapons.add(item);
		
		return weapons;
	}

	@Override
	public List<Item> getShields()
	{
		List<Item> shields = new ArrayList<Item>();
		
		Item item = equipmentSlots[RHAND_INDEX].getItem();
		if (isEquipmentSlotType(item, EquipmentSlotType.SHIELD))
			shields.add(item);
		
		item = equipmentSlots[LHAND_INDEX].getItem();
		if (isEquipmentSlotType(item, EquipmentSlotType.SHIELD))
			shields.add(item);
		
		return shields;
	}

	@Override
	public List<Item> getArmor()
	{
		List<Item> armor = new ArrayList<Item>();
		
		Item item = equipmentSlots[ARMOR_INDEX].getItem();
		if (isEquipmentSlotType(item, EquipmentSlotType.ARMOR))
			armor.add(item);
		
		return armor;
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
	public BasicEquipmentImpl clone()
	{
		BasicEquipmentImpl equipment = new BasicEquipmentImpl();
		
		for (int i = 0; i < TOTAL_SLOTS; i++)
			equipment.equipmentSlots[i] = equipmentSlots[i].clone();
		
		return equipment;
	}
	
	@Override
	public int hashCode()
	{
		int hash = 71;
		
		for (int i = 0; i < TOTAL_SLOTS; i++)
			hash = 71 * hash + (equipmentSlots[i].getItem() == null ? 0 : equipmentSlots[i].getItem().hashCode());

		return hash;
	}
}
