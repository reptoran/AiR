package main.data.event;

public enum InternalEventType
{
	WAIT,
	LOCAL_MOVE,
	WORLD_MOVE,
	ENTER_LOCAL,
	ENTER_WORLD,
	ZONE_TRANSITION,
	PICKUP,
	DROP,
	EQUIP,
	UNEQUIP,
	SWAP,
	CHAT,
	CHANGE_ACTOR_AI,
	CHANGE_ITEM_HP,
	CHANGE_ITEM_COORDS,
	CREATE_ITEM,
	DELETE_ITEM,
	MOVE_ITEM,
	UPGRADE_ITEM,
	DOWNGRADE_ITEM,
	GIVE,
	ATTACK,
	DEATH,
	INTERRUPTION,
	SAVE_GAME,
	EXIT_GAME
}
