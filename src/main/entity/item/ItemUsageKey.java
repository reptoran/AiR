package main.entity.item;

import main.entity.actor.ActorType;
import main.entity.feature.FeatureType;
import main.entity.tile.TileType;

public class ItemUsageKey
{
	private final ItemType itemUsed;
	private final ActorType actorTarget;
	private final ItemType itemTarget;
	private final FeatureType featureTarget;
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
		this.itemUsed = itemUsed;
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
		
		return new ItemUsageKey(itemUsed, actorTargetAny, itemTargetAny, featureTargetAny, tileTargetAny);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actorTarget == null) ? 0 : actorTarget.hashCode());
		result = prime * result + ((featureTarget == null) ? 0 : featureTarget.hashCode());
		result = prime * result + ((itemTarget == null) ? 0 : itemTarget.hashCode());
		result = prime * result + ((itemUsed == null) ? 0 : itemUsed.hashCode());
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
		if (itemUsed != other.itemUsed)
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
		
		return "ItemUsageKey[" + itemUsed.name() + ":" + target + "]";
	}

//	@Override
//	public boolean equals(Object obj)		//returns true when comparing against ANY_***** types
//	{
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		ItemUsageKey other = (ItemUsageKey) obj;
//		if (itemUsed != other.itemUsed)
//			return false;
//		if (!compareActorTargets(actorTarget, other.actorTarget))
//			return false;
//		if (!compareItemTargets(itemTarget, other.itemTarget))
//			return false;
//		if (!compareFeatureTargets(featureTarget, other.featureTarget))
//			return false;
//		if (!compareTileTargets(tileTarget, other.tileTarget))
//			return false;
//		return true;
//	}
//
//	private boolean compareActorTargets(ActorType actorTarget1, ActorType actorTarget2)
//	{
//		if (actorTarget1 == null && actorTarget2 != null)
//			return false;
//		if (actorTarget1 != null && actorTarget2 == null)
//			return false;
//		if (actorTarget1 == ActorType.ANY_ACTOR || actorTarget2 == ActorType.ANY_ACTOR)
//			return true;
//		
//		return actorTarget1 == actorTarget2;
//	}
//
//	private boolean compareItemTargets(ItemType itemTarget1, ItemType itemTarget2)
//	{
//		if (itemTarget1 == null && itemTarget2 != null)
//			return false;
//		if (itemTarget1 != null && itemTarget2 == null)
//			return false;
//		if (itemTarget1 == ItemType.ANY_ITEM || itemTarget2 == ItemType.ANY_ITEM)
//			return true;
//		
//		return itemTarget1 == itemTarget2;
//	}
//
//	private boolean compareFeatureTargets(FeatureType featureTarget1, FeatureType featureTarget2)
//	{
//		if (featureTarget1 == null && featureTarget2 != null)
//			return false;
//		if (featureTarget1 != null && featureTarget2 == null)
//			return false;
//		if (featureTarget1 == FeatureType.ANY_FEATURE || featureTarget2 == FeatureType.ANY_FEATURE)
//			return true;
//		
//		return featureTarget1 == featureTarget2;
//	}
//
//	private boolean compareTileTargets(TileType tileTarget1, TileType tileTarget2)
//	{
//		if (tileTarget1 == null && tileTarget2 != null)
//			return false;
//		if (tileTarget1 != null && tileTarget2 == null)
//			return false;
//		if (tileTarget1 == TileType.ANY_TILE || tileTarget2 == TileType.ANY_TILE)
//			return true;
//		
//		return tileTarget1 == tileTarget2;
//	}
}
