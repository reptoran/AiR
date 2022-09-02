package main.data.event.environment;

import main.data.event.EventObserver;
import main.data.event.InternalEvent;

public class EventInterruptionManager implements EventObserver
{
	private EnvironmentEventQueue queue = null;
	
	private static EventInterruptionManager instance = null;
	
	private EventInterruptionManager() {}
	
	public static EventInterruptionManager getInstance()
	{
		if (instance == null)
			instance = new EventInterruptionManager();
		
		return instance;
	}
	
	public void setEventQueue(EnvironmentEventQueue queue)
	{
		this.queue = queue;
	}
	
	@Override
	public void receiveInternalEvent(InternalEvent internalEvent)
	{
//		System.out.println("\t\tInternal event received by interruption manager with type " + internalEvent.getInternalEventType());
		
		if (queue == null)
			return;
		
		for (EnvironmentEvent event : queue.getQueueContents())
		{
			try {
				InterruptibleEnvironmentEvent intEvent = (InterruptibleEnvironmentEvent) event;
				intEvent.attemptInterrupt(internalEvent);
			} catch (ClassCastException cce)
			{
				continue;
			}
		}
	}
}
