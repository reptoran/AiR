package test.entity.zone.generator.dungeon;

import org.junit.Before;
import org.junit.Test;

import main.entity.zone.generator.dungeon.ClassicGenerator;

public class ClassicGeneratorTest
{
	private ClassicGenerator generator;
	
	@Before
	public void setup()
	{
		generator = new ClassicGenerator();
	}
	
	@Test
	public void test()
	{
		generator.generateCharMap(20, 80);
	}
}
