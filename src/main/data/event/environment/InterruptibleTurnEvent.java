package main.data.event.environment;

import java.util.List;

import main.data.DataAccessor;
import main.data.event.InternalEvent;

public class InterruptibleTurnEvent extends InterruptibleEnvironmentEvent
{
	private InternalEvent eventToExecute;
	
	public InterruptibleTurnEvent(InternalEvent eventToExecute, EnvironmentEventQueue eventQueue, ActorTurnEvent turnEvent)
	{
		super(eventQueue, turnEvent);
		this.ticksBeforeActing = eventToExecute.getActionCost();
		this.eventToExecute = eventToExecute;
		this.eventToExecute.setActionCost(0);
		this.actor = DataAccessor.getInstance().getActorFromIndex(eventToExecute.getFlag(0));
	}

	@Override
	public EnvironmentEventType getType()
	{
		return EnvironmentEventType.INTERRUPTABLE_EVENT;
	}

	@Override	//TODO: this code assumes the only interrupter is an attack event, which may not always be true; also in TransferrableInventoryItemEvent
	protected boolean interruptConditionsMet(InternalEvent interruptingEvent)	//TODO: for now, just interrupt only when attacked
	{
		int actorIndex = DataAccessor.getInstance().getIndexOfActor(actor);
		
		if (interruptingEvent.getFlag(0) == -1 && interruptingEvent.getFlag(2) <= 0)	//don't interrupt if it's an attack that doesn't originate from another actor and doesn't reduce hitpoints (such as HP regen)
			return false;
		
		if (actorIndex == interruptingEvent.getFlag(1))
			return true;
		
		return false;
	}

	@Override
	protected List<InternalEvent> completeEvent()
	{
		return singleEventList(eventToExecute);
	}
}
