package main.data.chat;

import java.awt.Point;
import java.util.Map;

import main.data.Data;
import main.data.SpecialLevelManager;
import main.data.event.InternalEvent;
import main.data.event.environment.EnvironmentEvent;
import main.data.event.environment.GiveItemEvent;
import main.data.event.environment.HitpointChangeEvent;
import main.entity.CompareOperator;
import main.entity.actor.Actor;
import main.entity.actor.ActorType;
import main.entity.chat.ChatResponse;
import main.entity.event.Trigger;
import main.entity.item.InventorySelectionKey;
import main.entity.item.ItemType;
import main.entity.quest.QuestManager;
import main.entity.quest.QuestNodeStatus;
import main.entity.tile.Tile;
import main.entity.tile.TileFactory;
import main.entity.tile.TileType;
import main.entity.zone.Zone;
import main.entity.zone.ZoneKey;
import main.logic.requirement.RequirementValidator;
import main.presentation.Logger;

public class EventTriggerExecutor
{
	//TODO: I may send this through the engine instead if enough logic is repeated.
	//		However, since chatting is agnostic of game time/flow (that is, you can't be interrupted), there's no harm in changing things directly,
	//		since nothing needs to be queued
	//		This could be a problem for triggers that don't come from chats, but we'll see how it all goes.  For those, I suppose add new events to the queue.
	private Data data = null;
	
	private Zone openedZone = null;
	private boolean zoneCloseRequired = false;
	
	private static EventTriggerExecutor instance = null;
	
	private EventTriggerExecutor() {}
	
	public static EventTriggerExecutor getInstance()
	{
		if (instance == null)
			instance = new EventTriggerExecutor();
		
		return instance;
	}
	
	public void setData(Data data)
	{
		this.data = data;
	}
	
	public void executeChatResponseTriggers(ChatResponse response)
	{
		for (Trigger trigger : response.getTriggers())
		{
			executeTrigger(trigger);
		}
	}

	public void executeTrigger(Trigger trigger)
	{
		if (trigger == null)
			return;
		
		Actor player = data.getPlayer();
		
		switch (trigger.getType())
		{
		case CHANGE_HP:
			changeActorHp(player, trigger.getValueInt());
			break;
		case CHANGE_HP_OF_ACTOR:
			String actorType = trigger.getModifier();
			Actor actor = data.getFirstActorOfType(ActorType.fromString(actorType));
			changeActorHp(actor, trigger.getValueInt());
			break;
		case GET_ITEM_FROM:
			receiveItem(trigger);
			break;
		case GIVE_ITEM_TO:
			giveItem(trigger);
			break;
		case SET_ZONE_TILE:
			setZoneTile(trigger);
			break;
		case ADD_ZONE_KEY:
			addZoneKey(trigger);
			break;
		case SET_QUEST_NODE_STATUS:
			setQuestNodeStatus(trigger);
			break;
		case ACTIVATE_QUEST:
			activateQuest(trigger);
			break;
		case DISCOVER_QUEST:
			discoverQuest(trigger);
			break;
		case COMPLETE_QUEST:
			completeQuest(trigger);
			break;
		//$CASES-OMITTED$
		default:
			break;
		}
	}

	private void receiveItem(Trigger trigger)
	{
		String giverActor = trigger.getModifier();
		String receiverActor = "PLAYER";
		transferItem(giverActor, receiverActor, trigger);
	}
	
	private void giveItem(Trigger trigger)
	{
		String giverActor = "PLAYER";
		String receiverActor = trigger.getModifier();
		transferItem(giverActor, receiverActor, trigger);
	}
	
	//Keep in mind that transfers are fully "internal" events, like in Runescape.  There won't ever be a command to give an item (though
	//there might be effects from using an item on a particular actor which include a transfer), so there don't need to be any messages
	//or any overt announcement to the player, beyond them seeing their inventory has more or fewer items than it used to.
	private void transferItem(String giverActorType, String receiverActorType, Trigger trigger)
	{
		String itemToTransfer = trigger.getValue();
		String itemCount = trigger.getComparison();
		
		String availableItems = RequirementValidator.getInstance().getValueToCheckForActorHasItem(giverActorType, itemToTransfer);
		boolean sourceActorHasEnoughItemsToGive = RequirementValidator.getInstance().checkRequirement(CompareOperator.GREATER_THAN_OR_EQUAL, itemCount, availableItems);
		
		int itemsToGive = Integer.parseInt(itemCount);
		
		//still give as many as possible
		if (!sourceActorHasEnoughItemsToGive)
			itemsToGive = Integer.parseInt(availableItems);
		
		if (itemsToGive == 0)
			return;
		
		Logger.debug("Giving [" + itemsToGive + "] items.");
		
		Actor giver = data.getFirstActorOfType(ActorType.fromString(giverActorType));
		Actor receiver = data.getFirstActorOfType(ActorType.fromString(receiverActorType));
		ItemType itemType = ItemType.fromString(itemToTransfer);
		
		triggerGiveEventsForMultipleSlots(giver, receiver, itemType, itemsToGive);
	}

	private void triggerGiveEventsForMultipleSlots(Actor giver, Actor receiver, ItemType itemType, int itemsToGive)
	{
		Map<InventorySelectionKey, Integer> itemsToTransfer = giver.getSlotsOfItemsToMeetQuantityRequirement(itemType, itemsToGive);
		
		for (InventorySelectionKey key : itemsToTransfer.keySet())
		{
			int quantity = itemsToTransfer.get(key);
			EnvironmentEvent event = new GiveItemEvent(giver, receiver, key.getItemSource(), key.getItemIndex(), quantity, null);
			triggerEvent(event);
		}
	}

	private void setZoneTile(Trigger trigger)
	{
		String zoneId = trigger.getModifier();
		Point coords = getCoordsFromString(trigger.getValue());
		TileType newTileType = TileType.valueOf(trigger.getComparison());
		
		openZone(zoneId);
		Tile oldTile = openedZone.getTile(coords);
		Tile newTile = TileFactory.generateNewTile(newTileType);
		newTile.setSeen(oldTile.isSeen());
		newTile.setVisible(oldTile.isVisible());
		openedZone.setTile(coords, newTile);
		closeOpenedZone();
	}

	private void addZoneKey(Trigger trigger)
	{
		String zoneId = trigger.getModifier();
		Point coords = getCoordsFromString(trigger.getValue());
		String targetZoneId = trigger.getComparison();
		
		ZoneKey zoneKey = new ZoneKey(targetZoneId, 0);
		
		openZone(zoneId);
		openedZone.addZoneKey(coords, zoneKey);
		closeOpenedZone();
	}

	private void setQuestNodeStatus(Trigger trigger)
	{
		String questTagAndNodeTag = trigger.getModifier();
		QuestNodeStatus newStatus = QuestNodeStatus.fromString(trigger.getValue());
		QuestManager.getInstance().updateNodeStatus(questTagAndNodeTag, newStatus);
	}

	private void activateQuest(Trigger trigger)
	{
		String questTag = trigger.getValue();
		QuestManager.getInstance().activateQuest(questTag);
	}

	private void discoverQuest(Trigger trigger)
	{
		String questTag = trigger.getValue();
		QuestManager.getInstance().discoverQuest(questTag);
	}

	private void completeQuest(Trigger trigger)
	{
		String questTag = trigger.getValue();
		QuestManager.getInstance().completeQuest(questTag);
	}

	private Point getCoordsFromString(String value)
	{
		String xCoord = "";
		String yCoord = "";
		boolean checkingX = true;
		
		for (int i = 0; i < value.length(); i++)
		{
			char currentChar = value.charAt(i);
			
			if (currentChar == ',')
			{
				checkingX = false;
				continue;
			}
			
			if (checkingX)
				xCoord += currentChar;
			else
				yCoord += currentChar;
		}
		
		return new Point(Integer.valueOf(xCoord), Integer.valueOf(yCoord));
	}

	private void changeActorHp(Actor player, int amount)
	{
		triggerEvent(new HitpointChangeEvent(player, amount, null));
	}
	
	//used for instantaneous events; delayed ones probably should be added to the event queue (which will also send it through Engine): data.getEventQueue().add(event);
	//also note that there will be no messages related to this, since those are generated by the engine (could be a trivial issue for, say, giving items)
	//finally, note that there may be other listeners beyond the data that might be interested in these events
	private void triggerEvent(EnvironmentEvent event)
	{
		for (InternalEvent iEvent : event.trigger())
		{
			data.receiveInternalEvent(iEvent);
		}
	}
	
	private void openZone(String zoneId)
	{
		openedZone = SpecialLevelManager.getInstance().retrieveZone(zoneId);
		
		//technically we only need to check that they're the same object, but that's coupling with SpecialLevelManager
		if (openedZone.getUniqueId().equals(data.getCurrentZone().getUniqueId())) 
			zoneCloseRequired = false;
		else
			zoneCloseRequired = true;
	}
	
	private void closeOpenedZone()
	{
		if (zoneCloseRequired)
			data.getZoneCacheUtility().cacheZone(openedZone);
			
		openedZone = null;
		zoneCloseRequired = false;
	}
}
