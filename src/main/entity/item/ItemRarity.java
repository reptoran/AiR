package main.entity.item;

public enum ItemRarity
{
	VERY_COMMON(6),
	COMMON(4),
	UNCOMMON(3),
	RARE(2);
	
	private int instances;
	
	private ItemRarity(int instances)
	{
		this.instances = instances;
	}
	
	public int getInstances()
	{
		return instances;
	}
}
