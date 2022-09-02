package main.entity.quest;

public class QuestNodeCompositeKey implements Comparable<QuestNodeCompositeKey>
{
	private String questTag;
	private String nodeTag;
	
	public QuestNodeCompositeKey(String questTag, String nodeTag)
	{
		this.questTag = questTag;
		this.nodeTag = nodeTag;
	}
	
	public String getQuestTag()
	{
		return questTag;
	}
	
	public String getNodeTag()
	{
		return nodeTag;
	}

	@Override
	public String toString()
	{
		return questTag + ":" + nodeTag;
	}

	@Override
	public int compareTo(QuestNodeCompositeKey qnck)
	{
		return toString().compareTo(qnck.toString());
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nodeTag == null) ? 0 : nodeTag.hashCode());
		result = prime * result + ((questTag == null) ? 0 : questTag.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QuestNodeCompositeKey other = (QuestNodeCompositeKey) obj;
		if (nodeTag == null)
		{
			if (other.nodeTag != null)
				return false;
		} else if (!nodeTag.equals(other.nodeTag))
			return false;
		if (questTag == null)
		{
			if (other.questTag != null)
				return false;
		} else if (!questTag.equals(other.questTag))
			return false;
		return true;
	}
}
