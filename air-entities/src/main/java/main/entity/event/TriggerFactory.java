package main.entity.event;

import main.entity.actor.ActorType;
import main.entity.chat.CompareOperator;
import main.entity.item.ItemType;

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
	
	public static Trigger zoneTransition(ActorType actor, boolean descending)
	{
		String direction = "UP";
		if (descending)
			direction = "DOWN";
		
		Trigger trigger = new Trigger(TriggerType.ZONE_TRANSITION);
		trigger.setDetails(actor.name(), direction, null, null);
		return trigger;
	}
}
