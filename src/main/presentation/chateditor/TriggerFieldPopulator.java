package main.presentation.chateditor;

import main.entity.event.TriggerType;

public class TriggerFieldPopulator extends AbstractFieldPopulator
{	
	private static TriggerFieldPopulator instance = null;
	
	private TriggerFieldPopulator() {}
	
	public static TriggerFieldPopulator getInstance()
	{
		if (instance == null)
			instance = new TriggerFieldPopulator();
		
		return instance;
	}
	
	public void updateFields(TriggerType triggerType)
	{
		if (triggerType == null)
		{
			disableModifiers();
			disableValues();
			clearComparisons();
			return;
		}
		
		switch (triggerType)
		{
		case CHANGE_HP:
			disableModifiers();
			enableValues();
			addFreeformValues();
			addExactOperator();
			disableComparisons();
			break;
		case CHANGE_HP_OF_ACTOR:
			enableModifiers();
			addActorModifiers();
			enableValues();
			addFreeformValues();
			addExactOperator();
			disableComparisons();
			break;
		case GIVE_ITEM_TO:	//falls through
		case GET_ITEM_FROM:
			enableModifiers();
			addActorModifiers();
			enableValues();
			addItemValues();
			addExactOperator();
			enableComparisons();
			clearComparisons();
			break;
		case ACTIVATE_QUEST: //falls through
		case DISCOVER_QUEST: //falls through
		case COMPLETE_QUEST:
			disableModifiers();
			addQuestTagValues();
			disableComparisons();
			break;
		case SET_QUEST_NODE_STATUS:
			enableModifiers();
			addQuestNodeTagModifiers();
			addQuestNodeStatusValues();
			disableComparisons();
			break;
			
		default:
			break;
		}
	}
}
