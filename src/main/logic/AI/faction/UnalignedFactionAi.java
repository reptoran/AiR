package main.logic.AI.faction;

import main.logic.AI.ActorAI;

public class UnalignedFactionAi extends ActorAI
{
	@Override
	protected void setEnemyFactionTypes()
	{
		getEnemyFactionTypes().clear();
		getEnemyFactionTypes().add(FactionType.COALIGNED);
		getEnemyFactionTypes().add(FactionType.WILD);
		getEnemyFactionTypes().add(FactionType.EVIL);
		getEnemyFactionTypes().add(FactionType.UNALIGNED);
	}

	@Override
	protected FactionType getFaction()
	{
		return FactionType.UNALIGNED;
	}
}
