package main.entity.zone.generator;

import main.entity.zone.Zone;
import main.entity.zone.ZoneType;
import main.entity.zone.generator.dungeon.CaveGenerator;
import main.presentation.Logger;

public class PersistentCaveGenerator extends AbstractLinearZoneSystemGenerator
{
	@Override
	protected Zone generateZonewithTilesAndDefaultZoneKeys()
	{
		Logger.debug("Generating new persistent cave: " + zoneKey + "; " + descendedIntoLevel + "; " + originZone);
		return new CaveGenerator().generate(zoneKey.getId(), HEIGHT, WIDTH, zoneKey.getLevel(), true, canTransitionToOverworld());
	}

	@Override
	protected ZoneType getDefaultZoneTypeForNextLevel()
	{
		return ZoneType.CAVE;
	}
}
