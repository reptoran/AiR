package main.data.event;

import java.awt.Point;

public class InternalEvent
{
	private static final int TOTAL_FLAGS = 4;
	
	private InternalEventType internalEventType;
	private int[] flags = new int[TOTAL_FLAGS];
	private int actionCost;
	
	private InternalEvent(InternalEventType internalEventType)
	{
		this.internalEventType = internalEventType;
		
		for (int i = 0; i < TOTAL_FLAGS; i++)
		{
			flags[i] = 0;
		}
		
		actionCost = 0;
	}
	
	private static InternalEvent moveInternalEvent(InternalEventType internalEventType, int actorIndex, int targetRow, int targetCol, int actionCost)
	{
		InternalEvent toRet = new InternalEvent(internalEventType);
		
		toRet.flags[0] = actorIndex;
		toRet.flags[1] = targetRow;
		toRet.flags[2] = targetCol;
		
		toRet.actionCost = actionCost;
		
		return toRet;
	}
	
	private static InternalEvent actorOnlyInternalEvent(InternalEventType internalEventType, int actorIndex)
	{
		InternalEvent toRet = new InternalEvent(internalEventType);
		
		toRet.flags[0] = actorIndex;
		
		return toRet;
	}

	//if flag 0 (actor index) is -1, then flags 1 and 2 are the coordinates of the item
	//otherwise, the item is held by an actor, either worn or in the pack
	// flag 1 is the equipment slot index (negative means it's in pack)
	// flag 2 is the inventory slot index (negative means it's equipped)
	private static InternalEvent changeItemHpInternalEvent(int actorIndex, int flag1, int flag2, int changeAmount)
	{
		InternalEvent internalEvent = actorOnlyInternalEvent(InternalEventType.CHANGE_ITEM_HP, actorIndex);
		internalEvent.flags[1] = flag1;
		internalEvent.flags[2] = flag2;
		internalEvent.flags[3] = changeAmount;
		return internalEvent;
	}

	public static InternalEvent waitInternalEvent(int actorIndex, int movementCost)
	{
		InternalEvent internalEvent = actorOnlyInternalEvent(InternalEventType.WAIT, actorIndex);
		internalEvent.actionCost = movementCost;
		return internalEvent;
	}
	
	public static InternalEvent pickupInternalEvent(int actorIndex)
	{
		return actorOnlyInternalEvent(InternalEventType.PICKUP, actorIndex);
	}
	
	public static InternalEvent dropInternalEvent(int actorIndex, int itemIndex)
	{
		InternalEvent internalEvent = actorOnlyInternalEvent(InternalEventType.DROP, actorIndex);
		internalEvent.flags[1] = itemIndex;
		return internalEvent;
	}
	
	public static InternalEvent equipInternalEvent(int actorIndex, int slotIndex, int itemIndex)
	{
		InternalEvent internalEvent = actorOnlyInternalEvent(InternalEventType.EQUIP, actorIndex);
		internalEvent.flags[1] = slotIndex;
		internalEvent.flags[2] = itemIndex;
		return internalEvent;
	}
	
	public static InternalEvent unequipInternalEvent(int actorIndex, int slotIndex)
	{
		InternalEvent internalEvent = actorOnlyInternalEvent(InternalEventType.UNEQUIP, actorIndex);
		internalEvent.flags[1] = slotIndex;
		return internalEvent;
	}
	
	public static InternalEvent changeHeldItemHpInternalEvent(int actorIndex, int slotIndex, int changeAmount)
	{
		return changeItemHpInternalEvent(actorIndex, slotIndex, -1, changeAmount);
	}
	
	public static InternalEvent changeInventoryItemHpInternalEvent(int actorIndex, int itemIndex, int changeAmount)
	{
		return changeItemHpInternalEvent(actorIndex, 1, itemIndex, changeAmount);
	}
	
	public static InternalEvent changeGroundItemHpInternalEvent(Point coords, int changeAmount)
	{
		return changeItemHpInternalEvent(-1, coords.x, coords.y, changeAmount);
	}

	public static InternalEvent localMoveInternalEvent(int actorIndex, int targetRow, int targetCol, int actionCost)
	{
		return moveInternalEvent(InternalEventType.LOCAL_MOVE, actorIndex, targetRow, targetCol, actionCost);
	}

	public static InternalEvent worldMoveInternalEvent(int actorIndex, int targetRow, int targetCol, int actionCost)
	{
		return moveInternalEvent(InternalEventType.WORLD_MOVE, actorIndex, targetRow, targetCol, actionCost);
	}

	public static InternalEvent transitionZoneInternalEvent(int actorIndex)
	{
		InternalEvent internalEvent = actorOnlyInternalEvent(InternalEventType.ZONE_TRANSITION, actorIndex);
		internalEvent.flags[1] = 1;
		return internalEvent;
	}

	public static InternalEvent transitionZoneInternalEvent(int actorIndex, boolean goingDown)
	{
		InternalEvent internalEvent = actorOnlyInternalEvent(InternalEventType.ZONE_TRANSITION, actorIndex);
		internalEvent.flags[1] = goingDown ? 1 : 0;
		return internalEvent;
	}
	
	public static InternalEvent enterWorldInternalEvent(int actorIndex)
	{
		return actorOnlyInternalEvent(InternalEventType.ENTER_WORLD, actorIndex);
	}
	
	public static InternalEvent enterLocalInternalEvent(int actorIndex)
	{
		return actorOnlyInternalEvent(InternalEventType.ENTER_LOCAL, actorIndex);
	}
	
	public static InternalEvent attackInternalEvent(int attackerIndex, int defenderIndex, int damage, int actionCost)
	{
		InternalEvent toRet = new InternalEvent(InternalEventType.ATTACK);
		
		toRet.flags[0] = attackerIndex;
		toRet.flags[1] = defenderIndex;
		toRet.flags[2] = damage;
		
		toRet.actionCost = actionCost;
		
		return toRet;
	}
	
	public static InternalEvent deathInternalEvent(int deadActorIndex)
	{
		return actorOnlyInternalEvent(InternalEventType.DEATH, deadActorIndex);
	}
	
	public static InternalEvent saveInternalEvent()
	{
		return new InternalEvent(InternalEventType.SAVE_GAME);
	}
	
	public static InternalEvent exitInternalEvent()
	{
		return new InternalEvent(InternalEventType.EXIT_GAME);
	}
	
	//the lack of setters means that Events are immutable for the rest of the project
	public InternalEventType getInternalEventType()
	{
		return internalEventType;
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
