package main.entity.item;

public enum ItemType
{
	NO_TYPE(-1, -1),
	VIRTUAL_ITEM(-1, -1),
	THICK_SHIRT(0, 5),
	QUILTED_SHIRT(1, 5, ItemRarity.UNCOMMON),
	SOFT_LEATHER_VEST(2, 6),
	HARD_LEATHER_VEST(3, 7),
	RING_MAIL(4, 15, ItemRarity.UNCOMMON),
	LAMINAR_ARMOR(6, 18, ItemRarity.UNCOMMON),
	SCALE_MAIL(10, 20, ItemRarity.UNCOMMON),
	PLATE_MAIL(10, 20, ItemRarity.RARE),
	DAGGER(0, 10),
	CLUB(0, 5),
	MACE(4, 12, ItemRarity.UNCOMMON),
	SHORT_SWORD(3, 15, ItemRarity.UNCOMMON),
	HAMMER(2, 8),
	HEAVY_CLUB(5, 12, ItemRarity.UNCOMMON),
	BUCKLER(2, 6),
	SMALL_SHIELD(3, 8),
	MEDIUM_SHIELD(3, 10, ItemRarity.UNCOMMON),
	LARGE_SHIELD(6, 13, ItemRarity.UNCOMMON),
	TOWER_SHIELD(8, 15, ItemRarity.RARE);
	
	private int minDepth;
	private int maxDepth;
	private ItemRarity rarity;
	
	private ItemType(int minDepth, int maxDepth)
	{
		this(minDepth, maxDepth, ItemRarity.COMMON);
	}
	
	private ItemType(int minDepth, int maxDepth, ItemRarity rarity)
	{
		this.minDepth = minDepth;
		this.maxDepth = maxDepth;
		this.rarity = rarity;
	}
	
	public int getMinDepth()
	{
		return minDepth;
	}
	
	public int getMaxDepth()
	{
		return maxDepth;
	}
	
	public ItemRarity getRarity()
	{
		return rarity;
	}
}
