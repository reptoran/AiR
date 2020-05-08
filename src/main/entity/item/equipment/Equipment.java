package main.entity.item.equipment;

import java.util.List;

import main.entity.item.Item;
import main.entity.item.ItemType;

public interface Equipment
{
	public Item getItem(int slotIndex);
	public Item removeItem(int slotIndex);
	public void equipItem(Item item, int slotIndex);
	public int getIndexOfItem(Item item);
	
	public boolean isEmpty();
	public boolean hasEmptySlotAvailable(EquipmentSlotType slotType);
	public int getIndexOfFirstSlotAvailable(EquipmentSlotType slotType);
	
	public int getTotalItemsOfType(ItemType type);
	public Item getFirstItemOfType(ItemType type);
	
	public List<Item> getWeapons();
	public List<Item> getShields();
	public List<Item> getArmor();
	
	public List<EquipmentSlot> getEquipmentSlots();
	
	public Equipment clone();
}
