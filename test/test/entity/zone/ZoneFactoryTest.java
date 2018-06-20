package test.entity.zone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import main.entity.world.WorldTile;
import main.entity.world.WorldTileFactory;
import main.entity.zone.Zone;
import main.entity.zone.ZoneFactory;
import main.entity.zone.ZoneKey;
import main.entity.zone.ZoneType;

public class ZoneFactoryTest
{
	private WorldTile forestTile;
	
	@Before
	public void setup()
	{
		forestTile = WorldTileFactory.generateNewTile(ZoneType.FOREST);
	}
	
	@Test
	public void generateZoneFromWorldTile_success()
	{
		assertNull(forestTile.getZoneId());
		
		Zone zone = ZoneFactory.getInstance().generateNewZone(forestTile);
		
		assertEquals("FOREST0", zone.getName());
		assertEquals("FOREST0", forestTile.getZoneId());
	}
	
	@Test
	public void generateAndViewCave()
	{
//		RPGlib.setRandomSeed(421);
		ZoneKey zoneKey = new ZoneKey(ZoneType.CAVE);
		
		Zone cave = ZoneFactory.getInstance().generateNewZone(zoneKey);
		
		for (int i = 0; i < cave.getHeight(); i++)
		{
			System.out.println();
			for (int j = 0; j < cave.getWidth(); j++)
			{
				System.out.print(cave.getTile(i, j).getIcon());
			}
		}
	}
}
