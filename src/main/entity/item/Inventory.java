package main.entity.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import main.entity.item.equipment.EquipmentSlotType;
import main.logic.RPGlib;

public class Inventory implements Collection<Item>
{
	private List<Item> items = new ArrayList<Item>();
	private ItemComparator comparator = new ItemComparator();
	private Map<Integer, Integer> filteredIndexes = null;

	public static final int MAX_BULK = 12;

	@Override
	public boolean add(Item itemToAdd)
	{
		List<Item> itemsToAdd = RPGlib.generateList(itemToAdd);
		int itemIndex = 0;

		for (Item item : items)
		{
			if (itemToAdd.equalsIgnoreAmount(item) && item.getAmount() < item.getMaxStackSize()) // don't add to this one if it's already a full stack
			{
				remove(itemIndex); // needed because otherwise it'll decrement from the first instance of the item (regardless of amount), and we want to take
									// out this specific one
				itemsToAdd = item.add(itemToAdd);
				break;
			}

			itemIndex++;
		}

		items.addAll(itemsToAdd);
		items.sort(comparator);
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends Item> c)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear()
	{
		items.clear();
	}

	@Override
	public boolean contains(Object o)
	{
		if (!(o instanceof Item))
			return false;

		Item itemToSearch = (Item) o;

		for (Item item : items)
		{
			if (itemToSearch.equalsIgnoreAmount(item))
				return true;
		}

		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEmpty()
	{
		return items.isEmpty();
	}

	@Override
	public Iterator<Item> iterator()
	{
		return items.iterator();
	}

	@Override
	public boolean remove(Object o)
	{
		if (!(o instanceof Item))
			throw new UnsupportedOperationException("Cannot remove an non-Item object from inventory.");

		return items.remove(o);
	}

	//TODO: perhaps make one that removes a list of items if the amount is greater than the item's stack size
	public Item remove(Item itemToRemove)
	{
		for (Item item : items)
		{
			if (item.equalsIgnoreAmount(itemToRemove))
			{
				int inventoryAmount = item.getAmount();

				if (itemToRemove.getAmount() < item.getAmount())
				{
					item.setAmount(inventoryAmount - itemToRemove.getAmount());
					return itemToRemove;
				}

				items.remove(item);
				return item;
			}
		}

		return null;
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public int size()
	{
		return items.size();
	}

	@Override
	public Object[] toArray()
	{
		return items.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a)
	{
		return items.toArray(a);
	}
	
	public int indexOf(Item item)
	{
		return items.indexOf(item);
	}

	public int getBulk()
	{
		int bulk = 0;
		for (Item item : items)
			bulk += item.getInventorySlot().getBulk();

		return bulk;
	}

	public int getMaxBulk()
	{
		return MAX_BULK;
	}

	public boolean hasSpaceForItem(Item item)
	{
		if (item == null)
			return false;
		
		return (getBulk() + item.getBulk() <= getMaxBulk());
	}

	public Item get(int index)
	{
		if (filteredIndexes == null)
			return items.get(index);

		Item item = items.get(filteredIndexes.get(index));
		resetFilters();
		return item;
	}

	public Item remove(int index)
	{
		Item itemToRemove = items.get(index);
		return remove(index, itemToRemove.getAmount());
	}

	public Item remove(int index, int amount)
	{
		Item itemToRemove = null;

		if (filteredIndexes == null)
		{
			itemToRemove = items.remove(index);
		} else
		{
			itemToRemove = items.remove(filteredIndexes.get(index).intValue());
			resetFilters();
		}
		
		if (itemToRemove == null)
			return null;
		
		if (itemToRemove.getAmount() <= amount)
			return itemToRemove;
		
		Item returnItem = itemToRemove.split(amount);
		add(itemToRemove);	//recombines items if necessary
		
		return returnItem;
	}

	public void resetFilters()
	{
		filteredIndexes = null;
	}

	public List<Item> getItemsForSlot(EquipmentSlotType equipType)
	{
		if (equipType == null)
			return items;

		List<Item> filteredItems = new ArrayList<Item>();
		filteredIndexes = new HashMap<Integer, Integer>();

		int originalIndex = 0;
		int filteredIndex = 0;

		for (Item item : items)
		{
			if (item.getInventorySlot() == equipType)
			{
				filteredItems.add(item);
				filteredIndexes.put(filteredIndex, originalIndex);
				filteredIndex++;
			}

			originalIndex++;
		}

		return filteredItems;
	}
	
	public int getTotalItemsOfType(ItemType type)
	{
		int count = 0;
		
		for (Item item : items)
		{
			if (item.getType() != type)
				continue;
			
			count = count + item.getAmount();
		}
		
		return count;
	}
	
	public Item getFirstItemOfType(ItemType type)
	{
		for (Item item : items)
		{
			if (item.getType() == type)
				return item;
		}
		
		return null;
	}

	// Right now, both inventories must be IDENTICAL - same order, same amounts. Once I implement sorting, this will really need to be the case.
	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
			return false;

		if (!(obj instanceof Inventory))
			return false;

		Inventory inv = (Inventory) obj;

		if (size() != inv.size())
			return false;

		for (int i = 0; i < size(); i++)
		{
			Item thisItem = items.get(i);
			Item otherItem = inv.get(i);

			if (!thisItem.equals(otherItem))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = 67;

		for (Item item : items)
		{
			hash = 179 * hash + item.hashCode();
		}

		return hash;
	}

	private class ItemComparator implements Comparator<Item>
	{
		@Override
		public int compare(Item item1, Item item2)
		{
			return item1.compareTo(item2);
		}
	}

	@Override
	public Inventory clone()
	{
		Inventory toRet = new Inventory();

		for (Item item : this)
		{
			toRet.add(item.clone());
		}

		return toRet;
	}
	
	@Override
	public String toString()
	{
		return items.toString();
	}
}
