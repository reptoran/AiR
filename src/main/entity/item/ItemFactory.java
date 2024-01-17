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
					.setSlot(EquipmentSlotType.MAGIC).setIcon('*').setColor(CursesGuiScreen.COLOR_LIGHT_CYAN).setMaterial(ItemMaterial.CRYSTAL).build();
		case DEBUG_GEM_UP:
			return ItemBuilder.generateItem(itemType).setName("gem of ascent").setPlural("gems of ascent").setSlot(EquipmentSlotType.MAGIC)
					.setIcon('*').setColor(CursesGuiScreen.COLOR_LIGHT_MAGENTA).setMaterial(ItemMaterial.CRYSTAL).build();
		case THICK_SHIRT:
			return ItemBuilder.generateArmor(itemType).setNames("thick shirt").setColor(10).setAR(1).setMaterial(ItemMaterial.NATURAL).setHP(10).build();
		case QUILTED_SHIRT:
			return ItemBuilder.generateArmor(itemType).setNames("quilted shirt").setColor(9).setAR(2).setMaterial(ItemMaterial.NATURAL).setHP(20).build();
		case SOFT_LEATHER_VEST:
			return ItemBuilder.generateArmor(itemType).setNames("soft leather vest").setColor(6).setAR(3).setMaterial(ItemMaterial.NATURAL).setHP(40)
					.build();
		case HARD_LEATHER_VEST:
			return ItemBuilder.generateArmor(itemType).setNames("hard leather vest").setColor(6).setAR(4).setMaterial(ItemMaterial.NATURAL).setHP(65)
					.build();
		case RING_MAIL:
			return ItemBuilder.generateArmor(itemType).setNames("ring mail").setColor(8).setAR(5).setMaterial(ItemMaterial.METAL).setHP(125).build();
		case LAMINAR_ARMOR:
			return ItemBuilder.generateArmor(itemType).setNames("laminar armor").setColor(8).setAR(6).setMaterial(ItemMaterial.METAL).setHP(200).build();
		case SCALE_MAIL:
			return ItemBuilder.generateArmor(itemType).setNames("scale mail").setColor(7).setAR(7).setMaterial(ItemMaterial.METAL).setHP(250).build();
		case PLATE_MAIL:
			return ItemBuilder.generateArmor(itemType).setNames("plate mail").setColor(7).setAR(8).setMaterial(ItemMaterial.METAL).setHP(300).build();
		case KNIFE:
			return ItemBuilder.generateWeapon(itemType, "1d3").setName("knife").setPlural("knives").setColor(7).setHP(10).setMaterial(ItemMaterial.METAL).addTrait(ItemTrait.SHARP).build();
		case AXE:
			return ItemBuilder.generateWeapon(itemType, "2d4").setNames("axe").setColor(8).setHP(20).setMaterial(ItemMaterial.METAL).addTrait(ItemTrait.SHARP).build();
		case SWORD:
			return ItemBuilder.generateWeapon(itemType, "1d8").setNames("sword").setColor(8).setHP(20).setMaterial(ItemMaterial.METAL).addTrait(ItemTrait.SHARP).build();
		case CLUB:
			return ItemBuilder.generateWeapon(itemType, "1d5").setNames("club").setColor(6).setHP(10).setMaterial(ItemMaterial.NATURAL).addTrait(ItemTrait.BLUNT).build();
		case HAMMER:
			return ItemBuilder.generateWeapon(itemType, "2d3").setNames("hammer").setColor(8).setHP(15).setMaterial(ItemMaterial.METAL).addTrait(ItemTrait.BLUNT).build();
		case HEAVY_CLUB:
			return ItemBuilder.generateWeapon(itemType, "2d5").setNames("heavy club").setColor(6).setHP(25).setMaterial(ItemMaterial.METAL).addTrait(ItemTrait.BLUNT).build();
		case BUCKLER:
			return ItemBuilder.generateShield(itemType).setNames("buckler").setColor(8).setCR(50).setAR(1).setHP(15).setMaterial(ItemMaterial.METAL).build();
		case SMALL_SHIELD:
			return ItemBuilder.generateShield(itemType).setNames("small shield").setColor(8).setCR(60).setAR(2).setHP(30).setMaterial(ItemMaterial.METAL)
					.build();
		case MEDIUM_SHIELD:
			return ItemBuilder.generateShield(itemType).setNames("medium shield").setColor(8).setCR(70).setAR(3).setHP(50).setMaterial(ItemMaterial.METAL)
					.build();
		case LARGE_SHIELD:
			return ItemBuilder.generateShield(itemType).setNames("large shield").setColor(8).setCR(80).setAR(4).setHP(75).setMaterial(ItemMaterial.METAL)
					.build();
		case TOWER_SHIELD:
			return ItemBuilder.generateShield(itemType).setNames("tower shield").setColor(8).setCR(90).setAR(5).setHP(100).setMaterial(ItemMaterial.METAL)
					.build();
		case MEDICINAL_FUNGUS:
			return ItemBuilder.generateMaterial(itemType).setName("medicinal fungus").setPlural("medicinal fungi").setIcon(',').setColor(4)
					.setMaterial(ItemMaterial.NATURAL).build();
		case METAL_SHARD:
			return ItemBuilder.generateMaterial(itemType).setName("metal shard").setPlural("metal shards").setIcon('\'').setColor(7)
					.setMaterial(ItemMaterial.METAL).build();
		case VALUABLE:
			return ItemBuilder.generateMaterial(itemType).setName("valuable").setPlural("valuables").setIcon('`').setColor(14)
					.setMaterial(ItemMaterial.CRYSTAL).build();
		case HEALING_SALVE:
			return ItemBuilder.generateMagicItem(itemType).setName("jar of healing salve").setPlural("jars of healing salve")
					.setIcon('*').setColor(CursesGuiScreen.COLOR_LIGHT_RED).setMaterial(ItemMaterial.NATURAL).build();
		case VIRTUAL_ITEM:
			return ItemBuilder.generateItem(itemType).setMaterial(ItemMaterial.VIRTUAL).build();
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
