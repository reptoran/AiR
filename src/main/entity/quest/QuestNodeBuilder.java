package main.entity.quest;

import main.entity.event.Trigger;
import main.entity.requirement.Requirement;

public class QuestNodeBuilder
{
	private QuestNode node = null;
	
	public QuestNodeBuilder()
	{
		node = new QuestNode();
	}
	
	public QuestNode build()
	{
		return node;
	}
	
	public QuestNodeBuilder setTag(String tag)
	{
		node.setTag(tag);
		return this;
	}
	
	public QuestNodeBuilder setDescription(String description)
	{
		node.setDescription(description);
		return this;
	}
	
	public QuestNodeBuilder addTrigger(Trigger trigger)
	{
		node.addTrigger(trigger);
		return this;
	}
	
	public QuestNodeBuilder setStatus(QuestNodeStatus status)
	{
		node.setStatus(status);
		return this;
	}
	
	public QuestNodeBuilder setObjective(Requirement objective)
	{
		node.setObjective(objective);
		return this;
	}
}
