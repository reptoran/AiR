package main.entity.save;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.entity.actor.Actor;
import main.entity.feature.Feature;
import main.entity.item.Item;
import main.entity.tile.Tile;
import main.entity.world.Overworld;
import main.entity.world.WorldTile;
import main.entity.zone.Zone;
import main.presentation.Logger;

public class EntityMap
{
	private static Map<String, Actor> knownActors = new HashMap<String, Actor>();
	private static Map<String, Feature> knownFeatures = new HashMap<String, Feature>();
	private static Map<String, Tile> knownTiles = new HashMap<String, Tile>();
	private static Map<String, Item> knownItems = new HashMap<String, Item>();
	private static Map<String, Zone> knownZones = new HashMap<String, Zone>();
	private static Map<String, WorldTile> knownWorldTiles = new HashMap<String, WorldTile>();
	private static Map<String, Overworld> knownOverworlds = new HashMap<String, Overworld>();
	
	private static Map<String, String> entityComplexIdFinder = new HashMap<String, String>();
	private static Map<String, String> entitySimpleIdFinder = new HashMap<String, String>();
	
	public static String put(String key, Object value)
	{
		//System.out.println("EntityMap - Putting object with key " + key);

		if (value == null)
		{
			Logger.warn("EntityMap - Warning! Object is null!");
		}
		
		String entityType = key.toUpperCase().substring(0, 1);
		String simpleKey = "";
		
		if (entityType.equals("A"))
		{
			knownActors.put(key, (Actor)value);
			simpleKey = entityType + String.valueOf(knownActors.size());
		}
		if (entityType.equals("F"))
		{
			knownFeatures.put(key, (Feature)value);
			simpleKey = entityType + String.valueOf(knownFeatures.size());
		}
		if (entityType.equals("T"))
		{
			knownTiles.put(key, (Tile)value);
			simpleKey = entityType + String.valueOf(knownTiles.size());
		}
		if (entityType.equals("I"))
		{
			knownItems.put(key, (Item)value);
			simpleKey = entityType + String.valueOf(knownItems.size());
		}
		if (entityType.equals("Z"))
		{
			knownZones.put(key, (Zone)value);
			simpleKey = entityType + String.valueOf(knownZones.size());
		}
		if (entityType.equals("W"))
		{
			knownWorldTiles.put(key, (WorldTile)value);
			simpleKey = entityType + String.valueOf(knownWorldTiles.size());
		}
		if (entityType.equals("O"))
		{
			knownOverworlds.put(key, (Overworld)value);
			simpleKey = entityType + String.valueOf(knownOverworlds.size());
		}
			
		entityComplexIdFinder.put(simpleKey, key);
		entitySimpleIdFinder.put(key, simpleKey);
			
		return simpleKey;
	}
	
	public static Actor getActor(String key)
	{
		return knownActors.get(key);
	}
	
	public static Feature getFeature(String key)
	{
		return knownFeatures.get(key);
	}
	
	public static Tile getTile(String key)
	{
		return knownTiles.get(key);
	}
	
	public static Item getItem(String key)
	{
		return knownItems.get(key);
	}
	
	public static Zone getZone(String key)
	{
		return knownZones.get(key);
	}
	
	public static WorldTile getWorldTile(String key)
	{
		return knownWorldTiles.get(key);
	}
	
	public static Overworld getOverworld(String key)
	{
		return knownOverworlds.get(key);
	}
	
	public static List<String> getActorKeys()
	{
		List<String> toRet = new ArrayList<String>();
		toRet.addAll(knownActors.keySet());
		return toRet;
	}
	
	public static List<String> getFeatureKeys()
	{
		List<String> toRet = new ArrayList<String>();
		toRet.addAll(knownFeatures.keySet());
		return toRet;
	}
	
	public static List<String> getTileKeys()
	{
		List<String> toRet = new ArrayList<String>();
		toRet.addAll(knownTiles.keySet());
		return toRet;
	}
	
	public static List<String> getItemKeys()
	{
		List<String> toRet = new ArrayList<String>();
		toRet.addAll(knownItems.keySet());
		return toRet;
	}
	
	public static List<String> getZoneKeys()
	{
		List<String> toRet = new ArrayList<String>();
		toRet.addAll(knownZones.keySet());
		return toRet;
	}
	
	public static List<String> getWorldTileKeys()
	{
		List<String> toRet = new ArrayList<String>();
		toRet.addAll(knownWorldTiles.keySet());
		return toRet;
	}
	
	public static List<String> getOverworldKeys()
	{
		List<String> toRet = new ArrayList<String>();
		toRet.addAll(knownOverworlds.keySet());
		return toRet;
	}
	
	public static String getSimpleKey(String complexKey)
	{
		return entitySimpleIdFinder.get(complexKey);
	}
	
	public static String getComplexKey(String simpleKey)
	{
		return entityComplexIdFinder.get(simpleKey);
	}
	
	public static void clearMappings()
	{
		knownActors.clear();
		knownFeatures.clear();
		knownTiles.clear();
		knownItems.clear();
		knownZones.clear();
		knownWorldTiles.clear();
		knownOverworlds.clear();
		
		entitySimpleIdFinder.clear();
		entityComplexIdFinder.clear();
	}
}
