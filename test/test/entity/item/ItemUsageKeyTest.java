package test.entity.item;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import main.entity.item.ItemGroup;
import main.entity.item.ItemType;
import main.entity.item.ItemUsageKey;

public class ItemUsageKeyTest
{
	@Test
	public void equalityTest()
	{
		ItemUsageKey singleWeaponKey = new ItemUsageKey(ItemType.METAL_SHARD, ItemType.KNIFE);
		ItemUsageKey weaponGroupKey = new ItemUsageKey(ItemType.METAL_SHARD, ItemGroup.weapons());
		
		assertEquals(singleWeaponKey, weaponGroupKey);
		assertEquals(singleWeaponKey.hashCode(), weaponGroupKey.hashCode());
	}
}
