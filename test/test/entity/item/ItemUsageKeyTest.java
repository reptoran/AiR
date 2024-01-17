package test.entity.item;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import main.entity.actor.ActorType;
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
	
	@Test
	public void knivesAreBladedWeaponsTest()
	{
		ItemGroup bladedWeapons = ItemGroup.bladedWeapons();
		ItemGroup knife = ItemGroup.singleItemGroup(ItemType.KNIFE);
		
		assertEquals(bladedWeapons, knife);
		assertEquals(bladedWeapons.hashCode(), knife.hashCode());
		
		ItemUsageKey bladesOnArcheoKey = new ItemUsageKey(bladedWeapons, ActorType.BOUND_ARCHEO);
		ItemUsageKey knifeOnArcheoKey = new ItemUsageKey(ItemType.KNIFE, ActorType.BOUND_ARCHEO);
		
		assertEquals(bladesOnArcheoKey, knifeOnArcheoKey);
		assertEquals(bladesOnArcheoKey.hashCode(), knifeOnArcheoKey.hashCode());
		
		Map<ItemUsageKey, String> map = new HashMap<ItemUsageKey, String>();
		map.put(bladesOnArcheoKey, "SUCCESS");
		
		assertEquals("SUCCESS", map.get(knifeOnArcheoKey));
	}
}
