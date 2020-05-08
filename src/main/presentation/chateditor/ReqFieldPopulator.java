package main.presentation.chateditor;

import main.entity.chat.ChatReqType;

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
	
	public void updateFields(ChatReqType requirementType)
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
		default:
			break;
		}
	}
}
