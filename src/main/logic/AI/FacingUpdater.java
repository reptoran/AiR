package main.logic.AI;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import main.data.DataAccessor;
import main.data.event.ActorCommand;
import main.entity.actor.Actor;
import main.entity.zone.Zone;
import main.logic.Direction;
import main.logic.AI.faction.FactionType;
import main.logic.AI.faction.NoneFactionAi;

public class FacingUpdater extends NoneFactionAi
{
	private List<FactionType> enemyFactions = new ArrayList<FactionType>();
	
	private static FacingUpdater instance = null;
	
	private FacingUpdater() {}
	
	public static FacingUpdater getInstance()
	{
		if (instance == null)
			instance = new FacingUpdater();
		
		return instance;
	}
	
	public Direction getDirectionOfNearestEnemy(Zone zone, Actor actor)
	{
		defineEnemyFactionsFromActorAi(actor);
		
		setActorTargets(zone, actor);
		
		getEnemyFactionTypes().clear();
		
		if (nearestEnemy == null)
			return null;
		
		return getDirectionOfTargetActor(zone, actor, nearestEnemy);
	}

	private void defineEnemyFactionsFromActorAi(Actor actor)
	{
		ActorAI actorAi = DataAccessor.getInstance().getActorAi(actor.getAI());
		enemyFactions = actorAi.getEnemyFactionTypes();
		getEnemyFactionTypes().clear();
		getEnemyFactionTypes().addAll(enemyFactions);
	}
	
	public Direction getDirectionOfTargetActor(Zone zone, Actor actor, Actor targetActor)
	{
		Point target = zone.getCoordsOfActor(targetActor);
		ActorCommand moveCommand = moveTowardPoint(zone, actor, target);
		
		String directionToFace = moveCommand.getArgument1();
		return Direction.fromString(directionToFace);
	}
	
	@Override
	public ActorCommand getNextCommand(Zone zone, Actor actor)
	{
		throw new UnsupportedOperationException("UpdateFacingAi should only be used by Engine.java, so this should never be called.  Call getDirectionOfNearestEnemy() instead.");
	}
}
