package main.logic.AI;

public enum AiType
{
	HUMAN_CONTROLLED, RAND_MOVE, ZOMBIE, MELEE, COALIGNED(true), FROZEN_CA(true), PHYSICIAN(true), BLACKSMITH(true), REPEAT_LAST_MOVE;
	
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
