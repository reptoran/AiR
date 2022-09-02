package main.presentation.chateditor;

import main.entity.requirement.RequirementType;

public class ReqFieldPopulator extends AbstractFieldPopulator
{
	private static ReqFieldPopulator instance = null;
	
	private ReqFieldPopulator() {}
	
	public static ReqFieldPopulator getInstance()
	{
		if (instance == null)
			instance = new ReqFieldPopulator();
		
		return instance;
	}
	
	public void updateFields(RequirementType requirementType)
	{
		if (requirementType == null)
		{
			disableModifiers();
			disableValues();
			clearComparisons();
			return;
		}
		
		switch (requirementType)
		{
		case ACTOR_TYPE:
			disableModifiers();
			disableValues();
			addStringOperators();
			addActorTypeComparisons();
			break;
		case HP_PERCENT:
			disableModifiers();
			disableValues();
			addIntOperators();
			clearComparisons();
			break;
		case ACTOR_HAS_ITEM:
			enableModifiers();
			addActorModifiers();
			enableValues();
			addItemValues();
			addIntOperators();
			clearComparisons();
			break;
		case PLAYER_ENTERS_ZONE:
			disableModifiers();
			disableValues();
			addStringOperators();
			addZoneNameComparisons();
			break;
		case QUEST_NODE_ACTIVE:
			disableModifiers();
			disableValues();
			enableComparisons();
			addStringOperators();
			addQuestNodeComparisons();
			break;
		case QUEST_NOT_STARTED:
			disableModifiers();
			disableValues();
			enableComparisons();
			addExactOperator();
			addQuestComparisons();
			break;
		default:
			break;
		}
	}
}
