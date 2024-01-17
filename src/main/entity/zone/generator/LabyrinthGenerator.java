package main.entity.zone.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import main.data.SaveableDataManager;
import main.data.SpecialLevelManager;
import main.entity.zone.Zone;
import main.entity.zone.ZoneType;
import main.entity.zone.generator.dungeon.CaveGenerator;
import main.entity.zone.generator.dungeon.ClassicGenerator;
import main.entity.zone.generator.dungeon.RiftsGenerator;
import main.logic.RPGlib;
import main.presentation.Logger;

public class LabyrinthGenerator extends AbstractLinearZoneSystemGenerator implements SaveableDataManager
{
	private static final int NO_CURRENT_BAND = -1;
	private static final int MAX_BANDS = 30;
	
	private int generatedLevels = 0;
	private boolean lastLevelMissedSpecialChance = false;
	
	private List<String> specialLevels = new ArrayList<String>();
	private boolean[] specialLevelGenerated = new boolean[MAX_BANDS];
	
	private static LabyrinthGenerator instance = null;
	
	private LabyrinthGenerator()
	{
		for (int i = 0; i < MAX_BANDS; i++)
		{
			List<String> potentialSpecialLevels = SpecialLevelManager.getInstance().getSpecialZonesForBand(i);
			
			if (potentialSpecialLevels.isEmpty())
			{
				specialLevels.add(null);
				continue;
			}
			
			String selectedSpecialLevelFileName = potentialSpecialLevels.get(RPGlib.randInt(0, potentialSpecialLevels.size() - 1));
			specialLevels.add(selectedSpecialLevelFileName);
			specialLevelGenerated[i] = false;
		}
	}
	
	public static LabyrinthGenerator getInstance()
	{
		if (instance == null)
			instance = new LabyrinthGenerator();
		
		return instance;
	}
	
	@Override
	public String saveState()
	{
		String hasSpecialLevelBeenGeneratedForBandString = "";
		
		for (int i = 0; i < MAX_BANDS; i++)
			hasSpecialLevelBeenGeneratedForBandString = hasSpecialLevelBeenGeneratedForBandString + specialLevelGenerated[i] + DELIMITER;
		
		String saveString = hasSpecialLevelBeenGeneratedForBandString + generatedLevels + DELIMITER;
		
		for (String zoneFileName : specialLevels)
		{
			saveString = saveString + zoneFileName + DELIMITER;
		}
		
		return saveString.substring(0, saveString.length() - 1);
	}
	
	@Override
	public void loadState(String saveString)
	{
		specialLevels.clear();
		
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(saveString).useDelimiter(DELIMITER);
		
		for (int i = 0; i < MAX_BANDS; i++)
		{
			String value = scanner.next();
			specialLevelGenerated[i] = Boolean.parseBoolean(value);
		}
		
		generatedLevels = Integer.parseInt(scanner.next());
		
		while (scanner.hasNext())
		{
			String zoneFileName = scanner.next();
			
			if (zoneFileName.equals("null"))
				specialLevels.add(null);
			else
				specialLevels.add(zoneFileName);
		}
		
		scanner.close();
	}
	
	@Override
	protected Zone generateZonewithTilesAndDefaultZoneKeys()
	{
		Zone generatedZone = null;
		
		if (specialLevelShouldBeGenerated())
		{
			int currentBand = getCurrentBand();
			
			generatedZone = SpecialLevelManager.getInstance().generateSpecialLevel(specialLevels.get(currentBand));
			
			if (generatedZone != null)
			{
				generatedZone.setDepth(generatedLevels);
				Logger.debug("Special level generated!");
				specialLevelGenerated[currentBand] = true;
			}
		}
		
		if (generatedZone == null)
		{
//			generatedZone = new CaveGenerator().generate(zoneKey.getId(), HEIGHT, WIDTH, generatedLevels, true, false);
//			generatedZone = new RiftsGenerator().generate(zoneKey.getId(), HEIGHT, WIDTH, generatedLevels, true, false);
			generatedZone = new ClassicGenerator().generate(zoneKey.getId(), HEIGHT, WIDTH, generatedLevels, true, false);
		}
		
		Logger.debug("just generated zone for level " + generatedLevels);
		generatedLevels++;
		return generatedZone;
	}
	
	private boolean specialLevelShouldBeGenerated()
	{
		int currentBand = getCurrentBand();
		
		if (currentBand == NO_CURRENT_BAND)
		{
			lastLevelMissedSpecialChance = false;
			return false;
		}
		
		if (specialLevelGenerated[currentBand])
			return false;
		
		if (RPGlib.randInt(1, 2) == 1 || lastLevelMissedSpecialChance || currentBand == 0 || currentBand == 30)
		{
			lastLevelMissedSpecialChance = false;
			return true;
		}
		
		lastLevelMissedSpecialChance = true;
		return false;
	}
	
	private int getCurrentBand()
	{
		if (generatedLevels == 0)
			return 0;
		
		if (generatedLevels < 3)
			return NO_CURRENT_BAND;
		
		if (generatedLevels % 10 == 0)
			return (generatedLevels - 1) / 3;
		
		if ((generatedLevels - 1) % 10 == 0)
			return (generatedLevels - 2) / 3;
		
		if (generatedLevels % 3 == 0)
			return generatedLevels / 3;
		
		if ((generatedLevels - 1) % 3 == 0)
			return (generatedLevels - 1) / 3;
		
		return NO_CURRENT_BAND;
	}

	@Override
	protected ZoneType getDefaultZoneTypeForNextLevel()
	{
		return ZoneType.LABYRINTH;
	}
}
