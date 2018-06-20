package test.logic;

import org.junit.Before;
import org.junit.Test;

import main.logic.Line;

public class LineTest
{
	@Before
	public void setup()
	{
		//
	}
	
	@Test
	public void generateDiagonalLine()
	{
		Line line = new Line(-12, 0, 5, -3);
		System.out.println(line.getPoints());
	}
	
	@Test
	public void generateHorizontalLine()
	{
		Line line = new Line(-12, 3, 5, 3);
		System.out.println(line.getPoints());
	}
}
