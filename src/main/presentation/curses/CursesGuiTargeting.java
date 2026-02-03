package main.presentation.curses;

import java.awt.Point;
import java.awt.event.KeyEvent;

import main.entity.actor.Actor;
import main.entity.item.Item;
import main.entity.item.ItemType;
import main.entity.item.equipment.Equipment;
import main.entity.tile.Tile;
import main.logic.Direction;
import main.logic.Engine;
import main.logic.TargetingManager;
import main.logic.combat.CombatAttackCalculator;
import main.presentation.GuiState;

public class CursesGuiTargeting extends ColorSchemeCursesGuiUtil
{
	private CursesGui parentGui;
	private Engine engine;
	private TargetingManager targetingManager;
	
	private Point lastReticleScreenCoords;
	
	public CursesGuiTargeting(CursesGui parentGui, Engine engine, ColorScheme colorScheme)
	{
		super(colorScheme);
		
		this.parentGui = parentGui;
		this.engine = engine;
		this.targetingManager = TargetingManager.getInstance();
		
		lastReticleScreenCoords = new Point(0, 0);
	}

	@Override
	public void refresh()
	{
		drawTargetingMessage();
		drawReticle();
		drawTargetHitpoints();
	}

	public void reset()
	{
		targetingManager.deactivateTargeting();
		clearScreen();
	}
	
	public void startTargeting()
	{
		targetingManager.activateTargeting(engine.getData().getPlayer(), engine.getCurrentZone());
		parentGui.refreshInterface();
	}
	
	public void endTargeting()
	{
		reset();
		parentGui.setSingleLayer(GuiState.MAIN_GAME);
	}

	private void drawTargetingMessage()
	{
		if (!targetingManager.isActive())
			return;
		
		Point target = targetingManager.getReticleCoords();
		Tile tile = engine.getCurrentZone().getTile(target);
		
		String targetLabel;
		
		if (!tile.isSeen())
			targetLabel = "unknown";
		else if (tile.getActorHere() != null && tile.isVisible())
			targetLabel = tile.getActorHere().getName();
		else if (tile.getItemHere() != null && tile.isVisible())
			targetLabel = tile.getItemHere().getName();
		else if (tile.getFeatureHere() != null)
			targetLabel = tile.getFeatureHere().getName();
		else
			targetLabel = tile.getName();
		
		int initialOffset = targetLabel.length();
		int fireColor = getShadeColor();
		
		if (fireOptionEnabled())
			fireColor = getTextColor();
		
		clearLineEnd();
		addText(0, 0, targetLabel, getHighlightColor());
		addText(0, initialOffset, " [M] More [T] Next [DIR] Move ", getTextColor());
		addText(0, initialOffset + 30, "[F] Fire", fireColor);
		
		for (int i = 0; i < 3; i++)
		{
			drawMagicOptions(initialOffset + 38 + (7 * i), i);
		}
	}

	private void clearLineEnd()
	{
		for (int i = 57; i < 80; i++)
			clearCharacter(0, i);
	}
	
	private void drawMagicOptions(int startCol, int index)
	{
		// TODO Auto-generated method stub
		Equipment magicItems = engine.getData().getPlayer().getMagicItems();
		Item item = magicItems.getItem(index);
		
		int optionColor = getShadeColor();
		int itemColor = Colors.BLACK;
		String itemString = " ";	
		
		if (item != null)
		{
			optionColor = getTextColor();
			itemColor = item.getColor();
			itemString = "" + item.getIcon();
		}
		
		addText(0, startCol, " [F" + String.valueOf(index + 1) + "] ", optionColor);
		addText(0, startCol + 6, itemString, itemColor);
	}
	
	private void drawReticle()
	{
		if (!targetingManager.isActive())
			return;
		
		Actor player = engine.getData().getPlayer();
		Point center = engine.getCurrentZone().getCoordsOfActor(player);
		
		int widthRadius = CursesGuiMainGameDisplay.DISPLAY_WIDTH / 2;
		int heightRadius = CursesGuiMainGameDisplay.DISPLAY_HEIGHT / 2;
		int startRow = center.x - heightRadius - CursesGuiMainGameDisplay.DISPLAY_START_ROW;
		int startCol = center.y - widthRadius - CursesGuiMainGameDisplay.DISPLAY_START_COL;
		
		Point target = targetingManager.getReticleCoords();
		lastReticleScreenCoords.x = target.x - startRow;
		lastReticleScreenCoords.y = target.y - startCol;
		int reticleColor = getTitleColor();
		
		Tile tile = engine.getData().getCurrentZone().getTile(target);
		Actor targetActor = getTargettedActor();
		
		if (targetActor != null && CombatAttackCalculator.getInstance().isCriticalHit(player, targetActor))
			reticleColor = TargetingManager.RETICLE_TARGET_CRITICAL_COLOR;
		else if (targetActor != null)
			reticleColor = TargetingManager.RETICLE_TARGET_COLOR;
		else if (!tile.isVisible())
			reticleColor = getBorderColor();
		
		addText(lastReticleScreenCoords.x, lastReticleScreenCoords.y, String.valueOf(TargetingManager.RETICLE_ICON), reticleColor);
	}
	
	//TODO: this is code duplication of the method in CursesGuiMainGameDisplay; commonize it if possible
	private void drawTargetHitpoints()
	{
		addText(23, 68, "[          ]", CursesGuiMainGameDisplay.PLAYER_INFO_COLOR);
		
		Actor target = getTargettedActor();
		
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
	
	private Actor getTargettedActor()
	{
		Point targetCoords = targetingManager.getReticleCoords();
		Actor targetActor = engine.getCurrentZone().getActorAtCoords(targetCoords);
		Tile targetTile = engine.getCurrentZone().getTile(targetCoords);
		
		if (targetTile == null || !targetTile.isVisible())
			return null;
		
		return targetActor;
	}

	@Override
	protected void handleKey(int code, char keyChar)
	{	
		if (code == KeyEvent.VK_ESCAPE)
		{
			endTargeting();
			return;
		} else if (code == KeyEvent.VK_NUMPAD1 || code == KeyEvent.VK_END)
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
		} else if (keyChar == 't')
		{
			clearReticle();
			targetingManager.nextTarget();
		} else if (keyChar == 'f' && fireOptionEnabled())
		{
			handleFiring();
		} else if (code == KeyEvent.VK_F1 || code == KeyEvent.VK_F2 || code == KeyEvent.VK_F3)
		{
//			handleFunctionKey(code, keyChar);
		}
		
		parentGui.refreshInterface();
	}

	private void handleDirection(Direction direction)
	{
		clearReticle();	
		targetingManager.moveReticle(direction);
	}
	
	private void handleFiring()
	{
		//TODO: do something with lastReticleScreenCoords
		endTargeting();
	}
	
	private void clearReticle()
	{
		if (lastReticleScreenCoords != null)
			clearCharacter(lastReticleScreenCoords.x, lastReticleScreenCoords.y);
	}
	
	private boolean fireOptionEnabled()
	{
		Actor player = engine.getData().getPlayer();
		if (player.getComponents().getFirstItemOfType(ItemType.AMMO) == null)
			return false;
		
		//TODO: if player is holding a weapon that can fire ammunition
		
		return false;
	}
}
