package main.entity.item;

import main.entity.actor.ActorType;
import main.entity.feature.FeatureType;
import main.entity.tile.TileType;

public class ItemUsageKey
{
	private final ItemGroup itemUsedGroup;		//TODO: This WILL NOT WORK in the usage maps, because maps need unique keys, and any group with an item
	private final ActorType actorTarget;		//		will prevent any individual item in that group from being added.
	private final ItemType itemTarget;			//		Note though that as long as the targets are different (so, for example, a one-off where any bladed
	private final FeatureType featureTarget;	//		weapon can free a particular NPC), it will still work out.
	private final TileType tileTarget;

	public ItemUsageKey(ItemType item, ActorType target)
	{
		this(item, target, null, null, null);
	}

	public ItemUsageKey(ItemType item, ItemType target)
	{
		this(item, null, target, null, null);
	}

	public ItemUsageKey(ItemType item, FeatureType target)
	{
		this(item, null, null, target, null);
	}

	public ItemUsageKey(ItemType item, TileType target)
	{
		this(item, null, null, null, target);
	}
	
	private ItemUsageKey(ItemType itemUsed, ActorType actorTarget, ItemType itemTarget, FeatureType featureTarget, TileType tileTarget)
	{
		this(ItemGroup.singleItemGroup(itemUsed), actorTarget, itemTarget, featureTarget, tileTarget);
	}
	
	private ItemUsageKey(ItemGroup itemUsedGroup, ActorType actorTarget, ItemType itemTarget, FeatureType featureTarget, TileType tileTarget)
	{
		this.itemUsedGroup = itemUsedGroup;
		this.actorTarget = actorTarget;
		this.itemTarget = itemTarget;
		this.featureTarget = featureTarget;
		this.tileTarget = tileTarget;
	}
	
	public ItemUsageKey asAnyTarget()
	{
		ActorType actorTargetAny = null;
		ItemType itemTargetAny = null;
		FeatureType featureTargetAny = null;
		TileType tileTargetAny = null;
		
		if (actorTarget != null)
			actorTargetAny = ActorType.ANY_ACTOR;
		if (itemTarget != null)
			itemTargetAny = ItemType.ANY_ITEM;
		if (featureTarget != null)
			featureTargetAny = FeatureType.ANY_FEATURE;
		if (tileTarget != null)
			tileTargetAny = TileType.ANY_TILE;
		
		return new ItemUsageKey(itemUsedGroup, actorTargetAny, itemTargetAny, featureTargetAny, tileTargetAny);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actorTarget == null) ? 0 : actorTarget.hashCode());
		result = prime * result + ((featureTarget == null) ? 0 : featureTarget.hashCode());
		result = prime * result + ((itemTarget == null) ? 0 : itemTarget.hashCode());
		result = prime * result + ((itemUsedGroup == null) ? 0 : itemUsedGroup.hashCode());
		result = prime * result + ((tileTarget == null) ? 0 : tileTarget.hashCode());
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
		ItemUsageKey other = (ItemUsageKey) obj;
		if (actorTarget != other.actorTarget)
			return false;
		if (featureTarget != other.featureTarget)
			return false;
		if (itemTarget != other.itemTarget)
			return false;
		if (!itemUsedGroup.equals(other.itemUsedGroup))
			return false;
		if (tileTarget != other.tileTarget)
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		String target = "";

		if (actorTarget != null)
			target = actorTarget.name();
		if (featureTarget != null)
			target = featureTarget.name();
		if (itemTarget != null)
			target = itemTarget.name();
		if (tileTarget != null)
			target = tileTarget.name();
		
		return "ItemUsageKey[" + itemUsedGroup.getName() + ":" + target + "]";
	}
}
