package main.entity.item;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import main.entity.EntityType;
import main.entity.SaveableEntity;
import main.entity.item.equipment.EquipmentSlotType;
import main.entity.save.EntityMap;
import main.entity.save.SaveStringBuilder;
import main.entity.save.SaveToken;
import main.entity.save.SaveTokenTag;
import main.logic.RPGlib;
import main.presentation.Logger;

public class Item extends SaveableEntity implements Comparable<Item>
{
	public static final String ZERO_DAMAGE = "1d1-1";
	
	private ItemType type;
	
	private String name = "";
	private String plural = "";
	private char icon = '%';
	private int color = 15;
	
	private String damage = ZERO_DAMAGE;
	private int size = 0;		//TODO: possibly remove this, since I think this is covered by bulk now
	private int amount = 1;
	private EquipmentSlotType inventorySlot = EquipmentSlotType.ANY;
	
	private int maxHp = 1;
	private int curHp = 1;
	
	private int CR = 0;
	private int AR = 0;
	private int DR = 0;
	
	@Override
	public Item clone()
	{
		Item toRet = new Item(type);
		
		toRet.name = name;
		toRet.plural = plural;
		toRet.icon = icon;
		toRet.color = color;
		toRet.damage = damage;
		toRet.size = size;
		toRet.amount = amount;
		toRet.inventorySlot = inventorySlot;
		toRet.maxHp = maxHp;
		toRet.curHp = curHp;
		toRet.AR = AR;
		toRet.CR = CR;
		toRet.DR = DR;
		
		return toRet;
	}
	
	public Item split(int amountToRemove)
	{
		if (amountToRemove > amount)
		{
			Logger.warn("Cannot split a greater amount than the item contains.");
			return null;
		}
		
		Item toRet = clone();
		toRet.setAmount(amountToRemove);
		amount = amount - amountToRemove;
		
		return toRet;
	}
	
	private void convertToType(ItemType itemType)
	{
		if (type == itemType)
			return;
		
		Item baseItem = ItemFactory.generateNewItem(itemType);
		
		this.type = baseItem.type;
		this.name = baseItem.name;
		this.plural = baseItem.plural;
		this.icon = baseItem.icon;
		this.color = baseItem.color;
		this.damage = baseItem.damage;
		this.size = baseItem.size;
		this.amount = baseItem.amount;
		this.inventorySlot = baseItem.inventorySlot;
		this.maxHp = baseItem.maxHp;
		this.curHp = baseItem.curHp;
		this.AR = baseItem.AR;
		this.CR = baseItem.CR;
		this.DR = baseItem.DR;
	}
	
	public Item(ItemType type)
	{
		this.type = type;
	}
	
	public String getNameOnGround()
	{
		if (amount == 1)
			return "a " + getName() + getNameSuffix();
		
		return "a pile of " + amount + " " + getPlural() + getNameSuffix();
	}
	
	public String getNameInPack()
	{
		if (amount == 1)
			return "a " + getName() + getNameSuffix();
		
		return amount + " " + getPlural() + getNameSuffix();
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	private String getPlural()
	{
		return plural;
	}
	
	public void setPlural(String plural)
	{
		this.plural = plural;
	}
	
	private String getNameSuffix()
	{
		String suffix = "";
		
		if (inventorySlot == EquipmentSlotType.MATERIAL || inventorySlot == EquipmentSlotType.MAGIC)
			return suffix;
		else if (!damage.equals(ZERO_DAMAGE))
			suffix = " (" + damage + ")";
		else if (isShield())
			suffix = " (" + CR + ")";
		else if (isArmor())
			suffix = " [" + AR + "]";
		
		return suffix + " {" + (int)(getConditionModifer() * 100) + "%}";
	}
	
	public boolean isWeapon()
	{
		return !damage.equals(ZERO_DAMAGE);
	}
	
	public boolean isArmor()
	{
		return AR != 0;
	}
	
	public boolean isShield()
	{
		return (inventorySlot == EquipmentSlotType.ARMAMENT && CR > 0);
	}
	
	public char getIcon()
	{
		return icon;
	}
	
	public void setIcon(char icon)
	{
		this.icon = icon;
	}
	
	public int getColor()
	{
		return color;
	}
	
	public void setColor(int color)
	{
		this.color = color;
	}
	
	public String getDamage()
	{
		return damage;
	}
	
	public void setDamage(String damage)
	{
		this.damage = damage;
	}
	
	public int getBulk()
	{
		return inventorySlot.getBulk();
	}
	
	public int getMaxStackSize()
	{
		switch (inventorySlot)
		{
			case MATERIAL:
				return 5;
		}
		
		return 1;
	}
	
	public int getSize()
	{
		return size;
	}
	
	public void setSize(int size)
	{
		this.size = size;
	}
	
	public int getAmount()
	{
		return amount;
	}
	
	public void setAmount(int amount)
	{
		this.amount = amount;
	}
	
	public List<Item> add(Item item)
	{
		if (!item.equalsIgnoreAmount(this))
			return RPGlib.generateList(this, item);
		
		return add(item.amount);
	}
	
	public List<Item> add(int amountToAdd)
	{
		List<Item> itemStacks = new ArrayList<Item>();
		
		amount += amountToAdd;
		
		while (amount > getMaxStackSize())
		{
			Item cloneItem = clone();
			cloneItem.setAmount(getMaxStackSize());
			itemStacks.add(cloneItem);
			amount -= getMaxStackSize();
		}
		
		itemStacks.add(this);
		return itemStacks;
	}
	
	public EquipmentSlotType getInventorySlot()
	{
		return inventorySlot;
	}
	
	public void setInventorySlot(EquipmentSlotType inventorySlot)
	{
		this.inventorySlot = inventorySlot;
	}
	
	public int getMaxHp()
	{
		return maxHp;
	}
	
	public void setMaxHp(int maxHp)
	{
		this.maxHp = maxHp;
	}
	
	public int getCurHp()
	{
		return curHp;
	}
	
	public double getConditionModifer()
	{
		return RPGlib.truncateDouble(((double)curHp) / maxHp, 2);
	}
	
	public String getConditionString()
	{
		double conditionModifier = getConditionModifer();
		
		if (conditionModifier > .75)
			return "Great";
		
		if (conditionModifier > .5)
			return "Good";
		
		if (conditionModifier > .25)
			return "Fair";
		
		return "Poor";
	}
	
	public void setCurHp(int curHp)
	{
		this.curHp = curHp;
	}
	
	public void changeCurHp(int changeAmount)
	{
		this.curHp += changeAmount;
	}

	public int getCR()
	{
		return CR;
	}

	public void setCR(int cR)
	{
		CR = cR;
	}

	public int getAR()
	{
		return AR;
	}

	public void setAR(int aR)
	{
		AR = aR;
	}

	public int getDR()
	{
		return DR;
	}

	public void setDR(int dR)
	{
		DR = dR;
	}

	public ItemType getType()
	{
		return type;
	}

	@Override
	public String saveAsText()
	{
		Item baseItem = ItemFactory.generateNewItem(type);
		SaveStringBuilder ssb = new SaveStringBuilder(EntityType.ITEM);
		
		String itemUid = getUniqueId();
		
		if (EntityMap.getItem(itemUid) == null)
			itemUid = EntityMap.put(itemUid, this);
		else
			itemUid = EntityMap.getSimpleKey(itemUid);
		
		//will be saved with every item
		ssb.addToken(new SaveToken(SaveTokenTag.I_UID, itemUid));
		ssb.addToken(new SaveToken(SaveTokenTag.I_TYP, type.toString()));
		
		//will be saved only if they differ from the default item of this type
		if (!name.equals(baseItem.name)) ssb.addToken(new SaveToken(SaveTokenTag.I_NAM, name));
		if (!plural.equals(baseItem.plural)) ssb.addToken(new SaveToken(SaveTokenTag.I_PLR, plural));
		if (icon != baseItem.icon) ssb.addToken(new SaveToken(SaveTokenTag.I_ICO, String.valueOf(icon)));
		if (color != baseItem.color) ssb.addToken(new SaveToken(SaveTokenTag.I_CLR, String.valueOf(color)));
		if (!damage.equals(baseItem.damage)) ssb.addToken(new SaveToken(SaveTokenTag.I_DAM, damage));
		if (size != baseItem.size) ssb.addToken(new SaveToken(SaveTokenTag.I_SIZ, String.valueOf(size)));
		if (amount != baseItem.amount) ssb.addToken(new SaveToken(SaveTokenTag.I_AMT, String.valueOf(amount)));
		if (inventorySlot != baseItem.inventorySlot) ssb.addToken(new SaveToken(SaveTokenTag.I_INV, type.toString()));
		if (maxHp != baseItem.maxHp) ssb.addToken(new SaveToken(SaveTokenTag.I_MHP, String.valueOf(maxHp)));
		if (curHp != baseItem.curHp) ssb.addToken(new SaveToken(SaveTokenTag.I_CHP, String.valueOf(curHp)));
		if (CR != baseItem.CR) ssb.addToken(new SaveToken(SaveTokenTag.I_CR_, String.valueOf(CR)));
		if (AR != baseItem.AR) ssb.addToken(new SaveToken(SaveTokenTag.I_AR_, String.valueOf(AR)));
		if (DR != baseItem.DR) ssb.addToken(new SaveToken(SaveTokenTag.I_DR_, String.valueOf(DR)));
				
		return ssb.getSaveString();
	}

	@Override
	public String loadFromText(String text) throws ParseException
	{
		SaveStringBuilder ssb = new SaveStringBuilder(EntityType.ITEM, text);
		
		String toRet = getContentsForTag(ssb, SaveTokenTag.I_UID);	//assumed to be defined
		
		setMember(ssb, SaveTokenTag.I_TYP);
		setMember(ssb, SaveTokenTag.I_NAM);
		setMember(ssb, SaveTokenTag.I_PLR);
		setMember(ssb, SaveTokenTag.I_ICO);
		setMember(ssb, SaveTokenTag.I_CLR);
		setMember(ssb, SaveTokenTag.I_DAM);
		setMember(ssb, SaveTokenTag.I_SIZ);
		setMember(ssb, SaveTokenTag.I_AMT);
		setMember(ssb, SaveTokenTag.I_INV);
		setMember(ssb, SaveTokenTag.I_MHP);
		setMember(ssb, SaveTokenTag.I_CHP);
		setMember(ssb, SaveTokenTag.I_CR_);
		setMember(ssb, SaveTokenTag.I_AR_);
		setMember(ssb, SaveTokenTag.I_DR_);
		
		return toRet;
	}

	@Override
	public String getUniqueId()
	{
		return EntityType.ITEM.toString() + String.valueOf(Math.abs(hashCode()));
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	protected void setMember(SaveStringBuilder ssb, SaveTokenTag saveTokenTag)
	{
		String contents = getContentsForTag(ssb, saveTokenTag);
		SaveToken saveToken = null;
		
		if (contents.equals("")) return;
		
		switch (saveTokenTag)
		{
			case I_TYP:
				ItemType itemType = ItemType.valueOf(contents); 
				if (!(itemType.equals(this.type)))
					convertToType(itemType);
				break;
				
			case I_NAM:
				saveToken = ssb.getToken(saveTokenTag);
				this.name = saveToken.getContents();
				break;
				
			case I_PLR:
				saveToken = ssb.getToken(saveTokenTag);
				this.plural = saveToken.getContents();
				break;
				
			case I_ICO:
				saveToken = ssb.getToken(saveTokenTag);
				this.icon = saveToken.getContents().charAt(0);
				break;
			
			case I_CLR:
				saveToken = ssb.getToken(saveTokenTag);
				this.color = Integer.parseInt(saveToken.getContents());
				break;
				
			case I_DAM:
				saveToken = ssb.getToken(saveTokenTag);
				this.damage = saveToken.getContents();
				break;
				
			case I_SIZ:
				saveToken = ssb.getToken(saveTokenTag);
				this.size = Integer.parseInt(saveToken.getContents());
				break;
				
			case I_AMT:
				saveToken = ssb.getToken(saveTokenTag);
				this.amount = Integer.parseInt(saveToken.getContents());
				break;
				
			case I_INV:
				EquipmentSlotType eqType = EquipmentSlotType.valueOf(contents); 
				this.inventorySlot = eqType;
				break;
				
			case I_MHP:
				saveToken = ssb.getToken(saveTokenTag);
				this.maxHp = Integer.parseInt(saveToken.getContents());
				break;
				
			case I_CHP:
				saveToken = ssb.getToken(saveTokenTag);
				this.curHp = Integer.parseInt(saveToken.getContents());
				break;
				
			case I_CR_:
				saveToken = ssb.getToken(saveTokenTag);
				this.CR = Integer.parseInt(saveToken.getContents());
				break;
				
			case I_AR_:
				saveToken = ssb.getToken(saveTokenTag);
				this.AR = Integer.parseInt(saveToken.getContents());
				break;
				
			case I_DR_:
				saveToken = ssb.getToken(saveTokenTag);
				this.DR = Integer.parseInt(saveToken.getContents());
				break;
				
			default:
				throw new IllegalArgumentException("Item - Unhandled token: " + saveTokenTag.toString());
		}
	}

	@Override
	public int hashCode()
	{
		final int prime = 37;
		int result = 1;
		result = prime * result + AR;
		result = prime * result + CR;
		result = prime * result + DR;
		result = prime * result + color;
		result = prime * result + curHp;
		result = prime * result + ((damage == null) ? 0 : damage.hashCode());
		result = prime * result + icon;
		result = prime * result + ((inventorySlot == null) ? 0 : inventorySlot.hashCode());
		result = prime * result + maxHp;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + size;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Item other = (Item) obj;
		if (AR != other.AR)
			return false;
		if (CR != other.CR)
			return false;
		if (DR != other.DR)
			return false;
		if (color != other.color)
			return false;
		if (curHp != other.curHp)
			return false;
		if (damage == null)
		{
			if (other.damage != null)
				return false;
		} else if (!damage.equals(other.damage))
			return false;
		if (icon != other.icon)
			return false;
		if (inventorySlot != other.inventorySlot)
			return false;
		if (maxHp != other.maxHp)
			return false;
		if (name == null)
		{
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (size != other.size)
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
	public boolean equalsIgnoreAmount(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		
		int originalAmount = amount;
		amount = other.amount;
		
		boolean doesEqual = false;
		
		if (this.equals(other))
			doesEqual = true;
		
		amount = originalAmount;
		return doesEqual;
	}

	@Override
	public int compareTo(Item other)
	{
		//bigger stacks of same items should come first
		if (equalsIgnoreAmount(other))
		{
			if (amount > other.amount)
				return -1;
			if (amount < other.amount)
				return 1;
			
			return 0;
		}
		
		if (inventorySlot != other.inventorySlot)
		{
			return inventorySlot.compareTo(other.inventorySlot);	//compares based on the order the enumeration elements are declared
		}
		
		return name.compareTo(other.name);
	}
}
