package main.data;

import java.util.ArrayList;
import java.util.List;

import main.entity.actor.Actor;
import main.logic.AI.AiType;

public class ActorTurnQueue {
	private List<Actor> actors = new ArrayList<Actor>();
	
	public AiType getNextActorAi() {
		return actors.get(0).getAI();
	}
	
	public Actor getNextActor() {
		Actor nextActor = actors.get(0);
		
		int ticksLeftBeforeActing = nextActor.getTicksLeftBeforeActing();
		
		if (ticksLeftBeforeActing > 0) {
			updateQueueToNextTurn(ticksLeftBeforeActing);
		}
		
		return nextActor;
	}
	
	public Actor popNextActor() {
		Actor nextActor = getNextActor();
		actors.remove(0);
		return nextActor;
	}
	
	public void add(Actor actor) {
		if (actors.contains(actor)) {
			return;
		}
		
		for (int i = 0; i < actors.size(); i++) {
			Actor currentActor = actors.get(i);
			
			//used to put the actor in front of other actors sharing its remaining ticks; this caused double turns
			if (actor.getTicksLeftBeforeActing() < currentActor.getTicksLeftBeforeActing()) {
				actors.add(i, actor);
				return;
			}
		}
		
		actors.add(actor);		//put it at the end if we haven't inserted it yet
	}
	
	public boolean remove(Actor actor) {
		if (actors.contains(actor))
			return actors.remove(actor);
		
		return false;
	}
	
	public void clear() {
		actors.clear();
	}

	private void updateQueueToNextTurn(int ticksLeftBeforeActing)
	{
		for (Actor actor : actors) {
			actor.reduceTicksLeftBeforeActing(ticksLeftBeforeActing);
		}
	}
}