package main.logic.AI.faction;

import main.logic.AI.ActorAI;

public abstract class NoneFactionAi extends ActorAI
{
	@Override
	protected void setEnemyFactionTypes()
	{
		getEnemyFactionTypes().clear();
	}

	@Override
	protected FactionType getFaction()
	{
		return FactionType.NONE;
	}
}
