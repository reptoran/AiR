package main.entity.actor;

public enum ActorType
{
	NO_TYPE,
	ANY_ACTOR,
	SOURCE_ACTOR,
	TARGET_ACTOR,
	PLAYER,
	PC_ELDER,
	PC_PHYSICIAN,
	PC_SMITH,
	BOUND_ARCHEO,
	ARCHAEOLOGIST,
	WEAPONSMITH,
	COMMANDER,
	CHAPLAIN,
	COOK,
	SQUAD_LEADER,
	HUMAN,
	ROGUE(2, 7),
	BAT(1, 6),
	GIANT_BAT(4, 10),
	RAT(1, 4),
	GIANT_RAT(3, 9),
	RATTLESNAKE(2, 5),
	SCORPION(3, 6),
	BEAR(3, 6),
	WOLF(0, 3),
	FOX(0, 2),
	SKELETON(6, 15),
	ZOMBIE(8, 16),
	WILDMAN(4, 8),
	BANDIT(0, 0),
	OGRE(12, 22),
	BOSS1(15, 15);
	
	private int minDepth;
	private int maxDepth;
	
	private ActorType()
	{
		this(-1, -1);
	}
	
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
	
	public static ActorType fromString(String string)
	{
		return ActorType.valueOf(string.toUpperCase());
	}
}
