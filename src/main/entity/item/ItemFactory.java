package main.entity.item;

import java.text.ParseException;

import main.entity.item.equipment.EquipmentSlotType;
import main.entity.save.EntityMap;
import main.presentation.curses.CursesGuiScreen;

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
					.setSlot(EquipmentSlotType.MAGIC).setIcon('*').setColor(CursesGuiScreen.COLOR_LIGHT_CYAN).build();
		case DEBUG_GEM_UP:
			return ItemBuilder.generateItem(itemType).setName("gem of ascent").setPlural("gems of ascent").setSlot(EquipmentSlotType.MAGIC)
					.setIcon('*').setColor(CursesGuiScreen.COLOR_LIGHT_MAGENTA).build();
		case THICK_SHIRT:
			return ItemBuilder.generateArmor(itemType).setNames("thick shirt").setColor(10).setAR(1).setCR(80).setDR(0).setHP(10).build();
		case QUILTED_SHIRT:
			return ItemBuilder.generateArmor(itemType).setNames("quilted shirt").setColor(9).setAR(2).setCR(80).setDR(0).setHP(20).build();
		case SOFT_LEATHER_VEST:
			return ItemBuilder.generateArmor(itemType).setNames("soft leather vest").setColor(6).setAR(3).setCR(85).setDR(1).setHP(40)
					.build();
		case HARD_LEATHER_VEST:
			return ItemBuilder.generateArmor(itemType).setNames("hard leather vest").setColor(6).setAR(4).setCR(85).setDR(1).setHP(65)
					.build();
		case RING_MAIL:
			return ItemBuilder.generateArmor(itemType).setNames("ring mail").setColor(8).setAR(5).setCR(90).setDR(2).setHP(125).build();
		case LAMINAR_ARMOR:
			return ItemBuilder.generateArmor(itemType).setNames("laminar armor").setColor(8).setAR(6).setCR(90).setDR(2).setHP(200).build();
		case SCALE_MAIL:
			return ItemBuilder.generateArmor(itemType).setNames("scale mail").setColor(7).setAR(7).setCR(95).setDR(3).setHP(250).build();
		case PLATE_MAIL:
			return ItemBuilder.generateArmor(itemType).setNames("plate mail").setColor(7).setAR(8).setCR(95).setDR(3).setHP(300).build();
		case KNIFE:
			return ItemBuilder.generateWeapon(itemType, "1d3").setName("knife").setPlural("knives").setColor(7).setHP(10).setDR(1).build();
		case AXE:
			return ItemBuilder.generateWeapon(itemType, "2d4").setNames("axe").setColor(8).setHP(20).setDR(1).build();
		case SWORD:
			return ItemBuilder.generateWeapon(itemType, "1d9").setNames("sword").setColor(8).setHP(20).setDR(1).build();
		case CLUB:
			return ItemBuilder.generateWeapon(itemType, "1d5").setNames("club").setColor(6).setHP(10).setDR(1).build();
		case HAMMER:
			return ItemBuilder.generateWeapon(itemType, "2d3").setNames("hammer").setColor(8).setHP(15).setDR(1).build();
		case HEAVY_CLUB:
			return ItemBuilder.generateWeapon(itemType, "2d5").setNames("heavy club").setColor(6).setHP(25).setDR(1).build();
		case BUCKLER:
			return ItemBuilder.generateShield(itemType).setNames("buckler").setColor(8).setCR(50).setAR(100).setHP(15).setDR(1).build();
		case SMALL_SHIELD:
			return ItemBuilder.generateShield(itemType).setNames("small shield").setColor(8).setCR(60).setAR(100).setHP(30).setDR(1)
					.build();
		case MEDIUM_SHIELD:
			return ItemBuilder.generateShield(itemType).setNames("medium shield").setColor(8).setCR(70).setAR(100).setHP(50).setDR(1)
					.build();
		case LARGE_SHIELD:
			return ItemBuilder.generateShield(itemType).setNames("large shield").setColor(8).setCR(80).setAR(100).setHP(75).setDR(2)
					.build();
		case TOWER_SHIELD:
			return ItemBuilder.generateShield(itemType).setNames("tower shield").setColor(8).setCR(90).setAR(100).setHP(100).setDR(2)
					.build();
		case MEDICINAL_FUNGUS:
			return ItemBuilder.generateMaterial(itemType).setName("medicinal fungus").setPlural("medicinal fungi").setIcon(',').setColor(4)
					.build();
		case METAL_SHARD:
			return ItemBuilder.generateMaterial(itemType).setName("metal shard").setPlural("metal shards").setIcon('\'').setColor(7)
					.build();
		case HEALING_SALVE:
			return ItemBuilder.generateItem(itemType).setName("jar of healing salve").setPlural("jars of healing salve")
					.setSlot(EquipmentSlotType.MAGIC).setIcon('!').setColor(CursesGuiScreen.COLOR_LIGHT_RED).build();
		case VIRTUAL_ITEM:
			return ItemBuilder.generateItem(itemType).setDR(1000).build();
		case NO_TYPE: // these all fall through
		case ANY_ITEM:
		case SOURCE_ITEM:
		case TARGET_ITEM:
		case UPGRADED_ITEM:
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
