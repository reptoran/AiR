package main.entity.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.data.event.environment.EnvironmentEvent;
import main.entity.actor.Actor;
import main.entity.actor.ActorType;
import main.entity.event.Trigger;
import main.entity.event.TriggerFactory;
import main.entity.event.TriggerType;
import main.entity.feature.Feature;
import main.entity.tile.Tile;

public class ItemUsageMap
{
	private Map<ItemUsageKey, List<Trigger>> usageMap = new HashMap<ItemUsageKey, List<Trigger>>();
	
	private static ItemUsageMap instance = null;
	
	public static ItemUsageMap getInstance()
	{
		if (instance == null)
			instance = new ItemUsageMap();
		
		return instance;
	}

	private ItemUsageMap()
	{
		List<Trigger> triggers = new ArrayList<>();
		
		triggers.add(TriggerFactory.consumeItem(ActorType.ANY_ACTOR, ItemType.MEDICINAL_FUNGUS, 1));
		triggers.add(TriggerFactory.changeHp(3));
		usageMap.put(new ItemUsageKey(ItemType.MEDICINAL_FUNGUS, ActorType.ANY_ACTOR), triggers);
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
			break;
		case CONSUME_ITEM:
			break;
		case GET_ITEM_FROM:
			break;
		case GIVE_ITEM_TO:
			break;
		default:
			break;
		}
		
		return null;
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
}
