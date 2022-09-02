package main.entity.zone.generator.dungeon.rifts;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public abstract class Room extends Node
{
	protected Rectangle area;
	protected List<Point> tiles = new ArrayList<Point>();
	
	public Room(Node node, Rectangle area)
	{
		super(node.coords);
		for (Node connection : node.connections)
			connections.add(connection);
		
		this.area = area;
		
		generateRoom();
	}
	
	public List<Point> getTiles()
	{
		return tiles;
	}

	abstract void generateRoom();
}
