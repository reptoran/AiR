package main.entity.quest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import main.presentation.Logger;

public class Quest
{
	private String name;
	private String tag;
	private boolean isKnown;
	
	private Map<String, QuestNode> nodes;
	
	@JsonProperty("startNode")
	private QuestNode startNode;
	
	//TODO: needs a constructor and all saving methods (note that this saving is different from the JSON reading/writing to load whole quests)
	//		only need to save active quests; completed ones can come later, and not started ones don't need to be saved
	
	public Quest()
	{
		name = "UNDEFINED_QUEST";
		tag = "UNDEFINED_QUEST";
		isKnown = false;
		
		startNode = null;
		nodes = new HashMap<String, QuestNode>();
	}
	
	public void addStartNode(QuestNode node)
	{
		startNode = node;
		addNode(node);
	}
	
	public void addNode(QuestNode node)
	{
		nodes.put(node.getTag(), node);
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getTag()
	{
		return tag;
	}
	
	//this specifically means "START quest."  There's no deactivating a quest to "pause" it or anything; if it's inactive, it's either
	//never been started, or has already been completed.
	public void activate()
	{
		if (startNode == null)
		{
			Logger.warn("Quest [" + tag + "] cannot be activated; start node is NULL.");
			return;
		}
		
		startNode.activate();
	}
	
	@JsonIgnore
	public boolean isActive()
	{
		return !getActiveNodes().isEmpty();
	}
	
	public boolean isKnown()
	{
		return isKnown;
	}
	
	public Map<String, QuestNode> getNodes()
	{
		return nodes;
	}
	
	@JsonIgnore
	public Set<QuestNode> getActiveNodes()
	{
		Set<QuestNode> activeNodes = new HashSet<QuestNode>();
		
		for (String nodeTag : nodes.keySet())
		{
			QuestNode node = nodes.get(nodeTag);
			
			if (node.isActive())
				activeNodes.add(node);
		}
		
		return activeNodes;
	}
	
	public List<QuestNode> getCompletedNodes()
	{
		List<QuestNode> completedNodes = new ArrayList<QuestNode>();
		
		for (String nodeKey : nodes.keySet())
		{
			QuestNode node = nodes.get(nodeKey);
			
			if (node.isComplete())
				completedNodes.add(node);
		}
		
		return completedNodes;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setTag(String tag)
	{
		this.tag = tag;
	}
	
	public void setKnown(boolean isKnown)
	{
		this.isKnown = isKnown;
	}
	
	public void updateNodeStatus(String nodeTag, QuestNodeStatus newStatus)
	{
		Logger.debug("Updating node [" + nodeTag + "] to status [" + newStatus.name() + "].");
		
		QuestNode node = nodes.get(nodeTag);
		
		if (node == null)
		{
			Logger.error("Cannot update node status for " + tag + ":" + nodeTag + "; node [" + nodeTag + "] not found.");
			return;
		}
		
		if (newStatus == QuestNodeStatus.COMPLETE)
		{
			node.complete();
			return;
		}
		
		node.setStatus(newStatus);
	}
}
