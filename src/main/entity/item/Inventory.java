package main.entity.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Inventory implements Collection<Item>
{
	private List<Item> items = new ArrayList<Item>();
	private ItemComparator comparator = new ItemComparator();
	
	//TODO: sort items as they're added (make Item implement comparable), so we can guarantee items will always be returned in the same order
	@Override
	public boolean add(Item itemToAdd)
	{
		for (Item item : items)
		{
			if (itemToAdd.equalsIgnoreAmount(item))
			{
				item.add(itemToAdd);
				return true;
			}
		}
		
		items.add(itemToAdd);
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
		
		Item itemToSearch = (Item)o;
		
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
		// TODO Auto-generated method stub
		return false;
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
	
	public Item get(int index)
	{
		return items.get(index);
	}
	
	public Item remove(int index)
	{
		return items.remove(index);
	}
	
	//Right now, both inventories must be IDENTICAL - same order, same amounts.  Once I implement sorting, this will really need to be the case.  
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
}