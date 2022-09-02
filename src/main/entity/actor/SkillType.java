package main.entity.actor;

public enum SkillType
{
	HERBALISM;
	
	public static SkillType fromString(String string)
	{
		return SkillType.valueOf(string.toUpperCase());
	}
}
