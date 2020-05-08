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
	
	public static Direction convertCoordChangeToDirection(int rowChange, int colChange)
	{
		String direction = "DIR";
		if (rowChange < 0)
			direction = direction + "N";
		if (rowChange > 0)
			direction = direction + "S";
		if (colChange < 0)
			direction = direction + "W";
		if (colChange > 0)
			direction = direction + "E";
		
		if (direction.equals("DIR"))
			return Direction.DIRNONE;

		return Direction.fromString(direction);
	}
	
	public static Point convertDirectionToCoordChange(Direction directionEnum)
	{
		String direction = directionEnum.name();
		
		int x = 0;
		int y = 0;
		
		if (direction.equals("DIRNW") || direction.equals("DIRN") || direction.equals("DIRNE"))
			x--;
		if (direction.equals("DIRSW") || direction.equals("DIRS") || direction.equals("DIRSE"))
			x++;
		if (direction.equals("DIRNW") || direction.equals("DIRW") || direction.equals("DIRSW"))
			y--;
		if (direction.equals("DIRNE") || direction.equals("DIRE") || direction.equals("DIRSE"))
			y++;
		
		return new Point(x, y);	
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
}
