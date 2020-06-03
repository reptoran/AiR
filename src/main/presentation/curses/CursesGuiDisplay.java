package main.presentation.curses;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.List;

import main.entity.actor.Actor;
import main.entity.item.Item;
import main.entity.item.equipment.EquipmentSlot;
import main.entity.tile.Tile;
import main.entity.world.Overworld;
import main.entity.world.WorldTile;
import main.entity.zone.Zone;
import main.logic.Engine;
import main.presentation.Logger;
import main.presentation.curses.terminal.CursesTerminal;

public class CursesGuiDisplay extends CursesGuiUtil
{
	private static final int BORDER_COLOR = COLOR_LIGHT_GREY;
	private static final int PLAYER_INFO_COLOR = COLOR_LIGHT_GREY;

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
	
	@Override
	public void refresh()
	{
//		printBorders();
		terminal.clear();
		updateMap();
		showPlayerInfo();
		terminal.refresh();
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

	private void clearPlayerInfoArea()
	{
		for (int i = 23; i < 24; i++)
		{
			for (int j = 0; j < 80; j++)
			{
				terminal.print(j, i, " ", Color.BLACK);
			}
		}
	}

	protected void showPlayerInfo()
	{
		clearPlayerInfoArea();
		
		Actor player = engine.getData().getPlayer();
		Zone localMap = engine.getCurrentZone();
		
		terminal.print(0, 23, player.getName(), PLAYER_INFO_COLOR);
		terminal.print(12, 23, "HP: " + player.getCurHp() + "/" + player.getMaxHp(), PLAYER_INFO_COLOR);
		terminal.print(0, 24, "Depth: " + localMap.getDepth(), PLAYER_INFO_COLOR);
		
		displayEquipmentCondition(player);
		displayMagicItems(player);
		displayTargetHitpoints();
	}
	
	private void displayTargetHitpoints()
	{
		int curHp = 0;
		int totalHp = 1;
		
		Actor target = engine.getTarget();
		
		if (target != null)
		{
			curHp = target.getCurHp();
			totalHp = target.getMaxHp();
		}
		
		int percentage = (int)(((((double)curHp) / ((double)totalHp)) * 10) + .5);
		if (percentage < 1 && curHp > 0)
			percentage = 1;
			
		Logger.debug("Target HP is " + curHp + "/" + totalHp + "; percentage is " + percentage + ".");
		
		int hpColor = COLOR_DARK_RED;
		
		if (percentage > 2)
			hpColor = COLOR_LIGHT_RED;
		if (percentage > 4)
			hpColor = COLOR_YELLOW;
		if (percentage > 6)
			hpColor = COLOR_LIGHT_GREEN;
		if (percentage > 8)
			hpColor = COLOR_DARK_GREEN;
		
		String hpGraph = "";
		StringBuilder builder = new StringBuilder(hpGraph);
		for (int i = 0; i < percentage; i++) {
		    builder.append("*");
		}
		
		terminal.print(68, 23, "[          ]", PLAYER_INFO_COLOR);
		terminal.print(69, 23, builder.toString(), hpColor);
	}
	
	private void displayEquipmentCondition(Actor player)
	{
		List<EquipmentSlot> slots = player.getEquipment().getEquipmentSlots();
		
		for (int i = 0; i < slots.size(); i++)
		{
			EquipmentSlot slot = slots.get(i);
			String slotName = slot.getShortName();
			
			int nameColumn = (14 * (i + 1)) - 2;
			int conditionColumn = nameColumn + slotName.length() + 2;
			
			terminal.print(nameColumn, 24, slotName + ":", PLAYER_INFO_COLOR);
			terminal.print(conditionColumn, 24, getItemConditionString(slot.getItem()), getItemConditionColor(slot.getItem()));
		}
	}
	
	private void displayMagicItems(Actor player)
	{
		List<EquipmentSlot> slots = player.getMagicItems().getEquipmentSlots();
		
//		terminal.print(54, 24, "Magic: ", BORDER_COLOR);
		
		for (int i = 0; i < slots.size(); i++)
		{
			EquipmentSlot slot = slots.get(i);
			Item item = slot.getItem();
			int col = 65 + (5 * i);
			
//			terminal.print(col, 24, "[F" + (i + 1) + "]", BORDER_COLOR);
			terminal.print(col, 24, "F" + (i + 1) + ":", BORDER_COLOR);
			
			if (item != null)
				terminal.print(col + 3, 24, "" + item.getIcon(), item.getColor());
		}
		
		terminal.print(64, 24, "[", BORDER_COLOR);
		terminal.print(79, 24, "]", BORDER_COLOR);
	}

	private String getItemConditionString(Item item)
	{
		if (item == null)
			return "N/A";
		
		return item.getConditionString();
	}

	private int getItemConditionColor(Item item)
	{
		if (item == null)
			return PLAYER_INFO_COLOR;
		
		if (item.getConditionModifer() > .75)
			return COLOR_LIGHT_GREEN;
		
		if (item.getConditionModifer() > .5)
			return COLOR_DARK_GREEN;
		
		if (item.getConditionModifer() > .25)
			return COLOR_YELLOW;
		
		return COLOR_LIGHT_RED;
	}

	@Override
	public void handleKeyEvent(KeyEvent ke)
	{
		return;	//nothing to do here
	}
}
