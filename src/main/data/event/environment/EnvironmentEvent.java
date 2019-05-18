package main.data.event.environment;

import main.data.event.InternalEvent;
import main.entity.Queueable;
import main.entity.actor.Actor;

public interface EnvironmentEvent extends Queueable
{
	public InternalEvent trigger();
	public EnvironmentEventQueue getEventQueue();
	public void setEventQueue(EnvironmentEventQueue eventQueue);
	public Actor getActor();
	public EnvironmentEventType getType();
	public SaveableEnvironmentEvent asSaveableEvent();
}
