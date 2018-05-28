package main.entity.zone.generator;

import java.awt.Point;

import main.entity.tile.Tile;
import main.entity.tile.TileFactory;
import main.entity.tile.TileType;
import main.entity.zone.Zone;
import main.entity.zone.ZoneKey;
import main.entity.zone.ZoneType;
import main.logic.RPGlib;

public class DesertGenerator extends WorldZoneGenerator
{
	@Override
	public Zone generateZone(ZoneKey zoneKey)
	{
		String name = zoneKey.getId();
		
		//should persist is true right now only because this zone leads to a dungeon and should be available when going upstairs fomr level 1
		Zone zone = new Zone(ZoneType.DESERT, name, 40, 160, true, canTransitionToOverworld());
		
		int height = zone.getHeight();
		int width = zone.getWidth();
		
		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				Tile newTile = TileFactory.generateNewTile(TileType.SAND);
				zone.setTile(i, j, newTile);
			}
		}
		
		zoneKey.updateToTransient(name);
		
		int caveEntryX = RPGlib.Randint(15, 25);
		int caveEntryY = RPGlib.Randint(75, 85);
		Point stairsLocation = new Point(caveEntryX, caveEntryY);
		
		zone.setTile(stairsLocation, TileFactory.generateNewTile(TileType.STAIRS_DOWN));
		ZoneKey nextZoneKey = new ZoneKey(ZoneType.CAVE, 1);
		zone.addZoneKey((Point)stairsLocation.clone(), nextZoneKey);
		
		return zone;
	}
}
