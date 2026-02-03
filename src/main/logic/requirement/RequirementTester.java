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
		QuestNode nodeToCheck = null;
		
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
			nodeToCheck = QuestManager.getInstance().getNodeForCombinedQuestNodeTag(value);
			if (nodeToCheck.isActive())
				return true;
			return false;
		case QUEST_NODE_INACTIVE:
			nodeToCheck = QuestManager.getInstance().getNodeForCombinedQuestNodeTag(value);
			if (nodeToCheck.isActive())
				return false;
			return true;
		case QUEST_NODE_COMPLETE:
			nodeToCheck = QuestManager.getInstance().getNodeForCombinedQuestNodeTag(value);
			if (nodeToCheck.isComplete())
				return true;
			return false;
		case QUEST_NOT_STARTED:
			boolean questStarted = QuestManager.getInstance().isQuestStarted(value);
			if (questStarted)
				return false;
			return true;
		case QUEST_NOT_KNOWN:
			boolean questKnown = QuestManager.getInstance().isQuestKnown(value);
			if (questKnown)
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
