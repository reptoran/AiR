package main.entity.item;

import java.text.ParseException;

import main.entity.save.EntityMap;

public class ItemFactory
{
	private ItemFactory() {}
	
	public static Item generateNewItem(ItemType itemType)
	{
		switch (itemType)
		{
			case THICK_SHIRT:
				return ItemBuilder.generateItem(itemType).setName("thick shirt").setPlural("thick shirts").setIcon('[').setColor(13).setCR(100).setAR(1).setDR(0).build();
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
