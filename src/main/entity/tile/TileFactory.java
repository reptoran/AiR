package main.entity.tile;

import java.text.ParseException;

import main.entity.save.EntityMap;

public class TileFactory
{
	private TileFactory() {}
	
	public static Tile generateNewTile(TileType tileType)
	{
		switch (tileType)
		{
			case GRASS:
				return new Tile(tileType, "grass", '.', 10, false, false, 100, "");
			case OCEAN:
				return new Tile(tileType, "ocean", '=', 1, false, true, 0, "That water looks much too treacherous for swimming.");
			case RIVER:
				return new Tile(tileType, "river", '=', 9, false, true, 0, "You'd rather not get wet right now.");
			case SAND:
				return new Tile(tileType, "sand", '.', 14, false, false, 120, "");
			case ROCK:
				return new Tile(tileType, "rock", '*', 8, false, true, 0, "The rocky wall is too steep to climb.");
			case ROAD:
				return new Tile(tileType, "road", '.', 6, false, false, 90, "");
			case FLOOR:
				return new Tile(tileType, "floor", '.', 7, false, false, 100, "");
			case STAIRS_DOWN:
				return new Tile(tileType, "stairs down", '>', 7, false, false, 100, "");
			case STAIRS_UP:
				return new Tile(tileType, "stairs up", '<', 7, false, false, 100, "");
			case NO_TYPE:	//falls through
			default:
				throw new IllegalArgumentException("No tile definition for tile type: " + tileType);
		}
	}
	
	public static Tile loadAndMapTileFromSaveString(String saveString)
	{
		Tile tile = null;
		
		try
		{
			tile = new Tile();
			String key = tile.loadFromText(saveString);
			EntityMap.put(key, tile);
		} catch (ParseException e)
		{
			System.out.println("TileFactory - " + e.getMessage());
		}
		
		return tile;
	}
}
