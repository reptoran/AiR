package main.entity.actor;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import main.entity.EntityType;
import main.entity.SaveableEntity;
import main.entity.item.Inventory;
import main.entity.item.Item;
import main.entity.item.ItemFactory;
import main.entity.item.ItemType;
import main.entity.item.equipment.Equipment;
import main.entity.item.equipment.EquipmentFactory;
import main.entity.item.equipment.EquipmentSlot;
import main.entity.item.equipment.EquipmentType;
import main.entity.save.EntityMap;
import main.entity.save.SaveStringBuilder;
import main.entity.save.SaveToken;
import main.entity.save.SaveTokenTag;
import main.logic.AI.AiType;

public class Actor extends SaveableEntity
{
	public static final int ATT_STR = 0;
	public static final int ATT_TOG = 1;
	public static final int ATT_MAG = 2;
	public static final int ATT_RES = 3;
	public static final int ATT_DEX = 4;
	public static final int TOTAL_ATTRIBUTES = 5;
	private static final int MOVEMENT_COST = 10; // not enough variation in speed to bother making this bigger right now

	private ActorType type;

	private char icon;
	private int color;
	private String name;
	private GenderType gender;
	private boolean unique;

	private int maxHp;
	private int curHp;
	private AiType AI;

	private int attributes[] = new int[TOTAL_ATTRIBUTES];
	
	private Set<ActorTraitType> traits = new HashSet<ActorTraitType>();
	private Inventory inventory = new Inventory();
	private EquipmentType equipmentType = EquipmentType.NONE;
	private Equipment equipment;

	private static int currentHash = 0;
	private int hashModifier;
	
	private String defaultDamage = "1D1";
	private int defaultArmor = 0;

	public Actor()
	{
		icon = '?';
		color = 14;
		name = "Default Actor";
		gender = GenderType.NONE;
		unique = false;

		type = ActorType.NO_TYPE;

		for (int i = 0; i < TOTAL_ATTRIBUTES; i++)
		{
			attributes[i] = 20;
		}

		maxHp = attributes[ATT_TOG];
		curHp = maxHp;

		AI = AiType.MELEE;

		hashModifier = currentHash;
		currentHash++;
		
		setEquipment(equipmentType);		//sets type and generates equipment from factory; likely called from clone, convertToType, and the save methods
	}

	public Actor(ActorType actorType, String name, char icon, int color, int[] attributes)
	{
		this(actorType, name, icon, color, attributes, AiType.MELEE);
	}

	public Actor(ActorType actorType, String name, char icon, int color, int[] attributes, AiType AI)
	{
		this();

		for (int i = 0; i < TOTAL_ATTRIBUTES; i++)
		{
			this.attributes[i] = attributes[i];
		}

		this.name = name;
		this.icon = icon;
		this.color = color;
		this.type = actorType;

		this.maxHp = attributes[ATT_TOG];
		this.curHp = maxHp;

		this.AI = AI;
	}

	@Override
	public Actor clone()
	{
		Actor toRet = new Actor(type, name, icon, color, attributes, AI);

		toRet.gender = gender;
		toRet.maxHp = maxHp;
		toRet.curHp = curHp;
		toRet.hashModifier = hashModifier; // if we're truly cloning this, then they need the same unique identifier as well.
		toRet.equipmentType = equipmentType;
		toRet.inventory = inventory.clone();
		toRet.equipment = equipment.clone();
		toRet.defaultDamage = defaultDamage;
		toRet.defaultArmor = defaultArmor;
		
		for (ActorTraitType trait : traits)
		{
			toRet.traits.add(trait);
		}

		return toRet;
	}

	private void convertToType(ActorType actorType)
	{
		if (type == actorType)
			return;

		Actor baseActor = ActorFactory.generateNewActor(actorType);

		this.type = baseActor.type;
		this.name = baseActor.name;
		this.gender = baseActor.gender;
		this.icon = baseActor.icon;
		this.color = baseActor.color;

		this.maxHp = baseActor.maxHp;
		this.curHp = baseActor.curHp;
		this.AI = baseActor.AI;

		for (int i = 0; i < TOTAL_ATTRIBUTES; i++)
		{
			this.attributes[i] = baseActor.attributes[i];
		}
		
		this.inventory = new Inventory();
		this.setEquipment(baseActor.equipmentType);
		
		for (ActorTraitType trait : baseActor.traits)
		{
			this.traits.add(trait);
		}
		
		this.defaultDamage = baseActor.defaultDamage;
		this.defaultArmor = baseActor.defaultArmor;
	}

	public void damage(int damageAmount)
	{
		curHp -= damageAmount;

		if (curHp > maxHp)
			curHp = maxHp;
	}

	public void setIcon(char icon)
	{
		this.icon = icon;
	}

	public char getIcon()
	{
		return icon;
	}

	public void setColor(int color)
	{
		this.color = color;
	}

	public int getColor()
	{
		return color;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setGender(GenderType gender)
	{
		this.gender = gender;
	}

	public GenderType getGender()
	{
		return gender;
	}

	public void setUnique(boolean unique)
	{
		this.unique = unique;
	}

	public boolean isUnique()
	{
		return unique;
	}

	public ActorType getType()
	{
		return type;
	}

	public void setAI(AiType newAI)
	{
		AI = newAI;
	}

	public AiType getAI()
	{
		return AI;
	}
	
	public Inventory getInventory()
	{
		return inventory;
	}
	
	public void receiveItem(Item i)
	{
		inventory.add(i);
	}
	
	public Item removeItem(int itemIndex)
	{
		return inventory.remove(itemIndex);
	}

	public void setAttribute(int index, int value)
	{
		attributes[index] = value;
	}

	public int getAttributeBaseValue(int index)
	{
		return attributes[index];
	}
	
	public boolean addTrait(ActorTraitType trait)
	{
		return traits.add(trait);
	}
	
	public boolean hasTrait(ActorTraitType trait)
	{
		return traits.contains(trait);
	}

	public int getMovementCost()
	{
		return MOVEMENT_COST;
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

	public void setCurHp(int curHp)
	{
		this.curHp = curHp;
	}
	
	//protected because the equipment type of an actor should never change (unless they polymorph, but that's a problem for the very, very distant future)
	protected void setEquipment(EquipmentType equipmentType)
	{
		this.equipmentType = equipmentType;
		this.equipment = EquipmentFactory.generateEquipment(equipmentType);
	}
	
	public EquipmentType getEquipmentType()
	{
		return equipmentType;
	}
	
	public Equipment getEquipment()
	{
		return equipment;
	}
	
	public void equipItem(Item item, int equipmentSlotIndex)
	{
		equipment.equipItem(item, equipmentSlotIndex);
	}
	
	public Item unequipItem(int equipmentSlotIndex)
	{
		return equipment.removeItem(equipmentSlotIndex);
	}

	public void setDefaultDamage(String damage)
	{
		this.defaultDamage = damage;
	}

	public void setDefaultArmor(int armor)
	{
		this.defaultArmor = armor;
	}
	
	public List<Item> getWeapons()
	{
		List<Item> weapons = equipment.getWeapons();
		
		if (weapons.isEmpty())
		{
			Item virtualWeapon = ItemFactory.generateNewItem(ItemType.VIRTUAL_ITEM);
			virtualWeapon.setDamage(defaultDamage);
			weapons.add(virtualWeapon);
		}
		
		return weapons;
	}
	
	public List<Item> getArmor()
	{
		List<Item> armor = equipment.getArmor();
		
		if (armor.isEmpty())
		{
			Item virtualArmor = ItemFactory.generateNewItem(ItemType.VIRTUAL_ITEM);
			virtualArmor.setAR(defaultArmor);
			armor.add(virtualArmor);
		}
		
		return armor;
	}
	
	public List<Item> getShields()
	{
		return equipment.getShields();
	}

	public int getIndexOfEquippedItem(Item item)
	{
		return equipment.getIndexOfItem(item);
	}

	@Override
	public String saveAsText()
	{
		Actor baseActor = ActorFactory.generateNewActor(type);
		SaveStringBuilder ssb = new SaveStringBuilder(EntityType.ACTOR);

		String actorUid = getUniqueId();

		if (EntityMap.getActor(actorUid) == null)
			actorUid = EntityMap.put(actorUid, this);
		else
			actorUid = EntityMap.getSimpleKey(actorUid);

		// will be saved with every actor
		ssb.addToken(new SaveToken(SaveTokenTag.A_UID, actorUid));
		ssb.addToken(new SaveToken(SaveTokenTag.A_TYP, type.toString()));

		// will be saved only if they differ from the default actor of this type
		if (!name.equals(baseActor.name))
			ssb.addToken(new SaveToken(SaveTokenTag.A_NAM, name));
		if (gender != baseActor.gender)
			ssb.addToken(new SaveToken(SaveTokenTag.A_GEN, gender.toString()));
		if (unique != baseActor.unique)
			ssb.addToken(new SaveToken(SaveTokenTag.A_UNQ, String.valueOf(unique)));
		if (icon != baseActor.icon)
			ssb.addToken(new SaveToken(SaveTokenTag.A_ICO, String.valueOf(icon)));
		if (color != baseActor.color)
			ssb.addToken(new SaveToken(SaveTokenTag.A_CLR, String.valueOf(color)));
		if (maxHp != baseActor.maxHp)
			ssb.addToken(new SaveToken(SaveTokenTag.A_MHP, String.valueOf(maxHp)));
		if (curHp != baseActor.curHp)
			ssb.addToken(new SaveToken(SaveTokenTag.A_CHP, String.valueOf(curHp)));
		if (AI != baseActor.AI)
			ssb.addToken(new SaveToken(SaveTokenTag.A_AI_, String.valueOf(AI)));
		if (!defaultDamage.equals(baseActor.defaultDamage))
			ssb.addToken(new SaveToken(SaveTokenTag.A_DAM, defaultDamage));
		if (defaultArmor != baseActor.defaultArmor)
			ssb.addToken(new SaveToken(SaveTokenTag.A_DAR, String.valueOf(defaultArmor)));

		saveAttributes(baseActor, ssb);
		
		if (inventory != null && !inventory.isEmpty())
			ssb.addToken(new SaveToken(SaveTokenTag.A_INV, convertInventoryToList()));
		
		if (!equipment.isEmpty())
			ssb.addToken(new SaveToken(SaveTokenTag.A_EQP, convertEquipmentToList()));
		
		if (!traits.isEmpty())
			ssb.addToken(new SaveToken(SaveTokenTag.A_TRT, convertTraitsToList()));

		return ssb.getSaveString();
	}

	private void saveAttributes(Actor baseActor, SaveStringBuilder ssb)
	{
		boolean changedAttributes = false;

		for (int i = 0; i < TOTAL_ATTRIBUTES; i++)
		{
			if (attributes[i] != baseActor.attributes[i])
				changedAttributes = true;
		}

		if (changedAttributes)
			ssb.addToken(new SaveToken(SaveTokenTag.A_ATT, convertAttributesToList()));
	}

	@Override
	public String loadFromText(String text) throws ParseException
	{
		SaveStringBuilder ssb = new SaveStringBuilder(EntityType.ACTOR, text);

		String toRet = getContentsForTag(ssb, SaveTokenTag.A_UID); // assumed to be defined

		setMember(ssb, SaveTokenTag.A_TYP);
		setMember(ssb, SaveTokenTag.A_NAM);
		setMember(ssb, SaveTokenTag.A_GEN);
		setMember(ssb, SaveTokenTag.A_UNQ);
		setMember(ssb, SaveTokenTag.A_ICO);
		setMember(ssb, SaveTokenTag.A_CLR);
		setMember(ssb, SaveTokenTag.A_MHP);
		setMember(ssb, SaveTokenTag.A_CHP);
		setMember(ssb, SaveTokenTag.A_AI_);
		setMember(ssb, SaveTokenTag.A_ATT);
		setMember(ssb, SaveTokenTag.A_INV);
		setMember(ssb, SaveTokenTag.A_EQP);
		setMember(ssb, SaveTokenTag.A_TRT);
		setMember(ssb, SaveTokenTag.A_DAM);
		setMember(ssb, SaveTokenTag.A_DAR);

		return toRet;
	}

	private List<String> convertAttributesToList()
	{
		List<String> toRet = new ArrayList<String>();

		for (int i = 0; i < TOTAL_ATTRIBUTES; i++)
		{
			toRet.add(String.valueOf(attributes[i]));
		}

		return toRet;
	}
	
	private List<String> convertInventoryToList()
	{
		List<String> toReturn = new ArrayList<String>();

		for (Item item : inventory)
			toReturn.add(getItemUid(item));

		return toReturn;
	}
	
	private List<String> convertTraitsToList()
	{
		List<String> toReturn = new ArrayList<String>();

		for (ActorTraitType trait : traits)
			toReturn.add(trait.toString());

		return toReturn;
	}

	private List<String> convertEquipmentToList()
	{
		//because an actor's equipment type can never change (for now), we don't need to take it into account when saving equipped items
		List<String> toReturn = new ArrayList<String>();
		List<EquipmentSlot> slots = equipment.getEquipmentSlots();
		
		for (EquipmentSlot slot : slots)
		{
			Item item = slot.getItem();
			if (item == null)
			{
				toReturn.add("");
				continue;
			}
			
			toReturn.add(getItemUid(item));
		}
		
		return toReturn;
	}
	
	private String getItemUid(Item item)
	{
		String itemUid = item.getUniqueId();
		
		if (EntityMap.getItem(itemUid) == null)
			itemUid = EntityMap.put(itemUid, item);
		else
			itemUid = EntityMap.getSimpleKey(itemUid);
			
		return itemUid.substring(1);
	}
	

	@SuppressWarnings("incomplete-switch")
	@Override
	protected void setMember(SaveStringBuilder ssb, SaveTokenTag saveTokenTag)
	{
		String contents = getContentsForTag(ssb, saveTokenTag);
		SaveToken saveToken = null;
		List<String> strVals = null;
		String referenceKey = "";

		if (contents.equals(""))
			return;

		switch (saveTokenTag)
		{
		// TODO: unit test this
		case A_TYP:
			ActorType actorType = ActorType.valueOf(contents);
			if (!(actorType.equals(this.type)))
				convertToType(actorType);
			break;

		case A_NAM:
			saveToken = ssb.getToken(saveTokenTag);
			this.name = saveToken.getContents();
			break;

		case A_GEN:
			GenderType genderType = GenderType.valueOf(contents);
			this.gender = genderType;
			break;

		case A_UNQ:
			saveToken = ssb.getToken(saveTokenTag);
			this.unique = Boolean.parseBoolean(saveToken.getContents());
			break;

		case A_ICO:
			saveToken = ssb.getToken(saveTokenTag);
			this.icon = saveToken.getContents().charAt(0);
			break;

		case A_CLR:
			saveToken = ssb.getToken(saveTokenTag);
			this.color = Integer.parseInt(saveToken.getContents());
			break;

		case A_MHP:
			saveToken = ssb.getToken(saveTokenTag);
			this.maxHp = Integer.parseInt(saveToken.getContents());
			break;

		case A_CHP:
			saveToken = ssb.getToken(saveTokenTag);
			this.curHp = Integer.parseInt(saveToken.getContents());
			break;

		case A_AI_:
			saveToken = ssb.getToken(saveTokenTag);
			this.AI = AiType.valueOf(saveToken.getContents());
			break;

		case A_ATT:
			saveToken = ssb.getToken(saveTokenTag);
			strVals = saveToken.getContentSet();
			for (int i = 0; i < TOTAL_ATTRIBUTES; i++)
			{
				attributes[i] = Integer.parseInt(strVals.get(i));
			}
			break;

		case A_INV:
			saveToken = ssb.getToken(saveTokenTag);
			strVals = saveToken.getContentSet();
			
			inventory = new Inventory();
			
			for (String value : strVals)
			{
				referenceKey = "I" + value;
				inventory.add(EntityMap.getItem(referenceKey).clone());
			}
			break;

		case A_EQP:
			saveToken = ssb.getToken(saveTokenTag);
			strVals = saveToken.getContentSet();
			
			//an empty equipment object is set when the actor is created, so we don't need to instantiate one here
			
			for (int i = 0; i < strVals.size(); i++)
			{
				String value = strVals.get(i);
				
				if (value.isEmpty())
					continue;
				
				referenceKey = "I" + value;
				
				equipment.equipItem(EntityMap.getItem(referenceKey).clone(), i);
			}
			break;
			
		case A_TRT:
			saveToken = ssb.getToken(saveTokenTag);
			strVals = saveToken.getContentSet();
			
			traits = new HashSet<ActorTraitType>();
			
			for (String value : strVals)
			{
				traits.add(ActorTraitType.valueOf(value));
			}
			break;
			
		case A_DAM:
			saveToken = ssb.getToken(saveTokenTag);
			this.defaultDamage = saveToken.getContents();
			break;

		case A_DAR:
			saveToken = ssb.getToken(saveTokenTag);
			this.defaultArmor = Integer.parseInt(saveToken.getContents());
			break;

		default:
			throw new IllegalArgumentException("Actor - Unhandled token: " + saveTokenTag.toString());
		}

		return;
	}

	@Override
	public boolean equals(Object obj)
	{
		Actor actor;

		if (obj instanceof Actor)
			actor = (Actor) obj;
		else
			return false;

		if (!type.equals(actor.type) || icon != actor.icon || color != actor.color || !name.equals(actor.name)
				|| !gender.equals(actor.gender) || unique != actor.unique || maxHp != actor.maxHp || curHp != actor.curHp
				|| AI != actor.AI || hashModifier != actor.hashModifier
				|| defaultDamage != actor.defaultDamage || defaultArmor != actor.defaultArmor)
			return false;

		for (int i = 0; i < TOTAL_ATTRIBUTES; i++)
		{
			if (attributes[i] != actor.attributes[i])
				return false;
		}
		
		if (!inventory.equals(actor.inventory))
			return false;
		
		if (!equipment.equals(actor.equipment))
			return false;
		
		if (!traits.equals(actor.traits))
			return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = 7;

		hash = 31 * hash + type.toString().hashCode();
		hash = 31 * hash + (int) icon;
		hash = 31 * hash + color;
		hash = 31 * hash + name.hashCode();
		hash = 31 * hash + gender.toString().hashCode();
		hash = 31 * hash + maxHp;
		hash = 31 * hash + curHp;
		hash = 31 * hash + AI.hashCode();

		for (int i = 0; i < TOTAL_ATTRIBUTES; i++)
		{
			hash = 31 * hash + attributes[i];
		}
		
		hash = 31 * hash + inventory.hashCode();
		hash = 31 * hash + equipment.hashCode();
		hash = 31 * hash + traits.hashCode();
		hash = 31 * hash + defaultDamage.hashCode();
		hash = 31 * hash + defaultArmor;
		
		hash = 31 * hash + hashModifier;

		return hash;
	}

	@Override
	public String getUniqueId()
	{
		return EntityType.ACTOR.toString() + String.valueOf(Math.abs(hashCode()));
	}
}
