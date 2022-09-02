package main.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import main.entity.zone.Zone;
import main.entity.zone.predefined.PredefinedZoneLoader;
import main.presentation.Logger;

public class SpecialLevelManager implements SaveableDataManager
{
	private Map<String, Zone> generatedZones = new HashMap<String, Zone>();
	private Map<Integer, List<String>> specialZonesByBand;
	
	private static final String DELIMITER = ";";
	
	private static SpecialLevelManager instance = null;
	
	private SpecialLevelManager()
	{
		resetSpecialZonesByBand();
		Map<String, Integer> bandsOfPredefinedZones = PredefinedZoneLoader.getInstance().getBandsOfPredefinedZones();
		populateSpecialZonesForBands(bandsOfPredefinedZones);
	}
	
	public static SpecialLevelManager getInstance()
	{
		if (instance == null)
			instance = new SpecialLevelManager();
		
		return instance;
	}
	
	private void resetSpecialZonesByBand()
	{
		specialZonesByBand = new HashMap<Integer, List<String>>();
		
		for (int i = 0; i <= 30; i++)
			specialZonesByBand.put(i, new ArrayList<String>());
	}

	@Override
	public String saveState()
	{
		String saveString = "";
		
		for (String zoneFileName : generatedZones.keySet())
		{
			saveString = saveString + zoneFileName + DELIMITER;
		}
		
		return saveString.substring(0, saveString.length() - 1);
	}
	
	@Override
	public void loadState(String saveString)
	{
		generatedZones.clear();
		
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(saveString).useDelimiter(DELIMITER);
		
		while (scanner.hasNext())
		{
			generateSpecialLevel(scanner.next());
		}
		
		scanner.close();
	}
	
	public Zone generateSpecialLevel(String zoneFileName)
	{		
		Logger.debug("Generating special level for file name " + zoneFileName);
		
		if (zoneFileName == null)
			return null;
		
		if (generatedZones.containsKey(zoneFileName))
			return generatedZones.get(zoneFileName);
		
		Zone generatedZone = PredefinedZoneLoader.getInstance().loadZoneFromDataFile(zoneFileName);
		
		generatedZones.put(zoneFileName, generatedZone);
		
		return generatedZone;
	}
	
	//IMPORTANT: the zone should be cached after it's retrieved, unless it's data's currentZone
	public Zone retrieveZone(String zoneName)
	{
		String uniqueId = PredefinedZoneLoader.getInstance().getCacheNameOfZone(zoneName);
		Zone currentZone = DataAccessor.getInstance().getCurrentZone();
		
		if (currentZone != null && currentZone.getUniqueId().equals(uniqueId))
			return currentZone;
		
		DataSaveUtils zoneCacheUtility = DataAccessor.getInstance().getZoneCacheUtility();
		
		if (!zoneCacheUtility.isZoneCached(uniqueId))
		{
			String fileName = PredefinedZoneLoader.getInstance().getFileNameOfZone(zoneName);
			return generateSpecialLevel(fileName);
		}
			
		
		return zoneCacheUtility.uncacheZone(uniqueId);
	}
	
	public List<String> getSpecialZonesForBand(int band)
	{
		return specialZonesByBand.get(band);
	}
	
	private void populateSpecialZonesForBands(Map<String, Integer> predefinedZones)
	{
		for (String zoneName : predefinedZones.keySet())
		{
			int zoneBand = predefinedZones.get(zoneName);
			
			if (zoneBand < 0 || zoneBand > 30)
			{
				Logger.warn("Zone defined in file " + zoneName + " has an invalid band and will never be randomly generated.");
				continue;
			}
			
			List<String> specialZonesAtCurrentBand = specialZonesByBand.get(zoneBand);
			specialZonesAtCurrentBand.add(zoneName);
		}
	}
}
