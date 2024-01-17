package main.entity.actor;

import org.apache.commons.lang3.StringUtils;

import main.entity.item.Item;
import main.entity.item.ItemFactory;
import main.entity.item.ItemType;

public class NaturalWeapon
{
	public static NaturalWeapon bump()
	{
		return new NaturalWeapon("bump", "1D1");
	}
	
	public static NaturalWeapon gnaw()
	{
		return new NaturalWeapon("gnaw", "1D2");
	}
	
	public static NaturalWeapon bite()
	{
		return new NaturalWeapon("bite", "1D3");
	}
	
	public static NaturalWeapon hit()
	{
		return new NaturalWeapon("hit", "1D4");
	}
	
	public static NaturalWeapon claw()
	{
		return new NaturalWeapon("claw", "1D4");
	}
	
	public static NaturalWeapon sting()
	{
		return new NaturalWeapon("sting", "1D5");
	}
	
	public static NaturalWeapon pummel()
	{
		return new NaturalWeapon("pummel", "1D6");
	}
	
	public static NaturalWeapon slam()
	{
		return new NaturalWeapon("slam", "1D8");
	}
	
	public static NaturalWeapon maul()
	{
		return new NaturalWeapon("maul", "2D3");
	}
	
	public static NaturalWeapon strike()
	{
		return new NaturalWeapon("strike", "2D4");
	}
	
	public static NaturalWeapon smite()
	{
		return new NaturalWeapon("smite", "3D3");
	}
	
	private String attackName;
	private String damage;

	public NaturalWeapon(String attackName, String damage)
	{
		this.attackName = attackName;
		this.damage = damage;
	}

	public NaturalWeapon(String saveString)
	{
		String[] values = saveString.split(":");
		attackName = values[0];
		damage = values[1];
	}

	public String getAttackName()
	{
		return attackName;
	}

	public String getDamage()
	{
		return damage;
	}
	
	public Item asItem()
	{
		Item virtualWeapon = ItemFactory.generateNewItem(ItemType.VIRTUAL_ITEM);
		virtualWeapon.setDamage(damage);
		virtualWeapon.setName(attackName);
		return virtualWeapon;
	}

	@Override
	public String toString()
	{
		return attackName + ":" + damage;
	}

	@Override
	public NaturalWeapon clone()
	{
		return new NaturalWeapon(attackName, damage);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attackName == null) ? 0 : attackName.hashCode());
		result = prime * result + ((damage == null) ? 0 : damage.hashCode());
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
		NaturalWeapon other = (NaturalWeapon) obj;

		return StringUtils.equals(attackName, other.attackName) && StringUtils.equals(damage, other.damage);
	}
}