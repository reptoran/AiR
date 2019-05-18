package main.entity.tile;

import java.text.ParseException;

import main.entity.save.EntityMap;
import main.presentation.Logger;

public class TileFactory
{
	private TileFactory() {}
	
	public static Tile generateNewTile(TileType tileType)
	{
		switch (tileType)
		{
			case GRASS:
				return TileBuilder.generateTile(tileType).setName("grass").setIcon('.').setColor(10).build();
			case OCEAN:
				return TileBuilder.generateTile(tileType).setName("ocean").setIcon('=').setColor(1).setObstructsMotion(true).setBlockedMessage("That water looks much too treacherous for swimming.").build();
			case RIVER:
				return TileBuilder.generateTile(tileType).setName("river").setIcon('=').setColor(9).setObstructsMotion(true).setBlockedMessage("You'd rather not get wet right now.").build();
			case SAND:
				return TileBuilder.generateTile(tileType).setName("sand").setIcon('.').setColor(14).setMoveCostModifier(1.2).build();
			case ROCK:
				return TileBuilder.generateTile(tileType).setName("rock").setIcon('*').setColor(8).setObstructsMotion(true).setBlockedMessage("The rocky wall is too steep to climb.").build();
			case ROAD:
				return TileBuilder.generateTile(tileType).setName("road").setIcon('.').setColor(6).setMoveCostModifier(.9).build();
			case FLOOR:
				return TileBuilder.generateTile(tileType).setName("floor").setIcon('.').setColor(7).build();
			case CA_BLOCKER:
				return TileBuilder.generateTile(tileType).setObstructsCoaligned(true).setName("floor").setIcon('.').setColor(7).build();
			case STAIRS_DOWN:
				return TileBuilder.generateTile(tileType).setName("stairs down").setIcon('>').setColor(7).build();
			case STAIRS_UP:
				return TileBuilder.generateTile(tileType).setName("stairs up").setIcon('<').setColor(7).build();
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
			Logger.warn("TileFactory - " + e.getMessage());
		}
		
		return tile;
	}
}
