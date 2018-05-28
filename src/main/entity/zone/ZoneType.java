package main.entity.zone;

public enum ZoneType
{
	NO_TYPE(""), OCEAN("OCEAN"), PLAINS("PLAINS"), FOREST("FOREST"), DESERT("DESERT"), CAVE("CAVE"), DUNGEON("DUNGEON"), TRANSIENT("TEMP"), PERMANENT("PERM"), UNMAPPED_UP("UP"), UNMAPPED_DOWN("DOWN");
	
	private String idPrefix;
	
	private ZoneType(String prefix)
	{
		idPrefix = prefix;
	}
	
	public String getIdPrefix()
	{
		return idPrefix;
	}
}
