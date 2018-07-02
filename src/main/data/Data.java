package main.data;

import java.awt.Point;
import java.util.List;

import javax.swing.JOptionPane;

import main.data.event.Event;
import main.entity.actor.Actor;
import main.entity.actor.ActorFactory;
import main.entity.actor.ActorType;
import main.entity.feature.Feature;
import main.entity.feature.FeatureFactory;
import main.entity.feature.FeatureType;
import main.entity.item.Inventory;
import main.entity.item.Item;
import main.entity.item.equipment.EquipmentSlot;
import main.entity.tile.Tile;
import main.entity.tile.TileFactory;
import main.entity.tile.TileType;
import main.entity.world.Overworld;
import main.entity.world.WorldTile;
import main.entity.zone.Zone;
import main.entity.zone.ZoneFactory;
import main.entity.zone.ZoneKey;
import main.entity.zone.predefined.PredefinedZone;
import main.entity.zone.predefined.PredefinedZoneLoader;
import main.presentation.Logger;

public class Data
{
	protected Zone currentZone;
	private Overworld overworld;
	private Actor player;

	private DataSaveUtils dataSaveUtils = null;
	List<PredefinedZone> predefinedZones;
	

	private String playerName = null;
	private ActorTurnQueue worldTravelQueue = new ActorTurnQueue();

	private boolean worldTravel = false;

	public Data()
	{
		PredefinedZoneLoader predefinedZoneLoader = new PredefinedZoneLoader();
		predefinedZones = predefinedZoneLoader.loadAllPredefinedZones();
	}

	public void begin(String newPlayerName)
	{
		playerName = newPlayerName;
		dataSaveUtils = new DataSaveUtils(playerName);

		if (!dataSaveUtils.loadSavedGame(this))
			newGame();

		worldTravelQueue.add(player);
	}

	public void newGame()
	{
		SpecialLevelManager.populateSpecialZonesForLevels(predefinedZones);
		
		overworld = new Overworld();

		Actor playerActor = ActorFactory.generateNewActor(ActorType.PLAYER);
		playerActor.setName(playerName);
		updatePlayerWorldCoords(2, 2);
		player = playerActor;

		enterLocalZoneFromWorldTravel();
	}

	public Zone getZoneAtLocation(int x, int y)
	{
		WorldTile tile = overworld.getTile(x, y);
		Zone toRet = ZoneFactory.getInstance().generateNewZone(tile);

		// TODO: if there's a saved field at these coordinates, update it based on the turn cycles that have gone by; otherwise return a random map

		return toRet;
	}

	public Zone getZoneAtLocation(Point coords)
	{
		return getZoneAtLocation(coords.x, coords.y);
	}
	
	public List<PredefinedZone> getPredefinedZones()
	{
		return predefinedZones;
	}

	public Actor getPlayer()
	{
		return player;
	}

	public Tile getGenericTile(TileType tileType)
	{
		return TileFactory.generateNewTile(tileType);
	}

	public Feature getGenericFeature(FeatureType featureType)
	{
		return FeatureFactory.generateNewFeature(featureType);
	}

	public Actor getGenericActor(ActorType actorType)
	{
		return ActorFactory.generateNewActor(actorType);
	}

	public ActorTurnQueue getActorQueue()
	{
		if (isWorldTravel())
			return worldTravelQueue;

		return currentZone.getActorQueue();
	}

	public int getActorIndex(Actor actor)
	{
		if (worldTravel)
			return 0;

		return currentZone.getActorIndex(actor);
	}

	public Actor getActor(int actorIndex)
	{
		if (worldTravel)
			return player;

		return currentZone.getActor(actorIndex);
	}

	public Zone getCurrentZone()
	{
		return currentZone;
	}

	public Overworld getOverworld()
	{
		return overworld;
	}

	public boolean isWorldTravel()
	{
		return worldTravel;
	}

	public void setOverworld(Overworld world)
	{
		this.overworld = world;
	}

	public void setWorldTravel(boolean worldTravel)
	{
		this.worldTravel = worldTravel;
	}

	public void setCurrentZone(Zone zone)
	{
		this.currentZone = zone;
	}

	public void setPlayer(Actor player)
	{
		this.player = player;
	}

	private void updateZone(ZoneKey newZoneKey, Actor actor, boolean goingDown)
	{
		currentZone.removeActor(actor);
		Zone originZone = currentZone;
		
		String newZoneId = "Z" + newZoneKey.getId();
		
		if (dataSaveUtils.isZoneCached(newZoneId))
		{
			currentZone = dataSaveUtils.uncacheZone(newZoneId);
		} else {
			currentZone = ZoneFactory.getInstance().generateNewZone(newZoneKey, goingDown, currentZone);
		}
		
		//because the ZoneKey in the origin zone may be updated by generating a new level, it needs to be cached after level generation, not before
		if (originZone.shouldPersist())
		{
			dataSaveUtils.cacheZone(originZone);
		}
		
		currentZone.addActor(actor, newZoneKey.getEntryPoint());		//TODO: naturally, if there is a null entry point (like when generated a special level with no up staircase), this fails
	}

	// events are assumed to be checked and sanitized by the logic layer, so just
	// act dumbly on whatever comes through
	public void receiveEvent(Event event)
	{
		if (event == null)
		{
			System.out.println("Data - NULL event received.");
			return;
		}

		Actor actor = getActor(event.getFlag(0));
		actor.increaseTicksLeftBeforeActing(event.getActionCost());

		switch (event.getEventType())
		{
		case WAIT:
			break;	//the only thing that happens here is the action cost reduction, which is already done
		case DEATH:
			killActor(actor);
			break;
			
		case LOCAL_MOVE:
			updateActorLocalCoords(actor, event.getFlag(1), event.getFlag(2));
			break;

		case WORLD_MOVE:
			updatePlayerWorldCoords(event.getFlag(1), event.getFlag(2));
			break;
			
		case ATTACK:
			damageActor(getActor(event.getFlag(1)), event.getFlag(2));
			break;
			
		case PICKUP:
			pickupItem(actor);
			break;
			
		case DROP:
			dropItem(actor, event.getFlag(1));
			break;
			
		case EQUIP:
			equipItem(actor, event.getFlag(1), event.getFlag(2));
			break;
			
		case UNEQUIP:
			unequipItem(actor, event.getFlag(1));
			break;
			
		case CHANGE_ITEM_HP:
			changeItemHp(actor, event);
		break;
			
		case ZONE_TRANSITION:
			Point playerLocation = currentZone.getCoordsOfActor(actor);
			ZoneKey zoneKey = currentZone.getZoneKey(playerLocation);
			updateZone(zoneKey, actor, (event.getFlag(1) == 1));
			break;

		case ENTER_LOCAL:
			if (!worldTravel)
				break;
			enterLocalZoneFromWorldTravel();
			break;

		case ENTER_WORLD:
			worldTravel = true;
			//TODO: cache the currentZone if it's permanent, remove the player as an actor, etc.
			break;

		case SAVE_GAME:
			dataSaveUtils.saveGameData(overworld, currentZone, player, worldTravel);
			break;

		case EXIT_GAME:
			dataSaveUtils.deleteCacheDir();
			System.exit(0);
			break;
		}
	}

	private void changeItemHp(Actor actor, Event event)
	{
		//if flag 0 (actor index) is -1, then flags 1 and 2 are the coordinates of the item
		//otherwise, the item is held by an actor, either worn or in the pack
		// flag 1 is the equipment slot index (negative means it's in pack)
		// flag 2 is the inventory slot index (negative means it's equipped)
		// flag 3 is the amount to change the item's hp
		
		Item item;
		int flag1 = event.getFlag(1);
		int flag2 = event.getFlag(2);
		int flag3 = event.getFlag(3);
		
		if (actor == null)
			item = currentZone.getTile(flag1, flag2).getItemHere();
		else if (flag1 != -1)
			item = actor.getEquipment().getItem(flag1);
		else
			item = actor.getInventory().get(flag1);
		
		try {
			item.changeCurHp(flag3);
		} catch (NullPointerException npe)
		{
			Logger.error("Null pointer exception caught when changing item HP! Actor: " + actor + ", Flag 1: " + flag1 + ", Flag 2: " + flag2 + ", Flag 3: " + flag3);
			return;
		}
		
		if (item.getCurHp() > 0)
			return;
		
		//else item is destroyed
		if (actor == null)
			currentZone.getTile(flag1, flag2).setItemHere(null);
		else if (flag1 != -1)
			actor.getEquipment().removeItem(flag1);
		else
			actor.getInventory().remove(flag1);
	}

	private void unequipItem(Actor actor, int slotIndex)
	{
		EquipmentSlot slot = actor.getEquipment().getEquipmentSlots().get(slotIndex);
		Inventory inventory = actor.getInventory();
		
		inventory.add(slot.removeItem());
	}

	private void equipItem(Actor actor, int slotIndex, int itemIndex)
	{
		EquipmentSlot slot = actor.getEquipment().getEquipmentSlots().get(slotIndex);
		Inventory inventory = actor.getInventory();
		
		Item item = inventory.remove(itemIndex);	//at this point, any filtering should still be active on the inventory, since it's the act of getting or removing the item that resets it
		Item itemToEquip = item.split(1);
		
		slot.setItem(itemToEquip);
		
		if (item.getAmount() > 0)
			inventory.add(item);
	}

	private void dropItem(Actor actor, int itemIndex)
	{
		Point coordsToDropItem = getClosestOpenTileCoords(currentZone.getCoordsOfActor(actor));
		Item itemToDrop = actor.removeItem(itemIndex);
		currentZone.getTile(coordsToDropItem).setItemHere(itemToDrop);
	}

	private void dropItem(Point coords, Item item)
	{
		Point coordsToDropItem = getClosestOpenTileCoords(coords);
		currentZone.getTile(coordsToDropItem).setItemHere(item);
	}

	private void pickupItem(Actor actor)
	{
		Tile tile = currentZone.getTile(actor);
		Item item = tile.getItemHere();
		tile.setItemHere(null);
		actor.receiveItem(item);
	}

	private void killActor(Actor actor)
	{
		if (actor == player)
		{
			//TODO: ideally refresh the GUI so the messages get displayed
			JOptionPane.showMessageDialog(null,"You have been killed.", "Game Over", JOptionPane.ERROR_MESSAGE);
			receiveEvent(Event.exitEvent());
		}
		
		dropAllItems(actor);
		currentZone.removeActor(actor);
	}

	private void dropAllItems(Actor actor)
	{
		Point coords = currentZone.getCoordsOfActor(actor);
		
		List<EquipmentSlot> equipment = actor.getEquipment().getEquipmentSlots();
		
		for (EquipmentSlot slot : equipment)
		{
			Item item = slot.getItem();
			
			if (item != null)
				dropItem(coords, item);
			
			slot.setItem(null);	//probably unncessary, but it can't hurt
		}
		
		for (Item item : actor.getInventory())
			dropItem(coords, item);
		
		actor.getInventory().clear();
	}

	private void enterLocalZoneFromWorldTravel()
	{
		Actor actor = player;
		currentZone = getZoneAtLocation(overworld.getPlayerCoords());
		currentZone.addActor(actor, new Point(12, 35));
		worldTravel = false;
	}

	private void updateActorLocalCoords(Actor actor, int x, int y)
	{
		currentZone.updateActorCoords(actor, new Point(x, y));
	}

	private void updatePlayerWorldCoords(int x, int y)
	{
		Logger.debug("Data.java - Updating world coordinates to (" + x + ", " + y + ").");

		currentZone = null;
		overworld.setPlayerCoords(new Point(x, y));
	}

	private void damageActor(Actor actor, int damage)
	{
		actor.setCurHp(actor.getCurHp() - damage);
	}
	
	//TODO: since this doesn't check for LOS, it's possible for items to get dropped behind solid wall.  generate a sight map and treat all blocked tiles as obstructions
	private Point getClosestOpenTileCoords(Point coords)
	{
		int maxDropRange = 10;
		
		for (int radiusFromPoint = 0; radiusFromPoint <= maxDropRange; radiusFromPoint++)
		{
			for (int row = -1 * radiusFromPoint; row <= radiusFromPoint; row++)
			{
				for (int column = -1 * radiusFromPoint; column <= radiusFromPoint; column++)
				{
					if (Math.abs(row) != radiusFromPoint && Math.abs(column) != radiusFromPoint)
						continue;	//only go around the border, since everything else has been checked
					
					Point coordsToCheck = new Point(coords.x + row, coords.y + column);
					
					if (!currentZone.getTile(coordsToCheck).obstructsItem())
						return coordsToCheck;
				}
			}
		}
		
		throw new IllegalStateException("No empty tile found to place item");
	}
}