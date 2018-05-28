package main.entity.zone;

import java.awt.Point;
import java.util.Scanner;

public class ZoneKey
{
	//note that all these fields refer to the zone that the key is pointing to, not the zone it's currently within
	private String id;
	private ZoneType type;
	private int level;
	private Point entryPoint;		//this is the coordinates of where the actor ends up on the zone this transitions to

	public ZoneKey()
	{
		id = null;
		type = null;
		level = -1;
		entryPoint = null;
	}
	
	public ZoneKey(ZoneType type)
	{
		this();
		this.type = type;
	}
	
	public ZoneKey(ZoneType type, int level)
	{
		this(type);
		this.level = level;
	}
	
	public ZoneKey(String id, int level)
	{
		this(ZoneType.PERMANENT);
		this.id = id;
		this.level = level;
	}
	
	public String getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = type.getIdPrefix() + String.valueOf(id);
	}

	public void updateToPermanent(String newId)
	{
		id = newId;
		type = ZoneType.PERMANENT;
	}

	public void updateToTransient(String newId)
	{
		id = newId;
		type = ZoneType.TRANSIENT;	//TODO: may not need this; perhaps leaving it as the original type is actually what we want (in which case we can get rid of that enum as well)
	}
	
	public ZoneType getType()
	{
		return type;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public Point getEntryPoint()
	{
		return entryPoint;
	}
	
	public void setEntryPoint(Point entryPoint)
	{
		this.entryPoint = entryPoint;
	}
	
	public ZoneKey(String loadingText)
	{
		this();
		Scanner s = new Scanner(loadingText).useDelimiter("[.]");
		
		id = s.next();
		type = ZoneType.valueOf(s.next());
		level = Integer.parseInt(s.next());
		entryPoint = new Point(-1, -1);
		
		try {
			entryPoint.x = Integer.parseInt(s.next());
			entryPoint.y = Integer.parseInt(s.next());
		} catch (NumberFormatException nfe) {
			entryPoint = null;
		}
	}
	
	@Override
	public String toString()
	{
		String toRet = id + "." + type.name() + "." + String.valueOf(level) + ".";
		String entryPointString = "null.null";
		
		if (entryPoint != null)
			entryPointString = String.valueOf(entryPoint.x) + "." + String.valueOf(entryPoint.y);
		
		return toRet + entryPointString;
	}
}
