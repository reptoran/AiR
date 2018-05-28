package main.entity.zone.generator;

import main.entity.feature.FeatureFactory;
import main.entity.feature.FeatureType;
import main.entity.tile.Tile;
import main.entity.tile.TileFactory;
import main.entity.tile.TileType;
import main.entity.zone.Zone;
import main.entity.zone.ZoneKey;
import main.entity.zone.ZoneType;
import main.logic.RPGlib;

public class PlainsGenerator extends WorldZoneGenerator
{
	@Override
	public Zone generateZone(ZoneKey zoneKey)
	{
		String name = zoneKey.getId();
		Zone zone = new Zone(ZoneType.PLAINS, name, 40, 160, false, canTransitionToOverworld());
		
		int height = zone.getHeight();
		int width = zone.getWidth();
		
		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				Tile newTile = TileFactory.generateNewTile(TileType.GRASS);
				
				if (RPGlib.Randint(1, 12) == 1)
					newTile.setFeatureHere(FeatureFactory.generateNewFeature(FeatureType.TREE));
				
				zone.setTile(i, j, newTile);
			}
		}
		
		zoneKey.updateToTransient(name);
		
		return zone;
	}
}
