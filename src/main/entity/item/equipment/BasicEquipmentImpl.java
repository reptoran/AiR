package main.entity.item.equipment;

import java.util.ArrayList;
import java.util.List;

import main.entity.item.Item;

public class BasicEquipmentImpl extends AbstractEquipment
{
	public static final int ARMOR_INDEX = 0;
	public static final int RHAND_INDEX = 1;
	public static final int LHAND_INDEX = 2;

	public BasicEquipmentImpl()
	{
		super(3);
		equipmentSlots[0] = new EquipmentSlot("Armor", EquipmentSlotType.ARMOR);
		equipmentSlots[1] = new EquipmentSlot("Right Hand", "RHand", EquipmentSlotType.ARMAMENT);
		equipmentSlots[2] = new EquipmentSlot("Left Hand", "LHand", EquipmentSlotType.ARMAMENT);
	}
	
	@Override
	public List<Item> getWeapons()
	{
		List<Item> weapons = new ArrayList<Item>();
		
		Item item = equipmentSlots[RHAND_INDEX].getItem();
		if (item != null && item.isWeapon())
			weapons.add(item);
		
		item = equipmentSlots[LHAND_INDEX].getItem();
		if (item != null && item.isWeapon())
			weapons.add(item);
		
		return weapons;
	}

	@Override
	public List<Item> getShields()
	{
		List<Item> shields = new ArrayList<Item>();
		
		Item item = equipmentSlots[RHAND_INDEX].getItem();
		if (item != null && item.isShield())
			shields.add(item);
		
		item = equipmentSlots[LHAND_INDEX].getItem();
		if (item != null && item.isShield())
			shields.add(item);
		
		return shields;
	}

	@Override
	public List<Item> getArmor()
	{
		List<Item> armor = new ArrayList<Item>();
		
		Item item = equipmentSlots[ARMOR_INDEX].getItem();
		if (item != null && item.isArmor())
			armor.add(item);
		
		return armor;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof BasicEquipmentImpl)
			return super.equals(obj);
		
		return false;
	}
	
	@Override
	public BasicEquipmentImpl clone()
	{
		return (BasicEquipmentImpl) copyEquipmentSlots(new BasicEquipmentImpl());
	}
}
