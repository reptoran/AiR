package main.entity.zone;

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
import main.entity.tile.Tile;
import main.entity.tile.TileFactory;
import main.entity.tile.TileType;

public class ZoneBuilder
{
	private Map<Character, Tile> tileMap = new HashMap<Character, Tile>();
	private Map<Point, Actor> actorMap = new HashMap<Point, Actor>();
	private List<String> mapLines = new ArrayList<String>();
	
	private int width = 0;
	private int height = 0;
	
	private String name;
	
	public ZoneBuilder(int zoneIndex)
	{
		name = "PERMZONE" + zoneIndex;
	}

	public Zone build()
	{
		Zone zone = new Zone(ZoneType.PERMANENT, name, height, width, -1, true, false);	//TODO: assuming all predefined levels are underground; depth will have to be set later
		
		setTiles(zone);
		addActors(zone);
		
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
				Tile tile = tileMap.get(key).clone();
				Point point = new Point(i, j);
				zone.setTile(point, tile);
				
				addZoneKeys(zone, tile, point);
			}
		}
	}

	private void addZoneKeys(Zone zone, Tile tile, Point point)
	{
		//TODO: these two may need to be switched; at any rate, anything digging into this level will have to find the matching zoneKey to know where to go, then update its own appropriately 
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
}
