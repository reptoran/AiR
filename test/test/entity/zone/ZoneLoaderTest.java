package test.entity.zone;

import static org.junit.Assert.assertEquals;

import java.awt.Point;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import main.entity.actor.Actor;
import main.entity.actor.ActorType;
import main.entity.zone.Zone;
import main.entity.zone.ZoneKey;
import main.entity.zone.ZoneType;
import main.entity.zone.predefined.PredefinedZone;
import main.entity.zone.predefined.PredefinedZoneLoader;

public class ZoneLoaderTest
{
	private PredefinedZoneLoader predefinedZoneLoader;
	
	@Before
	public void setup()
	{
		predefinedZoneLoader = new PredefinedZoneLoader();
	}
	
	@Test
	public void loadZones_singleValidDataFile_zoneCreated()
	{
		List<PredefinedZone> loadedZones = predefinedZoneLoader.loadAllPredefinedZones();
		Zone zone = loadedZones.get(0);
		
		List<Actor> actors = zone.getActors();
		Actor actor = actors.get(0);
		ZoneKey zoneKey = zone.getZoneKey(new Point(2, 11));
		
		assertEquals(5, zone.getHeight());
		assertEquals(23, zone.getWidth());
		assertEquals(1, actors.size());
		assertEquals(ActorType.BANDIT, actor.getType());
		assertEquals(new Point(3, 21), zone.getCoordsOfActor(actor));
		assertEquals(ZoneType.UNMAPPED_UP, zoneKey.getType());
	}
}
