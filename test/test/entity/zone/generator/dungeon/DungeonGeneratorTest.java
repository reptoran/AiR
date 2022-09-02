package test.entity.zone.generator.dungeon;

import org.junit.Before;
import org.junit.Test;

import main.entity.zone.generator.dungeon.ClassicGenerator;
import main.entity.zone.generator.dungeon.RandomGenerator;
import main.presentation.Logger;

public class DungeonGeneratorTest
{
	private RandomGenerator generator;
	
	@Before
	public void setup()
	{
		Logger.setLogLevel(Logger.ERROR);
		
		generator = new ClassicGenerator();
//		generator = new CaveGenerator();
//		generator = new RiftsGenerator();
	}
	
	@Test
	public void test()
	{
		generator.generate("test dungeon", 40, 80, 1, false, false);
		generator.printDungeonMap();
	}
}
