package main.entity.zone;

import java.awt.Point;
import java.text.ParseException;

import main.entity.actor.Actor;
import main.entity.save.EntityMap;
import main.entity.world.WorldTile;
import main.entity.zone.generator.AbstractGenerator;
import main.entity.zone.generator.DesertGenerator;
import main.entity.zone.generator.ForestGenerator;
import main.entity.zone.generator.OceanGenerator;
import main.entity.zone.generator.PersistentCaveGenerator;
import main.entity.zone.generator.PlainsGenerator;
import main.entity.zone.generator.ZonePopulationGenerator;
import main.logic.RPGlib;

public class ZoneFactory
{
	private static ZoneFactory instance = null;	//singleton
	
	private static final int MINIMUM_ACTORS_PER_ZONE = 8;
	private static final int MAXIMUM_ACTORS_PER_ZONE = 15;
	
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
		zoneKey.setId(generatedMaps++);
		Zone zone = zoneGenerator.generateZone(zoneKey, goingDown, oldZone);
		
		populateZone(zone);	//TODO: perhaps put this somewhere else
		
		return zone;
	}
	
	private void populateZone(Zone zone)
	{
		int totalActors = RPGlib.Randint(MINIMUM_ACTORS_PER_ZONE, MAXIMUM_ACTORS_PER_ZONE);
		
		for (int i = 0; i < totalActors; i++)
		{
			Point actorLocation = findOpenTile(zone);
			Actor actor = ZonePopulationGenerator.generateMonster(zone.getDepth());
			
			if (actor == null)
				return;
			
			zone.addActor(actor, actorLocation);
		}
	}
	
	private Point findOpenTile(Zone zone)
	{
		Point openTile = null;
		
		while (openTile == null)
		{
			int x = RPGlib.Randint(0, zone.getHeight() - 1);
			int y = RPGlib.Randint(0, zone.getWidth() - 1);
			
			if (!zone.getTile(x, y).obstructsMotion())
				openTile = new Point(x, y);
		}
		
		return openTile;
	}

	private void setGenerator(ZoneType zoneType)
	{
		switch (zoneType)
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
			case PERMANENT:
				throw new IllegalArgumentException("Zone has already been generated.");
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
