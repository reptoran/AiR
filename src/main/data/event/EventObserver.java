package main.data.event;

public interface EventObserver
{
	void receiveInternalEvent(InternalEvent internalEvent);
}
