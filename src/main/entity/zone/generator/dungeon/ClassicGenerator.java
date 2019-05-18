package main.entity.zone.generator.dungeon;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.entity.zone.ZoneType;
import main.entity.zone.generator.dungeon.classic.Direction;
import main.entity.zone.generator.dungeon.classic.DungeonElement;
import main.entity.zone.generator.dungeon.classic.DungeonElementCategory;
import main.entity.zone.generator.dungeon.classic.DungeonElementFactory;
import main.logic.RPGlib;

public class ClassicGenerator extends RandomGenerator
{
	private static final char EMPTY_MAP_CHAR = '#';
	
	private int minHallWidth;
	private int maxHallWidth;
	private int minHallLength;
	private int maxHallLength;
	private int minRoomWidth;
	private int maxRoomWidth;
	private int minRoomHeight;
	private int maxRoomHeight;
	
	private int minAdditionAttempts = 0;
	private int minElements = 0;
	private int maxElements = 0;
	private int maxAdditionAttempts = 0;
	private int mapHeight = -1;
	private int mapWidth = -1;
	
	private List<DungeonElement> generatedElements = new ArrayList<DungeonElement>();
	private Map<DungeonElement, Point> elementLocations = new HashMap<DungeonElement, Point>();
	
	private char[][] map = null;
	char[][] mappedElements = null;
	
	public ClassicGenerator()
	{
		this(1, 1, 5, 10, 3, 6, 3, 8);
	}
	
	public ClassicGenerator(int minHallWidth, int maxHallWidth, int minHallLength, int maxHallLength, int minRoomWidth, int maxRoomWidth,
			int minRoomHeight, int maxRoomHeight)
	{
		this.minHallWidth = minHallWidth;
		this.maxHallWidth = maxHallWidth;
		this.minHallLength = minHallLength;
		this.maxHallLength = maxHallLength;
		this.minRoomWidth = minRoomWidth;
		this.maxRoomWidth = maxRoomWidth;
		this.minRoomHeight = minRoomHeight;
		this.maxRoomHeight = maxRoomHeight;
	}

	@Override
	public char[][] generateCharMap(int height, int width)
	{
		setGenerationParametersBasedOnDimensions(height, width);
		map = generateFullDungeon('#');
		mappedElements = generateFullDungeon(EMPTY_MAP_CHAR);
		addStartingRoom();
		
		int attempts = 0;
		int elements = 1;
		
		while (attempts < minAdditionAttempts || elements < minElements)
		{
			if (addDungeonElement(elements))
			{
				elements++;
				System.out.println("Element generation successful; " + attempts + " total attempts so far");
				printDungeonMap();
				System.out.println();
			}
			
			attempts++;
			
			if (elements > maxElements)
				break;
			
			if (attempts > maxAdditionAttempts)
				break;
		}
		
		return map;
	}
	
	private void addStartingRoom()
	{
		int roomHeight = RPGlib.randInt(minRoomHeight, maxRoomHeight) + 2;
		int roomWidth = RPGlib.randInt(minRoomWidth, maxRoomWidth) + 2;
		
		int startX = RPGlib.randInt(1, mapHeight - roomHeight - 1);
		int startY = RPGlib.randInt(1, mapWidth - roomWidth - 1);
		
		DungeonElement startingRoom = DungeonElementFactory.getInstance().generateRoom(roomHeight, roomWidth);
		addElementToMap(startingRoom, new Rectangle(startX, startY, roomWidth, roomHeight), -1);
	}
	
	private void printRegionMap()
	{
		for (int i = 0; i < mapHeight; i++)
		{
			for (int j = 0; j < mapWidth; j++)
			{
				System.out.print(mappedElements[i][j]);
			}
			
			System.out.println();
		}
	}
	
	private void printDungeonMap()
	{
		for (int i = 0; i < mapHeight; i++)
		{
			for (int j = 0; j < mapWidth; j++)
			{
				System.out.print(map[i][j]);
			}
			
			System.out.println();
		}
	}

	private boolean addDungeonElement(int nextElementNumber)
	{
		DungeonElement elementToDigFrom = findNextElementToDigFrom();
		Point nextPointToDigFrom = calculateNextPointToDigFrom(elementToDigFrom);
		
		if (nextPointToDigFrom == null)
			return false;
		
		DungeonElementCategory nextType = DungeonElementCategory.HALL;
		
		//assume next element is a hall, but if we're digging from a hall, there's a 2 in 3 chance it will be a room instead
		if (elementToDigFrom.getType() == DungeonElementCategory.HALL && RPGlib.randInt(1, 3) != 1)
			nextType = DungeonElementCategory.ROOM;
		
		Direction nextElementDirection = determineNextElementDirection(elementToDigFrom, nextPointToDigFrom);
		Point entrance = calculateEntranceLocation(nextPointToDigFrom, nextElementDirection);
		Rectangle nextElementRegion = generateNewRegion(entrance, nextElementDirection, nextType);
		
		if (nextElementRegion == null)
			return false;
		
		DungeonElement nextElement = DungeonElementFactory.getInstance().generateRoom(nextElementRegion.height - 2, nextElementRegion.width - 2);	//remove the padding
		addElementToMap(nextElement, nextElementRegion, nextElementNumber);
		
		//TODO: commented out for now; perhaps have regions map their own entrances
		//could be a door, but for now is just a single floor tile that separates the next element from this one
//		Point entrance = calculateEntranceLocation(nextPointToDigFrom, nextElementDirection);
		
		//TODO: might not need these; just generate a region based on the point/direction and do a bounds check
//		int xMin = 0;
//		int yMin = 0;
//		int xMax = mapHeight - 1;
//		int yMax = mapWidth - 1;
		
//		int startX = RPGlib.randInt(xMin, upper)
		
		return true;
	}
	
	private void addElementToMap(DungeonElement element, Rectangle region, int elementNumber)
	{
		Point origin = new Point(region.x, region.y);
		Point unpaddedOrigin = new Point(region.x + 1, region.y + 1);
		addNewRegionToMap(region, elementNumber);
		addNewElementToMap(unpaddedOrigin, element);
		generatedElements.add(element);
		elementLocations.put(element, origin);
		printRegionMap();
	}

	private void addNewRegionToMap(Rectangle newRegion, int regionNumber)
	{
		for (int i = newRegion.x; i < newRegion.x + newRegion.height; i++)
		{
			for (int j = newRegion.y; j < newRegion.y + newRegion.width; j++)
			{
				mappedElements[i][j] = (char)(65 + regionNumber);
			}
		}
	}
	
	private void addNewElementToMap(Point origin, DungeonElement element)
	{
		char[][] elementMap = element.getTileMap();
		
		for (int i = 0; i < element.getHeight(); i++)
		{
			for (int j = 0; j < element.getWidth(); j++)
			{
				map[origin.x + i][origin.y + j] = elementMap[i][j];
			}
		}
	}

	private Point calculateNextPointToDigFrom(DungeonElement elementToDigFrom)
	{
		Point elementOrigin = elementLocations.get(elementToDigFrom);
		Point nextPointToDigFrom = elementToDigFrom.getNextPointToDigFrom();
		
		if (nextPointToDigFrom == null)
			return null;
		
		return new Point(elementOrigin.x + nextPointToDigFrom.x, elementOrigin.y + nextPointToDigFrom.y);
	}

	private Rectangle generateNewRegion(Point nextPointToDigFrom, Direction nextElementDirection, DungeonElementCategory nextType)
	{
		int regionHeight = 0;
		int regionWidth = 0;
		
		//these all add 2 all around for padding so elements don't open into each other incorrectly
		if (nextType == DungeonElementCategory.HALL && (nextElementDirection == Direction.EAST || nextElementDirection == Direction.WEST))
		{
			regionHeight = RPGlib.randInt(minHallWidth, maxHallWidth) + 2;
			regionWidth = RPGlib.randInt(minHallLength, maxHallLength) + 2;
		}
		else if (nextType == DungeonElementCategory.HALL && (nextElementDirection == Direction.NORTH || nextElementDirection == Direction.SOUTH))
		{
			regionHeight = RPGlib.randInt(minHallLength, maxHallLength) + 2;
			regionWidth = RPGlib.randInt(minHallWidth, maxHallWidth) + 2;
		}
		else if (nextType == DungeonElementCategory.ROOM)
		{
			regionWidth = RPGlib.randInt(minRoomWidth, maxRoomWidth) + 2;
			regionHeight = RPGlib.randInt(minRoomHeight, maxRoomHeight) + 2;
		}
		
		//verify region is within map and doesn't overlap any other regions
		for (int i = nextPointToDigFrom.x; i < nextPointToDigFrom.x + regionHeight; i++)
		{
			for (int j = nextPointToDigFrom.y; j < nextPointToDigFrom.y + regionWidth; j++)
			{
				if (i < 0 || j < 0 || i >= mapHeight || j >= mapWidth)
					return null;
				
				if (mappedElements[i][j] != EMPTY_MAP_CHAR)
					return null;
			}
		}
		
		return new Rectangle(nextPointToDigFrom.x, nextPointToDigFrom.y, regionWidth, regionHeight);
	}

	private Point calculateEntranceLocation(Point nextPointToDigFrom, Direction nextElementDirection)
	{
		int xChange = 0;
		int yChange = 0;

		if (nextElementDirection == Direction.NORTH)
			xChange--;
		if (nextElementDirection == Direction.EAST)
			yChange++;
		if (nextElementDirection == Direction.SOUTH)
			xChange++;
		if (nextElementDirection == Direction.WEST)
			yChange--;
		
		return new Point(nextPointToDigFrom.x + xChange, nextPointToDigFrom.y + yChange);
	}

	private Direction determineNextElementDirection(DungeonElement elementToDigFrom, Point nextPointToDigFrom)
	{
		Point origin = elementLocations.get(elementToDigFrom);
		Point transformedDigPoint = new Point(nextPointToDigFrom.x - origin.x, nextPointToDigFrom.y - origin.y);
		
		List<Direction> potentialDirections = new ArrayList<Direction>();
		potentialDirections.add(Direction.NORTH);
		potentialDirections.add(Direction.EAST);
		potentialDirections.add(Direction.SOUTH);
		potentialDirections.add(Direction.WEST);
		
		//the next point to dig from is in the format (row, column)
		if (transformedDigPoint.x != 0)
			potentialDirections.remove(Direction.NORTH);
		if (transformedDigPoint.y != 0)
			potentialDirections.remove(Direction.WEST);
		if (transformedDigPoint.x != elementToDigFrom.getHeight() - 1)
			potentialDirections.remove(Direction.SOUTH);
		if (transformedDigPoint.y != elementToDigFrom.getWidth() - 1)
			potentialDirections.remove(Direction.EAST);
		
		return RPGlib.getRandomListEntry(potentialDirections);
	}

	private DungeonElement findNextElementToDigFrom()
	{
		for (DungeonElement element : generatedElements)
		{
			if (element.hasDeadEnds())
				return element;
		}
		
		return generatedElements.get(RPGlib.randInt(0, generatedElements.size() - 1));
	}

	private char[][] generateFullDungeon(char symbol)
	{
		char[][] charMap = new char[mapHeight][mapWidth];
		
		for (int i = 0; i < mapHeight; i++)
			for (int j = 0; j < mapWidth; j++)
				charMap[i][j] = symbol;
		
		return charMap;
	}
	
	private void setGenerationParametersBasedOnDimensions(int height, int width)
	{
		mapHeight = height;
		mapWidth = width;
		
		int hallWidth = (minHallWidth + maxHallWidth) / 2;
		int hallLength = (minHallLength + maxHallLength) / 2;
		int roomWidth = (minRoomWidth + maxRoomWidth) / 2;
		int roomHeight = (minRoomHeight + maxRoomHeight) / 2;
		
		int dungeonArea = height * width;
		int hallArea = hallLength * hallWidth;
		int roomArea = roomHeight * roomWidth;
		
		//no science to these numbers; just experimenting for now
		minElements = (dungeonArea / roomArea) / 10;
		maxElements = (dungeonArea / roomArea) / 2;
		minAdditionAttempts = minElements * 2;
		maxAdditionAttempts = maxElements * 10;
	}

	@Override
	public ZoneType getZoneType()
	{
		return ZoneType.CLASSIC;
	}

}
