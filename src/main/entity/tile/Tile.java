package main.entity.tile;

import java.text.ParseException;

import main.entity.EntityType;
import main.entity.FieldCoord;
import main.entity.actor.Actor;
import main.entity.feature.Feature;
import main.entity.save.EntityMap;
import main.entity.save.SaveStringBuilder;
import main.entity.save.SaveToken;
import main.entity.save.SaveTokenTag;

public class Tile extends FieldCoord
{
	private TileType type;
	private Actor actorHere;
	private Feature featureHere;
	
	public Tile()
	{
		this(TileType.NO_TYPE, "empty tile", 'T', 15, false, false, 100, "");
	}
	
	public Tile(TileType tileType, String name, char icon, int color, boolean obstructsSight, boolean obstructsMotion, int moveCost, String blockedMessage)
	{
		super(name, icon, color, obstructsSight, obstructsMotion, moveCost, blockedMessage);
		
		type = tileType;
		actorHere = null;
		featureHere = null;
	}
	
	@Override
	public Tile clone()
	{
		Tile toRet = new Tile(type, name, icon, color, obstructsSight, obstructsMotion, moveCost, blockedMessage);
		toRet.actorHere = actorHere;
		toRet.featureHere = (featureHere == null) ? null : featureHere.clone();
		
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
		
		this.actorHere = null;
		this.featureHere = null;
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
	
	public char getDisplayIcon()
	{
		if (actorHere != null)
			return actorHere.getIcon();
		
		if (featureHere != null)
			return featureHere.getIcon();
		
		return icon;
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
	public String saveAsText()
	{
		Tile baseTile = TileFactory.generateNewTile(type);
		SaveStringBuilder ssb = new SaveStringBuilder(EntityType.TILE);
		
		String tileUid = getUniqueId();
		
		if (EntityMap.getTile(tileUid) == null)
			tileUid = EntityMap.put(tileUid, this);
		else
			tileUid = EntityMap.getSimpleKey(tileUid);
		
		//will be saved with every tile
		ssb.addToken(new SaveToken(SaveTokenTag.T_UID, tileUid));
		ssb.addToken(new SaveToken(SaveTokenTag.T_TYP, type.toString()));
		
		//will be saved only if they differ from the default tile of this type
		if (name != baseTile.name) ssb.addToken(new SaveToken(SaveTokenTag.C_NAM, name));
		if (icon != baseTile.icon) ssb.addToken(new SaveToken(SaveTokenTag.C_ICO, String.valueOf(icon)));
		if (color != baseTile.color) ssb.addToken(new SaveToken(SaveTokenTag.C_CLR, String.valueOf(color)));
		if (moveCost != baseTile.moveCost) ssb.addToken(new SaveToken(SaveTokenTag.C_MOV, String.valueOf(moveCost)));
		if (blockedMessage != baseTile.blockedMessage) ssb.addToken(new SaveToken(SaveTokenTag.C_BLK, blockedMessage));
		if (obstructsSight != baseTile.obstructsSight) ssb.addToken(new SaveToken(SaveTokenTag.C_OST, String.valueOf(obstructsSight)));
		if (obstructsMotion != baseTile.obstructsMotion) ssb.addToken(new SaveToken(SaveTokenTag.C_OMV, String.valueOf(obstructsMotion)));
		
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
		
		return ssb.getSaveString();
	}
	
	@Override
	public String loadFromText(String text) throws ParseException
	{
		SaveStringBuilder ssb = new SaveStringBuilder(EntityType.TILE, text);
		
		String toRet = getContentsForTag(ssb, SaveTokenTag.T_UID);	//assumed to be defined
		
		setMember(ssb, SaveTokenTag.C_NAM);
		setMember(ssb, SaveTokenTag.C_ICO);
		setMember(ssb, SaveTokenTag.C_CLR);
		setMember(ssb, SaveTokenTag.C_OST);
		setMember(ssb, SaveTokenTag.C_OMV);
		setMember(ssb, SaveTokenTag.C_MOV);
		setMember(ssb, SaveTokenTag.C_BLK);
		setMember(ssb, SaveTokenTag.T_TYP);
		setMember(ssb, SaveTokenTag.T_AHR);
		setMember(ssb, SaveTokenTag.T_FHR);
		
		return toRet;
	}

	@Override
	protected void setMember(SaveStringBuilder ssb, SaveTokenTag saveTokenTag)
	{
		String contents = getContentsForTag(ssb, saveTokenTag);
		SaveToken saveToken = null;
		String referenceKey = "";
		
		if (contents.equals("")) return;
		
		switch (saveTokenTag)
		{
			//TODO: unit test this
			case T_TYP:
				TileType tileType = TileType.valueOf(contents); 
				if (!(tileType.equals(this.type)))
					convertToType(tileType);
				break;
				
			case C_NAM:
				saveToken = ssb.getToken(saveTokenTag);
				this.name = saveToken.getContents();
				break;
				
			case C_ICO:
				saveToken = ssb.getToken(saveTokenTag);
				this.icon = saveToken.getContents().charAt(0);
				break;
			
			case C_CLR:
				saveToken = ssb.getToken(saveTokenTag);
				this.color = Integer.parseInt(saveToken.getContents());
				break;
			
			case T_AHR:
				saveToken = ssb.getToken(saveTokenTag);
				referenceKey = "A" + saveToken.getContents();
				Actor actor = EntityMap.getActor(referenceKey);
				this.actorHere = actor;
				break;
			
			case T_FHR:
				saveToken = ssb.getToken(saveTokenTag);
				referenceKey = "F" + saveToken.getContents();
				Feature feature = EntityMap.getFeature(referenceKey).clone();
				this.featureHere = feature;
				break;
			
			case C_OST:
				saveToken = ssb.getToken(saveTokenTag);
				this.obstructsSight = Boolean.parseBoolean(saveToken.getContents());
				break;
			
			case C_OMV:
				saveToken = ssb.getToken(saveTokenTag);
				this.obstructsMotion = Boolean.parseBoolean(saveToken.getContents());
				break;
			
			case C_MOV:
				saveToken = ssb.getToken(saveTokenTag);
				this.moveCost = Integer.parseInt(saveToken.getContents());
				break;
				
			case C_BLK:
				saveToken = ssb.getToken(saveTokenTag);
				this.blockedMessage = saveToken.getContents();
				break;
				
			default:
				throw new IllegalArgumentException("Tile - Unhandled token: " + saveTokenTag.toString());
		}
		
		return;
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
		
		Tile tile = (Tile)obj;
		
		if (!type.equals(tile.type))
			return false;
		
		if (actorHere != null && !actorHere.equals(tile.actorHere))
			return false;
		else if (actorHere == null && tile.actorHere != null)
			return false;
		
		if (featureHere != null && !featureHere.equals(tile.featureHere))
			return false;
		else if (featureHere == null && tile.featureHere != null)
			return false;
		
		return true;
	}
	
	@Override
	public int hashCode()
	{
		int hash = super.hashCode();
				
		hash = 31 * hash + type.toString().hashCode();
		hash = 31 * hash + (actorHere == null ? 0 : actorHere.hashCode());
		hash = 31 * hash + (featureHere == null ? 0 : featureHere.hashCode());
		
		return hash;
	}
}
