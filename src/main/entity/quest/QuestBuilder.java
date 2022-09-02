package main.entity.quest;

public class QuestBuilder
{
	private Quest quest = null;
	
	public QuestBuilder()
	{
		quest = new Quest();
	}
	
	public Quest build()
	{
		return quest;
	}
	
	public QuestBuilder setName(String name)
	{
		quest.setName(name);
		return this;
	}
	
	public QuestBuilder setTag(String tag)
	{
		quest.setTag(tag);
		return this;
	}
	
	public QuestBuilder setKnown(boolean known)
	{
		quest.setKnown(known);
		return this;
	}
}
