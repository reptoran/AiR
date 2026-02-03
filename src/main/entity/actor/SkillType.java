package main.entity.actor;

public enum SkillType
{
	ALCHEMY, HEALING, ARMOR_USE, SMITHING, AWARENESS, STEALTH;
	
	public static SkillType fromString(String string)
	{
		return SkillType.valueOf(string.toUpperCase());
	}
}
