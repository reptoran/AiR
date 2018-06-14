package main.entity.zone.predefined;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.entity.actor.Actor;
import main.entity.actor.ActorFactory;
import main.entity.actor.ActorType;
import main.entity.feature.FeatureFactory;
import main.entity.feature.FeatureType;
import main.entity.item.Item;
import main.entity.item.ItemFactory;
import main.entity.item.ItemType;
import main.entity.tile.Tile;
import main.entity.tile.TileFactory;
import main.entity.tile.TileType;
import main.entity.zone.Zone;
import main.entity.zone.ZoneAttribute;
import main.entity.zone.ZoneKey;
import main.entity.zone.ZoneType;
import main.presentation.Logger;

public class PredefinedZoneBuilder
{
	private Map<ZoneAttribute, String> zoneAttribute = new HashMap<ZoneAttribute, String>(); 
	private Map<Character, Tile> tileMap = new HashMap<Character, Tile>();
	private Map<Point, Actor> actorMap = new HashMap<Point, Actor>();
	private Map<Point, Item> itemMap = new HashMap<Point, Item>();
	private List<String> mapLines = new ArrayList<String>();
	
	private int width = 0;
	private int height = 0;
	
	private String name;
	
	public PredefinedZoneBuilder(int zoneIndex)
	{
		name = "PERMZONE" + zoneIndex;
	}

	public PredefinedZone build()
	{
		PredefinedZone zone = new PredefinedZone(ZoneType.PERMANENT, name, height, width, -1, true, false);	//TODO: assuming all predefined levels are underground; depth will have to be set later
		
		setTiles(zone);
		addActors(zone);
		addItems(zone);
		
		zone.setZoneAttributes(zoneAttribute);
		
		return zone;
	}

	private void setTiles(Zone zone)
	{
		for (int i = 0; i < mapLines.size(); i++)
		{
			String mapLine = mapLines.get(i);
			
			for (int j = 0; j < mapLine.length(); j++)
			{
				Character key = mapLine.charAt(j);
				Tile tile = null;
				
				try {
					tile = tileMap.get(key).clone();
				} catch (NullPointerException npe) {
					Logger.error("Data file does not contain tile definition for character [" + key + "].");
				}
				
				Point point = new Point(i, j);
				zone.setTile(point, tile);
				
				addZoneKeys(zone, tile, point);
			}
		}
	}

	private void addZoneKeys(Zone zone, Tile tile, Point point)
	{
		// Anything digging into this level will have to find the matching zoneKey to know where to go, then update its own appropriately 
		if (TileType.STAIRS_UP.equals(tile.getType()))
			zone.addZoneKey(point, new ZoneKey(ZoneType.UNMAPPED_UP));
		
		if (TileType.STAIRS_DOWN.equals(tile.getType()))
			zone.addZoneKey(point, new ZoneKey(ZoneType.UNMAPPED_DOWN));
	}

	private void addActors(Zone zone)
	{
		Set<Point> actorKeys = actorMap.keySet();
		for (Point point : actorKeys)
		{
			Actor actor = actorMap.get(point);
			zone.addActor(actor, point);
		}
	}

	private void addItems(Zone zone)
	{
		Set<Point> itemKeys = itemMap.keySet();
		for (Point point : itemKeys)
		{
			Item item = itemMap.get(point);
			zone.setItem(item, point);
		}
	}

	public void defineMapLine(String line)
	{
		mapLines.add(line);
		height = mapLines.size();
		
		if (line.length() > width)
			width = line.length();
	}

	public void defineTile(char tileIcon, TileType tileType)
	{
		Tile tile = TileFactory.generateNewTile(tileType);
		tileMap.put(tileIcon, tile);
	}

	public void defineTile(char tileIcon, TileType tileType, FeatureType featureType)
	{
		Tile tile = TileFactory.generateNewTile(tileType);
		tile.setFeatureHere(FeatureFactory.generateNewFeature(featureType));
		tileMap.put(tileIcon, tile);
	}

	public void defineActor(int row, int col, ActorType actorType)
	{
		Point point = new Point(row, col);
		actorMap.put(point, ActorFactory.generateNewActor(actorType));
	}

	public void defineItem(int row, int col, ItemType itemType)
	{
		Point point = new Point(row, col);
		itemMap.put(point, ItemFactory.generateNewItem(itemType));
	}

	public void defineItem(int row, int col, ItemType itemType, int amount)
	{
		Point point = new Point(row, col);
		Item item = ItemFactory.generateNewItem(itemType);
		item.setAmount(amount);
		itemMap.put(point, item);
	}

	public void defineAttribute(String key, String value)
	{
		zoneAttribute.put(ZoneAttribute.valueOf(key), value);
	}
}
