package main.logic;

import java.util.Random;


public class RPGlib
{
	private static Random random = new Random(System.currentTimeMillis());
	
	public static int Randint(int lower, int upper)
	{
		return random.nextInt(upper + 1 - lower) + lower;
	}
	
	public static int distance(int x1, int y1, int x2, int y2)
	{
	    return (int)Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}
	
	public static void setRandomSeed(long seed)
	{
		random.setSeed(seed);
	}
}
