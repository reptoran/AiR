package main.logic;

import java.awt.Point;
import java.util.ArrayList;
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
		return randInt(1, 100) <= percentage;
	}
	
	public static int roll(String diceString)
	{
		return roll(diceString, false);
	}
	
	public static int roll(String diceString, boolean maxValue)
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
		
		if (maxValue)
			return dice * sides;
		
		for (int i = 1; i <= dice; i++)
			total += randInt(1, sides);
		
		return total + modifier;
	}
	
	public static int tileDistance(Point point1, Point point2)
	{
	    return tileDistance(point1.x, point1.y, point2.x, point2.y);
	}
	
	public static int tileDistance(int x1, int y1, int x2, int y2)
	{
	    return Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1));
	}
	
	public static int distance(Point point1, Point point2)
	{
	    return distance(point1.x, point1.y, point2.x, point2.y);
	}
	
	public static int distance(int x1, int y1, int x2, int y2)
	{
	    return (int)Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}
	
	public static double trueDistance(Point point1, Point point2)
	{
	    return trueDistance(point1.x, point1.y, point2.x, point2.y);
	}
	
	public static double trueDistance(int x1, int y1, int x2, int y2)
	{
	    return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}
	
	public static void setRandomSeed(long seed)
	{
		random.setSeed(seed);
	}
	
	public static double truncateDouble(double value, int decimalPlaces)
	{
		if (decimalPlaces < 1 || decimalPlaces > 7)
			throw new IllegalArgumentException("Argument [decimalPlaces] must be greater than zero and less than 8.");
		
		double multiplier = Math.pow(10, decimalPlaces);
		int multipliedValue = (int)(value * multiplier);
		return multipliedValue / multiplier;
	}
	
	public static String padStringRight(String toPad, int width, char fill)
	{
		if (width <= toPad.length())
			return toPad;
		
		return new String(new char[width - toPad.length()]).replace('\0', fill) + toPad;
	}
	
	public static String padStringLeft(String toPad, int width, char fill)
	{
		if (width <= toPad.length())
			return toPad;
		
		String toReturn = toPad;
		
		while (toReturn.length() < width)
			toReturn = fill + toReturn;
		
		return toReturn;
	}
	
	public static String padString(String toPad, int width, char fill)
	{
		if (width <= toPad.length())
			return toPad;
		
		String toReturn = toPad;
		
		while (toReturn.length() < width)
			toReturn = fill + toReturn + fill;
		
		return toReturn;
	}
	
	public static String stringValue(Object object)
	{
		if (object == null)
			return null;
		return object.toString();
	}
	
	@SafeVarargs
	public static <T> List<T> generateList(T... listElements)
	{
		List<T> list = new ArrayList<T>();
		
		for (T element : listElements)
		{
			list.add(element);
		}
		
		return list;
	}

	public static Point addPoints(Point origin, Point coordChange)
	{
		return new Point(origin.x + coordChange.x, origin.y + coordChange.y);
	}
}
