package main.entity.world;

import java.text.ParseException;

import main.entity.EntityType;
import main.entity.FieldCoord;
import main.entity.save.EntityMap;
import main.entity.save.SaveStringBuilder;
import main.entity.save.SaveToken;
import main.entity.save.SaveTokenTag;
import main.entity.zone.ZoneType;

public class WorldTile extends FieldCoord
{
	private ZoneType zoneType;
	private String zoneId; // if this points to a specific zone (towns, etc.), that ID is here (perhaps a non-null value means it's unique, so we don't need the
							// variable below)

	public WorldTile()
	{
		this(ZoneType.NO_TYPE, "Undefined World Tile", 'W', 15, false, false, 10000, ""); // note the large value; overworld tiles take a while to cross
																							// (multiply by 100)
	}

	public WorldTile(ZoneType zoneType, String name, char icon, int color, boolean sightObstruct, boolean moveObstruct, double moveCostModifier,
			String blockedMessage)
	{
		super(name, icon, color, sightObstruct, moveObstruct, moveCostModifier, blockedMessage);

		this.zoneType = zoneType;
		this.zoneId = "";
	}

	public void setZoneId(String zoneId)
	{
		this.zoneId = zoneId;
	}

	public String getZoneId()
	{
		return zoneId;
	}

	public ZoneType getType()
	{
		return zoneType;
	}

	@Override
	public String saveAsText()
	{
		WorldTile baseTile = WorldTileFactory.generateNewTile(zoneType);
		SaveStringBuilder ssb = new SaveStringBuilder(EntityType.WORLD_TILE);

		String tileUid = getUniqueId();

		if (EntityMap.getWorldTile(tileUid) == null)
			tileUid = EntityMap.put(tileUid, this);
		else
			tileUid = EntityMap.getSimpleKey(tileUid);

		// will be saved with every world tile
		ssb.addToken(new SaveToken(SaveTokenTag.W_UID, tileUid));
		ssb.addToken(new SaveToken(SaveTokenTag.W_TYP, zoneType.toString()));

		// will be saved only if they differ from the default world tile of this type
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
		if (!zoneId.equals(baseTile.zoneId))
			ssb.addToken(new SaveToken(SaveTokenTag.W_ZID, zoneId));

		return ssb.getSaveString();
	}

	@Override
	public String loadFromText(String text) throws ParseException
	{
		SaveStringBuilder ssb = new SaveStringBuilder(EntityType.WORLD_TILE, text);

		String toRet = getContentsForTag(ssb, SaveTokenTag.W_UID); // assumed to be defined

		setMember(ssb, SaveTokenTag.W_TYP);
		setMember(ssb, SaveTokenTag.C_NAM);
		setMember(ssb, SaveTokenTag.C_ICO);
		setMember(ssb, SaveTokenTag.C_CLR);
		setMember(ssb, SaveTokenTag.C_OST);
		setMember(ssb, SaveTokenTag.C_OMV);
		setMember(ssb, SaveTokenTag.C_MOV);
		setMember(ssb, SaveTokenTag.C_BLK);
		setMember(ssb, SaveTokenTag.W_ZID);

		return toRet;
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	protected void setMember(SaveStringBuilder ssb, SaveTokenTag saveTokenTag)
	{
		String contents = getContentsForTag(ssb, saveTokenTag);

		if (contents.equals(""))
			return;

		switch (saveTokenTag)
		{
		// TODO: unit test this
		case W_TYP:
			ZoneType type = ZoneType.valueOf(contents);
			if (!(type.equals(zoneType)))
				convertToType(type);
			break;

		case C_NAM:
			this.name = contents;
			break;

		case C_ICO:
			this.icon = contents.charAt(0);
			break;

		case C_CLR:
			this.color = Integer.parseInt(contents);
			break;

		case C_OST:
			this.obstructsSight = Boolean.parseBoolean(contents);
			break;

		case C_OMV:
			this.obstructsMotion = Boolean.parseBoolean(contents);
			break;

		case C_MOV:
			this.moveCostModifier = Double.parseDouble(contents);
			break;

		case C_BLK:
			this.blockedMessage = contents;
			break;

		case W_ZID:
			this.zoneId = contents;
			break;

		default:
			throw new IllegalArgumentException("WorldTile - Unhandled token: " + saveTokenTag.toString());
		}

		return;
	}

	private void convertToType(ZoneType type)
	{
		if (zoneType == type)
			return;

		WorldTile baseTile = WorldTileFactory.generateNewTile(type);

		this.zoneType = baseTile.zoneType;

		this.name = baseTile.name;
		this.icon = baseTile.icon;
		this.color = baseTile.color;
		this.obstructsSight = baseTile.obstructsSight;
		this.obstructsMotion = baseTile.obstructsMotion;
		this.moveCostModifier = baseTile.moveCostModifier;
		this.blockedMessage = baseTile.blockedMessage;
		this.visible = baseTile.visible;
		this.seen = baseTile.seen;
	}

	@Override
	public String getUniqueId()
	{
		return EntityType.WORLD_TILE.toString() + String.valueOf(Math.abs(hashCode()));
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
			return false;
		if (!(obj instanceof WorldTile))
			return false;
		if (!super.equals(obj))
			return false;

		WorldTile worldTile = (WorldTile) obj;

		if (!zoneType.equals(worldTile.zoneType) || !zoneId.equals(worldTile.zoneId))
			return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = super.hashCode();

		hash = 31 * hash + zoneType.toString().hashCode();
		hash = 31 * hash + (zoneId != null ? zoneId.hashCode() : 0);

		return hash;
	}

	@Override
	public WorldTile clone()
	{
		WorldTile toRet = new WorldTile(zoneType, name, icon, color, obstructsSight, obstructsMotion, moveCostModifier, blockedMessage);
		toRet.zoneId = zoneId;

		return toRet;
	}
}
