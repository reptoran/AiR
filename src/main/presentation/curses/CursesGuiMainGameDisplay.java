package main.presentation.curses;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.List;

import main.data.GameSettings;
import main.data.PlayerAdvancementManager;
import main.data.SettingType;
import main.data.event.ActorCommand;
import main.entity.FieldCoord;
import main.entity.actor.Actor;
import main.entity.item.Item;
import main.entity.item.equipment.EquipmentSlot;
import main.entity.item.recipe.RecipeManager;
import main.entity.tile.Tile;
import main.entity.world.Overworld;
import main.entity.world.WorldTile;
import main.entity.zone.Zone;
import main.logic.Direction;
import main.logic.Engine;
import main.presentation.GuiState;
import main.presentation.Logger;
import main.presentation.curses.inventory.CursesGuiCompleteInventory;
import main.presentation.curses.inventory.InventoryState;
import main.presentation.message.MessageBuffer;

public class CursesGuiMainGameDisplay extends ColorSchemeCursesGuiUtil
{
	//TODO: base the colors on the color scheme
	private static final int BORDER_COLOR = Colors.LIGHT_GREY;
//	private static final int PLAYER_INFO_LABEL_COLOR = Colors.DARK_GREY;
	public static final int PLAYER_INFO_COLOR = Colors.LIGHT_GREY;

	public static final int DISPLAY_START_ROW = 2;
	public static final int DISPLAY_START_COL = 0;
	public static final int DISPLAY_HEIGHT = 21;
	public static final int DISPLAY_WIDTH = 80;
	
	private CursesGui parentGui;
	private CursesGuiCompleteInventory inventoryScreen;
	private Engine engine;
	
//	private ActorCommand pendingCommand = null;
	
	public CursesGuiMainGameDisplay(CursesGui parentGui, CursesGuiCompleteInventory inventoryScreen, Engine engine, ColorScheme colorScheme)
	{
		super(colorScheme);
		this.parentGui = parentGui;
		this.inventoryScreen = inventoryScreen;
		this.engine = engine;
		this.messageHandler = new CursesGuiMessages(parentGui, new Rectangle(0, 0, 80, 2));
	}
	
	@Override
	public void refresh()
	{
//		printBorders();
		clearScreen();
		updateMap();
		showPlayerInfo();
	}
	
	protected void printBorders()
	{
		// window frame
		for (int i = 0; i < 25; i++)
		{
			for (int j = 0; j < 80; j++)
			{
				if (i == 0 || i == 3 || i == 24 || j == 0 || j == 79)
					addText(i, j, "#", BORDER_COLOR);
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
		
		int widthRadius = DISPLAY_WIDTH / 2;
		int heightRadius = DISPLAY_HEIGHT / 2;
		int mapStartRow = center.x - heightRadius;
		int mapStartCol = center.y - widthRadius;

		for (int i = DISPLAY_START_ROW; i < DISPLAY_START_ROW + DISPLAY_HEIGHT; i++)
		{
			for (int j = DISPLAY_START_COL; j < DISPLAY_START_COL + DISPLAY_WIDTH; j++)
			{
				// make sure we're in the window
				if (i < 0 || j < 0 || i > 24 || j > 79)
					continue;
				
				Point windowCoords = new Point(i, j);
				Point mapCoords = new Point(i + mapStartRow - DISPLAY_START_ROW, j + mapStartCol - DISPLAY_START_COL);
				
				// updateTile() is assumed to do bounds checking for the field itself
				if (engine.isWorldTravel())
					updateWorldTile(windowCoords, mapCoords);
				else
					updateLocalTile(windowCoords, mapCoords);
			}
		}
		
		if (engine.isWorldTravel())
		{
			Actor player = engine.getData().getPlayer();
			addText(12, 40, String.valueOf(player.getIcon()), player.getColor(), Colors.BLACK);
		}
	}

	private void updateLocalTile(Point windowCoords, Point zoneCoords)
	{
		Zone localMap = engine.getCurrentZone();

		int row = windowCoords.x;
		int col = windowCoords.y;

		Tile tile = localMap.getTile(zoneCoords);
		updateTile(row, col, tile);
	}
	
	private void updateWorldTile(Point windowCoords, Point worldCoords)
	{
		Overworld overworld = engine.getData().getOverworld();
		
		int row = windowCoords.x;
		int col = windowCoords.y;
		
		WorldTile tile = overworld.getTile(worldCoords);
		updateTile(row, col, tile);
	}
	
	private void updateTile(int row, int col, FieldCoord tile)
	{
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

		addText(row, col, icon, fg, bg);
	}

	private void clearPlayerInfoArea()
	{
		for (int i = 23; i < 24; i++)
		{
			for (int j = 0; j < 80; j++)
			{
				addText(i, j, " ", Colors.BLACK);
			}
		}
	}

	protected void showPlayerInfo()
	{
		clearPlayerInfoArea();
		
		Actor player = engine.getData().getPlayer();
		Zone localMap = engine.getCurrentZone();
		
		String depth = "SURFACE";
		
		if (localMap != null)
		{
			depth = String.valueOf(localMap.getDepth());
			displayTargetHitpoints();
		}
		
		int hpColor = player.getHpColor();
		if (hpColor == Colors.LIGHT_GREEN && player.getCurHp() == player.getMaxHp())
			hpColor = PLAYER_INFO_COLOR;
		
		addText(23, 0, player.getName(), PLAYER_INFO_COLOR);
		addText(23, 12, "HP:", PLAYER_INFO_COLOR);
		addText(23, 16, player.getCurHp() + "/" + player.getMaxHp(), hpColor);
		addText(23, 26, "XP:", PLAYER_INFO_COLOR);
		addText(23, 30, PlayerAdvancementManager.getInstance().getXpString(), PLAYER_INFO_COLOR);
		addText(23, 40, "Level:", PLAYER_INFO_COLOR);
		addText(23, 47, String.valueOf(PlayerAdvancementManager.getInstance().getCharacterLevel()), PLAYER_INFO_COLOR);
		addText(24, 0, "Depth:", PLAYER_INFO_COLOR);
		addText(24, 7, depth, PLAYER_INFO_COLOR);
		
		//TODO: add character level, change XP color based on how close you are to next level
		
		displayEquipmentCondition(player);
		displayMagicItems(player);
	}

	private void displayTargetHitpoints()
	{
		addText(23, 68, "[          ]", PLAYER_INFO_COLOR);
		
		Actor target = engine.getTarget();
		
		if (target == null)
			return;
		
		int percentage = target.getHpPercentage();
		
		String hpGraph = "";
		StringBuilder builder = new StringBuilder(hpGraph);
		for (int i = 0; i < percentage; i++) {
		    builder.append("*");
		}
		
		addText(23, 69, builder.toString(), target.getHpColor());
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
			
			addText(24, nameColumn, slotName + ":", PLAYER_INFO_COLOR);
			addText(24, conditionColumn, getItemConditionString(slot.getItem()), getItemConditionColor(slot.getItem()));
		}
	}
	
	private void displayMagicItems(Actor player)
	{
		List<EquipmentSlot> slots = player.getMagicItems().getEquipmentSlots();
		
		for (int i = 0; i < slots.size(); i++)
		{
			EquipmentSlot slot = slots.get(i);
			Item item = slot.getItem();
			int col = 65 + (5 * i);
			
			addText(24, col, "F" + (i + 1) + ":", BORDER_COLOR);
			
			if (item != null)
				addText(24, col + 3, "" + item.getIcon(), item.getColor());
		}
		
		addText(24, 64, "[", BORDER_COLOR);
		addText(24, 79, "]", BORDER_COLOR);
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
			return Colors.LIGHT_GREEN;
		
		if (item.getConditionModifer() > .5)
			return Colors.DARK_GREEN;
		
		if (item.getConditionModifer() > .25)
			return Colors.YELLOW;
		
		return Colors.LIGHT_RED;
	}

	@Override
	protected void handleKey(int code, char keyChar)
	{
		Logger.info("GUI - Key press detected, code is: " + code);
		Logger.info("GUI - Key press detected, char is: " + keyChar);

		if (code == KeyEvent.VK_ESCAPE)
		{
			parentGui.setPendingCommand(null);
			parentGui.refreshInterface();
		} else if (code == KeyEvent.VK_1)
		{
			changeDisplay(DisplayType.STANDARD);
		} else if (code == KeyEvent.VK_2)
		{
			changeDisplay(DisplayType.FOG);
		} else if (code == KeyEvent.VK_3)
		{
			changeDisplay(DisplayType.TACTICAL);
		}
		else if (code == KeyEvent.VK_NUMPAD1 || code == KeyEvent.VK_END)
		{
			handleDirection(Direction.DIRSW);
		} else if (code == KeyEvent.VK_NUMPAD2 || code == KeyEvent.VK_KP_DOWN || code == KeyEvent.VK_DOWN)
		{
			handleDirection(Direction.DIRS);
		} else if (code == KeyEvent.VK_NUMPAD3 || code == KeyEvent.VK_PAGE_DOWN)
		{
			handleDirection(Direction.DIRSE);
		} else if (code == KeyEvent.VK_NUMPAD4 || code == KeyEvent.VK_KP_LEFT || code == KeyEvent.VK_LEFT)
		{
			handleDirection(Direction.DIRW);
		} else if (code == KeyEvent.VK_NUMPAD5 || code == KeyEvent.VK_CLEAR)
		{
			handleDirection(Direction.DIRNONE);
		} else if (code == KeyEvent.VK_NUMPAD6 || code == KeyEvent.VK_KP_RIGHT || code == KeyEvent.VK_RIGHT)
		{
			handleDirection(Direction.DIRE);
		} else if (code == KeyEvent.VK_NUMPAD7 || code == KeyEvent.VK_HOME)
		{
			handleDirection(Direction.DIRNW);
		} else if (code == KeyEvent.VK_NUMPAD8 || code == KeyEvent.VK_KP_UP|| code == KeyEvent.VK_UP)
		{
			handleDirection(Direction.DIRN);
		} else if (code == KeyEvent.VK_NUMPAD9 || code == KeyEvent.VK_PAGE_UP)
		{
			handleDirection(Direction.DIRNE);
		} else if (code == KeyEvent.VK_F1 || code == KeyEvent.VK_F2 || code == KeyEvent.VK_F3)
		{
			handleFunctionKey(code, keyChar);
		} else if (keyChar == '>')
		{
			engine.receiveCommand(ActorCommand.changeZoneDown());
		} else if (keyChar == '<')
		{
			engine.receiveCommand(ActorCommand.changeZoneUp());
		} else if (keyChar == 'i')
		{
			parentGui.setSingleLayer(GuiState.INVENTORY);
		} else if (keyChar == 'q')
		{
			parentGui.setSingleLayer(GuiState.QUEST);
		} else if (keyChar == 'd')
		{
			handleDrop();
		} else if (keyChar == 'u')
		{
			handleUse();
		} else if (keyChar == 'g')
		{
			engine.receiveCommand(ActorCommand.pickUp());
		} else if (keyChar == 't')
		{
			handleTarget();
		} else if (keyChar == 'C')
		{
			handleChatInput();
		} else if (keyChar == 'R')	//r can be to craft recipe (filtering out ones with insufficient components, etc.), R might be to show all known recipes
		{
			handleRecipe();
		} else if (keyChar == 'r')	//note this conflicts with what I suggest for recipes, so see if there's a better way to organize these
		{
			handleRepeatInput();
		}
		if (keyChar == 'S')

		{
			engine.receiveCommand(ActorCommand.save());
			engine.receiveCommand(ActorCommand.exit());
			parentGui.endGame();
		} else if (keyChar == 'Q')
		{
			engine.receiveCommand(ActorCommand.exit());
			parentGui.endGame();
		}
	}

	private void changeDisplay(DisplayType displayType)
	{
		switch(displayType)
		{
		case STANDARD:
			GameSettings.updateSetting(SettingType.SHOW_FOG, false);
			GameSettings.updateSetting(SettingType.SHOW_AWARENESS, false);
			GameSettings.updateSetting(SettingType.SHOW_FACING, false);
			break;
		case FOG:
			GameSettings.updateSetting(SettingType.SHOW_FOG, true);
			GameSettings.updateSetting(SettingType.SHOW_AWARENESS, false);
			GameSettings.updateSetting(SettingType.SHOW_FACING, false);
			break;
		case TACTICAL:
			GameSettings.updateSetting(SettingType.SHOW_FOG, true);
			GameSettings.updateSetting(SettingType.SHOW_AWARENESS, true);
			GameSettings.updateSetting(SettingType.SHOW_FACING, true);
			break;
		default:
			return;
		}
		
		parentGui.refreshInterface();
	}

	private void handleDirection(Direction direction)
	{
		Point coordChange = direction.getCoordChange();
		int rowChange = coordChange.x;
		int colChange = coordChange.y;
		
		ActorCommand pendingCommand = parentGui.getPendingCommand();
		parentGui.setPendingCommand(null);
		
		if (pendingCommand == null)
			pendingCommand = ActorCommand.move(null);	//default to move if there's nothing we're prompting a direction for

		String directionString = Direction.fromCoords(rowChange, colChange).name();
		engine.receiveCommand(pendingCommand.addArgument(directionString));
	}

	private void handleFunctionKey(int code, char keyChar)
	{
		inventoryScreen.setState(InventoryState.USE);
		inventoryScreen.handleKey(code, keyChar);
	}

	private void handleDrop()
	{
		Actor player = engine.getData().getPlayer();

		if (player.getStoredItems().isEmpty() && player.getEquipment().isEmpty() && player.getComponents().isEmpty() && player.getReadiedItems().isEmpty() && player.getMagicItems().isEmpty())
		{
			MessageBuffer.addMessage("You aren't carrying anything!");
			parentGui.refreshInterface();
			return;
		}

		inventoryScreen.setState(InventoryState.DROP);
		parentGui.setSingleLayer(GuiState.INVENTORY);
	}

	private void handleUse()
	{
		Actor player = engine.getData().getPlayer();

		if (player.getStoredItems().isEmpty() && player.getEquipment().isEmpty() && player.getComponents().isEmpty() && player.getReadiedItems().isEmpty() && player.getMagicItems().isEmpty())
		{
			MessageBuffer.addMessage("You have no items to use!");
			parentGui.refreshInterface();
			return;
		}

		inventoryScreen.setState(InventoryState.USE);
		parentGui.setSingleLayer(GuiState.INVENTORY);
	}
	
	private void handleTarget()
	{
		if (engine.isWorldTravel())
		{
			MessageBuffer.addMessage("Cannot target on the overworld.");		//TODO: for now
			parentGui.refreshInterface();
			return;
		}
		
		parentGui.addLayer(GuiState.TARGET, true);
		CursesGuiTargeting targetingGui = (CursesGuiTargeting) parentGui.getScreen(GuiState.TARGET);
		targetingGui.startTargeting();
	}

	private void handleChatInput()
	{
		ActorCommand command = ActorCommand.chat(null);
		Direction direction = engine.determineDirectionOfCommand(command.getType());
		if (direction == Direction.DIRNONE)
			parentGui.promptForDirectionAndSetPendingCommand(command);
		else
			engine.receiveCommand(ActorCommand.chat(direction));
	}
	
	private void handleRepeatInput()
	{
		parentGui.promptForDirectionAndSetPendingCommand(ActorCommand.repeat(null));
	}

	private void handleRecipe()
	{
		Actor player = engine.getData().getPlayer();

		if (RecipeManager.getInstance().getKnownRecipes(player).isEmpty())
		{
			MessageBuffer.addMessage("You don't know any recipes!");
			parentGui.refreshInterface();
			return;
		}

		parentGui.setSingleLayer(GuiState.RECIPE);
	}
}
