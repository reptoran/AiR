package main.entity.actor;

public enum SkillRank
{
	UNKNOWN(0),
	UNSKILLED(1),
	NOVICE(2),
	ADEPT(3),
	EXPERT(4),
	MASTER(5);
	
	private int level;
	
	private SkillRank(int level)
	{
		this.level = level;
	}
	
	public static SkillRank rankName(int level)
	{
		for (SkillRank rank : values())
		{
			if (rank.level == level)
				return rank;
		}
		
		throw new IllegalArgumentException("No skill rank exists for level [" + level + "].");
	}
	
	public int getLevel()
	{
		return level;
	}
}
