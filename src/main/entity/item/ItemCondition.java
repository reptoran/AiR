package main.entity.item;

import java.util.ArrayList;
import java.util.List;

import main.presentation.Logger;

public enum ItemCondition
{
	GREAT("Great", .75),
	GOOD("Good", .5),
	FAIR("Fair", .25),
	POOR("Poor", 0),
	DESTROYED("Destroyed", -100);	//TODO: this is an exception case and should really never be applied
	
	private String label;
	private double lowerBound;
	
	private static List<ItemCondition> orderedValues = null;
	
	private ItemCondition(String label, double lowerBound)
	{
		this.label = label;
		this.lowerBound = lowerBound;
	}
	
	//TODO: this whole method is pretty garbage, but it will only ever be called once, and it should work
	private static void defineOrderedValues()
	{
		if (orderedValues != null)
			return;
		
		ItemCondition[] unorderedValues = values();
		orderedValues = new ArrayList<ItemCondition>();
		
		for (ItemCondition itemCondition : unorderedValues)
		{
			boolean added = false;
			
			for (int i = 0; i < orderedValues.size(); i++)
			{
				ItemCondition currentValue = orderedValues.get(i);
				if (itemCondition.lowerBound > currentValue.lowerBound)
				{
					orderedValues.add(i, itemCondition);
					added = true;
					break;
				}
			}
			
			if (!added)
				orderedValues.add(itemCondition);
		}
	}
	
	public static String getLabel(double condition)
	{
		defineOrderedValues();
		
		for (ItemCondition itemCondition : orderedValues)
		{
			if (condition > itemCondition.lowerBound)
				return itemCondition.label;
		}
		
		Logger.warn("No condition label found for condition value " + condition);
		return "N/A";
	}
	
	public static ItemCondition getCondition(double condition)
	{
		return valueOf(getLabel(condition).toUpperCase());
	}
}
