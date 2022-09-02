package main.entity.zone.generator;

import java.awt.Point;

import main.data.SpecialLevelManager;
import main.entity.tile.Tile;
import main.entity.tile.TileType;
import main.entity.zone.Zone;
import main.entity.zone.ZoneKey;

public class PredefinedZoneGenerator extends AbstractGenerator
{
	private static PredefinedZoneGenerator instance = null;
	
	public static PredefinedZoneGenerator getInstance()
	{
		if (instance == null)
			instance = new PredefinedZoneGenerator();
		
		return instance;
	}
	
	@Override
	public Zone generateZone(ZoneKey zoneKey, boolean descending, Zone originZone)
	{
		Zone zone = generateZone(zoneKey);
		
		//TODO: eventually special levels might have multiple entries or exits, but for now, just assume that there's only one transfer point
		//		(that is, if you're entering a special level outside of the "linear zone system," it's a dead end, and the only way out is back the way you came
		addReturnZoneKey(zone, zoneKey, originZone);
		
		return zone;
	}

	private void addReturnZoneKey(Zone generatedZone, ZoneKey keyFromOriginZone, Zone originZone)
	{
		for (int i = 0; i < generatedZone.getHeight(); i++)
		{
			for (int j = 0; j < generatedZone.getWidth(); j++)
			{
				Tile tile = generatedZone.getTile(i, j);
				
				if (tile.getType() != TileType.STAIRS_DOWN && tile.getType() != TileType.STAIRS_UP)
					continue;
				
				Point entryIntoGeneratedZoneFromOriginZone = originZone.getLocationOfZoneKey(keyFromOriginZone);
				Point stairCoords = new Point(i, j);
				
				keyFromOriginZone.setEntryPoint(stairCoords);
				keyFromOriginZone.setId(generatedZone.getName());
				ZoneKey keyToOriginZone = new ZoneKey();
				keyToOriginZone.updateToPermanent(originZone.getName());
				keyToOriginZone.setEntryPoint(entryIntoGeneratedZoneFromOriginZone);
				
				generatedZone.addZoneKey(stairCoords, keyToOriginZone);
				generatedZone.setDepth(0);
				break;
			}
		}
	}

	@Override
	protected Zone generateZone(ZoneKey zoneKey)
	{
		return SpecialLevelManager.getInstance().retrieveZone(zoneKey.getId());
	}

}
