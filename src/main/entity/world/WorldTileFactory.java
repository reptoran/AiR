package main.entity.world;

import java.text.ParseException;

import main.entity.save.EntityMap;
import main.entity.zone.ZoneType;

public class WorldTileFactory
{
	private WorldTileFactory() {}
	
	public static WorldTile generateNewTile(ZoneType zoneType)
	{
		WorldTile toRet = new WorldTile();
		
		switch (zoneType)
		{
			case OCEAN:
				return new WorldTile(zoneType, "ocean", '=', 1, false, true, 0, "You don't dare go into those waters.");
			case PLAINS:
				return new WorldTile(zoneType, "plains", '.', 10, false, false, 10000, "");
			case FOREST:
				return new WorldTile(zoneType, "forest", '&', 2, false, false, 14000, "");
			case DESERT:
				return new WorldTile(zoneType, "desert", '.', 14, false, false, 12000, "");
			case DUNGEON:
				return new WorldTile(zoneType, "dungeon", '*', 8, false, false, 10000, "");
			default:
				break;
		}
		
		return toRet;
	}
	
	public static WorldTile loadAndMapTileFromSaveString(String saveString)
	{
		WorldTile tile = null;
		
		try
		{
			tile = new WorldTile();
			String key = tile.loadFromText(saveString);
			EntityMap.put(key, tile);
		} catch (ParseException e)
		{
			System.out.println("WorldTileFactory - " + e.getMessage());
		}
		
		return tile;
	}
}
