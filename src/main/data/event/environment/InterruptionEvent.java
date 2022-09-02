package main.data.event.environment;

import java.util.List;

import main.data.DataAccessor;
import main.data.event.InternalEvent;

public class InterruptionEvent extends AbstractEnvironmentEvent
{
	private AbstractEnvironmentEvent interruptedEvent;
	
	public InterruptionEvent(AbstractEnvironmentEvent interruptedEvent)
	{
		this.eventQueue = interruptedEvent.eventQueue;
		this.ticksBeforeActing = -1;
		this.type = EnvironmentEventType.INTERRUPTION;
		this.actor = interruptedEvent.actor;
		this.secondaryActor = interruptedEvent.secondaryActor;
		this.item = interruptedEvent.item;
		
		for (int i = 0; i < TOTAL_VALUES; i++)
			this.values[i] = interruptedEvent.values[i];
		
		this.interruptedEvent = interruptedEvent;
	}
	
	public EnvironmentEvent getInterruptedEvent()
	{
		return interruptedEvent;
	}
	
	@Override
	public List<InternalEvent> trigger()
	{
		return singleEventList(InternalEvent.interruptionEvent(DataAccessor.getInstance().getIndexOfActor(actor)));
	}

	@Override
	public EnvironmentEventType getType()
	{
		return EnvironmentEventType.INTERRUPTION;
	}
}
