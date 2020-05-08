package main.data.event.environment;

import java.util.List;

import main.data.event.InternalEvent;
import main.entity.Queueable;
import main.entity.actor.Actor;

public interface EnvironmentEvent extends Queueable
{
	public List<InternalEvent> trigger();
	public EnvironmentEventQueue getEventQueue();
	public void setEventQueue(EnvironmentEventQueue eventQueue);
	public Actor getActor();
	public Actor getSecondaryActor();
	public int getValue();
	public int getSecondaryValue();
	public EnvironmentEventType getType();
	public SaveableEnvironmentEvent asSaveableEvent();
}
