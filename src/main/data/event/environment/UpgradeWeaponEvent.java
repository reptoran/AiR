package main.data.event.environment;

import java.util.ArrayList;
import java.util.List;

import main.data.event.InternalEvent;
import main.entity.actor.Actor;
import main.entity.item.InventorySelectionKey;
import main.entity.item.Item;

public class UpgradeWeaponEvent extends AbstractInventorySingleItemEvent
{
	public UpgradeWeaponEvent(Actor owner, Item itemToUpgrade, EnvironmentEventQueue eventQueue)
	{
		super(owner, itemToUpgrade, 1, eventQueue);
	}
	
	@Override
	public List<InternalEvent> trigger()
	{
		List<InternalEvent> eventList = new ArrayList<InternalEvent>();
		
		InternalEvent event = InternalEvent.upgradeWeaponInternalEvent(actorIndex, new InventorySelectionKey(itemSource, itemIndex));
		eventList.add(event);
		
		return eventList;
	}

	@Override
	public EnvironmentEventType getType()
	{
		return EnvironmentEventType.UPGRADE_WEAPON;
	}
}
