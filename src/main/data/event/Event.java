package main.data.event;

import java.awt.Point;

public class Event
{
	private static final int TOTAL_FLAGS = 4;
	
	private EventType eventType;
	private int[] flags = new int[TOTAL_FLAGS];
	private int actionCost;
	
	private Event(EventType eventType)
	{
		this.eventType = eventType;
		
		for (int i = 0; i < TOTAL_FLAGS; i++)
		{
			flags[i] = 0;
		}
		
		actionCost = 0;
	}
	
	private static Event moveEvent(EventType eventType, int actorIndex, int targetRow, int targetCol, int actionCost)
	{
		Event toRet = new Event(eventType);
		
		toRet.flags[0] = actorIndex;
		toRet.flags[1] = targetRow;
		toRet.flags[2] = targetCol;
		
		toRet.actionCost = actionCost;
		
		return toRet;
	}
	
	private static Event actorOnlyEvent(EventType eventType, int actorIndex)
	{
		Event toRet = new Event(eventType);
		
		toRet.flags[0] = actorIndex;
		
		return toRet;
	}

	//if flag 0 (actor index) is -1, then flags 1 and 2 are the coordinates of the item
	//otherwise, the item is held by an actor, either worn or in the pack
	// flag 1 is the equipment slot index (negative means it's in pack)
	// flag 2 is the inventory slot index (negative means it's equipped)
	private static Event changeItemHpEvent(int actorIndex, int flag1, int flag2, int changeAmount)
	{
		Event event = actorOnlyEvent(EventType.CHANGE_ITEM_HP, actorIndex);
		event.flags[1] = flag1;
		event.flags[2] = flag2;
		event.flags[3] = changeAmount;
		return event;
	}

	public static Event waitEvent(int actorIndex, int movementCost)
	{
		Event event = actorOnlyEvent(EventType.WAIT, actorIndex);
		event.actionCost = movementCost;
		return event;
	}
	
	public static Event pickupEvent(int actorIndex)
	{
		return actorOnlyEvent(EventType.PICKUP, actorIndex);
	}
	
	public static Event dropEvent(int actorIndex, int itemIndex)
	{
		Event event = actorOnlyEvent(EventType.DROP, actorIndex);
		event.flags[1] = itemIndex;
		return event;
	}
	
	public static Event equipEvent(int actorIndex, int slotIndex, int itemIndex)
	{
		Event event = actorOnlyEvent(EventType.EQUIP, actorIndex);
		event.flags[1] = slotIndex;
		event.flags[2] = itemIndex;
		return event;
	}
	
	public static Event unequipEvent(int actorIndex, int slotIndex)
	{
		Event event = actorOnlyEvent(EventType.UNEQUIP, actorIndex);
		event.flags[1] = slotIndex;
		return event;
	}
	
	public static Event changeHeldItemHpEvent(int actorIndex, int slotIndex, int changeAmount)
	{
		return changeItemHpEvent(actorIndex, slotIndex, -1, changeAmount);
	}
	
	public static Event changeInventoryItemHpEvent(int actorIndex, int itemIndex, int changeAmount)
	{
		return changeItemHpEvent(actorIndex, 1, itemIndex, changeAmount);
	}
	
	public static Event changeGroundItemHpEvent(Point coords, int changeAmount)
	{
		return changeItemHpEvent(-1, coords.x, coords.y, changeAmount);
	}

	public static Event localMoveEvent(int actorIndex, int targetRow, int targetCol, int actionCost)
	{
		return moveEvent(EventType.LOCAL_MOVE, actorIndex, targetRow, targetCol, actionCost);
	}

	public static Event worldMoveEvent(int actorIndex, int targetRow, int targetCol, int actionCost)
	{
		return moveEvent(EventType.WORLD_MOVE, actorIndex, targetRow, targetCol, actionCost);
	}

	public static Event transitionZoneEvent(int actorIndex)
	{
		Event event = actorOnlyEvent(EventType.ZONE_TRANSITION, actorIndex);
		event.flags[1] = 1;
		return event;
	}

	public static Event transitionZoneEvent(int actorIndex, boolean goingDown)
	{
		Event event = actorOnlyEvent(EventType.ZONE_TRANSITION, actorIndex);
		event.flags[1] = goingDown ? 1 : 0;
		return event;
	}
	
	public static Event enterWorldEvent(int actorIndex)
	{
		return actorOnlyEvent(EventType.ENTER_WORLD, actorIndex);
	}
	
	public static Event enterLocalEvent(int actorIndex)
	{
		return actorOnlyEvent(EventType.ENTER_LOCAL, actorIndex);
	}
	
	public static Event attackEvent(int attackerIndex, int defenderIndex, int damage, int actionCost)
	{
		Event toRet = new Event(EventType.ATTACK);
		
		toRet.flags[0] = attackerIndex;
		toRet.flags[1] = defenderIndex;
		toRet.flags[2] = damage;
		
		toRet.actionCost = actionCost;
		
		return toRet;
	}
	
	public static Event deathEvent(int deadActorIndex)
	{
		return actorOnlyEvent(EventType.DEATH, deadActorIndex);
	}
	
	public static Event saveEvent()
	{
		return new Event(EventType.SAVE_GAME);
	}
	
	public static Event exitEvent()
	{
		return new Event(EventType.EXIT_GAME);
	}
	
	//the lack of setters means that Events are immutable for the rest of the project
	public EventType getEventType()
	{
		return eventType;
	}

	public int getFlag(int index)
	{
		return flags[index];
	}
	
	public int getActionCost()
	{
		return actionCost;
	}
}
