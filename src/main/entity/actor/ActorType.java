package main.entity.actor;

public enum ActorType
{
	NO_TYPE(-1, -1),
	PLAYER(-1, -1),
	HUMAN(1, 1),
	ROGUE(1, 4),
	RAT(2, 4),
	RATTLESNAKE(3, 6),
	SCORPION(4, 6),
	BEAR(0, 3),
	WOLF(0, 2),
	FOX(0, 1),
	BANDIT(0, 0),
	OGRE(12, 22),
	BOSS1(15, 15);
	
	private int minDepth;
	private int maxDepth;
	
	private ActorType(int minDepth, int maxDepth)
	{
		this.minDepth = minDepth;
		this.maxDepth = maxDepth;
	}
	
	public int getMinDepth()
	{
		return minDepth;
	}
	
	public int getMaxDepth()
	{
		return maxDepth;
	}
}
