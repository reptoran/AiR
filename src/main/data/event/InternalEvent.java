package main.data.event;

import java.awt.Point;

import main.entity.item.InventorySelectionKey;
import main.entity.item.ItemSource;
import main.entity.item.ItemType;
import main.logic.Direction;
import main.logic.AI.AiType;

public class InternalEvent
{
	private static final int TOTAL_FLAGS = 5;
	
	private InternalEventType internalEventType;
	private int[] flags = new int[TOTAL_FLAGS];
	private String value;
	private int actionCost;
	private boolean interruptible = false;
	
	private InternalEvent(InternalEventType internalEventType)
	{
		this.internalEventType = internalEventType;
		
		for (int i = 0; i < TOTAL_FLAGS; i++)
		{
			flags[i] = 0;
		}
		
		value = "";
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
	
	private static InternalEvent noActorInternalEvent(InternalEventType internalEventType)
	{
		InternalEvent toRet = new InternalEvent(internalEventType);
		
		toRet.flags[0] = -1;
		
		return toRet;
	}

	//if flag 0 (actor index) is -1, then flags 1 and 2 are the coordinates of the item
	//otherwise, the item is held by an actor
	// flag 1 is the item source
	// flag 2 is the item index in that source
	private static InternalEvent changeItemHpInternalEvent(int actorIndex, int flag1, int flag2, int changeAmount)
	{
		InternalEvent internalEvent = actorOnlyInternalEvent(InternalEventType.CHANGE_ITEM_HP, actorIndex);
		internalEvent.flags[1] = flag1;
		internalEvent.flags[2] = flag2;
		internalEvent.flags[3] = changeAmount;
		return internalEvent;
	}
	
	//if flag 0 (actor index) is -1, then flags 1 and 2 are the coordinates of the item
	//otherwise, the item is held by an actor
	// flag 1 is the item source as an int
	// flag 2 is the slot index
	private static InternalEvent moveItemInternalEvent(int actorIndex, int flag1, int flag2, Point target)
	{
		InternalEvent internalEvent = actorOnlyInternalEvent(InternalEventType.MOVE_ITEM, actorIndex);
		internalEvent.flags[1] = flag1;
		internalEvent.flags[2] = flag2;
		internalEvent.flags[3] = target.x;
		internalEvent.flags[4] = target.y;
		return internalEvent;
	}
	
	//fields as above
	private static InternalEvent deleteItemInternalEvent(int actorIndex, int flag1, int flag2, int amountToRemove)
	{
		InternalEvent internalEvent = actorOnlyInternalEvent(InternalEventType.DELETE_ITEM, actorIndex);
		internalEvent.flags[1] = flag1;
		internalEvent.flags[2] = flag2;
		internalEvent.flags[3] = amountToRemove;
		return internalEvent;
	}
	
	public static InternalEvent createItemForActorInternalEvent(int actorIndex, ItemType itemType, int quantity)
	{
		InternalEvent internalEvent = actorOnlyInternalEvent(InternalEventType.CREATE_ITEM, actorIndex);
		internalEvent.flags[3] = quantity;
		internalEvent.value = itemType.name();
		return internalEvent;
	}
	
	public static InternalEvent createItemOnGroundInternalEvent(ItemType itemType, Point coords, int quantity)
	{
		InternalEvent internalEvent = noActorInternalEvent(InternalEventType.CREATE_ITEM);
		internalEvent.flags[1] = coords.x;
		internalEvent.flags[2] = coords.y;
		internalEvent.flags[3] = quantity;
		internalEvent.value = itemType.name();
		return internalEvent;
	}
	
	public static InternalEvent moveItemInternalEvent(Point origin, Point target)
	{
		return moveItemInternalEvent(-1, origin.x, origin.y, target);
	}
	
	public static InternalEvent upgradeWeaponInternalEvent(int actorIndex, InventorySelectionKey itemSource)
	{
		return inventoryItemInternalEvent(InternalEventType.UPGRADE_ITEM, actorIndex, itemSource);
	}
	
	public static InternalEvent downgradeItemInternalEvent(int actorIndex, InventorySelectionKey itemSource)
	{
		return inventoryItemInternalEvent(InternalEventType.DOWNGRADE_ITEM, actorIndex, itemSource);
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
	
	public static InternalEvent pickupInternalEvent(int actorIndex, ItemSource targetInventorySection)
	{
		InternalEvent internalEvent = actorOnlyInternalEvent(InternalEventType.PICKUP, actorIndex);
		internalEvent.value = targetInventorySection.name();
		return internalEvent;
	}
	
	public static InternalEvent dropInternalEvent(int actorIndex, InventorySelectionKey itemSource)
	{
		return inventoryItemInternalEvent(InternalEventType.DROP, actorIndex, itemSource);
	}
	
	public static InternalEvent equipInternalEvent(int actorIndex, InventorySelectionKey originalInventorySlot, InventorySelectionKey targetInventorySlot)
	{
		return inventoryItemTransferInternalEvent(InternalEventType.EQUIP, actorIndex, originalInventorySlot, targetInventorySlot);
	}
	
	public static InternalEvent unequipInternalEvent(int actorIndex, InventorySelectionKey originalInventorySlot, InventorySelectionKey targetInventorySlot)
	{
		return inventoryItemTransferInternalEvent(InternalEventType.UNEQUIP, actorIndex, originalInventorySlot, targetInventorySlot);
	}
	
	public static InternalEvent swapItemsInternalEvent(int actorIndex, InventorySelectionKey inventorySlot1, InventorySelectionKey inventorySlot2)
	{
		return inventoryItemTransferInternalEvent(InternalEventType.SWAP, actorIndex, inventorySlot1, inventorySlot2);
	}
	
	public static InternalEvent inventoryItemInternalEvent(InternalEventType eventType, int actorIndex, InventorySelectionKey itemSource)
	{
		InternalEvent internalEvent = actorOnlyInternalEvent(eventType, actorIndex);
		internalEvent.flags[1] = itemSource.getItemSource().intValue();
		internalEvent.flags[2] = itemSource.getItemIndex();
		return internalEvent;
	}
	
	public static InternalEvent inventoryItemTransferInternalEvent(InternalEventType eventType, int actorIndex, InventorySelectionKey originalInventorySlot, InventorySelectionKey targetInventorySlot)
	{
		InternalEvent internalEvent = actorOnlyInternalEvent(eventType, actorIndex);
		internalEvent.flags[1] = originalInventorySlot.getItemSource().intValue();
		internalEvent.flags[2] = originalInventorySlot.getItemIndex();
		internalEvent.flags[3] = targetInventorySlot.getItemSource().intValue();
		internalEvent.flags[4] = targetInventorySlot.getItemIndex();
		return internalEvent;
	}
	
	public static InternalEvent giveItemInternalEvent(int giverActorIndex, ItemSource itemSource, int itemIndex, int quantity, int receiverActorIndex)
	{
		InternalEvent internalEvent = actorOnlyInternalEvent(InternalEventType.GIVE, giverActorIndex);
		internalEvent.flags[1] = itemSource.intValue();
		internalEvent.flags[2] = itemIndex;
		internalEvent.flags[3] = quantity;
		internalEvent.flags[4] = receiverActorIndex;
		return internalEvent;
	}
	
	public static InternalEvent chatInternalEvent(int actorIndex, int targetIndex)
	{
		InternalEvent toRet = actorOnlyInternalEvent(InternalEventType.CHAT, actorIndex);
		
		toRet.flags[1] = targetIndex;
		
		return toRet;
	}
	
	public static InternalEvent changeInventoryItemHpInternalEvent(int actorIndex, InventorySelectionKey itemSource, int changeAmount)
	{
		return changeItemHpInternalEvent(actorIndex, itemSource.getItemSource().intValue(), itemSource.getItemIndex(), changeAmount);
	}
	
	public static InternalEvent changeGroundItemHpInternalEvent(Point coords, int changeAmount)
	{
		return changeItemHpInternalEvent(-1, coords.x, coords.y, changeAmount);
	}
	
	public static InternalEvent deleteHeldItemInternalEvent(int actorIndex, int slotIndex, int amountToRemove)
	{
		return deleteItemInternalEvent(actorIndex, ItemSource.EQUIPMENT.intValue(), slotIndex, amountToRemove);
	}
	
	public static InternalEvent deleteStoredItemInternalEvent(int actorIndex, int itemIndex, int amountToRemove)
	{
		return deleteItemInternalEvent(actorIndex, ItemSource.PACK.intValue(), itemIndex, amountToRemove);
	}
	
	public static InternalEvent deleteInventoryItemInternalEvent(int actorIndex, ItemSource itemSource, int itemIndex, int amountToRemove)
	{
		return deleteItemInternalEvent(actorIndex, itemSource.intValue(), itemIndex, amountToRemove);
	}
	
	public static InternalEvent deleteGroundItemInternalEvent(Point coords, int amountToRemove)
	{
		return deleteItemInternalEvent(-1, coords.x, coords.y, amountToRemove);
	}
	
	public static InternalEvent moveHeldItemInternalEvent(int actorIndex, int slotIndex, Point target)
	{
		return moveItemInternalEvent(actorIndex, ItemSource.EQUIPMENT.intValue(), slotIndex, target);
	}
	
	public static InternalEvent moveInventoryItemInternalEvent(int actorIndex, int itemIndex, Point target)
	{
		return moveItemInternalEvent(actorIndex, ItemSource.PACK.intValue(), itemIndex, target);
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
	
	public static InternalEvent changeHpInternalEvent(int actorIndex, int amount)
	{
		InternalEvent toRet = new InternalEvent(InternalEventType.ATTACK);
		
		toRet.flags[1] = actorIndex;
		toRet.flags[2] = amount * -1;
		
		toRet.actionCost = 0;
		
		return toRet;
	}
	
	public static InternalEvent changeActorAiInternalEvent(int actorIndex, AiType newAi)
	{
		InternalEvent toRet = actorOnlyInternalEvent(InternalEventType.CHANGE_ACTOR_AI, actorIndex);
		
		toRet.value = newAi.name();
		
		return toRet;
	}
	
	public static InternalEvent setActorAiToRepeatDirectionInternalEvent(int actorIndex, Direction direction)
	{
		InternalEvent toRet = changeActorAiInternalEvent(actorIndex, AiType.REPEAT_LAST_MOVE);
		Point coordChange = direction.getCoordChange();
		
		toRet.flags[1] = coordChange.x;
		toRet.flags[2] = coordChange.y;
		
		return toRet;
	}
	
	public static InternalEvent interruptionEvent(int actorIndex)
	{
		return actorOnlyInternalEvent(InternalEventType.INTERRUPTION, actorIndex);
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
	
	public void setActionCost(int cost)
	{
		actionCost = cost;
	}

	public void setInterruptible(boolean isInterruptible)
	{
		interruptible = isInterruptible;
	}
	
	//the lack of additional setters means that Events are largely immutable for the rest of the project
	public InternalEventType getInternalEventType()
	{
		return internalEventType;
	}

	public int getFlag(int index)
	{
		return flags[index];
	}
	
	public String getValue()
	{
		return value;
	}
	
	public int getActionCost()
	{
		return actionCost;
	}
	
	public boolean isInterruptible()
	{
		return interruptible;
	}
	
	@Override
	public String toString()
	{
		String returnString =  "InternalEvent(" + internalEventType + ")[";
		
		for (int i = 0; i < TOTAL_FLAGS; i++)
			returnString = returnString + flags[i] + " ";
		
		return returnString + "], value=" + value + ", cost=" + actionCost;
	}
}
