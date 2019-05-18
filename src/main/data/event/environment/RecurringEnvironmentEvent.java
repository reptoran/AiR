package main.data.event.environment;

public abstract class RecurringEnvironmentEvent extends AbstractEnvironmentEvent
{	
	public void recur(int ticksUntilTrigger)
	{
		ticksBeforeActing = ticksUntilTrigger;
		eventQueue.add(this);
	}
}
