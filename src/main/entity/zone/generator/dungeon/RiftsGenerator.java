package main.entity.zone.generator.dungeon;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import main.entity.zone.ZoneType;
import main.entity.zone.generator.dungeon.rifts.EmptySquareRoom;
import main.entity.zone.generator.dungeon.rifts.Node;
import main.logic.Line;
import main.logic.RPGlib;
import main.presentation.Logger;

public class RiftsGenerator extends RandomGenerator
{
	private int size = 60;			//function of amount of intersections generated
	private int density = 20;		//this is the minimum distance between intersections, so right now a high density value means a less dense level
	private int openness = 10;		//max height/width of a room
	private int accessibility = 3;
	
	private List<Node> nodes = new ArrayList<Node>();
	
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
				dungeonMap[i][j] = '#';
			}
		}
		
		Logger.debug("generating nodes");
		generateNodes(dungeonMap);
		
		Logger.debug("connecting nodes");
		for (int i = 0; i < accessibility; i++)
			connectNodes();
		
		Logger.debug("digging corridors");
		digConnections(dungeonMap);
		
		Logger.debug("digging rooms");
		expandAndDigRooms(dungeonMap);
		
		Logger.debug("adding stairs");
		addStairs(dungeonMap);
		
		Logger.debug("done!");
		
		return dungeonMap;
	}

	private void digConnections(char[][] dungeonMap)
	{
		//right now, double paths are dug - both two and from both origin and target
		for (Node node : nodes)
		{
			for (Node nodeToDigTo : node.getConnections())
			{
				Line line = new Line(node.getCoords().x, node.getCoords().y, nodeToDigTo.getCoords().x, nodeToDigTo.getCoords().y);
				
				for (Point point : line.getPoints())
					dungeonMap[point.x][point.y] = '.';
			}
		}
	}

	private void generateNodes(char[][] dungeonMap)
	{
		int dungeonAverageSize = (mapHeight + mapWidth) / 2;			//20x80 would mean an average size of 50 (100 / 2)
		int nodeCount =  (size / (dungeonAverageSize / 10));	//size 100 means 20 intersections for average size of 50
		
		int iterations = 0;
		int densityModifier = 0;
		
		while (nodes.size() < nodeCount)
		{
			iterations++;
			
			if (iterations % 1000 == 0)
			{
				Logger.debug(" On iteration " + iterations + ", " + nodes.size() + " of " + nodeCount + " generated.");
				densityModifier--;
			}
			
			Point intersection = generateIntersection(densityModifier);
			
			if (intersection == null)
				continue;
			
			densityModifier = 0;
			
			dungeonMap[intersection.x][intersection.y] = '.';
			nodes.add(new Node(intersection));
		}
	}

	private void connectNodes()
	{
		for (Node node : nodes)
		{
			if (node.getTotalConnections() > accessibility)		//no more connections than accessibility
				continue;
			
			Node closestNode = getClosestUnconnectedNode(node);
			if (closestNode != null && (node.getTotalConnections() == 0 || RPGlib.randInt(1, accessibility) != 1))
				node.connectTo(closestNode);
		}
	}
	
	private Node getClosestUnconnectedNode(Node node)
	{
		Node closestNode = null;
		int shortestDistance = mapHeight * mapWidth;
		
		for (Node targetNode : nodes)
		{
			if(node == targetNode)
				continue;
			
			if (node.isConnectedTo(targetNode))
				continue;
			
			int distance = node.getDistance(targetNode);
			if (distance < shortestDistance)
			{
				closestNode = targetNode;
				shortestDistance = distance;
			}
		}
		
		return closestNode;
	}

	private Point generateIntersection(int densityModifier)
	{
		int roomRadius = (openness / 2) + 1;
		
		int row = RPGlib.randInt(roomRadius, mapHeight - roomRadius);
		int col = RPGlib.randInt(roomRadius, mapWidth - roomRadius);

		for (Node node : nodes)
		{
			Point point = node.getCoords();
			if (RPGlib.distance(point.x, point.y, row, col) < density + densityModifier)
				return null;
		}
		
		return new Point(row, col);
	}
	
	private void expandAndDigRooms(char[][] dungeonMap)
	{
		List<Node> nodesAndRooms = new ArrayList<Node>();
		
		for (Node node : nodes)
		{
			//this commented out code makes it so that there's a chance to not generate a room at a node
//			if (RPGlib.randInt(1,  6) == 6)
//			{
//				nodesAndRooms.add(node);
//				continue;
//			}
			
			int radius = openness / 2;
			int leftWall = node.getCoords().x - RPGlib.randInt(1, radius);
			int rightWall = node.getCoords().x + RPGlib.randInt(1, radius);
			int topWall = node.getCoords().y - RPGlib.randInt(1, radius);
			int bottomWall = node.getCoords().y + RPGlib.randInt(1, radius);
			
			Rectangle area = new Rectangle(leftWall, topWall, rightWall - leftWall, bottomWall - topWall);
			EmptySquareRoom room = new EmptySquareRoom(node, area);
			nodesAndRooms.add(room);
			
			for (Point tile : room.getTiles())
			{
				dungeonMap[tile.x][tile.y] = '.'; 
			}
		}
		
		nodes = nodesAndRooms;
	}

	private void addStairs(char[][] dungeonMap)
	{
		int minStairDistance = openness * 4;
		int iterations = 0;
		
		while (true)
		{
			iterations++;
			
			if (iterations % 1000 == 0)
			{
				Logger.debug(" On iteration " + iterations + ", reducing minimum stair distance.");
				minStairDistance--;
			}
			
			Node upNode = nodes.get(RPGlib.randInt(0, nodes.size() - 1));
			Node downNode = nodes.get(RPGlib.randInt(0, nodes.size() - 1));
			
			if (upNode.getDistance(downNode) < minStairDistance)
				continue;
			
			dungeonMap[upNode.getCoords().x][upNode.getCoords().y] = '<';
			dungeonMap[downNode.getCoords().x][downNode.getCoords().y] = '>';
			return;
		}
	}

	@Override
	public ZoneType getZoneType()
	{
		return ZoneType.RIFT;
	}
}