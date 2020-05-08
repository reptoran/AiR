package main.entity.item;

public class InventorySelectionKey
{
	private final ItemSource itemSource;
	private final int itemIndex;
	
	public InventorySelectionKey(ItemSource itemSource, int itemIndex)
	{
		this.itemSource = itemSource;
		this.itemIndex = itemIndex;
	}
	
	public static InventorySelectionKey fromKey(String key)
	{
		ItemSource source = ItemSource.fromChar(key.charAt(0));
		int index = Integer.parseInt(key.substring(1));
		return new InventorySelectionKey(source, index);
	}
	
	public ItemSource getItemSource()
	{
		return itemSource;
	}
	
	public int getItemIndex()
	{
		return itemIndex;
	}
	
	@Override
	public String toString()
	{
		return itemSource.charValue() + String.valueOf(itemIndex);
	}
}
