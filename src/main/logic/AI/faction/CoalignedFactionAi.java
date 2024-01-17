package main.logic.AI.faction;

import main.logic.AI.ActorAI;

public abstract class CoalignedFactionAi extends ActorAI
{
	@Override
	protected void setEnemyFactionTypes()
	{
		getEnemyFactionTypes().clear();
		getEnemyFactionTypes().add(FactionType.WILD);
		getEnemyFactionTypes().add(FactionType.EVIL);
		getEnemyFactionTypes().add(FactionType.SHADOW);
		getEnemyFactionTypes().add(FactionType.UNALIGNED);
	}

	@Override
	protected FactionType getFaction()
	{
		return FactionType.COALIGNED;
	}
}
