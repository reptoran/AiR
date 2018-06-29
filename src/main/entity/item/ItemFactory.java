package main.entity.item;

import java.text.ParseException;

import main.entity.item.equipment.EquipmentSlotType;
import main.entity.save.EntityMap;

public class ItemFactory
{
	private ItemFactory() {}
	
	public static Item generateNewItem(ItemType itemType)
	{
		switch (itemType)
		{
			case THICK_SHIRT:
				return ItemBuilder.generateItem(itemType).setNames("thick shirt").setSlot(EquipmentSlotType.ARMOR).setIcon('[').setColor(10).setAR(1).setDR(0).build();
			case QUILTED_SHIRT:
				return ItemBuilder.generateItem(itemType).setNames("quilted shirt").setSlot(EquipmentSlotType.ARMOR).setIcon('[').setColor(9).setAR(2).setDR(0).build();
			case SOFT_LEATHER_VEST:
				return ItemBuilder.generateItem(itemType).setNames("soft leather vest").setSlot(EquipmentSlotType.ARMOR).setIcon('[').setColor(6).setAR(3).setDR(1).build();
			case HARD_LEATHER_VEST:
				return ItemBuilder.generateItem(itemType).setNames("hard leather vest").setSlot(EquipmentSlotType.ARMOR).setIcon('[').setColor(6).setAR(4).setDR(1).build();
			case RING_MAIL:
				return ItemBuilder.generateItem(itemType).setNames("ring mail").setSlot(EquipmentSlotType.ARMOR).setIcon('[').setColor(8).setAR(5).setDR(2).build();
			case LAMINAR_ARMOR:
				return ItemBuilder.generateItem(itemType).setNames("laminar armor").setSlot(EquipmentSlotType.ARMOR).setIcon('[').setColor(8).setAR(6).setDR(2).build();
			case SCALE_MAIL:
				return ItemBuilder.generateItem(itemType).setNames("scale mail").setSlot(EquipmentSlotType.ARMOR).setIcon('[').setColor(7).setAR(7).setDR(3).build();
			case PLATE_MAIL:
				return ItemBuilder.generateItem(itemType).setNames("plate mail").setSlot(EquipmentSlotType.ARMOR).setIcon('[').setColor(7).setAR(8).setDR(3).build();
			case DAGGER:
				return ItemBuilder.generateItem(itemType).setNames("dagger").setSlot(EquipmentSlotType.WEAPON).setIcon('/').setColor(8).setDamage("1d4").build();
			case CLUB:
				return ItemBuilder.generateItem(itemType).setNames("club").setSlot(EquipmentSlotType.WEAPON).setIcon('/').setColor(6).setDamage("1d5").build();
			case HAMMER:
				return ItemBuilder.generateItem(itemType).setNames("hammer").setSlot(EquipmentSlotType.WEAPON).setIcon('/').setColor(8).setDamage("2d3").build();
			case MACE:
				return ItemBuilder.generateItem(itemType).setNames("mace").setSlot(EquipmentSlotType.WEAPON).setIcon('/').setColor(8).setDamage("3d2").build();
			case SHORT_SWORD:
				return ItemBuilder.generateItem(itemType).setNames("short sword").setSlot(EquipmentSlotType.WEAPON).setIcon('/').setColor(8).setDamage("1d7").build();
			case NO_TYPE:	//falls through
			default:
				throw new IllegalArgumentException("No item definition for item type: " + itemType);
		}
	}
	
	public static Item loadAndMapItemFromSaveString(String saveString)
	{
		Item item = null;
		
		try
		{
			item = new Item(ItemType.NO_TYPE);
			String key = item.loadFromText(saveString);
			EntityMap.put(key, item);
		} catch (ParseException e)
		{
			System.out.println("ItemFactory - " + e.getMessage());
		}
		
		return item;
	}
}
