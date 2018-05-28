package main.entity.zone.generator;

import main.entity.zone.Zone;
import main.entity.zone.ZoneKey;
import main.entity.zone.ZoneType;
import main.presentation.Logger;

public abstract class AbstractGenerator
{
	public abstract Zone generateZone(ZoneKey zoneKey);
	
	public Zone generateZone(ZoneKey zoneKey, boolean descending, Zone originZone)
	{
		if (originZone != null)
			Logger.warn("Generating a new zone, ignoring originZone " + originZone.getName());
		
		return generateZone(zoneKey);
	}
	
	protected boolean canTransitionToOverworld()
	{
		return false;
	}
	
	protected ZoneType getDefaultZoneTypeForNextLevel()
	{
		return ZoneType.NO_TYPE;
	}
}
