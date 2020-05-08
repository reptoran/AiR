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
			disableValues();
			addExactOperator();
			clearComparisons();
			break;
		case GIVE_ITEM_TO:	//falls through
		case GET_ITEM_FROM:
			enableModifiers();
			addActorModifiers();
			enableValues();
			addItemValues();
			addExactOperator();
			clearComparisons();
			break;
		default:
			break;
		}
	}
}
