package main.logic.requirement;

import main.data.Data;
import main.entity.CompareOperator;
import main.entity.quest.QuestManager;
import main.entity.quest.QuestNode;
import main.entity.requirement.Requirement;
import main.entity.requirement.RequirementType;

public abstract class RequirementTester
{
	protected boolean validateRequirement(Requirement requirement)
	{
		RequirementType reqType = requirement.getType();
		CompareOperator operator = requirement.getOperator();
		String modifier = requirement.getModifier();
		String value = requirement.getValue();
		String comparison = requirement.getComparison();
		
		String valueToCheck = "";
		
		switch (reqType)
		{
		case ACTOR_TYPE:
			valueToCheck = getData().getPlayer().getType().name();
			break;
		case HP_PERCENT:
			valueToCheck = String.valueOf(getData().getPlayer().getHpPercent());
			break;
		case ACTOR_HAS_ITEM:
			valueToCheck = RequirementValidator.getInstance().getValueToCheckForActorHasItem(modifier, value);
			break;
		case QUEST_NODE_ACTIVE:
			QuestNode nodeToCheck = QuestManager.getInstance().getNodeForCombinedQuestNodeTag(comparison);
			if (nodeToCheck.isActive())
				return true;
			return false;
		case QUEST_NOT_STARTED:
			boolean questStarted = QuestManager.getInstance().isQuestStarted(comparison);
			if (questStarted)
				return false;
			return true;
		//$CASES-OMITTED$
		default:
			return true;
		}
		
		if (!RequirementValidator.getInstance().checkRequirement(operator, comparison, valueToCheck))
			return false;
		
		return true;
	}
	
	protected abstract Data getData();
}
