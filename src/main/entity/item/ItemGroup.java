package main.entity.item;

import java.util.ArrayList;
import java.util.List;

public class ItemGroup
{
	private String name;
	private List<ItemType> itemsInGroup = new ArrayList<ItemType>();

	private ItemGroup(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public static ItemGroup singleItemGroup(ItemType itemType)
	{
		ItemGroup group = new ItemGroup(itemType.name());
		group.itemsInGroup.add(itemType);
		return group;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((itemsInGroup == null) ? 0 : itemsInGroup.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ItemGroup other = (ItemGroup) obj;
		if (name == null)
		{
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
		{
			return compareGroups(itemsInGroup, other.itemsInGroup);
		}
		return true;
	}

	//if either group contains a single item, then these are "equal" if the other group contains that item
	private boolean compareGroups(List<ItemType> group1, List<ItemType> group2)
	{
		if (group1.size() == 1)
			return group2.contains(group1.get(0));
		if (group2.size() == 1)
			return group1.contains(group2.get(0));
		
		if (group1.size() != group2.size())
			return false;
		
		for (ItemType itemType : group1)
		{
			if (!group2.contains(itemType))
				return false;
		}
		
		return true;
	}
}
