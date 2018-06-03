package main.entity.zone.predefined;

import java.util.HashMap;
import java.util.Map;

import main.entity.zone.Zone;
import main.entity.zone.ZoneAttribute;
import main.entity.zone.ZoneType;

public class PredefinedZone extends Zone
{
	private Map<ZoneAttribute, String> zoneAttributes = new HashMap<ZoneAttribute, String>();
	
	public PredefinedZone(ZoneType type, String name, int height, int width, int depth, boolean shouldPersist, boolean canEnterWorld)
	{
		super(type, name, height, width, depth, shouldPersist, canEnterWorld);
	}
	
	public Zone getZone()
	{
		return (Zone) this;
	}
	
	public String getAttribute(ZoneAttribute key)
	{
		return zoneAttributes.get(key);
	}
	
	public void setZoneAttributes(Map<ZoneAttribute, String> zoneAttributeMap)
	{
		zoneAttributes = zoneAttributeMap;
	}
	
	@Override
	public String toString()
	{
		return super.toString();		//TODO: add attributes to this string
	}
}
