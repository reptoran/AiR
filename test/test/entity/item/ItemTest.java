package test.entity.item;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import main.entity.item.Item;
import main.entity.item.ItemBuilder;
import main.entity.item.ItemType;

public class ItemTest
{
	@Test
	public void armorRatingConditionTest()
	{
		Item item = ItemBuilder.generateArmor(ItemType.PLATE_MAIL).setName("plate mail").setHP(100).setAR(8).build();
		
		assertEquals(8, item.getAR());
		assertEquals(item.getAR(), item.getARAdjustedForCondition());
		System.out.println(item.getNameInPack());
		
		item.changeCurHp(-25);
		assertEquals(75, item.getCurHp());
		assertEquals(6, item.getARAdjustedForCondition());
		System.out.println(item.getNameInPack());

		item.changeCurHp(-24);
		assertEquals(51, item.getCurHp());
		assertEquals(5, item.getARAdjustedForCondition());
		System.out.println(item.getNameInPack());
		
		item.changeCurHp(-1);
		assertEquals(50, item.getCurHp());
		assertEquals(4, item.getARAdjustedForCondition());
		System.out.println(item.getNameInPack());
		
		item.changeCurHp(-49);
		assertEquals(1, item.getCurHp());
		assertEquals(1, item.getARAdjustedForCondition());
		System.out.println(item.getNameInPack());
	}
}
