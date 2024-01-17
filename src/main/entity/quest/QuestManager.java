package main.entity.quest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import main.data.Data;
import main.data.PlayerAdvancementManager;
import main.data.SaveableDataManager;
import main.data.event.EventObserver;
import main.data.event.InternalEvent;
import main.data.event.InternalEventType;
import main.entity.requirement.Requirement;
import main.entity.zone.predefined.PredefinedZoneLoader;
import main.logic.requirement.RequirementTester;
import main.logic.requirement.RequirementValidator;
import main.presentation.Logger;
import main.presentation.message.MessageBuffer;

public class QuestManager extends RequirementTester implements EventObserver, SaveableDataManager
{
	private Map<String, Quest> incompleteQuestsByTag = new HashMap<String, Quest>();
	private Map<String, Quest> completedQuestsByTag = new HashMap<String, Quest>();
	private Map<String, Quest> allQuestsByTag = new HashMap<String, Quest>();
	
	private Data data = null;
	
	private static final String NO_COMPLETED_QUESTS = "NONE";
	
	private static QuestManager instance = null;
	
	private QuestManager() {}
	
	public static QuestManager getInstance()
	{
		if (instance == null)
			instance = new QuestManager();
		
		return instance;
	}
	
	public void setData(Data data)
	{
		this.data = data;
	}
	
	public void populateQuests(List<Quest> quests)
	{
		incompleteQuestsByTag.clear();
		
		for (Quest quest : quests)
		{
			incompleteQuestsByTag.put(quest.getTag(), quest);
			allQuestsByTag.put(quest.getTag(), quest);
		}
	}
	
	public List<Quest> getActiveQuests()
	{
		List<Quest> activeQuests = new ArrayList<Quest>();
		
		for (String key : incompleteQuestsByTag.keySet())
		{
			Quest quest = incompleteQuestsByTag.get(key);
			
			if (quest.isActive())
				activeQuests.add(quest);
		}
		
		return activeQuests;
	}
	
	public void updateNodeStatus(String combinedTag, QuestNodeStatus newStatus)
	{
		int separatorIndex = combinedTag.indexOf('|');
		String questTag = combinedTag.substring(0, separatorIndex);
		String nodeTag = combinedTag.substring(separatorIndex + 1);
		updateNodeStatus(questTag, nodeTag, newStatus);
	}

	public void updateNodeStatus(String questTag, String nodeTag, QuestNodeStatus newStatus)
	{
		Quest quest = incompleteQuestsByTag.get(questTag);
		
		if (quest == null)
		{
			Logger.error("Cannot update node status for " + questTag + ":" + nodeTag + "; quest [" + questTag + "] not found or has been completed.");
			return;
		}
		else if (!quest.isActive())
		{
			Logger.warn("Cannot update node status for " + questTag + ":" + nodeTag + "; quest [" + questTag + "] is not active.");
			return;
		}
		
		quest.updateNodeStatus(nodeTag, newStatus);
	}

	public void activateQuest(String questTag)
	{
		Quest quest = incompleteQuestsByTag.get(questTag);		//only checking incomplete because completed ones should already be active
		quest.activate();
	}

	public void discoverQuest(String questTag)
	{
		Quest quest = allQuestsByTag.get(questTag);
		quest.setKnown(true);
	}

	public void completeQuest(String questTag)
	{
		Quest quest = allQuestsByTag.get(questTag);
		
		for (QuestNode node : quest.getActiveNodes())
		{
			node.deactivate();
		}
		
		incompleteQuestsByTag.remove(questTag);
		completedQuestsByTag.put(questTag, quest);
		
		//TODO: maybe not perfect, especially for quests that might have a lot of setup nodes (that is, nodes that complete automatically), but it should be fine enough
		PlayerAdvancementManager.getInstance().gainXP(quest.getCompletedNodes().size());

		//TODO: popup a proper congratulations window instead
		MessageBuffer.addMessage("Congratulations! You have completed " + quest.getName() + "!");
	}
	
	public boolean isQuestStarted(String questTag)
	{
		if (completedQuestsByTag.containsKey(questTag))
			return true;
		
		if (incompleteQuestsByTag.get(questTag).isActive())
			return true;
		
		return false;
	}
	
	public QuestNode getNodeForCombinedQuestNodeTag(String combinedTag)
	{
		int separatorIndex = combinedTag.indexOf('|');
		String questTag = combinedTag.substring(0, separatorIndex);
		String nodeTag = combinedTag.substring(separatorIndex + 1);
		
		Quest quest = allQuestsByTag.get(questTag);
		
		if (quest == null)
			return null;
		
		return quest.getNodes().get(nodeTag);
	}

	@Override
	public void receiveInternalEvent(InternalEvent internalEvent)
	{
		Map<QuestNodeCompositeKey, QuestNode> activeNodes = getActiveNodes();
		
		for (QuestNodeCompositeKey key : activeNodes.keySet())
		{
			QuestNode node = activeNodes.get(key);
			resolveEventForNode(node, internalEvent);
		}
	}

	private Map<QuestNodeCompositeKey, QuestNode> getActiveNodes()
	{
		Map<QuestNodeCompositeKey, QuestNode> activeNodes = new HashMap<QuestNodeCompositeKey, QuestNode>();
		
		List<Quest> activeQuests = getActiveQuests();
		
		for (Quest quest : activeQuests)
		{
			Set<QuestNode> questActiveNodes = quest.getActiveNodes();
			
			for (QuestNode node : questActiveNodes)
			{
				activeNodes.put(new QuestNodeCompositeKey(quest.getTag(), node.getTag()), node);
			}
		}
		
		return activeNodes;
	}
	
	private void resolveEventForNode(QuestNode node, InternalEvent internalEvent)
	{
		if (node.getObjective() == null)
			return;
		
		switch (node.getObjective().getType())
		{
		case PLAYER_ENTERS_ZONE:
			checkZoneEntryRequirement(node, internalEvent);
			break;
		case ACTOR_HAS_ITEM:
			checkActorItemRequirement(node);
			break;
			//$CASES-OMITTED$
		default:
			return;
		}
	}

	private void checkZoneEntryRequirement(QuestNode node, InternalEvent internalEvent)
	{
		if (internalEvent.getInternalEventType() != InternalEventType.ZONE_TRANSITION)
			return;
		
		//this is cheating a little bit, since it only works because I know this is checked after the player enters the level
		String currentZoneName = data.getCurrentZone().getName();
		String requiredZoneName = node.getObjective().getValue();
		String requiredZoneCacheName = PredefinedZoneLoader.getInstance().getCacheNameOfZone(requiredZoneName);
		
		if (requiredZoneCacheName == null)
			return;
		
		if (currentZoneName.equals(requiredZoneCacheName.substring(1)))
			node.increaseObjectiveCompletionCount(1);
	}

	private void checkActorItemRequirement(QuestNode node)
	{
		Requirement objective = node.getObjective();
		String ownedItemCount = RequirementValidator.getInstance().getValueToCheckForActorHasItem(objective.getModifier(), objective.getValue());
		node.setObjectiveCompletionCount(Integer.parseInt(ownedItemCount));
	}
	
	@Override
	protected Data getData()
	{
		return data;
	}

	@Override
	public String saveState()
	{
		String saveString = "";
		
		for (String finishedQuestTag : completedQuestsByTag.keySet())
		{
			saveString = saveString + finishedQuestTag + DELIMITER;
		}
		
		if (saveString.isEmpty())
			return NO_COMPLETED_QUESTS;
		
		return saveString.substring(0, saveString.length() - 1);
	}

	@Override
	public void loadState(String saveString)
	{
		completedQuestsByTag.clear();
		
		if (NO_COMPLETED_QUESTS.equals(saveString))
			return;
		
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(saveString).useDelimiter(DELIMITER);
		
		while (scanner.hasNext())
		{
			String completedQuestTag = scanner.next();
			Quest completedQuest = incompleteQuestsByTag.remove(completedQuestTag);
			completedQuestsByTag.put(completedQuestTag, completedQuest);
		}
		
		scanner.close();
	}
	
	public void setActiveQuests(List<Quest> activeQuests)
	{
		for (Quest quest : activeQuests)
		{
			String questTag = quest.getTag();
			replaceQuest(questTag, quest);
		}
	}
	
	//TODO: Right now, completed quests are ignored, since once they're finished, there's nothing more that can be done with them.
	//		However, if I keep a detailed completed quest log later, I may want to record which steps were chosen, etc., so this may need to change. 
	private void replaceQuest(String questTag, Quest replacementQuest)
	{
		if (completedQuestsByTag.containsKey(questTag))
		{
			Logger.warn("Cannot replace quest ["+  questTag + "], since it's already been completed.");
			return;
		}
		
		if (!allQuestsByTag.containsKey(questTag) && !incompleteQuestsByTag.containsKey(questTag))
		{
			Logger.warn("Cannot replace quest ["+  questTag + "], as it is not recognized as a loaded quest.");
			return;
		}
		
		allQuestsByTag.remove(questTag);
		incompleteQuestsByTag.remove(questTag);
		
		allQuestsByTag.put(questTag, replacementQuest);
		incompleteQuestsByTag.put(questTag, replacementQuest);
	}
}
