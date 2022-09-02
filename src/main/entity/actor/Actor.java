package main.entity.actor;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.entity.EntityType;
import main.entity.SaveableEntity;
import main.entity.item.Inventory;
import main.entity.item.InventorySelectionKey;
import main.entity.item.Item;
import main.entity.item.ItemFactory;
import main.entity.item.ItemSource;
import main.entity.item.ItemType;
import main.entity.item.equipment.Equipment;
import main.entity.item.equipment.EquipmentFactory;
import main.entity.item.equipment.EquipmentSlot;
import main.entity.item.equipment.EquipmentSlotType;
import main.entity.item.equipment.EquipmentType;
import main.entity.item.equipment.MagicEquipmentImpl;
import main.entity.item.equipment.ReadyEquipmentImpl;
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
	
	private static final int DEFAULT_READY_SLOTS = 4;
	private static final int DEFAULT_MAGIC_SLOTS = 3;

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
	private Map<SkillType, Integer> skills;
	
	private Set<ActorTraitType> traits = new HashSet<ActorTraitType>();
	private Inventory storedItems = new Inventory();
	private Inventory materials = new Inventory();
	private EquipmentType equipmentType = EquipmentType.NONE;
	private Equipment equipment;
	private Equipment readiedItems = new ReadyEquipmentImpl(DEFAULT_READY_SLOTS);  		//TODO: or perhaps make it monster by monster, like the main equipment
	private Equipment magicItems = new MagicEquipmentImpl(DEFAULT_MAGIC_SLOTS);			//TODO: as above, though we'll see how monsters use readied items

	private static int currentHash = 0;
	private int hashModifier;
	
	private String defaultDamage = "1D1";
	private int defaultArmor = 0;
	private String defaultTalkResponse = "There's no response.";

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

		skills = new HashMap<SkillType, Integer>();
		
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
		toRet.storedItems = storedItems.clone();
		toRet.materials = materials.clone();
		toRet.equipment = equipment.clone();
		toRet.readiedItems = readiedItems.clone();
		toRet.magicItems = magicItems.clone();
		toRet.defaultDamage = defaultDamage;
		toRet.defaultArmor = defaultArmor;
		toRet.defaultTalkResponse = defaultTalkResponse;
		
		for (ActorTraitType trait : traits)
		{
			toRet.traits.add(trait);
		}
		
		for (SkillType skill : skills.keySet())
		{
			toRet.skills.put(skill, skills.get(skill));
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
		
		this.storedItems = new Inventory();
		this.materials = new Inventory();
		this.setEquipment(baseActor.equipmentType);
		this.magicItems = new MagicEquipmentImpl(DEFAULT_MAGIC_SLOTS);
		this.readiedItems = new ReadyEquipmentImpl(DEFAULT_READY_SLOTS);
		
		for (ActorTraitType trait : baseActor.traits)
		{
			this.traits.add(trait);
		}
		
		for (SkillType skill : baseActor.skills.keySet())
		{
			this.skills.put(skill, baseActor.skills.get(skill));
		}
		
		this.defaultDamage = baseActor.defaultDamage;
		this.defaultArmor = baseActor.defaultArmor;
		this.defaultTalkResponse = baseActor.defaultTalkResponse;
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
	
	public boolean isPlayer()
	{
		return gender == GenderType.PLAYER;
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
	
	public boolean hasSpaceForItem(Item item)
	{
		if (item.getInventorySlot().equals(EquipmentSlotType.MATERIAL))
			return materials.hasSpaceForItem(item);
		else if (item.getInventorySlot().equals(EquipmentSlotType.MAGIC))
			return (magicItems.hasEmptySlotAvailable(EquipmentSlotType.MAGIC) || storedItems.hasSpaceForItem(item));
		else if (item.getInventorySlot().equals(EquipmentSlotType.ARMAMENT) && readiedItems.hasEmptySlotAvailable(EquipmentSlotType.ARMAMENT))
			return true;
		else
			return storedItems.hasSpaceForItem(item);
	}
	
	public Inventory getStoredItems()
	{
		return storedItems;
	}
	
	public Inventory getMaterials()
	{
		return materials;
	}
	
	public Equipment getReadiedItems()
	{
		return readiedItems;
	}
	
	public Equipment getMagicItems()
	{
		return magicItems;
	}
	
	//TODO: there is no check here for capacity limits
	public void receiveItem(Item item)
	{
		if (item.getInventorySlot().equals(EquipmentSlotType.MATERIAL))
			materials.add(item);
		else if (item.getInventorySlot().equals(EquipmentSlotType.MAGIC) && magicItems.hasEmptySlotAvailable(EquipmentSlotType.MAGIC))
		{
			int slotIndex = magicItems.getIndexOfFirstSlotAvailable(EquipmentSlotType.MAGIC);
			magicItems.equipItem(item, slotIndex);
		}
		else if (item.getInventorySlot().equals(EquipmentSlotType.ARMAMENT) && readiedItems.hasEmptySlotAvailable(EquipmentSlotType.ARMAMENT))
		{
			int slotIndex = readiedItems.getIndexOfFirstSlotAvailable(EquipmentSlotType.ARMAMENT);
			readiedItems.equipItem(item, slotIndex);
		}
		else
			storedItems.add(item);
	}
	
	public Item removeStoredItem(int itemIndex)
	{
		return storedItems.remove(itemIndex);
	}
	
	public Item removeMaterial(int itemIndex)
	{
		return materials.remove(itemIndex);
	}
	
	public Item removeStoredItem(int itemIndex, int quantity)
	{
		return storedItems.remove(itemIndex, quantity);
	}
	
	public Item removeMaterial(int itemIndex, int quantity)
	{
		return materials.remove(itemIndex, quantity);
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
	
	public void setSkill(SkillType skill, int level)
	{
		skills.put(skill, level);
	}
	
	public void gainSkillLevel(SkillType skill)
	{
		int currentLevel = 0;
		
		if (skills.containsKey(skill))
			currentLevel = skills.get(skill);
		
		skills.put(skill, currentLevel++);
	}
	
	public boolean hasSkill(SkillType skill, int level)
	{
		if (!skills.containsKey(skill))
			return false;
		
		int skillLevel = skills.get(skill);
		
		return (skillLevel >= level);
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
	
	public int getHpPercent()
	{
		int percentage = (int)(((((double)curHp) / ((double)maxHp)) * 100) + .5);
		if (percentage < 1 && curHp > 0)
			percentage = 1;
		
		return percentage;
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
	
	public void setDefaultTalkResponse(String defaultTalk)
	{
		this.defaultTalkResponse = defaultTalk;
	}
	
	public String getDefaultTalkResponse()
	{
		return defaultTalkResponse;
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
	
	public int getIndexOfMagicItem(Item item)
	{
		return magicItems.getIndexOfItem(item);
	}
	
	public int getIndexOfMaterial(Item item)
	{
		return materials.indexOf(item); 
	}
	
	public int getIndexOfStoredItem(Item item)
	{
		return storedItems.indexOf(item); 
	}
	
	public Item getItem(InventorySelectionKey itemLocation)
	{
		return getItem(itemLocation.getItemSource(), itemLocation.getItemIndex());
	}
	
	public Item getItem(ItemSource itemSource, int itemIndex)
	{
		switch (itemSource)
		{
		case EQUIPMENT:
			return equipment.getItem(itemIndex);
		case MAGIC:
			return magicItems.getItem(itemIndex);
		case MATERIAL:
			return materials.get(itemIndex);
		case PACK:
			return storedItems.get(itemIndex);
		case READY:
			return readiedItems.getItem(itemIndex);
		case GROUND:
		case NONE:
		default:
			break;
		}
		
		return null;
	}
	
	//TODO: right now this doesn't check held or ready items - only materials, magic items, and items in the pack
	public int getTotalItemCount(ItemType itemType)
	{
		Item virtualItem = ItemFactory.generateNewItem(itemType);
		
		if (virtualItem.getInventorySlot().equals(EquipmentSlotType.MATERIAL))
			return materials.getTotalItemsOfType(itemType);
		else if (virtualItem.getInventorySlot().equals(EquipmentSlotType.MAGIC))
			return magicItems.getTotalItemsOfType(itemType);
		return storedItems.getTotalItemsOfType(itemType);
	}
	
	public ItemSource getFirstAvailableSourceForItem(ItemType itemType)
	{
		if (materials.getTotalItemsOfType(itemType) != 0)
			return ItemSource.MATERIAL;
		if (magicItems.getTotalItemsOfType(itemType) != 0)
			return ItemSource.MAGIC;
		if (storedItems.getTotalItemsOfType(itemType) != 0)
			return ItemSource.PACK;
		if (readiedItems.getTotalItemsOfType(itemType) != 0)
			return ItemSource.READY;
		if (equipment.getTotalItemsOfType(itemType) != 0)
			return ItemSource.EQUIPMENT;
		
		return null;
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
		if (!defaultTalkResponse.equals(baseActor.defaultTalkResponse))
			ssb.addToken(new SaveToken(SaveTokenTag.A_TLK, defaultTalkResponse));

		saveAttributes(baseActor, ssb);
		
		if (storedItems != null && !storedItems.isEmpty())
			ssb.addToken(new SaveToken(SaveTokenTag.A_INV, convertInventoryToList(storedItems)));
		
		if (materials != null && !materials.isEmpty())
			ssb.addToken(new SaveToken(SaveTokenTag.A_MAT, convertInventoryToList(materials)));

		if (!equipment.isEmpty())
			ssb.addToken(new SaveToken(SaveTokenTag.A_EQP, convertEquipmentObjectToList(equipment)));
		
		if (!readiedItems.isEmpty())
			ssb.addToken(new SaveToken(SaveTokenTag.A_RDY, convertEquipmentObjectToList(readiedItems)));
		
		if (!magicItems.isEmpty())
			ssb.addToken(new SaveToken(SaveTokenTag.A_MAG, convertEquipmentObjectToList(magicItems)));
		
		if (!traits.isEmpty())
			ssb.addToken(new SaveToken(SaveTokenTag.A_TRT, convertTraitsToList()));
		
		if (!skills.isEmpty())
			ssb.addToken(new SaveToken(SaveTokenTag.A_SKL, convertSkillsToMap()));

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
		setMember(ssb, SaveTokenTag.A_MAT);
		setMember(ssb, SaveTokenTag.A_EQP);
		setMember(ssb, SaveTokenTag.A_RDY);
		setMember(ssb, SaveTokenTag.A_MAG);
		setMember(ssb, SaveTokenTag.A_TRT);
		setMember(ssb, SaveTokenTag.A_DAM);
		setMember(ssb, SaveTokenTag.A_DAR);
		setMember(ssb, SaveTokenTag.A_TLK);
		setMember(ssb, SaveTokenTag.A_SKL);

		return toRet;
	}

	private Map<String, String> convertSkillsToMap()
	{
		Map<String, String> toRet = new HashMap<String, String>();

		for (SkillType skill : skills.keySet())
		{
			String key = skill.name();
			String value = String.valueOf(skills.get(skill));
			toRet.put(key, value);
		}

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
	
	private List<String> convertInventoryToList(Inventory inventory)
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

	//if an actor's equipment type ever changes, that will need to be taken into account here
	private List<String> convertEquipmentObjectToList(Equipment equipmentObj)
	{
		List<String> toReturn = new ArrayList<String>();
		List<EquipmentSlot> slots = equipmentObj.getEquipmentSlots();
		
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
	
	@Override
	protected void setMember(SaveStringBuilder ssb, SaveTokenTag saveTokenTag)
	{
		String contents = getContentsForTag(ssb, saveTokenTag);
		int intContents = getIntContentsForTag(ssb, saveTokenTag);
		List<String> strVals = getContentSetForTag(ssb, saveTokenTag);
		Map<String, String> strMap = getContentMapForTag(ssb, saveTokenTag);
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
			this.name = contents;
			break;

		case A_GEN:
			GenderType genderType = GenderType.valueOf(contents);
			this.gender = genderType;
			break;

		case A_UNQ:
			this.unique = Boolean.parseBoolean(contents);
			break;

		case A_ICO:
			this.icon = contents.charAt(0);
			break;

		case A_CLR:
			this.color = intContents;
			break;

		case A_MHP:
			this.maxHp = intContents;
			break;

		case A_CHP:
			this.curHp = intContents;
			break;

		case A_AI_:
			this.AI = AiType.valueOf(contents);
			break;

		case A_ATT:
			for (int i = 0; i < TOTAL_ATTRIBUTES; i++)
			{
				attributes[i] = Integer.parseInt(strVals.get(i));
			}
			break;

		case A_INV:
			storedItems = new Inventory();
			
			for (String value : strVals)
			{
				referenceKey = "I" + value;
				storedItems.add(EntityMap.getItem(referenceKey).clone());
			}
			break;

		case A_MAT:
			materials = new Inventory();
			
			for (String value : strVals)
			{
				referenceKey = "I" + value;
				materials.add(EntityMap.getItem(referenceKey).clone());
			}
			break;

		case A_EQP:	//an empty equipment object is set when the actor is created, so we don't need to instantiate one here
			for (int i = 0; i < strVals.size(); i++)
			{
				String value = strVals.get(i);
				
				if (value.isEmpty())
					continue;
				
				referenceKey = "I" + value;
				
				equipment.equipItem(EntityMap.getItem(referenceKey).clone(), i);
			}
			break;

		case A_RDY:
			readiedItems = new ReadyEquipmentImpl(DEFAULT_READY_SLOTS);
			
			for (int i = 0; i < strVals.size(); i++)
			{
				String value = strVals.get(i);
				
				if (value.isEmpty())
					continue;
				
				referenceKey = "I" + value;
				
				readiedItems.equipItem(EntityMap.getItem(referenceKey).clone(), i);
			}
			break;

		case A_MAG:
			magicItems = new MagicEquipmentImpl(DEFAULT_MAGIC_SLOTS);
			
			for (int i = 0; i < strVals.size(); i++)
			{
				String value = strVals.get(i);
				
				if (value.isEmpty())
					continue;
				
				referenceKey = "I" + value;
				
				magicItems.equipItem(EntityMap.getItem(referenceKey).clone(), i);
			}
			break;
			
		case A_TRT:
			traits = new HashSet<ActorTraitType>();
			
			for (String value : strVals)
			{
				traits.add(ActorTraitType.valueOf(value));
			}
			break;
			
		case A_SKL:
			skills = new HashMap<SkillType, Integer>();
			
			for (String key : strMap.keySet())
			{
				SkillType skill = SkillType.fromString(key);
				int level = Integer.parseInt(strMap.get(key));
				
				skills.put(skill, level);
			}
			break;
			
		case A_DAM:
			this.defaultDamage = contents;
			break;

		case A_DAR:
			this.defaultArmor = intContents;
			break;
			
		case A_TLK:
			this.defaultTalkResponse = contents;
			break;
		//$CASES-OMITTED$
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
				|| defaultDamage != actor.defaultDamage || defaultArmor != actor.defaultArmor || !defaultTalkResponse.equals(actor.defaultTalkResponse))
			return false;

		for (int i = 0; i < TOTAL_ATTRIBUTES; i++)
		{
			if (attributes[i] != actor.attributes[i])
				return false;
		}
		
		if (!storedItems.equals(actor.storedItems))
			return false;
		
		if (!materials.equals(actor.materials))
			return false;
		
		if (!equipment.equals(actor.equipment))
			return false;
		
		if (!readiedItems.equals(actor.readiedItems))
			return false;
		
		if (!magicItems.equals(actor.magicItems))
			return false;

		if (!traits.equals(actor.traits))
			return false;
		
		if (!skills.equals(actor.skills))
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

		hash = 31 * hash + storedItems.hashCode();
		hash = 31 * hash + materials.hashCode();
		hash = 31 * hash + equipment.hashCode();
		hash = 31 * hash + readiedItems.hashCode();
		hash = 31 * hash + magicItems.hashCode();
		hash = 31 * hash + traits.hashCode();
		hash = 31 * hash + skills.hashCode();
		hash = 31 * hash + defaultDamage.hashCode();
		hash = 31 * hash + defaultArmor;
		hash = 31 * hash + defaultTalkResponse.hashCode();
		
		hash = 31 * hash + hashModifier;

		return hash;
	}

	@Override
	public String getUniqueId()
	{
		return EntityType.ACTOR.toString() + String.valueOf(Math.abs(hashCode()));
	}
}
