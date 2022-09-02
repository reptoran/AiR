package main.entity.requirement;

import main.entity.CompareOperator;
import main.entity.actor.ActorType;
import main.entity.item.ItemType;

public class RequirementFactory
{
	public static Requirement impossibleRequirement()
	{
		Requirement requirement = new Requirement(RequirementType.PLAYER_ENTERS_ZONE);
		requirement.setDetails(null, "ZONE_THAT_DOESNT_EXIST_AND_NEVER_WILL", CompareOperator.GREATER_THAN, "99999");
		return requirement;
	}
	
	public static Requirement playerEntersZone(String zoneId)
	{
		Requirement requirement = new Requirement(RequirementType.PLAYER_ENTERS_ZONE);
		requirement.setDetails(null, zoneId, CompareOperator.GREATER_THAN, "0");
		return requirement;
	}
	
	public static Requirement actorHasItem(ActorType actor, ItemType item, CompareOperator compare, int quantity)
	{
		Requirement requirement = new Requirement(RequirementType.ACTOR_HAS_ITEM);
		requirement.setDetails(actor.name(), item.name(), compare, String.valueOf(quantity));
		return requirement;
	}
}
