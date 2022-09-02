package main.entity.zone.generator.dungeon.rifts;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import main.logic.RPGlib;

public class Node
{
	protected Point coords;
	protected List<Node> connections;
	
	public Node(Point coords)
	{
		this.coords = new Point(coords.x, coords.y);
		connections = new ArrayList<Node>();
	}
	
	public boolean connectTo(Node node)
	{
		if (connections.contains(node))
			return false;
		
		boolean isSuccessful = connections.add(node);
		
		if (isSuccessful)
			node.connectTo(this);
		
		return isSuccessful;
	}
	
	public Point getCoords()
	{
		return new Point(coords.x, coords.y);
	}
	
	public List<Node> getConnections()
	{
		return connections;
	}
	
	public int getTotalConnections()
	{
		return connections.size();
	}
	
	public int getDistance(Node node)
	{
		return RPGlib.distance(coords.x, coords.y, node.coords.x, node.coords.y);
	}
	
	public boolean isConnectedTo(Node node)
	{
		return connections.contains(node);
	}
}
