package main.entity.zone.generator.dungeon;

import java.awt.Point;

import main.entity.feature.FeatureFactory;
import main.entity.feature.FeatureType;
import main.entity.tile.Tile;
import main.entity.tile.TileFactory;
import main.entity.tile.TileType;
import main.entity.zone.Zone;
import main.entity.zone.ZoneKey;
import main.entity.zone.ZoneType;

public abstract class RandomGenerator
{
	public Zone generate(String name, int height, int width, int depth, boolean shouldPersist, boolean canEnterWorld)
	{
		Zone zone = new Zone(getZoneType(), name, height, width, depth, shouldPersist, canEnterWorld);
		
		char[][] mapTiles = generateCharMap(height, width);

		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				Tile newTile = TileFactory.generateNewTile(TileType.FLOOR);

				switch (mapTiles[i][j])
				{
				case '#':
					newTile.setFeatureHere(FeatureFactory.generateNewFeature(FeatureType.WALL));
					break;
				case '<':
					newTile = TileFactory.generateNewTile(TileType.STAIRS_UP);
					zone.addZoneKey(new Point(i, j), new ZoneKey(ZoneType.UNMAPPED_UP));
					break;
				case '>':
					newTile = TileFactory.generateNewTile(TileType.STAIRS_DOWN);
					zone.addZoneKey(new Point(i, j), new ZoneKey(ZoneType.UNMAPPED_DOWN));
					break;
				}

				zone.setTile(i, j, newTile);
			}
		}
		
		return zone;
	}
	
	public abstract char[][] generateCharMap(int height, int width);
	public abstract ZoneType getZoneType();
}
