package main.data.event.environment;

import java.util.ArrayList;
import java.util.List;

import main.data.DataAccessor;
import main.data.event.InternalEvent;
import main.entity.actor.Actor;
import main.entity.actor.SkillType;

public class HitpointRegenEvent extends RecurringEnvironmentEvent
{	
	public HitpointRegenEvent(Actor actor, EnvironmentEventQueue eventQueue)
	{
		this.actor = actor;
		this.eventQueue = eventQueue;
	}
	
	@Override
	public List<InternalEvent> trigger()
	{
		int recurTicks;
		
		switch(actor.getSkillRank(SkillType.HEALING)) 
		{
		case NOVICE:
			recurTicks = 100;
			break;
		case ADEPT:
			recurTicks = 80;
			break;
		case EXPERT:
			recurTicks = 60;
			break;
		case MASTER:
			recurTicks = 40;
			break;
		case UNSKILLED:
			recurTicks = 500;
			break;
		case UNKNOWN:
		default:
			recurTicks = 5000;
			break;
		}
		
		recur(recurTicks);
		
		List<InternalEvent> eventList = new ArrayList<InternalEvent>();
		
		if (actor.getCurHp() >= actor.getMaxHp())
			return eventList;
			
		InternalEvent event = InternalEvent.attackInternalEvent(-1, DataAccessor.getInstance().getIndexOfActor(actor), -1, 0);
		eventList.add(event);
		return eventList;
	}

	@Override
	public EnvironmentEventType getType()
	{
		return EnvironmentEventType.HP_REGEN;
	}
}
