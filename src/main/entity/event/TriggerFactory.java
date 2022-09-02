package main.entity.event;

import java.awt.Point;

import main.entity.CompareOperator;
import main.entity.actor.ActorType;
import main.entity.item.ItemType;
import main.entity.quest.QuestNodeStatus;
import main.entity.tile.TileType;

public class TriggerFactory
{
	public static Trigger changeHp(int amount)
	{
		Trigger trigger = new Trigger(TriggerType.CHANGE_HP);
		trigger.setDetails(null, String.valueOf(amount), null, null);
		return trigger;
	}
	public static Trigger changeActorHp(ActorType actorType, int amount)
	{
		Trigger trigger = new Trigger(TriggerType.CHANGE_HP_OF_ACTOR);
		trigger.setDetails(actorType.name(), String.valueOf(amount), null, null);
		return trigger;
	}
	
	public static Trigger consumeItem(ActorType itemOwner, ItemType item, int amount)
	{
		Trigger trigger = new Trigger(TriggerType.CONSUME_ITEM);
		trigger.setDetails(itemOwner.name(), item.name(), CompareOperator.EQUAL, String.valueOf(amount));
		return trigger;
	}
	
	public static Trigger upgradeWeapon(ActorType itemOwner, ItemType item)
	{
		Trigger trigger = new Trigger(TriggerType.UPGRADE_ITEM);
		trigger.setDetails(itemOwner.name(), item.name(), null, null);
		return trigger;
	}
	
	public static Trigger zoneTransition(ActorType actor, boolean descending)
	{
		String direction = "UP";
		if (descending)
			direction = "DOWN";
		
		Trigger trigger = new Trigger(TriggerType.ZONE_TRANSITION);
		trigger.setDetails(actor.name(), direction, null, null);
		return trigger;
	}
	
	public static Trigger activateQuest(String questTag)
	{
		Trigger trigger = new Trigger(TriggerType.ACTIVATE_QUEST);
		trigger.setDetails(null, questTag, null, null);
		return trigger;
	}
	
	public static Trigger discoverQuest(String questTag)
	{
		Trigger trigger = new Trigger(TriggerType.DISCOVER_QUEST);
		trigger.setDetails(null, questTag, null, null);
		return trigger;
	}
	
	public static Trigger completeQuest(String questTag)
	{
		Trigger trigger = new Trigger(TriggerType.COMPLETE_QUEST);
		trigger.setDetails(null, questTag, null, null);
		return trigger;
	}
	
	public static Trigger setQuestNodeStatus(String questTag, String nodeTag, QuestNodeStatus newStatus)
	{
		Trigger trigger = new Trigger(TriggerType.SET_QUEST_NODE_STATUS);
		String combinedQuestAndNodeTag = questTag + "|" + nodeTag;
		trigger.setDetails(combinedQuestAndNodeTag, newStatus.name(), null, null);
		return trigger;
	}
	
	public static Trigger setZoneTile(String zoneId, Point coords, TileType tileType)
	{
		Trigger trigger = new Trigger(TriggerType.SET_ZONE_TILE);
		trigger.setDetails(zoneId, convertCoordsToString(coords), CompareOperator.EQUAL, tileType.name());
		return trigger;
	}
	
	public static Trigger addZoneKey(String zoneId, Point coords, String newZoneId)
	{
		Trigger trigger = new Trigger(TriggerType.ADD_ZONE_KEY);
		trigger.setDetails(zoneId, convertCoordsToString(coords), CompareOperator.EQUAL, newZoneId);
		return trigger;
	}
	
	private static String convertCoordsToString(Point coords)
	{
		return coords.x + "," + coords.y;
	}
}
