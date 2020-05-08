package main.entity.item.equipment;

import java.util.ArrayList;
import java.util.List;

import main.entity.item.Item;

public class MagicEquipmentImpl extends AbstractEquipment
{
	public MagicEquipmentImpl(int totalMagicItemSlots)
	{
		super(totalMagicItemSlots);
		
		for (int i = 0; i < TOTAL_SLOTS; i++)
		{
			equipmentSlots[i] = new EquipmentSlot("Magic Item " + i, "Magic" + i, EquipmentSlotType.MAGIC);
		}
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
	public MagicEquipmentImpl clone()
	{
		return (MagicEquipmentImpl) copyEquipmentSlots(new MagicEquipmentImpl(TOTAL_SLOTS));
	}
}
