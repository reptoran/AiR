package main.entity.item.equipment;

import java.util.ArrayList;
import java.util.List;

import main.entity.item.Item;

public class ReadyEquipmentImpl extends AbstractEquipment
{
	public ReadyEquipmentImpl(int totalReadyItemSlots)
	{
		super(totalReadyItemSlots);
		
		for (int i = 0; i < TOTAL_SLOTS; i++)
		{
			equipmentSlots[i] = new EquipmentSlot("Ready Item " + i, "Ready" + i, EquipmentSlotType.ARMAMENT);
		}
	}

	@Override
	public List<Item> getWeapons()
	{
		List<Item> weapons = new ArrayList<Item>();
		
		for (EquipmentSlot slot : equipmentSlots)
		{
			Item item = slot.getItem();
			if (item != null && item.isWeapon())
				weapons.add(item);
		}
		
		return weapons;
	}

	@Override
	public List<Item> getShields()
	{
		List<Item> shields = new ArrayList<Item>();
		
		for (EquipmentSlot slot : equipmentSlots)
		{
			Item item = slot.getItem();
			if (item != null && item.isShield())
				shields.add(item);
		}
		
		return shields;
	}

	@Override
	public List<Item> getArmor()
	{
		return new ArrayList<Item>();
	}

	@Override
	public ReadyEquipmentImpl clone()
	{
		return (ReadyEquipmentImpl) copyEquipmentSlots(new ReadyEquipmentImpl(TOTAL_SLOTS));
	}
}
