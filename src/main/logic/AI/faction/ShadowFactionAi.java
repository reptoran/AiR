package main.logic.AI.faction;

import main.logic.AI.ActorAI;

public class ShadowFactionAi extends ActorAI
{
	@Override
	protected void setEnemyFactionTypes()
	{
		getEnemyFactionTypes().clear();
		getEnemyFactionTypes().add(FactionType.COALIGNED);
	}

	@Override
	protected FactionType getFaction()
	{
		return FactionType.SHADOW;
	}
}
