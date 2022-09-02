package main.entity.quest;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import main.data.chat.EventTriggerExecutor;
import main.entity.CompareOperator;
import main.entity.event.Trigger;
import main.entity.requirement.Requirement;
import main.logic.requirement.RequirementValidator;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class QuestNode implements Comparable<QuestNode>
{
	//A node has a name, a unique tag, a boolean for whether it's active, a requirement to complete it,
	//a record of how many times that requirement has been hit (say, for a node involving killing 7 orcs,
	//it would track the kill count), and a list of triggers that occur when it's complete (usually this
	//would be at least activating another node).

	private String tag;
	private String description;
	
	@JsonProperty("status")
	private QuestNodeStatus status;
	
	private Requirement objective;
	
	@JsonProperty("objectiveCompletionCount")
	private int objectiveCompletionCount;
	
	@JsonProperty("triggers")
	private List<Trigger> triggers;
	
	public QuestNode()
	{
		tag = "UNDEFINED_NODE";
		description = "UNDEFINED_NODE";
		status = QuestNodeStatus.INACTIVE;
		objective = null;
		objectiveCompletionCount = 0;
		triggers = new ArrayList<Trigger>();
	}

	public String getDescription()
	{
		return description;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getTag()
	{
		return tag;
	}
	
	public void setTag(String tag)
	{
		this.tag = tag;
	}

	@JsonIgnore
	public boolean isActive()
	{
		return status == QuestNodeStatus.ACTIVE;
	}
	
	@JsonIgnore
	public boolean isComplete()
	{
		return status == QuestNodeStatus.COMPLETE;
	}
	
	public Requirement getObjective()
	{
		return objective;
	}
	
	public void setObjective(Requirement objective)
	{
		this.objective = objective;
	}
	
	public List<Trigger> getTriggers()
	{
		return triggers;
	}
	
	public void addTrigger(Trigger trigger)
	{
		triggers.add(trigger);
	}

	public void activate()
	{
		status = QuestNodeStatus.ACTIVE;
	}
	
	public void deactivate()
	{
		status = QuestNodeStatus.INACTIVE;
	}
	
	public void complete()
	{
		for (Trigger trigger : triggers)
		{
			EventTriggerExecutor.getInstance().executeTrigger(trigger);
		}
		
		status = QuestNodeStatus.COMPLETE;
	}
	
	public void setStatus(QuestNodeStatus status)
	{
		if (status == QuestNodeStatus.COMPLETE)
			complete();
		else
			this.status = status;
	}
	
	public void increaseObjectiveCompletionCount(int amount)
	{
		objectiveCompletionCount += amount;
		checkForCompletion();
	}
	
	public void decreaseObjectiveCompletionCount(int amount)
	{
		objectiveCompletionCount -= amount;
		checkForCompletion();
	}
	
	public void setObjectiveCompletionCount(int newCount)
	{
		objectiveCompletionCount = newCount;
		checkForCompletion();
	}
	
	private void checkForCompletion()
	{
		if (objective == null)		//nodes without requirements can only be completed by other triggers
			return;
		else if (requirementMet())
			complete();
	}
	
	private boolean requirementMet()
	{
		CompareOperator operator = objective.getOperator();
		int requiredValue = objective.getComparisonInt();
		
		if (RequirementValidator.getInstance().checkIntegerRequirement(operator, requiredValue, objectiveCompletionCount))
			return true;
		
		return false;
	}

	@Override
	public QuestNode clone()
	{
		QuestNode node = new QuestNode();
		
		node.description = description;
		node.tag = tag;
		node.status = status;
		node.objective = objective.clone();
		node.objectiveCompletionCount = objectiveCompletionCount;
		
		for (Trigger trigger : triggers)
			node.triggers.add(trigger.clone());
		
		return node;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((objective == null) ? 0 : objective.hashCode());
		result = prime * result + objectiveCompletionCount;
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
		result = prime * result + ((triggers == null) ? 0 : triggers.hashCode());
		return result;
	}

	//consider doing null-safe collections checks (empty trigger list and null trigger list should still be equal, for example)
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QuestNode other = (QuestNode) obj;
		if (description == null)
		{
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (objective == null)
		{
			if (other.objective != null)
				return false;
		} else if (!objective.equals(other.objective))
			return false;
		if (objectiveCompletionCount != other.objectiveCompletionCount)
			return false;
		if (status != other.status)
			return false;
		if (tag == null)
		{
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		if (triggers == null)
		{
			if (other.triggers != null)
				return false;
		} else if (!triggers.equals(other.triggers))
			return false;
		return true;
	}

	@Override
	public int compareTo(QuestNode node)
	{
		int compare = this.getTag().compareTo(node.getTag());
		
		if (compare == 0)
			return this.getDescription().compareTo(node.getDescription());
		
		return compare;
	}
}