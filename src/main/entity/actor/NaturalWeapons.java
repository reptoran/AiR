package main.entity.actor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import main.entity.item.Item;

public class NaturalWeapons
{
	private List<NaturalWeapon> attacks;
	
	private static final String EMPTY_VALUE = "EMPTY";
	
	public NaturalWeapons()
	{
		attacks = new ArrayList<NaturalWeapon>();
	}
	
	public NaturalWeapons(String saveString)
	{
		attacks = new ArrayList<NaturalWeapon>();
		
		if (StringUtils.equals(EMPTY_VALUE, saveString))
			return;
		
		String[] values = { saveString };
				
		if (saveString.contains("|"))	//TODO: this really shouldn't be necessary, but for some reason it splits it into the individual characters when it can't find the | character
			values = saveString.split("|");
		
		for (String value : values)
		{
			attacks.add(new NaturalWeapon(value));
		}
	}
	
	public List<Item> getAttackItems()
	{
		List<Item> weapons = new ArrayList<Item>();
		
		for (NaturalWeapon attack : attacks)
		{
			weapons.add(attack.asItem());
		}
		
		return weapons;
	}
	
	public List<NaturalWeapon> getAttacks()
	{
		return attacks;
	}
	
	@Override
	public String toString()
	{
		if (attacks.isEmpty())
			return EMPTY_VALUE;
		
		String returnString = "";
		for (NaturalWeapon attack : attacks)
		{
			returnString = attack.toString() + "|";
		}
		return returnString.substring(0, returnString.length() - 1);
	}
	
	@Override
	public NaturalWeapons clone()
	{
		return new NaturalWeapons(this.toString());
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attacks == null) ? 0 : attacks.hashCode());
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
		NaturalWeapons other = (NaturalWeapons) obj;
		if (attacks == null)
		{
			if (other.attacks != null)
				return false;
		} else if (!attacks.equals(other.attacks))
			return false;
		
		return true;
	}
}
