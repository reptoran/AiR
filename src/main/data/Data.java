package main.data;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import main.data.chat.ChatManager;
import main.data.chat.EventTriggerExecutor;
import main.data.chat.RequirementValidator;
import main.data.event.EventObserver;
import main.data.event.InternalEvent;
import main.data.event.environment.EnvironmentEventQueue;
import main.data.file.ChatLoader;
import main.data.file.TextLoader;
import main.entity.actor.Actor;
import main.entity.actor.ActorFactory;
import main.entity.actor.ActorType;
import main.entity.actor.GenderType;
import main.entity.chat.Chat;
import main.entity.feature.Feature;
import main.entity.feature.FeatureFactory;
import main.entity.feature.FeatureType;
import main.entity.item.Inventory;
import main.entity.item.Item;
import main.entity.item.ItemFactory;
import main.entity.item.ItemSource;
import main.entity.item.ItemType;
import main.entity.item.ItemUsageMap;
import main.entity.item.equipment.Equipment;
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

public class Data implements EventObserver
{
	protected Zone currentZone;
	private Overworld overworld;
	private ActorType profession = null;
	private Actor player;
	private Actor target = null;

	private DataSaveUtils dataSaveUtils = null;
	private List<PredefinedZone> predefinedZones;
	private Map<String, String> gameTextEntries;
	private Map<ActorType, List<Chat>> gameChatEntries;

	private String playerName = null;
	private EnvironmentEventQueue worldTravelQueue = new EnvironmentEventQueue();

	private boolean worldTravel = false;

	public Data()
	{
		ItemUsageMap.getInstance();									//populates item usage map
		RequirementValidator.getInstance().setData(this);
		ChatManager.getInstance().setData(this);
		EventTriggerExecutor.getInstance().setData(this);
		
		gameTextEntries = TextLoader.getInstance().loadAllGameText();
		predefinedZones = PredefinedZoneLoader.getInstance().loadAllPredefinedZones();
		gameChatEntries = ChatLoader.getInstance().loadAllChats();	//TODO: currently returns a map of talk elements associated with a given (presumably unique) actor type,
																	//		which is specified by the name of the file containing those elements.
																	//		That said, it also makes sense for multiple non-unique monsters to have the same conversation trees,
																	//		so this is fine.
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
		SpecialLevelManager.getInstance().populateSpecialZonesForLevels(predefinedZones);
		ChatManager.getInstance().populateChats(gameChatEntries);
		
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
				actor.setDefaultTalkResponse("You engage in a deep, introspective conversation with yourself.");
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
	
	public Map<ActorType, List<Chat>> getChatEntries()
	{
		return gameChatEntries;
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
	
	public Actor getFirstActorOfType(ActorType actorType)
	{
		if (actorType == ActorType.PLAYER)
			return player;
		
		if (worldTravel)
			return null;
		
		if (currentZone == null)
			return null;

		List<Actor> actorsInZone = currentZone.getActors();
		
		for (Actor actor : actorsInZone)
		{
			if (actor.getType() == actorType)
				return actor;
		}
		
		return null;
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
	
	public List<Actor> getAdjacentActors(Actor actor)
	{
		Point point = currentZone.getCoordsOfActor(actor);
		return getAdjacentActors(point);
	}
	
	public List<Actor> getAdjacentActors(Point point)
	{
		List<Actor> adjacentActors = new ArrayList<Actor>();
		
		for (int i = -1; i <= 1; i++)
		{
			for (int j = -1; j <= 1; j++)
			{
				if (i == 0 && j == 0)
					continue;
				
				Point toCheck = new Point(point.x + i, point.y + j);
				Actor actorHere = currentZone.getActorAtCoords(toCheck);
				if (actorHere != null)
					adjacentActors.add(actorHere);
			}
		}
		
		return adjacentActors;
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
	@Override
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
			dropItem(actor, internalEvent.getFlag(1), internalEvent.getFlag(2));
			break;
			
		case EQUIP:
			equipItem(actor, internalEvent);
			break;
			
		case UNEQUIP:
			unequipItem(actor, internalEvent);
			break;
			
		case CHANGE_ITEM_HP:
			changeItemHp(actor, internalEvent);
			break;
			
		case DELETE_ITEM:
			deleteItem(actor, internalEvent);
			break;
			
		case CREATE_ITEM:
			createItem(actor, internalEvent);
			
		case MOVE_ITEM:
			moveItem(actor, internalEvent);
			break;
			
		case GIVE:
			giveItem(actor, internalEvent);
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
		default:
			break;
		}
	}

	//TODO: note that this has logic, when it should just be changing item HP and nothing more
	//new internal event should be created to remove items (as in the "else item is destroyed" code here)
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
			item = actor.getStoredItems().get(flag1);
		
		try {
			item.changeCurHp(flag3);
		} catch (NullPointerException npe)
		{
			Logger.error("Data - Null pointer exception caught when changing item HP! Actor: " + actor + ", Flag 1: " + flag1 + ", Flag 2: " + flag2 + ", Flag 3: " + flag3);
			return;
		}
	}
	
	private void deleteItem(Actor actor, InternalEvent internalEvent)
	{
		Item item;
		int flag1 = internalEvent.getFlag(1);
		int flag2 = internalEvent.getFlag(2);
		int amountToDelete = internalEvent.getFlag(3);
		
		if (actor == null)
			item = currentZone.getTile(flag1, flag2).getItemHere();
		else if (flag1 != -1)
			item = actor.getEquipment().getItem(flag1);
		else
			item = actor.getStoredItems().get(flag1);
		
		int currentAmount = item.getAmount();
		
		if (currentAmount > amountToDelete)
		{
			item.setAmount(currentAmount - amountToDelete);
			return;
		}
		
		//otherwise remove it entirely
		if (actor == null)
			currentZone.getTile(flag1, flag2).setItemHere(null);
		else if (flag1 != -1)
			actor.getEquipment().removeItem(flag1);
		else
			actor.getStoredItems().remove(flag1);
	}

	private void createItem(Actor actor, InternalEvent internalEvent)
	{
		Item item = ItemFactory.generateNewItem(ItemType.fromString(internalEvent.getValue()));
		item.setAmount(internalEvent.getFlag(3));
		
		if (actor == null)
		{
			Point destination = new Point(internalEvent.getFlag(1), internalEvent.getFlag(2));
			dropItem(destination, item);
			return;
		}
		
		actor.receiveItem(item);
	}

	private void moveItem(Actor actor, InternalEvent internalEvent)
	{
		Point destination = new Point(internalEvent.getFlag(3), internalEvent.getFlag(4));
		
		Item item;
		int flag1 = internalEvent.getFlag(1);
		int flag2 = internalEvent.getFlag(2);
		
		if (actor == null)
		{
			Point origin = new Point(internalEvent.getFlag(1), internalEvent.getFlag(2));
			item = currentZone.getTile(origin).getItemHere();
			
			if (item == null)
				return;
			
			currentZone.getTile(origin).setItemHere(null);
		}
		else
		{
			item = removeItemFromActor(actor, flag2, ItemSource.fromInt(flag1));
		}
		
		if (item != null)
			dropItem(destination, item);
	}

	//TODO: generify this for unequipping from spots other than just equipment (magic, readied)
	private void unequipItem(Actor actor, InternalEvent event)
	{
		int slotIndex = event.getFlag(2);
		
		EquipmentSlot slot = actor.getEquipment().getEquipmentSlots().get(slotIndex);
		Inventory inventory = actor.getStoredItems();
		
		inventory.add(slot.removeItem());
	}
	
	private void equipItem(Actor actor, InternalEvent event)
	{
		ItemSource itemSource = ItemSource.fromInt(event.getFlag(1));
		int itemIndex = event.getFlag(2);
		ItemSource equipmentZone = ItemSource.fromInt(event.getFlag(3));
		int slotIndex = event.getFlag(4);
		
		Equipment equipment;
		
		switch (equipmentZone)
		{
		case EQUIPMENT:
			equipment = actor.getEquipment();
			break;
		case MAGIC:
			equipment = actor.getMagicItems();
			break;
		case READY:
			equipment = actor.getReadiedItems();
			break;
		//$CASES-OMITTED$
		default:
			throw new IllegalArgumentException("Items cannot be equipped to zone " + equipmentZone);
		
		}
		
		EquipmentSlot slot = equipment.getEquipmentSlots().get(slotIndex);
		Item itemToEquip = null;
		
		switch (itemSource)
		{
		case PACK:
			Inventory inventory = actor.getStoredItems();
			
			itemToEquip = inventory.remove(itemIndex, 1);	//at this point, any filtering should still be active on the inventory, since it's the act of getting or removing the item that resets it
			break;
			
		case GROUND:
			Tile tile = currentZone.getTile(actor);
			itemToEquip = tile.getItemHere();
			tile.setItemHere(null);
			break;
		//$CASES-OMITTED$	
		default:
			throw new IllegalArgumentException("No equip behavior defined for item source " + itemSource);
		}
		
		slot.setItem(itemToEquip);
	}

	private void dropItem(Actor actor, int itemSourceInt, int itemIndex)
	{
		ItemSource itemSource = ItemSource.fromInt(itemSourceInt);
		Item itemToDrop = removeItemFromActor(actor, itemIndex, itemSource);
		
		Point coordsToDropItem = getClosestOpenTileCoords(currentZone.getCoordsOfActor(actor));
		currentZone.getTile(coordsToDropItem).setItemHere(itemToDrop);
	}
	
	private void giveItem(Actor giver, InternalEvent internalEvent)
	{
		ItemSource itemSource = ItemSource.fromInt(internalEvent.getFlag(1));
		int itemIndex = internalEvent.getFlag(2);
		Item itemToGive = removeItemFromActor(giver, itemIndex, itemSource);
		Actor receiver = getActor(internalEvent.getFlag(3));
		receiver.receiveItem(itemToGive);
	}
	
	private Item removeItemFromActor(Actor actor, int itemIndex, ItemSource itemSource)
	{
		switch (itemSource)
		{
		case PACK:
			return actor.removeStoredItem(itemIndex);
		case MATERIAL:
			return actor.removeMaterial(itemIndex);
		case EQUIPMENT:
			return actor.getEquipment().removeItem(itemIndex);
		case MAGIC:
			return actor.getMagicItems().removeItem(itemIndex);
		default:
			throw new IllegalArgumentException("No drop behavior defined for item source " + itemSource);
		}
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
		
		for (Item item : actor.getStoredItems())
			dropItem(coords, item);
		
		actor.getStoredItems().clear();
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