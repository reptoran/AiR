package main.entity.item.equipment;

import java.util.List;

import main.entity.item.Item;

public interface Equipment
{
	public Item getItem(int slotIndex);
	public Item removeItem(int slotIndex);
	public void equipItem(Item item, int slotIndex);
	public int getIndexOfItem(Item item);
	
	public boolean isEmpty();
	
	public List<Item> getWeapons();
	public List<Item> getShields();
	public List<Item> getArmor();
	
	public List<EquipmentSlot> getEquipmentSlots();
	
	public Equipment clone();
}
