package main.entity.zone;

import java.awt.Point;
import java.text.ParseException;

import main.entity.actor.Actor;
import main.entity.item.Item;
import main.entity.save.EntityMap;
import main.entity.tile.Tile;
import main.entity.world.WorldTile;
import main.entity.zone.generator.AbstractGenerator;
import main.entity.zone.generator.DesertGenerator;
import main.entity.zone.generator.ForestGenerator;
import main.entity.zone.generator.LabyrinthGenerator;
import main.entity.zone.generator.OceanGenerator;
import main.entity.zone.generator.PersistentCaveGenerator;
import main.entity.zone.generator.PlainsGenerator;
import main.entity.zone.generator.PredefinedZoneGenerator;
import main.entity.zone.generator.ZoneItemGenerator;
import main.entity.zone.generator.ZonePopulationGenerator;
import main.logic.RPGlib;

public class ZoneFactory
{
	private static ZoneFactory instance = null;	//singleton
	
	private static final int MINIMUM_ACTORS_PER_ZONE = 8;
	private static final int MAXIMUM_ACTORS_PER_ZONE = 15;
	private static final int ITEMS_PER_ZONE = 5;
	
	private AbstractGenerator zoneGenerator;
	private int generatedMaps = 0;
	
	private ZoneFactory(){}
	
	public static ZoneFactory getInstance()
	{
		if (instance == null)
		{
			instance = new ZoneFactory();
		}
		
		return instance;
	}
	
	public static void setGeneratedMapCount(int count)
	{
		getInstance().generatedMaps = count;
	}
	
	public static int getGeneratedMapCount()
	{
		return instance.generatedMaps;
	}

	public Zone generateNewZone(WorldTile worldTile)
	{
		ZoneKey key = new ZoneKey(worldTile.getType());
		Zone zone = generateNewZone(key);
		worldTile.setZoneId(zone.getName());	//this SHOULD work because we're passing it by reference
		
		return zone;
	}
	
	public Zone generateNewZone(ZoneKey zoneKey)
	{
		return generateNewZone(zoneKey, true, null);
	}
	
	public Zone generateNewZone(ZoneKey zoneKey, boolean goingDown, Zone oldZone)
	{
		setGenerator(zoneKey.getType());
		
		if (zoneKey.getType() != ZoneType.PERMANENT)
			zoneKey.generateId(generatedMaps++);
		
		Zone zone = zoneGenerator.generateZone(zoneKey, goingDown, oldZone);
		
		//TODO: perhaps put this somewhere else
		if (zone.getType() != ZoneType.PERMANENT)
			populateZone(zone);
		
		return zone;
	}
	
	private void populateZone(Zone zone)
	{
		int totalActors = RPGlib.randInt(MINIMUM_ACTORS_PER_ZONE, MAXIMUM_ACTORS_PER_ZONE);
		
		for (int i = 0; i < totalActors; i++)
		{
			Point actorLocation = findOpenTile(zone, true, false);
			Actor actor = ZonePopulationGenerator.generateMonster(zone.getDepth());
			
			if (actor == null)
				return;
			
			zone.addActor(actor, actorLocation);
		}
		
		for (int i = 0; i < ITEMS_PER_ZONE; i++)
		{
			Point itemLocation = findOpenTile(zone, false, true);
			Item item = ZoneItemGenerator.generateItem(zone.getDepth());
			
			if (item == null)
				return;
			
			zone.getTile(itemLocation).setItemHere(item);
		}
	}
	
	private Point findOpenTile(Zone zone, boolean noActorAllowed, boolean noItemAllowed)
	{
		Point openTile = null;
		
		while (openTile == null)
		{
			int x = RPGlib.randInt(0, zone.getHeight() - 1);
			int y = RPGlib.randInt(0, zone.getWidth() - 1);
			
			Tile tile = zone.getTile(x, y);
			boolean valid = true;
			
			//don't put an item on an up or down staircase
			for (ZoneKey zoneKey : zone.getAllZoneKeys())
			{
				Point zoneKeyCoords = zone.getLocationOfZoneKey(zoneKey);
				if (zoneKeyCoords.x == x && zoneKeyCoords.y == y)
				{
					valid = false;
					break;
				}
			}
			
			if (tile.obstructsMotion())
				valid = false;
			
			if (noActorAllowed && tile.getActorHere() != null)
				valid = false;
			
			if (noItemAllowed && tile.getItemHere() != null)
				valid = false;
			
			if (valid)
				openTile = new Point(x, y);
		}
		
		return openTile;
	}

	private void setGenerator(ZoneType zoneType)
	{
		switch(zoneType)	
		{
			case FOREST:
				zoneGenerator = new ForestGenerator();
				break;
			case OCEAN:
				zoneGenerator = new OceanGenerator();
				break;
			case DESERT:
				zoneGenerator = new DesertGenerator();
				break;
			case PLAINS:
				zoneGenerator = new PlainsGenerator();
				break;
			case CAVE:
				zoneGenerator = new PersistentCaveGenerator();
				break;
			case LABYRINTH:
				zoneGenerator = LabyrinthGenerator.getInstance();
				break;
			case PERMANENT:
				zoneGenerator = PredefinedZoneGenerator.getInstance();
				break;
			case TRANSIENT:	//falls through
			default:
				throw new IllegalArgumentException("Unrecognized Zone Type: " + zoneType);
		}
	}
	
	public Zone loadAndMapZoneFromSaveString(String saveString)
	{
		Zone zone = null;
		
		try
		{
			zone = new Zone();
			String key = zone.loadFromText(saveString);
			EntityMap.put(key, zone);
		} catch (ParseException e)
		{
			System.out.println("ZoneFactory - " + e.getMessage());
		}
		
		return zone;
	}
}
