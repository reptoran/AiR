package main.entity.zone.generator.dungeon;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import main.entity.zone.ZoneType;
import main.entity.zone.generator.dungeon.classic.Direction;
import main.entity.zone.generator.dungeon.classic.DungeonElement;
import main.entity.zone.generator.dungeon.classic.DungeonElementCategory;
import main.entity.zone.generator.dungeon.classic.DungeonElementFactory;
import main.logic.RPGlib;
import main.presentation.Logger;

//NOTE THAT THERE ARE NO LOOPS POSSIBLE WITH THE CURRENT ALGORITHM
public class ClassicGenerator extends RandomGenerator
{
	private List<DungeonElement> openElements = new ArrayList<DungeonElement>();
	
	private DungeonElement firstRoom = null;
	
	private int totalElements = 0;
	private int maxElements = 40;
	
	private int minElementHeight = 4;
	private int maxElementHeight = 8;
	private int minElementWidth = 4;
	private int maxElementWidth = 10;
	
	private static final char WALL = '#';
	
	private DungeonElementFactory factory = DungeonElementFactory.getInstance();
	
	@Override
	public char[][] generateCharMap(int height, int width)
	{
		mapHeight = height;
		mapWidth = width;
		
		char[][] dungeonMap = new char[height][width];
		
		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				dungeonMap[i][j] = WALL;
			}
		}
		
		generateStartingElement(dungeonMap);
		
		int iterationsWithoutNewElement = 0;
		
		while(totalElements < maxElements && !openElements.isEmpty())
		{
			if (iterationsWithoutNewElement > 1000)	//get rid of elements causing trouble if it's taking too long to generate
			{
				iterationsWithoutNewElement = 0;
				openElements.remove(0);
			}
			
			if (openElements.isEmpty())
				break;
			
			int currentElementCount = totalElements;
			generateNewElement(dungeonMap, getFirstHallOrRandomOpenElement());
			
			if (currentElementCount == totalElements)	//no element was added
			{
				iterationsWithoutNewElement++;
				continue;
			}
			
//			printDungeonMap(dungeonMap);
//			try
//			{
//				Thread.currentThread().sleep(500);
//			} catch (InterruptedException e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		
		polishDungeon(dungeonMap);
		addStairs(dungeonMap);
		
		return dungeonMap;
	}

	public void printDungeonMap(char[][] dungeonMap)
	{
		for (int i = 0; i < mapHeight; i++)
		{
			for (int j = 0; j < mapWidth; j++)
			{
				System.out.print(dungeonMap[i][j]);
			}
			
			System.out.println();
		}
	}

	private void generateNewElement(char[][] dungeonMap, DungeonElement originElement)
	{
		if (originElement == null)
			originElement = getFirstHallOrRandomOpenElement();
		
		if (originElement.getType() == DungeonElementCategory.HALL)
			Logger.debug("Generating element from hall");
		
		Point originalDigPoint = originElement.getNextPointToDigFrom();
		
		if (originalDigPoint == null)
		{
			openElements.remove(originElement);
			return;
		}
		
		Point digPoint = new Point(originalDigPoint.x, originalDigPoint.y);
		
		Point nextElementEntryPoint = new Point(digPoint.x, digPoint.y);
		Direction nextElementDirection = getNextElementDirection(originElement, digPoint);
		
		if (nextElementDirection == Direction.NORTH)
		{
			digPoint.x--;
			nextElementEntryPoint.x -= 2;
		}
		if (nextElementDirection == Direction.EAST)
		{
			digPoint.y++;
			nextElementEntryPoint.y += 2;
		}
		if (nextElementDirection == Direction.SOUTH)
		{
			digPoint.x++;
			nextElementEntryPoint.x += 2;
		}
		if (nextElementDirection == Direction.WEST)
		{
			digPoint.y--;
			nextElementEntryPoint.y -= 2;
		}
		
		Logger.debug("Generating element in direction " + nextElementDirection + " from element " + originElement);
		
		DungeonElement nextElement = generateElement(nextElementDirection, nextElementEntryPoint, originElement.getType());
		
		if (!areaIsValidForElement(dungeonMap, nextElement))
		{
			originElement.addEdge(originalDigPoint);
			return;
		}
		
		dungeonMap[digPoint.x][digPoint.y]= '.';
		
		if (originElement.getType() == DungeonElementCategory.ROOM || nextElement.getType() == DungeonElementCategory.ROOM)
			dungeonMap[digPoint.x][digPoint.y]= '+';
		
		digElement(dungeonMap, nextElement);
		openElements.add(0, nextElement);		//always put it in the front so it's the first to removed it there's trouble
		totalElements++;
	}

	private Direction getNextElementDirection(DungeonElement originElement, Point digPoint)
	{
		Point origin = originElement.getOrigin();
		
		List<Direction> possibleDirections = new ArrayList<Direction>();
		possibleDirections.addAll(Arrays.asList(Direction.values()));
		
		if (origin.x != digPoint.x)
			possibleDirections.remove(Direction.NORTH);
		if (origin.y != digPoint.y)
			possibleDirections.remove(Direction.WEST);
		if (origin.x + originElement.getHeight() - 1 != digPoint.x)
			possibleDirections.remove(Direction.SOUTH);
		if (origin.y + originElement.getWidth() - 1 != digPoint.y)
			possibleDirections.remove(Direction.EAST);
		
		if (possibleDirections.isEmpty())
			throw new IllegalArgumentException("Dig point is not on edge of dungeon element.");
		
		return possibleDirections.get(RPGlib.randInt(0, possibleDirections.size() - 1));
	}

	private void generateStartingElement(char[][] dungeonMap)
	{
		int originRow = RPGlib.randInt(mapHeight / 3, mapHeight / 3 * 2);
		int originCol = RPGlib.randInt(mapWidth / 3, mapWidth / 3 * 2);
		firstRoom = generateElement(Direction.WEST, new Point(originRow, originCol));
		openElements.clear();
		openElements.add(firstRoom);
		totalElements = 1;
		digElement(dungeonMap, firstRoom);
//		printDungeonMap(dungeonMap);
	}
	
	private DungeonElement generateElement(Direction nextElementDirection, Point nextElementEntryPoint)
	{
		return generateElement(nextElementDirection, nextElementEntryPoint, null);
	}

	private DungeonElement generateElement(Direction direction, Point entryPoint, DungeonElementCategory lastElementType)
	{
		int height = RPGlib.randInt(minElementHeight, maxElementHeight);
		int width = RPGlib.randInt(minElementWidth, maxElementWidth);
		
		int rowAdjust = 0;
		int colAdjust = 0;

		if (direction == Direction.NORTH)
		{
			rowAdjust = -1 * height + 1;
			colAdjust = -1 * RPGlib.randInt(0, width - 1);
		}
		if (direction == Direction.SOUTH)
		{
			colAdjust = -1 * RPGlib.randInt(0, width - 1);
		}
		if (direction == Direction.WEST)
		{
			rowAdjust = -1 * RPGlib.randInt(0, height - 1);
			colAdjust = -1 * width + 1;
		}
		if (direction == Direction.EAST)
		{
			rowAdjust = -1 * RPGlib.randInt(0, height - 1);
		}
		
		Point origin = new Point(entryPoint.x + rowAdjust, entryPoint.y + colAdjust);
		
		
		//rooms always connect to halls, but halls have a chance to keep going
		if (lastElementType == null || (lastElementType == DungeonElementCategory.HALL && RPGlib.randInt(1, 4) != 1))
			return factory.generateRoom(origin, height, width);
		
		if (direction == Direction.NORTH || direction == Direction.SOUTH)
			return factory.generateVerticalHall(new Point(origin.x, entryPoint.y), height, direction);
		else
			return factory.generateHorizontalHall(new Point(entryPoint.x, origin.y), width, direction);
	}

	private DungeonElement getFirstHallOrRandomOpenElement()
	{
		for (DungeonElement hall : openElements)
		{
			if (hall.getType() == DungeonElementCategory.HALL)
				return hall;
		}
		
		int elementIndex = RPGlib.randInt(0, openElements.size() - 1);
		return openElements.get(elementIndex);
	}
	
	private void digElement(char[][] dungeonMap, DungeonElement element)
	{
		char[][] elementTileMap = element.getTileMap();
		Point origin = element.getOrigin();
		
		for (int i = 0; i < element.getHeight(); i++)
		{
			for (int j = 0; j < element.getWidth(); j++)
			{
				dungeonMap[origin.x + i][origin.y + j] = elementTileMap[i][j];
			}
		}
	}

	private boolean areaIsValidForElement(char[][] dungeonMap, DungeonElement element)
	{
		Point origin = element.getOrigin();
		
		for (int i = -1; i <= element.getHeight(); i++)
		{
			for (int j = -1; j < element.getWidth() + 1; j++)
			{
				int row = origin.x + i;
				int col = origin.y + j;
				
				if (row < 0 || col < 0 || row >= mapHeight || col >= mapWidth)
					return false;
				
				if (dungeonMap[row][col] != WALL)
					return false;
			}
		}
		
		return true;
	}
	
	private void polishDungeon(char[][] dungeonMap)
	{
		for (int i = 0; i < mapHeight; i++)
		{
			for (int j = 0; j < mapWidth; j++)
			{
				Point coords = new Point(i, j);
				if (isHallwayEnd(dungeonMap, coords))
					fillHallwayTile(dungeonMap, coords);
			}
		}
	}
	
	private boolean isHallwayEnd(char[][] dungeonMap, Point tileCoords)
	{		
		return (getAdjacentWallCount(dungeonMap, tileCoords) > 2);
	}
	
	private int getAdjacentWallCount(char[][] dungeonMap, Point tileCoords)
	{
		int x = tileCoords.x;
		int y = tileCoords.y;
		int adjacentWalls = 0;
		
		if (x > 0 && dungeonMap[x - 1][y] == WALL)
			adjacentWalls++;
		if (x < mapHeight - 1&& dungeonMap[x + 1][y] == WALL)
			adjacentWalls++;
		if (y > 0 && dungeonMap[x][y - 1] == WALL)
			adjacentWalls++;
		if (y < mapWidth - 1 && dungeonMap[x][y + 1] == WALL)
			adjacentWalls++;
		
		return adjacentWalls;
	}
	
	private void fillHallwayTile(char[][] dungeonMap, Point tileCoords)
	{
		if (!isHallwayEnd(dungeonMap, tileCoords))
			return;
		
		int x = tileCoords.x;
		int y = tileCoords.y;
		
		dungeonMap[x][y] = WALL;
		
		if (x > 0 && dungeonMap[x - 1][y] != WALL)
			fillHallwayTile(dungeonMap, new Point(x - 1, y));
		if (x < mapHeight - 1&& dungeonMap[x + 1][y] != WALL)
			fillHallwayTile(dungeonMap, new Point(x + 1, y));
		if (y > 0 && dungeonMap[x][y - 1] != WALL)
			fillHallwayTile(dungeonMap, new Point(x, y - 1));
		if (y < mapWidth - 1 && dungeonMap[x][y + 1] != WALL)
			fillHallwayTile(dungeonMap, new Point(x, y + 1));
	}
	
	private void addStairs(char[][] dungeonMap)
	{
		int minStairDistance = Math.max(mapHeight, mapWidth) / 2;
		int iterations = 0;
		
		Point upStairs = firstRoom.getOrigin();
		
		try {
			upStairs.x += RPGlib.randInt(1, firstRoom.getHeight() - 2);
			upStairs.y += RPGlib.randInt(1, firstRoom.getWidth() - 2);
		} catch (IllegalArgumentException iae)
		{
			Logger.error(iae.getMessage());
		}
		
		
		while (true)
		{
			iterations++;
			
			if (iterations % 1000 == 0)
			{
				Logger.debug(" On iteration " + iterations + ", reducing minimum stair distance.");
				minStairDistance--;
			}
			
			Point downStairs = findDownStairsLocation(dungeonMap);
			
			if (RPGlib.distance(upStairs.x, upStairs.y, downStairs.x, downStairs.y) < minStairDistance)
				continue;
			
			dungeonMap[upStairs.x][upStairs.y] = '<';
			dungeonMap[downStairs.x][downStairs.y] = '>';
			return;
		}
	}

	private Point findDownStairsLocation(char[][] dungeonMap)
	{
		Point downStairs = null;
		
		while (downStairs == null)
		{
			int row = RPGlib.randInt(0, mapHeight - 1);
			int col = RPGlib.randInt(0, mapWidth - 1);
			
			downStairs = new Point(row, col);
			
			if (getAdjacentWallCount(dungeonMap, downStairs) != 0)
				downStairs = null;
		}
		
		return downStairs;
	}

	@Override
	public ZoneType getZoneType()
	{
		return ZoneType.CLASSIC;
	}
}
