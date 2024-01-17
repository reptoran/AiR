package main.logic.AI;

import java.awt.Point;

import main.data.Data;
import main.entity.actor.Actor;
import main.entity.zone.Zone;
import main.logic.Direction;
import main.presentation.Logger;

public class FacingCalculator
{
	private static FacingCalculator instance = null;
	
	private Data data = null;
	
	private FacingCalculator() {}
	
	public static FacingCalculator getInstance()
	{
		if (instance == null)
			instance = new FacingCalculator();
		
		return instance;
	}
	
	public void initialize(Data newData)
	{
		this.data = newData;
	}
	
	public boolean isActorBehindTarget(Actor actor, Actor target)
	{
		return determineRelativePosition(actor, target) == RelativePosition.BEHIND;
	}
	
	public boolean isActorDiagonallyBehindTarget(Actor actor, Actor target)
	{
		return determineRelativePosition(actor, target) == RelativePosition.DIAGONAL_BEHIND;
	}
	
	public boolean isActorBesideTarget(Actor actor, Actor target)
	{
		return determineRelativePosition(actor, target) == RelativePosition.BESIDE;
	}
	
	public boolean isActorDiagonallyInFrontOfTarget(Actor actor, Actor target)
	{
		return determineRelativePosition(actor, target) == RelativePosition.DIAGONAL_IN_FRONT;
	}
	
	public boolean isActorInFrontOfTarget(Actor actor, Actor target)
	{
		return determineRelativePosition(actor, target) == RelativePosition.IN_FRONT;
	}
	
	private RelativePosition determineRelativePosition(Actor actor, Actor target)
	{
		Zone zone = data.getCurrentZone();
		
		Point actorCoords = zone.getCoordsOfActor(actor);
		Point targetCoords = zone.getCoordsOfActor(target);
		Direction targetFacing = target.getFacing();
		
		int rowDif = actorCoords.x - targetCoords.x;
		int colDif = actorCoords.y - targetCoords.y;
		
		if (rowDif == -1 && colDif == -1)		// A
		{										//  T
			if (targetFacing == Direction.DIRNW)
				return RelativePosition.IN_FRONT;
			if (targetFacing == Direction.DIRN || targetFacing == Direction.DIRW)
				return RelativePosition.DIAGONAL_IN_FRONT;
			if (targetFacing == Direction.DIRNE || targetFacing == Direction.DIRSW)
				return RelativePosition.BESIDE;
			if (targetFacing == Direction.DIRE || targetFacing == Direction.DIRS)
				return RelativePosition.DIAGONAL_BEHIND;
			if (targetFacing == Direction.DIRSE)
				return RelativePosition.BEHIND;
		}
		
		if (rowDif == -1 && colDif == 0)		// A
		{										// T
			if (targetFacing == Direction.DIRN)
				return RelativePosition.IN_FRONT;
			if (targetFacing == Direction.DIRNW || targetFacing == Direction.DIRNE)
				return RelativePosition.DIAGONAL_IN_FRONT;
			if (targetFacing == Direction.DIRW || targetFacing == Direction.DIRE)
				return RelativePosition.BESIDE;
			if (targetFacing == Direction.DIRSW || targetFacing == Direction.DIRSE)
				return RelativePosition.DIAGONAL_BEHIND;
			if (targetFacing == Direction.DIRS)
				return RelativePosition.BEHIND;
		}
		
		if (rowDif == -1 && colDif == 1)		//  A
		{										// T
			if (targetFacing == Direction.DIRNE)
				return RelativePosition.IN_FRONT;
			if (targetFacing == Direction.DIRN || targetFacing == Direction.DIRE)
				return RelativePosition.DIAGONAL_IN_FRONT;
			if (targetFacing == Direction.DIRNW || targetFacing == Direction.DIRSE)
				return RelativePosition.BESIDE;
			if (targetFacing == Direction.DIRW || targetFacing == Direction.DIRS)
				return RelativePosition.DIAGONAL_BEHIND;
			if (targetFacing == Direction.DIRSW)
				return RelativePosition.BEHIND;
		}
		
		
		if (rowDif == 0 && colDif == -1)		// AT
		{
			if (targetFacing == Direction.DIRW)
				return RelativePosition.IN_FRONT;
			if (targetFacing == Direction.DIRNW || targetFacing == Direction.DIRSW)
				return RelativePosition.DIAGONAL_IN_FRONT;
			if (targetFacing == Direction.DIRN || targetFacing == Direction.DIRS)
				return RelativePosition.BESIDE;
			if (targetFacing == Direction.DIRNE || targetFacing == Direction.DIRSE)
				return RelativePosition.DIAGONAL_BEHIND;
			if (targetFacing == Direction.DIRE)
				return RelativePosition.BEHIND;
		}
		
		if (rowDif == 0 && colDif == 1)			// TA
		{
			if (targetFacing == Direction.DIRE)
				return RelativePosition.IN_FRONT;
			if (targetFacing == Direction.DIRNE || targetFacing == Direction.DIRSE)
				return RelativePosition.DIAGONAL_IN_FRONT;
			if (targetFacing == Direction.DIRN || targetFacing == Direction.DIRS)
				return RelativePosition.BESIDE;
			if (targetFacing == Direction.DIRNW || targetFacing == Direction.DIRSW)
				return RelativePosition.DIAGONAL_BEHIND;
			if (targetFacing == Direction.DIRW)
				return RelativePosition.BEHIND;
		}

		
		if (rowDif == 1 && colDif == -1)		//  T
		{										// A 
			if (targetFacing == Direction.DIRSW)
				return RelativePosition.IN_FRONT;
			if (targetFacing == Direction.DIRS || targetFacing == Direction.DIRW)
				return RelativePosition.DIAGONAL_IN_FRONT;
			if (targetFacing == Direction.DIRSE || targetFacing == Direction.DIRNW)
				return RelativePosition.BESIDE;
			if (targetFacing == Direction.DIRE || targetFacing == Direction.DIRN)
				return RelativePosition.DIAGONAL_BEHIND;
			if (targetFacing == Direction.DIRNE)
				return RelativePosition.BEHIND;
		}
		
		if (rowDif == 1 && colDif == 0)			// T
		{										// A
			if (targetFacing == Direction.DIRS)
				return RelativePosition.IN_FRONT;
			if (targetFacing == Direction.DIRSW || targetFacing == Direction.DIRSE)
				return RelativePosition.DIAGONAL_IN_FRONT;
			if (targetFacing == Direction.DIRW || targetFacing == Direction.DIRE)
				return RelativePosition.BESIDE;
			if (targetFacing == Direction.DIRNW || targetFacing == Direction.DIRNE)
				return RelativePosition.DIAGONAL_BEHIND;
			if (targetFacing == Direction.DIRN)
				return RelativePosition.BEHIND;
		}
		
		if (rowDif == 1 && colDif == 1)			// T
		{										//  A
			if (targetFacing == Direction.DIRSE)
				return RelativePosition.IN_FRONT;
			if (targetFacing == Direction.DIRS || targetFacing == Direction.DIRE)
				return RelativePosition.DIAGONAL_IN_FRONT;
			if (targetFacing == Direction.DIRSW || targetFacing == Direction.DIRNE)
				return RelativePosition.BESIDE;
			if (targetFacing == Direction.DIRW || targetFacing == Direction.DIRN)
				return RelativePosition.DIAGONAL_BEHIND;
			if (targetFacing == Direction.DIRNW)
				return RelativePosition.BEHIND;
		}

		
		Logger.warn("FacingCalculator - relative position could not be determined for actor at " + actorCoords + " and target at " + targetCoords + ".");
		return null;
	}
	
	private enum RelativePosition
	{
		BEHIND, DIAGONAL_BEHIND, BESIDE, DIAGONAL_IN_FRONT, IN_FRONT;
	}
}
