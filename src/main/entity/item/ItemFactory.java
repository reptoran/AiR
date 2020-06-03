package main.entity.item;

import java.text.ParseException;

import main.entity.item.equipment.EquipmentSlotType;
import main.entity.save.EntityMap;
import main.presentation.curses.CursesGuiUtil;

public class ItemFactory
{
	private ItemFactory()
	{
	}

	public static Item generateNewItem(ItemType itemType)
	{
		switch (itemType)
		{
		case DEBUG_GEM_DOWN:
			return ItemBuilder.generateItem(itemType).setName("gem of descent").setPlural("gems of descent")
					.setSlot(EquipmentSlotType.MAGIC).setIcon('*').setColor(CursesGuiUtil.COLOR_LIGHT_CYAN).build();
		case DEBUG_GEM_UP:
			return ItemBuilder.generateItem(itemType).setName("gem of ascent").setPlural("gems of ascent")
					.setSlot(EquipmentSlotType.MAGIC).setIcon('*').setColor(CursesGuiUtil.COLOR_LIGHT_MAGENTA).build();
		case THICK_SHIRT:
			return ItemBuilder.generateItem(itemType).setNames("thick shirt").setSlot(EquipmentSlotType.ARMOR).setIcon('[').setColor(10)
					.setAR(1).setCR(80).setDR(0).setHP(10).build();
		case QUILTED_SHIRT:
			return ItemBuilder.generateItem(itemType).setNames("quilted shirt").setSlot(EquipmentSlotType.ARMOR).setIcon('[').setColor(9)
					.setAR(2).setCR(80).setDR(0).setHP(20).build();
		case SOFT_LEATHER_VEST:
			return ItemBuilder.generateItem(itemType).setNames("soft leather vest").setSlot(EquipmentSlotType.ARMOR).setIcon('[')
					.setColor(6).setAR(3).setCR(85).setDR(1).setHP(40).build();
		case HARD_LEATHER_VEST:
			return ItemBuilder.generateItem(itemType).setNames("hard leather vest").setSlot(EquipmentSlotType.ARMOR).setIcon('[')
					.setColor(6).setAR(4).setCR(85).setDR(1).setHP(65).build();
		case RING_MAIL:
			return ItemBuilder.generateItem(itemType).setNames("ring mail").setSlot(EquipmentSlotType.ARMOR).setIcon('[').setColor(8)
					.setAR(5).setCR(90).setDR(2).setHP(125).build();
		case LAMINAR_ARMOR:
			return ItemBuilder.generateItem(itemType).setNames("laminar armor").setSlot(EquipmentSlotType.ARMOR).setIcon('[').setColor(8)
					.setAR(6).setCR(90).setDR(2).setHP(200).build();
		case SCALE_MAIL:
			return ItemBuilder.generateItem(itemType).setNames("scale mail").setSlot(EquipmentSlotType.ARMOR).setIcon('[').setColor(7)
					.setAR(7).setCR(95).setDR(3).setHP(250).build();
		case PLATE_MAIL:
			return ItemBuilder.generateItem(itemType).setNames("plate mail").setSlot(EquipmentSlotType.ARMOR).setIcon('[').setColor(7)
					.setAR(8).setCR(95).setDR(3).setHP(300).build();
		case KNIFE:
			return ItemBuilder.generateItem(itemType).setName("knife").setPlural("knives").setSlot(EquipmentSlotType.ARMAMENT).setIcon('/')
					.setColor(7).setDamage("1d3").setHP(10).setDR(1).build();
		case DAGGER:
			return ItemBuilder.generateItem(itemType).setNames("dagger").setSlot(EquipmentSlotType.ARMAMENT).setIcon('/').setColor(8)
					.setDamage("1d4").setHP(10).setDR(1).build();
		case CLUB:
			return ItemBuilder.generateItem(itemType).setNames("club").setSlot(EquipmentSlotType.ARMAMENT).setIcon('/').setColor(6)
					.setDamage("1d5").setHP(10).setDR(1).build();
		case HEAVY_CLUB:
			return ItemBuilder.generateItem(itemType).setNames("heavy club").setSlot(EquipmentSlotType.ARMAMENT).setIcon('/').setColor(6)
					.setDamage("2d4").setHP(25).setDR(1).build();
		case HAMMER:
			return ItemBuilder.generateItem(itemType).setNames("hammer").setSlot(EquipmentSlotType.ARMAMENT).setIcon('/').setColor(8)
					.setDamage("2d3").setHP(15).setDR(1).build();
		case MACE:
			return ItemBuilder.generateItem(itemType).setNames("mace").setSlot(EquipmentSlotType.ARMAMENT).setIcon('/').setColor(8)
					.setDamage("3d2").setHP(15).setDR(1).build();
		case SHORT_SWORD:
			return ItemBuilder.generateItem(itemType).setNames("short sword").setSlot(EquipmentSlotType.ARMAMENT).setIcon('/').setColor(8)
					.setDamage("1d7").setHP(20).setDR(1).build();
		case BUCKLER:
			return ItemBuilder.generateItem(itemType).setNames("buckler").setSlot(EquipmentSlotType.ARMAMENT).setIcon('(').setColor(8)
					.setCR(30).setAR(100).setHP(20).setDR(1).build();
		case SMALL_SHIELD:
			return ItemBuilder.generateItem(itemType).setNames("small shield").setSlot(EquipmentSlotType.ARMAMENT).setIcon('(').setColor(8)
					.setCR(40).setAR(100).setHP(45).setDR(1).build();
		case MEDIUM_SHIELD:
			return ItemBuilder.generateItem(itemType).setNames("medium shield").setSlot(EquipmentSlotType.ARMAMENT).setIcon('(').setColor(8)
					.setCR(50).setAR(100).setHP(80).setDR(1).build();
		case LARGE_SHIELD:
			return ItemBuilder.generateItem(itemType).setNames("large shield").setSlot(EquipmentSlotType.ARMAMENT).setIcon('(').setColor(8)
					.setCR(65).setAR(100).setHP(125).setDR(2).build();
		case TOWER_SHIELD:
			return ItemBuilder.generateItem(itemType).setNames("tower shield").setSlot(EquipmentSlotType.ARMAMENT).setIcon('(').setColor(8)
					.setCR(75).setAR(100).setHP(200).setDR(2).build();
		case MEDICINAL_FUNGUS:
			return ItemBuilder.generateItem(itemType).setName("medicinal fungus").setPlural("medicinal fungi")
					.setSlot(EquipmentSlotType.MATERIAL).setIcon(',').setColor(4).build();
		case HEALING_SALVE:
			return ItemBuilder.generateItem(itemType).setName("jar of healing salve").setPlural("jars of healing salve")
					.setSlot(EquipmentSlotType.MAGIC).setIcon('!').setColor(CursesGuiUtil.COLOR_LIGHT_RED).build();
		case VIRTUAL_ITEM:
			return ItemBuilder.generateItem(itemType).setDR(1000).build();
		case NO_TYPE: // falls through
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
