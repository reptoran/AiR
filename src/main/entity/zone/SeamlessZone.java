package main.entity.zone;

import java.awt.Point;

import main.entity.tile.Tile;
import main.entity.world.Overworld;

public class SeamlessZone extends Zone
{
	private Overworld world;
	private Zone[][] zoneGrid;
	
	public SeamlessZone(Overworld world, int height, int width)
	{
		super(null, null, height, width, 0);
		
		this.world = world;
		zoneGrid = new Zone[3][3];
	}
	
	public void updateToLocation(int row, int col)
	{
		if (row < 1 || col < 1 || row > world.getHeight() - 2 || col > world.getWidth() - 2)
		{
			throw new IllegalArgumentException("Cannot update to location at (" + row + ", " + col + ")");
		}
		
		for (int i = -1; i <= 1; i++)
		{
			for (int j = -1; j <= 1; j++)
			{
				zoneGrid[i][j] = ZoneFactory.getInstance().generateNewZone(world.getTile(row + i, col + j));		//TODO: get saved zones if they exist, blend edges
			}
		}
	}
	
	//TODO: the idea here is to treat this as a standard 40x160 (or whatever) zone to anyone calling the class, but in reality, nine zones are loaded up
	//this means all coords values are pushed 40 and 160 tiles so that they actually occur in the center zone
	//I'm foreseeing an issue with handling actors on surrounding zones, but we'll deal with that later
	
	@Override
	public void setTurn(int turn)
	{
		zoneGrid[1][1].setTurn(turn);
	}
	
	@Override
	public String getName()
	{
		return zoneGrid[1][1].getName();
	}
	
	@Override
	public int getHeight()
	{
		return zoneGrid[1][1].getHeight();
	}
	
	@Override
	public int getWidth()
	{
		return zoneGrid[1][1].getWidth();
	}
	
	@Override
	public Tile getTile(int row, int column)
	{
		if (row < 0 || row >= getHeight() || column < 0 || column >= getWidth())
		{
			return null;
		}
		
		return zoneGrid[1][1].getTile(row, column);
	}
	
	@Override
	public Tile getTile(Point coords)
	{
		return getTile(coords.x, coords.y);
	}
	
	@Override
	public void setTile(int row, int column, Tile tile)
	{
		zoneGrid[1][1].setTile(row, column, tile);
	}

	@Override
	public void setTile(Point coords, Tile tile)
	{
		setTile(coords.x, coords.y, tile);
	}
}
