package main.entity.feature;

import java.text.ParseException;

import main.entity.save.EntityMap;

public class FeatureFactory
{
	private FeatureFactory() {}
	
	public static Feature generateNewFeature(FeatureType featureType)
	{
		switch (featureType)
		{
			case TREE:
				return new Feature(featureType, "tree", 'T', 2, true, true, 0, "There is a tree in the way!");
			case WALL:
				return new Feature(featureType, "wall", '#', 8, true, true, 0, "You run into the wall.");
			case NO_TYPE:	//falls through
			default:
				throw new IllegalArgumentException("Unrecognized Feature Type: " + featureType);
		}
	}
	
	public static Feature loadAndMapFeatureFromSaveString(String saveString)
	{
		Feature feature = null;
		
		try
		{
			feature = new Feature();
			String key = feature.loadFromText(saveString);
			EntityMap.put(key, feature);
		} catch (ParseException e)
		{
			System.out.println("FeatureFactory - " + e.getMessage());
		}
		
		return feature;
	}
}
