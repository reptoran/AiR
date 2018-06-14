package main.entity.feature;

import java.text.ParseException;

import main.entity.EntityType;
import main.entity.FieldCoord;
import main.entity.save.EntityMap;
import main.entity.save.SaveStringBuilder;
import main.entity.save.SaveToken;
import main.entity.save.SaveTokenTag;

public class Feature extends FieldCoord
{
	private FeatureType type;
	private int curHP;
	private int maxHP;
	
	public Feature()
	{
		this(FeatureType.NO_TYPE, "empty feature", 'F', 15, false, false, 1, "");
	}
	
	public Feature(FeatureType featureType, String name, char icon, int color, boolean obstructsSight, boolean obstructsMotion, double moveCostModifier, String blockedMessage)
	{
		super(name, icon, color, obstructsSight, obstructsMotion, moveCostModifier, blockedMessage);
		
		type = featureType;
		curHP = 100;
		maxHP = 100;
	}
	
	@Override
	public Feature clone()
	{
		Feature toRet = new Feature(type, name, icon, color, obstructsSight, obstructsMotion, moveCostModifier, blockedMessage);
		toRet.curHP = curHP;
		toRet.maxHP = maxHP;
		
		return toRet;
	}

	public void setType(FeatureType type)
	{
		this.type = type;
	}
	
	private void convertToType(FeatureType featureType)
	{
		if (type == featureType)
			return;
		
		Feature baseFeature = FeatureFactory.generateNewFeature(featureType);
		
		this.type = baseFeature.type;
		
		this.name = baseFeature.name;
		this.icon = baseFeature.icon;
		this.color = baseFeature.color;
		this.obstructsSight = baseFeature.obstructsSight;
		this.obstructsMotion = baseFeature.obstructsMotion;
		
		this.maxHP = baseFeature.maxHP;
		this.curHP = baseFeature.curHP;
	}

	public void damage(int damageAmount)
	{
		curHP -= damageAmount;
		
		if (curHP > maxHP)
			curHP = maxHP;
	}
	
	@Override
	public String saveAsText()
	{
		Feature baseFeature = FeatureFactory.generateNewFeature(type);
		SaveStringBuilder ssb = new SaveStringBuilder(EntityType.FEATURE);
		
		String featureUid = getUniqueId();
		
		if (EntityMap.getFeature(featureUid) == null)
			featureUid = EntityMap.put(featureUid, this);
		else
			featureUid = EntityMap.getSimpleKey(featureUid);
		
		//will be saved with every feature
		ssb.addToken(new SaveToken(SaveTokenTag.F_UID, featureUid));
		ssb.addToken(new SaveToken(SaveTokenTag.F_TYP, type.toString()));
		
		//will be saved only if they differ from the default feature of this type
		if (name != baseFeature.name) ssb.addToken(new SaveToken(SaveTokenTag.C_NAM, name));
		if (icon != baseFeature.icon) ssb.addToken(new SaveToken(SaveTokenTag.C_ICO, String.valueOf(icon)));
		if (color != baseFeature.color) ssb.addToken(new SaveToken(SaveTokenTag.C_CLR, String.valueOf(color)));
		if (moveCostModifier != baseFeature.moveCostModifier) ssb.addToken(new SaveToken(SaveTokenTag.C_MOV, String.valueOf(moveCostModifier)));
		if (blockedMessage != baseFeature.blockedMessage) ssb.addToken(new SaveToken(SaveTokenTag.C_BLK, blockedMessage));
		if (curHP != baseFeature.curHP) ssb.addToken(new SaveToken(SaveTokenTag.F_CHP, String.valueOf(curHP)));
		if (maxHP != baseFeature.maxHP) ssb.addToken(new SaveToken(SaveTokenTag.F_MHP, String.valueOf(maxHP)));
		if (obstructsSight != baseFeature.obstructsSight) ssb.addToken(new SaveToken(SaveTokenTag.C_OST, String.valueOf(obstructsSight)));
		if (obstructsMotion != baseFeature.obstructsMotion) ssb.addToken(new SaveToken(SaveTokenTag.C_OMV, String.valueOf(obstructsMotion)));
		
		return ssb.getSaveString();
	}
	
	@Override
	public String loadFromText(String text) throws ParseException
	{
		SaveStringBuilder ssb = new SaveStringBuilder(EntityType.FEATURE, text);
		
		String toRet = getContentsForTag(ssb, SaveTokenTag.F_UID);	//assumed to be defined
		
		setMember(ssb, SaveTokenTag.F_TYP);
		setMember(ssb, SaveTokenTag.C_NAM);
		setMember(ssb, SaveTokenTag.C_ICO);
		setMember(ssb, SaveTokenTag.C_CLR);
		setMember(ssb, SaveTokenTag.C_OST);
		setMember(ssb, SaveTokenTag.C_OMV);
		setMember(ssb, SaveTokenTag.C_MOV);
		setMember(ssb, SaveTokenTag.C_BLK);
		setMember(ssb, SaveTokenTag.F_CHP);
		setMember(ssb, SaveTokenTag.F_MHP);
		
		return toRet;
	}

	@Override
	protected void setMember(SaveStringBuilder ssb, SaveTokenTag saveTokenTag)
	{
		String contents = getContentsForTag(ssb, saveTokenTag);
		SaveToken saveToken = null;
		
		if (contents.equals("")) return;
		
		switch (saveTokenTag)
		{
			//TODO: unit test this
			case F_TYP:
				FeatureType featureType = FeatureType.valueOf(contents); 
				if (!(featureType.equals(this.type)))
					convertToType(featureType);
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
			
			case F_CHP:
				saveToken = ssb.getToken(saveTokenTag);
				this.curHP = Integer.parseInt(saveToken.getContents());
				break;
			
			case F_MHP:
				saveToken = ssb.getToken(saveTokenTag);
				this.maxHP = Integer.parseInt(saveToken.getContents());
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
				this.moveCostModifier = Double.parseDouble(saveToken.getContents());
				break;
				
			case C_BLK:
				saveToken = ssb.getToken(saveTokenTag);
				this.blockedMessage = saveToken.getContents();
				break;
				
			default:
				throw new IllegalArgumentException("Feature - Unhandled token: " + saveTokenTag.toString());
		}
		
		return;
	}

	@Override
	public String getUniqueId()
	{
		return EntityType.FEATURE.toString() + String.valueOf(Math.abs(hashCode()));
	}
	
	@Override
	public boolean equals(Object obj)
	{	
		if (obj == null)
			return false;
		if (!(obj instanceof Feature))
			return false;
		if (!super.equals(obj))
			return false;
		
		Feature feature = (Feature)obj;
		
		if (!type.equals(feature.type) || curHP != feature.curHP || maxHP != feature.maxHP)
			return false;
		
		return true;
	}
	
	@Override
	public int hashCode()
	{
		int hash = super.hashCode();
				
		hash = 31 * hash + type.toString().hashCode();
		hash = 31 * hash + curHP;
		hash = 31 * hash + maxHP;
		
		return hash;
	}
}
