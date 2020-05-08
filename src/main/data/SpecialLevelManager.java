package main.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import main.entity.zone.Zone;
import main.entity.zone.ZoneAttribute;
import main.entity.zone.predefined.PredefinedZone;
import main.logic.RPGlib;
import main.presentation.Logger;

public class SpecialLevelManager
{
	private List<PredefinedZone> specialZones = new ArrayList<PredefinedZone>();
	private List<String> generatedZoneNames = new ArrayList<String>();
	
	private static final String DELIMITER = ";";
	
	private static SpecialLevelManager instance = null;
	
	private SpecialLevelManager() {}
	
	public static SpecialLevelManager getInstance()
	{
		if (instance == null)
			instance = new SpecialLevelManager();
		
		return instance;
	}

	public String saveState()
	{
		String saveString = "";
		
		for (String zoneName : generatedZoneNames)
		{
			saveString = saveString + zoneName + DELIMITER;
		}
		
		return saveString.substring(0, saveString.length() - 1);
	}
	
	public void loadState(String saveString)
	{
		generatedZoneNames.clear();
		
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(saveString).useDelimiter(DELIMITER);
		
		while (scanner.hasNext())
		{
			generatedZoneNames.add(scanner.next());
		}
		
		scanner.close();
	}
	
	public Zone generateSpecialLevel(int level)
	{
		if (specialZones == null)
			throw new IllegalStateException("No special levels loaded into the manager yet.");
		
		List<PredefinedZone> zonesForCurrentLevel = new ArrayList<PredefinedZone>();
		
		for (PredefinedZone zone : specialZones)
		{
			int minLevel = getIntegerAttribute(zone.getAttribute(ZoneAttribute.MINLEVEL), 1);
			int maxLevel = getIntegerAttribute(zone.getAttribute(ZoneAttribute.MAXLEVEL), 99);
			
			if (minLevel <= level && maxLevel >= level)
				zonesForCurrentLevel.add(zone);
		}
		
		int totalPossibleZonesToGenerate = zonesForCurrentLevel.size();
		
		if (totalPossibleZonesToGenerate == 0)
		{
			Logger.warn("No predefined zones are eligible to be generated for level " + level + ".");
			return null;
		}
		
		int indexOfZoneToGenerate = RPGlib.randInt(0, totalPossibleZonesToGenerate - 1);
		PredefinedZone zoneToGenerate = zonesForCurrentLevel.get(indexOfZoneToGenerate);
		
		specialZones.remove(zoneToGenerate);	//this zone can never be generated again
		generatedZoneNames.add(zoneToGenerate.getAttribute(ZoneAttribute.NAME));
		
		return zoneToGenerate.getZone(); 
	}

	public void populateSpecialZonesForLevels(List<PredefinedZone> predefinedZones)
	{
		specialZones.clear();
		
		for (PredefinedZone zone : predefinedZones)
		{
			String zoneName = zone.getAttribute(ZoneAttribute.NAME);
			if (generatedZoneNames.contains(zoneName))
				continue;
			
			specialZones.add(zone);
		}
	}

	private int getIntegerAttribute(String attribute, int defaultValue)
	{
		if (attribute == null)
			return defaultValue;
		
		return Integer.valueOf(attribute);
	} 
}
