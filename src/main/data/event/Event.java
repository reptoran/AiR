package main.data.event;

public class Event
{
	private static final int TOTAL_FLAGS = 3;
	
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

	public static Event waitEvent(int actorIndex, int movementCost)
	{
		Event event = actorOnlyEvent(EventType.WAIT, actorIndex);
		event.actionCost = movementCost;
		return event;
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
