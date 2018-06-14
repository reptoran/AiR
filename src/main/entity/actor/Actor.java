package main.entity.actor;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import main.entity.EntityType;
import main.entity.SaveableEntity;
import main.entity.save.EntityMap;
import main.entity.save.SaveStringBuilder;
import main.entity.save.SaveToken;
import main.entity.save.SaveTokenTag;
import main.logic.AI.AiType;

//This is either a player or an NPC.

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
	private int ticksLeftBeforeActing;
	private AiType AI;

	private int attributes[] = new int[TOTAL_ATTRIBUTES];

	private static int currentHash = 0;
	private int hashModifier;

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

		AI = AiType.RAND_MOVE;

		ticksLeftBeforeActing = 0;

		hashModifier = currentHash;
		currentHash++;
	}

	public Actor(ActorType actorType, String name, char icon, int color, int[] attributes)
	{
		this(actorType, name, icon, color, attributes, AiType.RAND_MOVE);
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
		toRet.ticksLeftBeforeActing = ticksLeftBeforeActing;
		toRet.hashModifier = hashModifier; // if we're truly cloning this, then they need the same unique identifier as well.

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
		this.ticksLeftBeforeActing = baseActor.ticksLeftBeforeActing;

		for (int i = 0; i < TOTAL_ATTRIBUTES; i++)
		{
			this.attributes[i] = baseActor.attributes[i];
		}
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

	public void setAttribute(int index, int value)
	{
		attributes[index] = value;
	}

	public int getAttributeBaseValue(int index)
	{
		return attributes[index];
	}

	public void reduceTicksLeftBeforeActing(int amount)
	{
		ticksLeftBeforeActing -= amount;
	}

	public void increaseTicksLeftBeforeActing(int amount)
	{
		ticksLeftBeforeActing += amount;
	}

	public int getTicksLeftBeforeActing()
	{
		return ticksLeftBeforeActing;
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
		if (name != baseActor.name)
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
		if (ticksLeftBeforeActing != baseActor.ticksLeftBeforeActing)
			ssb.addToken(new SaveToken(SaveTokenTag.A_SPD, String.valueOf(ticksLeftBeforeActing)));
		if (AI != baseActor.AI)
			ssb.addToken(new SaveToken(SaveTokenTag.A_AI_, String.valueOf(AI)));

		boolean changedAttributes = false;

		for (int i = 0; i < TOTAL_ATTRIBUTES; i++)
		{
			if (attributes[i] != baseActor.attributes[i])
				changedAttributes = true;
		}

		if (changedAttributes)
			ssb.addToken(new SaveToken(SaveTokenTag.A_ATT, convertAttributesToList()));

		return ssb.getSaveString();
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
		setMember(ssb, SaveTokenTag.A_SPD);
		setMember(ssb, SaveTokenTag.A_AI_);
		setMember(ssb, SaveTokenTag.A_ATT);

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

	@Override
	protected void setMember(SaveStringBuilder ssb, SaveTokenTag saveTokenTag)
	{
		String contents = getContentsForTag(ssb, saveTokenTag);
		SaveToken saveToken = null;
		List<String> strVals = null;

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

		case A_SPD:
			saveToken = ssb.getToken(saveTokenTag);
			this.ticksLeftBeforeActing = Integer.parseInt(saveToken.getContents());
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
				|| ticksLeftBeforeActing != actor.ticksLeftBeforeActing || AI != actor.AI || hashModifier != actor.hashModifier)
			return false;

		for (int i = 0; i < TOTAL_ATTRIBUTES; i++)
		{
			if (attributes[i] != actor.attributes[i])
				return false;
		}

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
		hash = 31 * hash + ticksLeftBeforeActing;
		hash = 31 * hash + AI.hashCode();

		for (int i = 0; i < TOTAL_ATTRIBUTES; i++)
		{
			hash = 31 * hash + attributes[i];
		}

		hash = 31 * hash + hashModifier;

		return hash;
	}

	@Override
	public String getUniqueId()
	{
		return EntityType.ACTOR.toString() + String.valueOf(Math.abs(hashCode()));
	}
}
