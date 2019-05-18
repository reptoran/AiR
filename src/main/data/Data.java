package main.data;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import main.data.event.InternalEvent;
import main.data.event.environment.EnvironmentEventQueue;
import main.data.file.TextLoader;
import main.entity.actor.Actor;
import main.entity.actor.ActorFactory;
import main.entity.actor.ActorType;
import main.entity.actor.GenderType;
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
import main.logic.AI.AiType;
import main.presentation.Logger;
import main.presentation.message.FormattedMessageBuilder;
import main.presentation.message.MessageBuffer;

public class Data
{
	protected Zone currentZone;
	private Overworld overworld;
	private ActorType profession = null;
	private Actor player;
	private Actor target = null;

	private DataSaveUtils dataSaveUtils = null;
	private List<PredefinedZone> predefinedZones;
	private Map<String, String> gameTextEntries;

	private String playerName = null;
	private EnvironmentEventQueue worldTravelQueue = new EnvironmentEventQueue();

	private boolean worldTravel = false;

	public Data()
	{
		TextLoader gameTextLoader = new TextLoader();
		PredefinedZoneLoader predefinedZoneLoader = new PredefinedZoneLoader();
		
		gameTextEntries = gameTextLoader.loadAllGameText();
		predefinedZones = predefinedZoneLoader.loadAllPredefinedZones();
	}
	
	public boolean setPlayerNameAndLoadGame(String newPlayerName)
	{
		playerName = newPlayerName;
		dataSaveUtils = new DataSaveUtils(playerName);

		boolean gameHasBeenLoaded = dataSaveUtils.loadSavedGame(this);
		
		if (gameHasBeenLoaded)
			worldTravelQueue.add(player);
		
		return gameHasBeenLoaded;
	}

	public void initializeNewGame()
	{
		SpecialLevelManager.populateSpecialZonesForLevels(predefinedZones);
		
		Point playerWorldCoords = new Point(2, 2);	//this is where the dungeon is located on the overworld
		
		overworld = new Overworld();
		currentZone = getZoneAtLocation(playerWorldCoords);
		
		for (Actor actor : currentZone.getActors())
		{
			if (actor.getType() == profession)
			{
				actor.setAI(AiType.HUMAN_CONTROLLED);
				actor.setGender(GenderType.PLAYER);
				actor.setName(playerName);
				player = actor;
				return;
			}
		}

		//since we're here, no actor matching the chosen profession was found on the map, so create a new one 
		Actor playerActor = ActorFactory.generateNewActor(ActorType.PLAYER);
		playerActor.setName(playerName);
		updatePlayerWorldCoords(playerWorldCoords.x, playerWorldCoords.y);
		player = playerActor;

		enterLocalZoneFromWorldTravel();
		worldTravelQueue.add(player);
	}
	
	public List<String> getAvailableProfessions()
	{
		List<String> professionNames = new ArrayList<String>();
		professionNames.add("Physician");
		professionNames.add("Blacksmith");
		professionNames.add("Wanderer");
		return professionNames;
	}
	
	public void setPlayerProfession(int professionIndex)
	{
		Map<Integer, ActorType> professionMap = new HashMap<Integer, ActorType>();
		professionMap.put(0, ActorType.PC_PHYSICIAN);
		professionMap.put(1, ActorType.PC_SMITH);
		profession = professionMap.get(professionIndex);
		setProfessionBackground(professionIndex);
	}

	private void setProfessionBackground(int professionIndex)
	{
		List<String> professionBackgroundKeys = new ArrayList<String>();
		professionBackgroundKeys.add("PROF_BG_PHYS");
		professionBackgroundKeys.add("PROF_BG_SMITH");
		
		if (professionBackgroundKeys.size() <= professionIndex)
			return;
		
		Actor nameActor = new Actor();
		nameActor.setName(playerName);
		String backgroundMessage = gameTextEntries.get(professionBackgroundKeys.get(professionIndex));
		String formattedMessage = new FormattedMessageBuilder(backgroundMessage).setSource(nameActor).setSourceVisibility(true).format();
		MessageBuffer.addMessage(formattedMessage);	//this will be displayed immediately after the profession is chosen
	}

	public Zone getZoneAtLocation(int x, int y)
	{
		Zone toRet = null;
		WorldTile tile = overworld.getTile(x, y);
		String newZoneId = tile.getZoneId();
		
		if (currentZone != null && currentZone.getUniqueId().equals(newZoneId))	//if we're already here, no need to update anything
		{
			return currentZone;
		} else if (dataSaveUtils.isZoneCached(newZoneId))	//if the current zone is not here, then any saved zone must already be cached
		{
			toRet = dataSaveUtils.uncacheZone(newZoneId);
			//TODO: update the saved zone based on the turn cycles that have gone by
		} else {
			toRet = ZoneFactory.getInstance().generateNewZone(tile);
			
			if (toRet.shouldPersist())
				tile.setZoneId(toRet.getUniqueId());
		}

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
	
	public Actor getTarget()
	{
		return target;
	}
	
	public void setTarget(Actor target)
	{
		this.target = target;
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

	public EnvironmentEventQueue getEventQueue()
	{
		if (isWorldTravel())
			return worldTravelQueue;

		return currentZone.getEventQueue();
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
		
		if (currentZone == null)
			return null;

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
	public void receiveInternalEvent(InternalEvent internalEvent)
	{
		if (internalEvent == null)
		{
			Logger.info("Data - NULL event received.");
			return;
		}
		
		Logger.debug("Data - Internal event came through with an actor index of " + internalEvent.getFlag(0));
		
		Actor actor = getActor(internalEvent.getFlag(0));
//		actor.increaseTicksLeftBeforeActing(internalEvent.getActionCost());		//TODO: done in engine now; remove line once this is verified to be working

		switch (internalEvent.getInternalEventType())
		{
		case WAIT:
			break;	//the only thing that happens here is the action cost reduction, which is already done
		case DEATH:
			killActor(actor);
			break;
			
		case LOCAL_MOVE:
			updateActorLocalCoords(actor, internalEvent.getFlag(1), internalEvent.getFlag(2));
			Logger.debug("Actor " + actor.getName() + " has hit points of " + actor.getCurHp());
			break;

		case WORLD_MOVE:
			updatePlayerWorldCoords(internalEvent.getFlag(1), internalEvent.getFlag(2));
			break;
			
		case ATTACK:
			damageActor(getActor(internalEvent.getFlag(1)), internalEvent.getFlag(2));
			break;
			
		case PICKUP:
			pickupItem(actor);
			break;
			
		case DROP:
			dropItem(actor, internalEvent.getFlag(1));
			break;
			
		case EQUIP:
			equipItem(actor, internalEvent.getFlag(1), internalEvent.getFlag(2));
			break;
			
		case UNEQUIP:
			unequipItem(actor, internalEvent.getFlag(1));
			break;
			
		case CHANGE_ITEM_HP:
			changeItemHp(actor, internalEvent);
		break;
			
		case ZONE_TRANSITION:
			Point playerLocation = currentZone.getCoordsOfActor(actor);
			ZoneKey zoneKey = currentZone.getZoneKey(playerLocation);
			updateZone(zoneKey, actor, (internalEvent.getFlag(1) == 1));
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
			dataSaveUtils.saveGameData(overworld, currentZone, player, target, worldTravel);
			break;

		case EXIT_GAME:
			dataSaveUtils.deleteCacheDir();
			System.exit(0);
			break;
		}
	}

	private void changeItemHp(Actor actor, InternalEvent internalEvent)
	{
		//if flag 0 (actor index) is -1, then flags 1 and 2 are the coordinates of the item
		//otherwise, the item is held by an actor, either worn or in the pack
		// flag 1 is the equipment slot index (negative means it's in pack)
		// flag 2 is the inventory slot index (negative means it's equipped)
		// flag 3 is the amount to change the item's hp
		
		Item item;
		int flag1 = internalEvent.getFlag(1);
		int flag2 = internalEvent.getFlag(2);
		int flag3 = internalEvent.getFlag(3);
		
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
			Logger.error("Data - Null pointer exception caught when changing item HP! Actor: " + actor + ", Flag 1: " + flag1 + ", Flag 2: " + flag2 + ", Flag 3: " + flag3);
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
			receiveInternalEvent(InternalEvent.exitInternalEvent());
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
		Logger.debug("Data - Moving actor " + actor.getName() + " to (" + x + ", " + y + ").");
		currentZone.updateActorCoords(actor, new Point(x, y));
	}

	private void updatePlayerWorldCoords(int x, int y)
	{
		Logger.debug("Data - Updating world coordinates to (" + x + ", " + y + ").");

		//if the player is moving elsewhere in the world, they can't be in the same zone
		//TODO: this probably shouldn't be here, since it's not this method's job to "fix" the currentZone with every world move.  it should be assumed that that's taken care of already
		if (currentZone != null && currentZone.shouldPersist())
		{
			currentZone.removeActor(player);
			dataSaveUtils.cacheZone(currentZone);
			currentZone = null;
		}
		
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