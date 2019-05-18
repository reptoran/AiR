package main.logic;

import java.util.List;
import java.util.Random;


public class RPGlib
{
	private static Random random = new Random(System.currentTimeMillis());
	
	public static int randInt(int lower, int upper)
	{
		return random.nextInt(upper + 1 - lower) + lower;
	}
	
	public static <T> T getRandomListEntry(List<T> list)
	{
		if (list.isEmpty())
			return null;
		
		return list.get(randInt(0, list.size() - 1));
	}
	
	public static boolean percentage(int percentage)
	{
		return randInt(1, 100) > percentage;
	}
	
	public static int roll(String diceString)
	{
		int dice = 0;
		int sides = 0;
		int modifier = 0;
		int total = 0;
		
		int dIndex = diceString.toUpperCase().indexOf("D");
		int plusIndex = diceString.indexOf("+");
		int minusIndex = diceString.indexOf("-");
		
		if (dIndex == -1)
			throw new IllegalArgumentException("Invalid dice string: " + diceString);

		if (plusIndex != -1)
		{
			modifier = Integer.parseInt(diceString.substring(plusIndex));
		}
		else if (minusIndex != -1)
		{
			modifier = Integer.parseInt(diceString.substring(minusIndex));
			plusIndex = minusIndex;
		}
		else
		{
			plusIndex = diceString.length();
		}
		
		dice = Integer.parseInt(diceString.substring(0, dIndex));
		sides = Integer.parseInt(diceString.substring(dIndex + 1, plusIndex));
		
		for (int i = 1; i <= dice; i++)
			total += randInt(1, sides);
		
		return total + modifier;
	}
	
	public static int distance(int x1, int y1, int x2, int y2)
	{
	    return (int)Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}
	
	public static void setRandomSeed(long seed)
	{
		random.setSeed(seed);
	}
	
	public static String convertCoordChangeToDirection(int rowChange, int colChange)
	{
		String command = "DIR";
		if (rowChange < 0)
			command = command + "N";
		if (rowChange > 0)
			command = command + "S";
		if (colChange < 0)
			command = command + "W";
		if (colChange > 0)
			command = command + "E";
		
		if (command.equals("DIR"))
			return "DIRNONE";

		return command;
	}
	
	public static double truncateDouble(double value, int decimalPlaces)
	{
		if (decimalPlaces < 1 || decimalPlaces > 7)
			throw new IllegalArgumentException("Argument [decimalPlaces] must be greater than zero and less than 8.");
		
		double multiplier = Math.pow(10, decimalPlaces);
		int multipliedValue = (int)(value * multiplier);
		return multipliedValue / multiplier;
	}
}
