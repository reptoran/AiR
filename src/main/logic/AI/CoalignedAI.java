package main.logic.AI;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import main.data.event.ActorCommand;
import main.entity.actor.Actor;
import main.entity.tile.Tile;
import main.entity.zone.Zone;
import main.logic.Direction;
import main.logic.RPGlib;
import main.logic.pathfinding.Pathfinder;

public class CoalignedAI extends ActorAI
{
	@Override
	public ActorCommand getNextCommand(Zone zone, Actor actor)
	{
		return getRandomLegalMoveCommand(zone, actor);
	}
	
	@Override
	protected List<Direction> getValidRandomMoveDirections(Zone zone, Actor actor)
	{
		Direction allDirections[] = Direction.values();
		List<Direction> directions = new ArrayList<Direction>();
		Point origin = zone.getCoordsOfActor(actor);
		
		for (Direction direction : allDirections)
		{
			if (direction == Direction.DIRNONE)
			{
				directions.add(direction);
				continue;
			}
			
			Point coordChange = direction.getCoordChange();
			Point target = RPGlib.addPoints(origin, coordChange);
			
			Tile targetTile = zone.getTile(target);
			
			if (targetTile.obstructsCoaligned())
				continue;
			
			if (targetTile.obstructsMotion())
				continue;
			
			Actor targetActor = targetTile.getActorHere();
			
			if (targetActor != null)
			{
				if (targetActor.getAI() == AiType.COALIGNED)
					continue;
				if (targetActor.getAI() == AiType.HUMAN_CONTROLLED)
					continue;
			}
			
			directions.add(direction);
		}
		
		return directions;
	}
	
	@Override
	protected Point nextPointToApproachTarget(Zone zone, Actor actor, Point target)
	{
		Point origin = zone.getCoordsOfActor(actor);
		List<Point> line = Pathfinder.findPath(zone, origin, target);
		
		if (line.isEmpty())
			return origin;
		
		Point nextPoint = line.get(0);
		
		if (zone.getTile(nextPoint).obstructsCoaligned())
			return origin;
		
		return nextPoint;
	}

	@Override
	protected List<AiType> getEnemyAiTypes()
	{
		return generateAiList();
	}
}
