package main.presentation.curses;

import java.awt.Color;
import java.awt.Point;

import main.entity.actor.Actor;
import main.entity.tile.Tile;
import main.entity.world.Overworld;
import main.entity.world.WorldTile;
import main.entity.zone.Zone;
import main.logic.Engine;
import main.presentation.curses.terminal.CursesTerminal;

public class CursesGuiDisplay extends CursesGuiUtil
{
	private static final Color BORDER_COLOR = Color.LIGHT_GRAY;
	private static final Color PLAYER_INFO_COLOR = Color.LIGHT_GRAY;

	private int displayStartRow = 2;
	private int displayStartCol = 0;
	private int displayHeight = 21;
	private int displayWidth = 80;
	
	private Engine engine;
	
	public CursesGuiDisplay(Engine engine, CursesTerminal terminal)
	{
		super(terminal);
		
		this.engine = engine;
	}
	
	protected void printBorders()
	{
		// window frame
		for (int i = 0; i < 25; i++)
		{
			for (int j = 0; j < 80; j++)
			{
				if (i == 0 || i == 3 || i == 24 || j == 0 || j == 79)
					terminal.print(j, i, "#", BORDER_COLOR);
			}
		}
	}
	
	protected void updateMap()
	{
		Point center;	//TODO: the map always centers on the player, even on the edges, but perhaps allow the option of moving the player if the map is small enough
		
		if (engine.isWorldTravel())
			center = engine.getOverworld().getPlayerCoords();
		else
			center = engine.getCurrentZone().getCoordsOfActor(engine.getData().getPlayer());
		
		int widthRadius = displayWidth / 2;
		int heightRadius = displayHeight / 2;
		int mapStartRow = center.x - heightRadius;
		int mapStartCol = center.y - widthRadius;

		for (int i = displayStartRow; i < displayStartRow + displayHeight; i++)
		{
			for (int j = displayStartCol; j < displayStartCol + displayWidth; j++)
			{
				// make sure we're in the window
				if (i < 0 || j < 0 || i > 24 || j > 79)
					continue;

				// updateTile() is assumed to do bounds checking for the field itself
				if (engine.isWorldTravel())
					updateWorldTile(new Point(i, j), new Point(i + mapStartRow - displayStartRow, j + mapStartCol - displayStartCol));
				else
					updateLocalTile(new Point(i, j), new Point(i + mapStartRow - displayStartRow, j + mapStartCol - displayStartCol));
			}
		}
		
		terminal.refresh();
	}

	private void updateLocalTile(Point windowCoords, Point zoneCoords)
	{
		Zone localMap = engine.getCurrentZone();

		int row = windowCoords.x;
		int col = windowCoords.y;

		Tile tile = localMap.getTile(zoneCoords.x, zoneCoords.y);

		// display nothing by default
		int fg = 0;
		int bg = 0;
		String icon = " ";

		// print the tile at this location (getColor() and getIcon() within Tile check the features, actors, etc.
		if (tile != null)
		{
			fg = tile.getColor();
			icon = "" + tile.getIcon();
		}

		terminal.print(col, row, icon, fg, bg);
	}

	private void updateWorldTile(Point windowCoords, Point worldCoords)
	{
		Overworld overworldMap = engine.getOverworld();

		int row = windowCoords.x;
		int col = windowCoords.y;

		WorldTile tile = overworldMap.getTile(worldCoords.x, worldCoords.y);

		// display nothing by default
		int fg = 0;
		int bg = 0;
		String icon = " ";

		// print the tile at this location
		if (tile != null)
		{
			fg = tile.getColor();
			icon = "" + tile.getIcon();
			
			Actor player = engine.getData().getPlayer();
			Point playerCoords = overworldMap.getPlayerCoords();
			
			if (playerCoords.x == worldCoords.x && playerCoords.y == worldCoords.y)
			{
				fg = player.getColor();
				icon = "" + player.getIcon();
			}
		}

		terminal.print(col, row, icon, fg, bg);
	}

	protected void showPlayerInfo()
	{
		clearPlayerInfoArea();
		
		Actor player = engine.getData().getPlayer();
		Zone localMap = engine.getCurrentZone();
		
		terminal.print(0, 23, player.getName(), PLAYER_INFO_COLOR);
		terminal.print(10, 23, "HP: " + player.getCurHp() + "/" + player.getMaxHp(), PLAYER_INFO_COLOR);
		terminal.print(0, 24, "Depth: " + localMap.getDepth(), PLAYER_INFO_COLOR);
		
		terminal.refresh();
	}
	
	private void clearPlayerInfoArea()
	{
		for (int i = 23; i < 24; i++)
		{
			for (int j = 0; j < 80; j++)
			{
				terminal.print(j, i, " ", Color.BLACK);
			}
		}
		
		terminal.refresh();
	}
}
