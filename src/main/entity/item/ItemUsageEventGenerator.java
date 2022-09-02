package main.entity.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.data.event.environment.ConsumeInventoryItemEvent;
import main.data.event.environment.EnvironmentEvent;
import main.data.event.environment.HitpointChangeEvent;
import main.data.event.environment.UpgradeWeaponEvent;
import main.data.event.environment.ZoneTransitionEvent;
import main.entity.actor.Actor;
import main.entity.actor.ActorType;
import main.entity.event.Trigger;
import main.entity.event.TriggerFactory;
import main.entity.feature.Feature;
import main.entity.tile.Tile;

public class ItemUsageEventGenerator
{
	private Map<ItemUsageKey, List<Trigger>> usageMap = new HashMap<ItemUsageKey, List<Trigger>>();
	
	private static ItemUsageEventGenerator instance = null;
	
	public static ItemUsageEventGenerator getInstance()
	{
		if (instance == null)
			instance = new ItemUsageEventGenerator();
		
		return instance;
	}
	
	private List<EnvironmentEvent> generateTriggers(Actor itemUser, Item usedItem, ItemUsageKey key, Actor actor, Item item, Feature feature, Tile tile)
	{
		List<Trigger> triggers = usageMap.get(key);
		List<EnvironmentEvent> events = new ArrayList<EnvironmentEvent>();
		
		if (triggers == null)
			triggers = usageMap.get(key.asAnyTarget());
		
		if (triggers == null)
			return events;
		
		for (Trigger trigger : triggers)
		{	
			EnvironmentEvent event = generateEvent(itemUser, usedItem, trigger, actor, item, feature, tile);
			
			if (event != null)
				events.add(event);
		}
		
		return events;
	}
	
	private EnvironmentEvent generateEvent(Actor itemUser, Item usedItem, Trigger trigger, Actor actor, Item item, Feature feature, Tile tile)
	{
		switch(trigger.getType())
		{
		case CHANGE_HP:
			break;
		case CHANGE_HP_OF_ACTOR:
			if (!ActorType.TARGET_ACTOR.equals(trigger.getModifierActor()))
				break;	//no clue what to do if it specifies an actor other than the target, but this is better than assuming, I suppose
			return new HitpointChangeEvent(actor, trigger.getValueInt(), null);
		case CONSUME_ITEM:
			return consumeItem(itemUser, usedItem, trigger, item);
		case UPGRADE_ITEM:
			return upgradeItem(itemUser, usedItem, trigger, item);
		case GET_ITEM_FROM:
			break;
		case GIVE_ITEM_TO:
			break;
		case ZONE_TRANSITION:
			return transitionZone(trigger, actor);
		default:
			break;
		}
		
		return null;
	}

	private EnvironmentEvent consumeItem(Actor itemUser, Item usedItem, Trigger trigger, Item targetItem)
	{
		if (!ActorType.SOURCE_ACTOR.equals(trigger.getModifierActor()))
			return null;	//not sure what else to do here
		
		Item itemToConsume = null;
		
		if (ItemType.SOURCE_ITEM.equals(trigger.getValueItem()))
			itemToConsume = usedItem;
		else if (ItemType.TARGET_ITEM.equals(trigger.getValueItem()))
			itemToConsume = targetItem;
		else
			return null;	//as above
		
		return new ConsumeInventoryItemEvent(itemUser, itemToConsume, trigger.getComparisonInt(), null);
	}

	private EnvironmentEvent upgradeItem(Actor itemUser, Item usedItem, Trigger trigger, Item targetItem)
	{
		if (!ActorType.SOURCE_ACTOR.equals(trigger.getModifierActor()))
			return null;	//not sure what else to do here
		
		Item itemToUpgrade = null;
		
		if (ItemType.SOURCE_ITEM.equals(trigger.getValueItem()))
			itemToUpgrade = usedItem;
		else if (ItemType.TARGET_ITEM.equals(trigger.getValueItem()))
			itemToUpgrade = targetItem;
		else
			return null;	//as above
		
		return new UpgradeWeaponEvent(itemUser, itemToUpgrade, null);
	}
	
	private EnvironmentEvent transitionZone(Trigger trigger, Actor actor)
	{
		String direction = trigger.getValue();
		boolean descending = false;
		
		if ("DOWN".equals(direction))
			descending = true;
			
		return new ZoneTransitionEvent(actor, descending, null);
	}

	public List<EnvironmentEvent> useItem(Actor itemUser, Item item, Object target)
	{
		if (item == null)
			return null;
		
		if (target instanceof Actor)
			return useItem(itemUser, item, (Actor) target);
		if (target instanceof Item)
			return useItem(itemUser, item, (Item) target);
		if (target instanceof Feature)
			return useItem(itemUser, item, (Feature) target);
		if (target instanceof Tile)
			return useItem(itemUser, item, (Tile) target);
		
		return null;
	}

	public List<EnvironmentEvent> useItem(Actor itemUser, Item item, Actor target)
	{
		return generateTriggers(itemUser, item, new ItemUsageKey(item.getType(), target.getType()), target, null, null, null);
	}
	
	public List<EnvironmentEvent> useItem(Actor itemUser, Item item, Item target)
	{
		return generateTriggers(itemUser, item, new ItemUsageKey(item.getType(), target.getType()), null, target, null, null);
	}
	
	public List<EnvironmentEvent> useItem(Actor itemUser, Item item, Feature target)
	{
		return generateTriggers(itemUser, item, new ItemUsageKey(item.getType(), target.getType()), null, null, target, null);
	}
	
	public List<EnvironmentEvent> useItem(Actor itemUser, Item item, Tile target)
	{
		return generateTriggers(itemUser, item, new ItemUsageKey(item.getType(), target.getType()), null, null, null, target);
	}
	
	private ItemUsageEventGenerator()
	{
		List<Trigger> triggers = new ArrayList<>();
		triggers.add(TriggerFactory.zoneTransition(ActorType.TARGET_ACTOR, true));
		usageMap.put(new ItemUsageKey(ItemType.DEBUG_GEM_DOWN, ActorType.ANY_ACTOR), triggers);
		
		triggers = new ArrayList<>();
		triggers.add(TriggerFactory.zoneTransition(ActorType.TARGET_ACTOR, false));
		usageMap.put(new ItemUsageKey(ItemType.DEBUG_GEM_UP, ActorType.ANY_ACTOR), triggers);
		
		triggers = new ArrayList<>();
		triggers.add(TriggerFactory.changeActorHp(ActorType.TARGET_ACTOR, 3));
		triggers.add(TriggerFactory.consumeItem(ActorType.SOURCE_ACTOR, ItemType.SOURCE_ITEM, 1));
		usageMap.put(new ItemUsageKey(ItemType.MEDICINAL_FUNGUS, ActorType.ANY_ACTOR), triggers);
		
		triggers = new ArrayList<>();
		triggers.add(TriggerFactory.changeActorHp(ActorType.TARGET_ACTOR, 10));
		triggers.add(TriggerFactory.consumeItem(ActorType.SOURCE_ACTOR, ItemType.SOURCE_ITEM, 1));
		usageMap.put(new ItemUsageKey(ItemType.HEALING_SALVE, ActorType.ANY_ACTOR), triggers);
		
		triggers = new ArrayList<>();
		triggers.add(TriggerFactory.upgradeWeapon(ActorType.SOURCE_ACTOR, ItemType.TARGET_ITEM));
		triggers.add(TriggerFactory.consumeItem(ActorType.SOURCE_ACTOR, ItemType.SOURCE_ITEM, 1));
		usageMap.put(new ItemUsageKey(ItemType.METAL_SHARD, ItemGroup.weapons()), triggers);
		
		triggers = new ArrayList<>();
		//TODO: add trigger to transform one actor into another using Actor.convertToType() - in this case, bound_archeo to archeologist
		usageMap.put(new ItemUsageKey(ItemGroup.bladedWeapons(), ActorType.BOUND_ARCHEO), triggers);
	}
}
