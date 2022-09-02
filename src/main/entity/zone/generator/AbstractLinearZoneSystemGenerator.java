package main.entity.zone.generator;

import java.awt.Point;

import main.entity.zone.Zone;
import main.entity.zone.ZoneKey;
import main.entity.zone.ZoneType;

public abstract class AbstractLinearZoneSystemGenerator extends AbstractGenerator
{
//	protected static final int HEIGHT = 40;
//	protected static final int WIDTH = 80;
	
	protected static final int HEIGHT = 30;
	protected static final int WIDTH = 60;

	protected ZoneKey zoneKey;
	protected boolean descendedIntoLevel;
	protected Zone originZone;
	
	@Override
	public Zone generateZone(ZoneKey zoneKeyArg)
	{
		return generateZone(zoneKeyArg, true, null);
	}

	@Override
	public Zone generateZone(ZoneKey zoneKeyArg, boolean descendingArg, Zone originZoneArg)
	{
		zoneKey = zoneKeyArg;
		descendedIntoLevel = descendingArg;
		originZone = originZoneArg;

		if (originZone != null && !originZone.shouldPersist())
			originZone = null;
		
		Zone zone = generateZonewithTilesAndDefaultZoneKeys();
		return updateZoneKeys(zone);
	}
	
	private Zone updateZoneKeys(Zone zone)
	{
		Point upwardLevelEntry = zone.getLocationOfZoneKey(new ZoneKey(ZoneType.UNMAPPED_UP));
		Point downwardLevelEntry = zone.getLocationOfZoneKey(new ZoneKey(ZoneType.UNMAPPED_DOWN));
		
		ZoneKey keyToAboveZone = new ZoneKey(getDefaultZoneTypeForNextLevel(), zoneKey.getLevel() - 1);
		ZoneKey keyToBelowZone = new ZoneKey(getDefaultZoneTypeForNextLevel(), zoneKey.getLevel() + 1);
		
		if (descendedIntoLevel && upwardLevelEntry != null)
			zoneKey.setEntryPoint(new Point(upwardLevelEntry.x, upwardLevelEntry.y));
		else if (!descendedIntoLevel && downwardLevelEntry != null)
			zoneKey.setEntryPoint(new Point(downwardLevelEntry.x, downwardLevelEntry.y));
		
		if (originZone != null)
		{	
			Point entryIntoGeneratedZoneFromOriginZone = originZone.getLocationOfZoneKey(zoneKey);
			
			if (descendedIntoLevel)
			{
				keyToAboveZone.updateToPermanent(originZone.getName());
				keyToAboveZone.setEntryPoint(entryIntoGeneratedZoneFromOriginZone);
			}
			else
			{
				keyToBelowZone.updateToPermanent(originZone.getName());
				keyToBelowZone.setEntryPoint(entryIntoGeneratedZoneFromOriginZone);
			}
		}
		
		if (upwardLevelEntry != null)
			zone.addZoneKey(upwardLevelEntry, keyToAboveZone);
		
		if (downwardLevelEntry != null)
			zone.addZoneKey(downwardLevelEntry, keyToBelowZone);

		zoneKey.updateToPermanent(zone.getName());
		
		return zone;
	}
	
	protected abstract Zone generateZonewithTilesAndDefaultZoneKeys();
	protected abstract ZoneType getDefaultZoneTypeForNextLevel();
}
