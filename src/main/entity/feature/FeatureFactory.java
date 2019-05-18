package main.entity.feature;

import java.text.ParseException;

import main.entity.save.EntityMap;

public class FeatureFactory
{
	private FeatureFactory()
	{
	}

	public static Feature generateNewFeature(FeatureType featureType)
	{
		switch (featureType)
		{
		case TREE:
			return FeatureBuilder.generateFeature(featureType).setName("tree").setIcon('T').setColor(2).setObstructsMotion(true)
					.setObstructsSight(true).setBlockedMessage("There is a tree in the way!").build();
		case WALL:
			return FeatureBuilder.generateFeature(featureType).setName("wall").setIcon('#').setColor(8).setObstructsMotion(true)
					.setObstructsSight(true).setBlockedMessage("You run into the wall.").build();
		case BANNER:
			return FeatureBuilder.generateFeature(featureType).setName("banner").setIcon('~').setColor(13).setObstructsSight(true).build();
		case STRUCTURE:
			return FeatureBuilder.generateFeature(featureType).setName("structure").setIcon('#').setColor(7).setObstructsMotion(true)
					.setObstructsSight(true).setBlockedMessage("You run into the wall.").build();
		case TENT:
			return FeatureBuilder.generateFeature(featureType).setName("hide tent").setIcon('#').setColor(6).setObstructsMotion(true)
					.setObstructsSight(true).setBlockedMessage("You run into the tent.").build();
		case NO_TYPE: // falls through
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
