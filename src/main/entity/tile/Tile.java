package main.entity.tile;

import java.text.ParseException;

import main.data.GameSettings;
import main.data.SettingType;
import main.entity.EntityType;
import main.entity.FieldCoord;
import main.entity.actor.Actor;
import main.entity.feature.Feature;
import main.entity.item.Item;
import main.entity.save.EntityMap;
import main.entity.save.SaveStringBuilder;
import main.entity.save.SaveToken;
import main.entity.save.SaveTokenTag;

public class Tile extends FieldCoord
{
	private TileType type;
	private Actor actorHere;
	private Feature featureHere;
	private Item itemHere;

	// may move these up to fieldcoord
	protected boolean obstructsCoaligned;
	protected boolean obstructsEnemy;
	private char rememberedIcon = ' ';
	private int rememberedColor = 0;
	private char fogIcon = ' ';

	private static final int FOG_COLOR = 7;

	public Tile()
	{
		this(TileType.NO_TYPE, "empty tile", 'T', 15, false, false, false, false, 1, "");
	}

	public Tile(TileType tileType, String name, char icon, int color, boolean obstructsSight, boolean obstructsMotion,
			boolean obstructsCoaligned, boolean obstructsEnemy, double moveCostModifier, String blockedMessage)
	{
		super(name, icon, color, obstructsSight, obstructsMotion, moveCostModifier, blockedMessage);

		this.obstructsCoaligned = obstructsCoaligned;
		this.obstructsEnemy = obstructsEnemy;
		type = tileType;
		actorHere = null;
		featureHere = null;
		itemHere = null;
	}

	@Override
	public Tile clone()
	{
		Tile toRet = new Tile(type, name, icon, color, obstructsSight, obstructsMotion, obstructsCoaligned, obstructsEnemy,
				moveCostModifier, blockedMessage);
		toRet.visible = visible;
		toRet.seen = seen;
		toRet.actorHere = actorHere;
		toRet.featureHere = (featureHere == null) ? null : featureHere.clone();
		toRet.itemHere = itemHere;

		toRet.rememberedIcon = rememberedIcon;
		toRet.rememberedColor = rememberedColor;
		toRet.fogIcon = fogIcon;

		return toRet;
	}

	private void convertToType(TileType tileType)
	{
		if (type == tileType)
			return;

		Tile baseTile = TileFactory.generateNewTile(tileType);

		this.type = baseTile.type;

		this.name = baseTile.name;
		this.icon = baseTile.icon;
		this.color = baseTile.color;
		this.obstructsSight = baseTile.obstructsSight;
		this.obstructsMotion = baseTile.obstructsMotion;
		this.obstructsCoaligned = baseTile.obstructsCoaligned;
		this.obstructsEnemy = baseTile.obstructsEnemy;

		this.visible = false;
		this.seen = false;

		this.actorHere = null;
		this.featureHere = null;
		this.itemHere = null;
	}

	public void setType(TileType type)
	{
		this.type = type;
	}

	public TileType getType()
	{
		return type;
	}

	public Actor getActorHere()
	{
		return actorHere;
	}

	public void setActorHere(Actor actorHere)
	{
		this.actorHere = actorHere;
	}

	public Feature getFeatureHere()
	{
		return featureHere;
	}

	public void setFeatureHere(Feature featureHere)
	{
		this.featureHere = featureHere;
	}

	public Item getItemHere()
	{
		return itemHere;
	}

	public void setItemHere(Item itemHere)
	{
		this.itemHere = itemHere;
	}

	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);

		if (visible)
		{
			setRememberedIcon();
			setFogIcon();
		}
	}

	private void setFogIcon()
	{
		fogIcon = getIcon();
	}

	private void setRememberedIcon()
	{
		Actor tempActor = null;

		if (actorHere != null) // suspend the actor so icon/color logic doesn't need to be rewritten
		{
			tempActor = actorHere;
			actorHere = null;
		}

		rememberedIcon = getIcon();
		rememberedColor = getColor();
		actorHere = tempActor; // this is fine because tempActor will only stay null if actorHere is null
	}

	@Override
	public char getIcon()
	{
		if (!seen)
			return ' ';

		if (seen && !visible)
			return rememberedIcon();

		if (actorHere != null && visible)
			return actorHere.getIcon();

		if (itemHere != null && visible)
			return itemHere.getIcon();

		if (featureHere != null)
			return featureHere.getIcon();

		return icon;
	}

	public boolean obstructsCoaligned()
	{
		return obstructsCoaligned;
	}

	public void setObstructsCoaligned(boolean obstructsCoaligned)
	{
		this.obstructsCoaligned = obstructsCoaligned;
	}

	public boolean obstructsEnemy()
	{
		return obstructsEnemy;
	}

	public void setObstructsEnemy(boolean obstructsEnemy)
	{
		this.obstructsEnemy = obstructsEnemy;
	}

	private char rememberedIcon()
	{
		if (GameSettings.getBoolean(SettingType.SHOW_FOG))
			return fogIcon;

		return rememberedIcon;
	}

	@Override
	public int getColor()
	{
		if (!seen)
			return 0;

		if (seen && !visible)
			return rememberedColor();

		if (actorHere != null && visible)
			return actorHere.getColor();

		if (itemHere != null && visible)
			return itemHere.getColor();

		if (featureHere != null)
			return featureHere.getColor();

		return color;
	}

	private int rememberedColor()
	{
		if (GameSettings.getBoolean(SettingType.SHOW_FOG))
			return FOG_COLOR;

		return rememberedColor;
	}

	@Override
	public boolean obstructsMotion()
	{
		if (featureHere != null)
			return featureHere.obstructsMotion();

		return obstructsMotion;
	}

	@Override
	public boolean obstructsSight()
	{
		if (featureHere != null)
			return featureHere.obstructsSight();

		return obstructsSight;
	}

	@Override
	public String getBlockedMessage()
	{
		if (featureHere != null)
			return featureHere.getBlockedMessage();

		return blockedMessage;
	}

	public boolean obstructsItem()
	{
		return (obstructsMotion() || itemHere != null);
	}

	@Override
	public String saveAsText()
	{
		Tile baseTile = TileFactory.generateNewTile(type);
		SaveStringBuilder ssb = new SaveStringBuilder(EntityType.TILE);

		String tileUid = getUniqueId();

		if (EntityMap.getTile(tileUid) == null)
			tileUid = EntityMap.put(tileUid, this);
		else
			tileUid = EntityMap.getSimpleKey(tileUid);

		// will be saved with every tile
		ssb.addToken(new SaveToken(SaveTokenTag.T_UID, tileUid));
		ssb.addToken(new SaveToken(SaveTokenTag.T_TYP, type.toString()));

		// will be saved only if they differ from the default tile of this type
		if (!name.equals(baseTile.name))
			ssb.addToken(new SaveToken(SaveTokenTag.C_NAM, name));
		if (icon != baseTile.icon)
			ssb.addToken(new SaveToken(SaveTokenTag.C_ICO, String.valueOf(icon)));
		if (color != baseTile.color)
			ssb.addToken(new SaveToken(SaveTokenTag.C_CLR, String.valueOf(color)));
		if (moveCostModifier != baseTile.moveCostModifier)
			ssb.addToken(new SaveToken(SaveTokenTag.C_MOV, String.valueOf(moveCostModifier)));
		if (!blockedMessage.equals(baseTile.blockedMessage))
			ssb.addToken(new SaveToken(SaveTokenTag.C_BLK, blockedMessage));
		if (obstructsSight != baseTile.obstructsSight)
			ssb.addToken(new SaveToken(SaveTokenTag.C_OST, String.valueOf(obstructsSight)));
		if (obstructsMotion != baseTile.obstructsMotion)
			ssb.addToken(new SaveToken(SaveTokenTag.C_OMV, String.valueOf(obstructsMotion)));
		if (obstructsCoaligned != baseTile.obstructsCoaligned)
			ssb.addToken(new SaveToken(SaveTokenTag.T_OCA, String.valueOf(obstructsCoaligned)));
		if (obstructsEnemy != baseTile.obstructsEnemy)
			ssb.addToken(new SaveToken(SaveTokenTag.T_OEN, String.valueOf(obstructsEnemy)));
		if (visible != baseTile.visible)
			ssb.addToken(new SaveToken(SaveTokenTag.C_VIS, String.valueOf(visible)));
		if (seen != baseTile.seen)
			ssb.addToken(new SaveToken(SaveTokenTag.C_SEN, String.valueOf(seen)));
		if (rememberedIcon != baseTile.rememberedIcon)
			ssb.addToken(new SaveToken(SaveTokenTag.T_RIC, String.valueOf(rememberedIcon)));
		if (rememberedColor != baseTile.rememberedColor)
			ssb.addToken(new SaveToken(SaveTokenTag.T_RCL, String.valueOf(rememberedColor)));
		if (fogIcon != baseTile.fogIcon)
			ssb.addToken(new SaveToken(SaveTokenTag.T_FIC, String.valueOf(fogIcon)));

		if (actorHere != baseTile.actorHere)
		{
			String actorUid = actorHere.getUniqueId();

			if (EntityMap.getActor(actorUid) == null)
				actorUid = EntityMap.put(actorUid, actorHere);
			else
				actorUid = EntityMap.getSimpleKey(actorUid);

			ssb.addToken(new SaveToken(SaveTokenTag.T_AHR, actorUid.substring(1)));
		}
		if (featureHere != baseTile.featureHere)
		{
			String featureUid = featureHere.getUniqueId();

			if (EntityMap.getFeature(featureUid) == null)
				featureUid = EntityMap.put(featureUid, featureHere);
			else
				featureUid = EntityMap.getSimpleKey(featureUid);

			ssb.addToken(new SaveToken(SaveTokenTag.T_FHR, featureUid.substring(1)));
		}

		if (itemHere != baseTile.itemHere)
		{
			String itemUid = itemHere.getUniqueId();

			if (EntityMap.getItem(itemUid) == null)
				itemUid = EntityMap.put(itemUid, itemHere);
			else
				itemUid = EntityMap.getSimpleKey(itemUid);

			ssb.addToken(new SaveToken(SaveTokenTag.T_IHR, itemUid.substring(1)));
		}

		return ssb.getSaveString();
	}

	@Override
	public String loadFromText(String text) throws ParseException
	{
		SaveStringBuilder ssb = new SaveStringBuilder(EntityType.TILE, text);

		String toRet = getContentsForTag(ssb, SaveTokenTag.T_UID); // assumed to be defined

		setMember(ssb, SaveTokenTag.T_TYP);
		setMember(ssb, SaveTokenTag.C_NAM);
		setMember(ssb, SaveTokenTag.C_ICO);
		setMember(ssb, SaveTokenTag.C_CLR);
		setMember(ssb, SaveTokenTag.C_OST);
		setMember(ssb, SaveTokenTag.C_OMV);
		setMember(ssb, SaveTokenTag.T_OCA);
		setMember(ssb, SaveTokenTag.T_OEN);
		setMember(ssb, SaveTokenTag.C_MOV);
		setMember(ssb, SaveTokenTag.C_BLK);
		setMember(ssb, SaveTokenTag.C_VIS);
		setMember(ssb, SaveTokenTag.C_SEN);
		setMember(ssb, SaveTokenTag.T_RIC);
		setMember(ssb, SaveTokenTag.T_RCL);
		setMember(ssb, SaveTokenTag.T_FIC);
		setMember(ssb, SaveTokenTag.T_AHR);
		setMember(ssb, SaveTokenTag.T_FHR);
		setMember(ssb, SaveTokenTag.T_IHR);

		return toRet;
	}

	@Override
	protected void setMember(SaveStringBuilder ssb, SaveTokenTag saveTokenTag)
	{
		String contents = getContentsForTag(ssb, saveTokenTag);
		int intContents = getIntContentsForTag(ssb, saveTokenTag);
		boolean boolContents = getBooleanContentsForTag(ssb, saveTokenTag);
		String referenceKey = "";

		if (contents.equals(""))
			return;

		switch (saveTokenTag)
		{
		// TODO: unit test this
		case T_TYP:
			TileType tileType = TileType.valueOf(contents);
			if (!(tileType.equals(this.type)))
				convertToType(tileType);
			break;

		case C_NAM:
			this.name = contents;
			break;

		case C_ICO:
			this.icon = contents.charAt(0);
			break;

		case C_CLR:
			this.color = intContents;
			break;

		case T_RIC:
			this.rememberedIcon = contents.charAt(0);
			break;

		case T_RCL:
			this.rememberedColor = intContents;
			break;

		case T_FIC:
			this.fogIcon = contents.charAt(0);
			break;

		case T_AHR:
			referenceKey = "A" + contents;
			Actor actor = EntityMap.getActor(referenceKey);
			this.actorHere = actor;
			break;

		case T_FHR:
			referenceKey = "F" + contents;
			Feature feature = EntityMap.getFeature(referenceKey).clone();
			this.featureHere = feature;
			break;

		case T_IHR:
			referenceKey = "I" + contents;
			Item item = EntityMap.getItem(referenceKey).clone();
			this.itemHere = item;
			break;

		case C_OST:
			this.obstructsSight = boolContents;
			break;

		case C_OMV:
			this.obstructsMotion = boolContents;
			break;

		case T_OCA:
			this.obstructsCoaligned = boolContents;
			break;

		case T_OEN:
			this.obstructsEnemy = boolContents;
			break;

		case C_VIS:
			this.visible = boolContents;
			break;

		case C_SEN:
			this.seen = boolContents;
			break;

		case C_MOV:
			this.moveCostModifier = Double.parseDouble(contents);
			break;

		case C_BLK:
			this.blockedMessage = contents;
			break;

		// $CASES-OMITTED$
		default:
			throw new IllegalArgumentException("Tile - Unhandled token: " + saveTokenTag.toString());
		}
	}

	@Override
	public String getUniqueId()
	{
		return EntityType.TILE.toString() + String.valueOf(Math.abs(hashCode()));
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
			return false;
		if (!(obj instanceof Tile))
			return false;
		if (!super.equals(obj))
			return false;

		Tile tile = (Tile) obj;

		if (!type.equals(tile.type))
			return false;

		if (obstructsCoaligned != tile.obstructsCoaligned || obstructsEnemy != tile.obstructsEnemy || rememberedIcon != tile.rememberedIcon
				|| rememberedColor != tile.rememberedColor || fogIcon != tile.fogIcon)
			return false;

		if (actorHere != null && !actorHere.equals(tile.actorHere))
			return false;
		else if (actorHere == null && tile.actorHere != null)
			return false;

		if (featureHere != null && !featureHere.equals(tile.featureHere))
			return false;
		else if (featureHere == null && tile.featureHere != null)
			return false;

		if (itemHere != null && !itemHere.equals(tile.itemHere))
			return false;
		else if (itemHere == null && tile.itemHere != null)
			return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = super.hashCode();

		hash = 31 * hash + type.toString().hashCode();
		hash = 31 * hash + (obstructsCoaligned ? 1 : 0);
		hash = 31 * hash + (obstructsEnemy ? 1 : 0);
		hash = 31 * hash + rememberedIcon;
		hash = 31 * hash + rememberedColor;
		hash = 31 * hash + fogIcon;
		hash = 31 * hash + (actorHere == null ? 0 : actorHere.hashCode());
		hash = 31 * hash + (featureHere == null ? 0 : featureHere.hashCode());
		hash = 31 * hash + (itemHere == null ? 0 : itemHere.hashCode());

		return hash;
	}
}
