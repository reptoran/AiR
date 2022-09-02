package main.data.event.environment;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import main.data.event.InternalEvent;
import main.data.event.InternalEventType;

public abstract class InterruptibleEnvironmentEvent extends AbstractEnvironmentEvent
{
	private Set<InternalEventType> interrupters = new HashSet<InternalEventType>();
	private ActorTurnEvent actorTurnEventToTriggerUponConclusionOrInterruption;
	
	protected InterruptibleEnvironmentEvent(EnvironmentEventQueue eventQueue, ActorTurnEvent turnEvent)
	{
		actorTurnEventToTriggerUponConclusionOrInterruption = turnEvent;
		this.eventQueue = eventQueue;
		this.eventQueue.remove(actorTurnEventToTriggerUponConclusionOrInterruption);
		addInterruptingEventType(InternalEventType.ATTACK);
	}
	
	protected void addInterruptingEventType(InternalEventType internalEventType)
	{
		interrupters.add(internalEventType);
	}
	
	public void attemptInterrupt(InternalEvent interruptingEvent)
	{
		for (InternalEventType eventType : interrupters)
		{
			if (eventType == interruptingEvent.getInternalEventType())
			{
				interrupt(interruptingEvent);
				return;
			}
		}
	}
	
	@Override
	public List<InternalEvent> trigger()
	{
		resetActorTurnEvent();
		return completeEvent();
	}
	
	private void interrupt(InternalEvent interruptingEvent)
	{
		if (!interruptConditionsMet(interruptingEvent))
			return;
		
		eventQueue.remove(this);
		resetActorTurnEvent();
		eventQueue.add(new InterruptionEvent(this));
	}
	
	//TODO: Note that this intentionally gives the interrupted actor the very next action after the interruption.  The rationale is that yes, maybe a very
	//		fast monster would have otherwise been able to get in two, three, four attacks before the player could have normally attacked again, but they are
	//		giving up that attack by performing something interruptible.  So say it's my turn and the monster can attack me three times.  If I attack him, that's
	//		atomic, and I hit once to get hit three times.  But if I start to take off armor, I'm giving up a hit against me (just one), and I don't get to
	//		complete my action.  Then, if I choose to attack on the turn after the interruption, I trade my single hit for three more, but I've also suffered
	//		that "bonus" attack before I took my attack action.
	private void resetActorTurnEvent()
	{
		if (actorTurnEventToTriggerUponConclusionOrInterruption == null)
			return;
		
		actorTurnEventToTriggerUponConclusionOrInterruption.ticksBeforeActing = -1;
		eventQueue.add(actorTurnEventToTriggerUponConclusionOrInterruption);
	}
	
	protected abstract boolean interruptConditionsMet(InternalEvent interruptingEvent);
	protected abstract List<InternalEvent> completeEvent();
}
