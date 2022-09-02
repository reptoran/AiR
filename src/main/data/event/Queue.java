package main.data.event;

import java.util.ArrayList;
import java.util.List;

import main.data.event.environment.InterruptionEvent;
import main.entity.Queueable;
import main.presentation.Logger;

public class Queue
{
	protected List<Queueable> elements = new ArrayList<Queueable>();

	public Queueable getNextElement()
	{
		Queueable nextElement = elements.get(0);

		int ticksLeftBeforeActing = nextElement.getTicksLeftBeforeActing();

		if (ticksLeftBeforeActing > 0)
		{
			updateQueueToNextTurn(ticksLeftBeforeActing);
		}

		return nextElement;
	}

	public Queueable popNextElement()
	{
		Queueable nextElement = getNextElement();
		elements.remove(0);
		return nextElement;
	}

	public void add(Queueable element)
	{
		if (elements.contains(element))
		{
			return;
		}
		
		//negative ticks left before acting means we want this at the very front of the queue no matter what, such as for the actor turn when their last event was interrupted
		if (element.getTicksLeftBeforeActing() < 0)
		{
			element.increaseTicksLeftBeforeActing(element.getTicksLeftBeforeActing() * -1);
			elements.add(0, element);
			return;
		}

		for (int i = 0; i < elements.size(); i++)
		{
			Queueable currentElement = elements.get(i);

			// originally put the element in front of other element sharing its remaining ticks; this caused double turns
			if (element.getTicksLeftBeforeActing() < currentElement.getTicksLeftBeforeActing())
			{
				elements.add(i, element);
				Logger.debug("Adding element [" + element.getClass() + "] to queue with " + element.getTicksLeftBeforeActing() + " ticks left before acting");
				return;
			}
		}

		elements.add(element); // put it at the end if we haven't inserted it yet
	}
	
	public void addPriority(Queueable element)
	{
		if (elements.contains(element))
		{
			return;
		}

		for (int i = 0; i < elements.size(); i++)
		{
			Queueable currentElement = elements.get(i);

			// this should allow events to be inserted earlier in the queue than other events with the same ticks remaining
			if (element.getTicksLeftBeforeActing() <= currentElement.getTicksLeftBeforeActing())
			{
				elements.add(i, element);
				return;
			}
		}

		elements.add(element); // put it at the end if we haven't inserted it yet
	}

	public boolean remove(Queueable element)
	{
		if (elements.contains(element))
			return elements.remove(element);

		return false;
	}

	public int size()
	{
		return elements.size();
	}

	public void clear()
	{
		elements.clear();
	}

	protected void updateQueueToNextTurn(int ticksLeftBeforeActing)
	{
		for (Queueable actor : elements)
		{
			actor.reduceTicksLeftBeforeActing(ticksLeftBeforeActing);
		}
	}

	@Override
	public int hashCode()
	{
		final int prime = 127;
		int result = 1;
		int elementsCounted = 0;

		if (elements == null)
			return prime;

		for (Queueable element : elements)
			result = prime * (result + element.hashCode() + (elementsCounted++));

		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Queue other = (Queue) obj;
		if (elements == null)
		{
			if (other.elements != null)
				return false;
		} else if (!elements.equals(other.elements))
			return false;
		return true;
	}
}
