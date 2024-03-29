package main.logic.AI;

public enum AiType
{
	HUMAN_CONTROLLED(),
	RAND_MOVE(),
	ZOMBIE(),
	WILD(),
	EVIL(),
	SHADOW(),
	UNALIGNED(),
	COALIGNED(true),
	FROZEN_CA(true),
	PHYSICIAN(true),
	BLACKSMITH(true),
	RAT_KING(),
	REPEAT_LAST_MOVE();

	private boolean chatDefault;
	
	private AiType()
	{
		this(false);
	}
	
	private AiType(boolean chatDefault)
	{
		this.chatDefault = chatDefault;
	}

	public boolean chatDefault()
	{
		return chatDefault;
	}
}
