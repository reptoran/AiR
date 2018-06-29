package main.entity.zone.generator;

import java.util.ArrayList;
import java.util.List;

import main.entity.item.Item;
import main.entity.item.ItemFactory;
import main.entity.item.ItemType;
import main.logic.RPGlib;
import main.presentation.Logger;

public class ZoneItemGenerator
{
	private static List<List<ItemType>> itemsForLevels = null;
	
	public static Item generateItem(int level)
	{
		if (itemsForLevels == null)
			populateItemsForLevels();
		
		List<ItemType> itemsForCurrentLevel = itemsForLevels.get(level);
		
		int totalPossibleItemsToGenerate = itemsForCurrentLevel.size();
		
		if (totalPossibleItemsToGenerate == 0)
			return null;
		
		int indexOfItemToGenerate = RPGlib.Randint(0, totalPossibleItemsToGenerate - 1);
		
		return ItemFactory.generateNewItem(itemsForCurrentLevel.get(indexOfItemToGenerate));
	}

	private static void populateItemsForLevels()
	{
		itemsForLevels = new ArrayList<List<ItemType>>();
		
		for (int i = 0; i <= 100; i++)
		{
			itemsForLevels.add(new ArrayList<ItemType>());
		}
		
		for (ItemType itemType : ItemType.values())
		{
			for (int i = itemType.getMinDepth(); i <= itemType.getMaxDepth(); i++)
			{
				try
				{
					for (int j = 1; j <= itemType.getRarity().getInstances(); j++)
						itemsForLevels.get(i).add(itemType);
				} catch (ArrayIndexOutOfBoundsException aioobe) 
				{
					Logger.warn("Item type " + itemType + " could not be included to be generated on level " + i + ".");
					break;
				}
			}
		}
	}
}
