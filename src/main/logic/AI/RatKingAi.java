package main.logic.AI;

import java.awt.Point;

import main.data.event.ActorCommand;
import main.entity.actor.Actor;
import main.entity.actor.ActorFactory;
import main.entity.actor.NaturalWeapon;
import main.entity.zone.Zone;
import main.logic.Direction;
import main.logic.AI.faction.WildFactionAi;
import main.presentation.Logger;

public class RatKingAi extends WildFactionAi
{
	//remember, this cannot have any state, since AI is essentially singleton and isn't saved
	
	@Override
	public ActorCommand getNextCommand(Zone zone, Actor actor)
	{
		setActorTargets(zone, actor);
		
		if (facingAndAdjacentToNearestEnemy(zone, actor))
		{
			increaseActorNaturalWeaponsByOne(actor);
			Point target = zone.getCoordsOfActor(nearestEnemy);
			return moveTowardPoint(zone, actor, target);
		}
		
		resetActorNaturalWeapons(actor);
		return ActorCommand.move(Direction.DIRNONE);
	}

	private void increaseActorNaturalWeaponsByOne(Actor actor)
	{
		if (actor.getNaturalWeapons().getAttacks().size() >= 10)
		{
			Logger.debug("rat king has the maximum possible attacks; attacks not increased.");
			return;
		}
		
		NaturalWeapon mainWeapon = actor.getNaturalWeapons().getAttacks().get(0);
		actor.getNaturalWeapons().getAttacks().add(mainWeapon.clone());
		
		Logger.debug("rat king is increasing attacks; current attack count is " + actor.getNaturalWeapons().getAttacks().size());
	}

	private void resetActorNaturalWeapons(Actor actor)
	{
		Actor baseActor = ActorFactory.generateNewActor(actor.getType());
		actor.getNaturalWeapons().getAttacks().clear();
		actor.getNaturalWeapons().getAttacks().addAll(baseActor.getNaturalWeapons().getAttacks());
		
		Logger.debug("rat king has reset its attacks; current attack count is " + actor.getNaturalWeapons().getAttacks().size());
	}

	private boolean facingAndAdjacentToNearestEnemy(Zone zone, Actor actor)
	{
		Point coordChange = actor.getFacing().getCoordChange();
		Point actorCoords = zone.getCoordsOfActor(actor);
		Point targetCoords = new Point(actorCoords.x + coordChange.x, actorCoords.y + coordChange.y);
		Actor actorAtFacingPoint = zone.getActorAtCoords(targetCoords);
		
		if (nearestEnemy != null && actorAtFacingPoint == nearestEnemy)
		{
			Logger.debug("rat king IS facing adjacent enemy [" + nearestEnemy.getType() + "]");
			return true;
		}
		
		Logger.debug("rat king is not facing adjacent enemy");
		
		return false;
	}
}
