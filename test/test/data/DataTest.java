package test.data;

import org.junit.Before;
import org.junit.Test;

import main.data.Data;
import main.entity.zone.Zone;

public class DataTest
{
	Data data;
	
	@Before
	public void setup()
	{
		data = new Data();
	}
	
	@Test
	public void testBandCalculation()
	{
		Zone zone = new Zone();
		
		for (int i = 0; i <= 100; i++)
		{
			zone.setDepth(i);
			int band = data.getBandOfZone(zone);
			
			System.out.println("At depth " + i + ", band is " + band);
		}
	}
}
