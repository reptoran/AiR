package main.logic.AI;

import main.data.event.ActorCommand;
import main.entity.actor.Actor;
import main.entity.item.ItemType;
import main.entity.item.recipe.RecipeManager;
import main.entity.zone.Zone;
import main.logic.requirement.RequirementValidator;

public class PhysicianAI extends CoalignedAI
{
	@Override
	public ActorCommand getNextCommand(Zone zone, Actor actor)
	{
		if (RequirementValidator.getInstance().doesActorHaveItem(actor, ItemType.MEDICINAL_FUNGUS, 2))
			return ActorCommand.recipe(RecipeManager.getInstance().getRecipeForItem(ItemType.HEALING_SALVE));
		
		return getRandomLegalMoveCommand(zone, actor);
	}
}
