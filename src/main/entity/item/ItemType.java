package main.entity.item;

public enum ItemType
{
	//progamming only
	NO_TYPE,
	ANY_ITEM,
	SOURCE_ITEM,
	TARGET_ITEM,
	VIRTUAL_ITEM,
	UPGRADED_ITEM,
	
	//debug
	DEBUG_GEM_UP,
	DEBUG_GEM_DOWN,
	
	//armor
	THICK_SHIRT(0, 3, ItemRarity.COMMON),
	QUILTED_SHIRT(1, 5, ItemRarity.UNCOMMON),
	SOFT_LEATHER_VEST(3, 8, ItemRarity.COMMON),
	HARD_LEATHER_VEST(5, 10, ItemRarity.COMMON),
	RING_MAIL(8, 15, ItemRarity.UNCOMMON),
	LAMINAR_ARMOR(12, 25, ItemRarity.UNCOMMON),
	SCALE_MAIL(18, 35, ItemRarity.UNCOMMON),
	PLATE_MAIL(25, 50, ItemRarity.RARE),
	
	//bladed weapons
	KNIFE(0, 6, ItemRarity.COMMON),
	AXE(3, 15, ItemRarity.COMMON),
	SWORD(8, 20, ItemRarity.UNCOMMON),
	
	//blunt weapons
	CLUB(0, 5, ItemRarity.COMMON),
	HAMMER(2, 12, ItemRarity.COMMON),
	HEAVY_CLUB(7, 18, ItemRarity.UNCOMMON),
	
	//shields
	BUCKLER(2, 8, ItemRarity.COMMON),
	SMALL_SHIELD(5, 12, ItemRarity.COMMON),
	MEDIUM_SHIELD(8, 18, ItemRarity.UNCOMMON),
	LARGE_SHIELD(10, 20, ItemRarity.UNCOMMON),
	TOWER_SHIELD(15, 25, ItemRarity.RARE),
	
	//materials
	MEDICINAL_FUNGUS(0, 100, ItemRarity.VERY_COMMON),
	METAL_SHARD(0, 100, ItemRarity.COMMON),
	VALUABLE(0, 100, ItemRarity.UNCOMMON),
	
	//magic items
	HEALING_SALVE(0, 100, ItemRarity.RARE);
	
	private int minDepth;
	private int maxDepth;
	private ItemRarity rarity;
	
	private ItemType()
	{
		this(-1, -1, ItemRarity.COMMON);
	}
	
//	private ItemType(int minDepth, int maxDepth)
//	{
//		this(minDepth, maxDepth, ItemRarity.COMMON);
//	}
	
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
	
	public static ItemType fromString(String string)
	{
		return ItemType.valueOf(string.toUpperCase());
	}
}
