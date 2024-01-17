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
	RAT_KING,
	BOSS1,
	FOX(1),
	RAT(1),
	BAT(1),
	WOLF(2),
	BEAR(2),
	RATTLESNAKE(2),
	ROGUE(3),
	GIANT_BAT(3),
	GIANT_RAT(3),
	WILDMAN(4),
	GIANT_BEETLE(4),
	BANDIT(5),
	LIZARD(5),
	MANIAC(6),
	GOBLIN(6),
	WORM_MASS(7),
	GOBLIN_CARVER(7),
	CAVE_CRAWLER(8),
	DESERTER(10),
	SKELETON(11),
	SHADOW_SLAVE(11),
	ZOMBIE(12),
	OGRE(13),
	SCORPION(90);
	
	private int minDepth;
	private int maxDepth;
	
	private ActorType()
	{
		this(-1);
		maxDepth = -1;
	}
	
	private ActorType(int minDepth)
	{
		this.minDepth = minDepth;
		this.maxDepth = minDepth + 5;
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
