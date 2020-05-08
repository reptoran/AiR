package main.entity.zone.generator;

import main.data.SpecialLevelManager;
import main.entity.zone.Zone;
import main.entity.zone.ZoneType;
import main.entity.zone.generator.dungeon.CaveGenerator;
import main.logic.RPGlib;
import main.presentation.Logger;

public class LabyrinthGenerator extends AbstractLinearZoneSystemGenerator
{
	private static int generatedLevels = 0;
	private static int specialLevels = 0;
	private static int levelsSinceLastSpecial = 0;
	
	private static final int BOTTOM_LEVEL = 15;
	private static final String DELIMITER = ";";
	
	public static String saveState()
	{
		return generatedLevels + DELIMITER + specialLevels + DELIMITER + levelsSinceLastSpecial;
	}
	
	public static void loadState(String saveString)
	{
		int firstDelimiterIndex = saveString.indexOf(DELIMITER.charAt(0));
		int lastDelimiterIndex = saveString.lastIndexOf(DELIMITER.charAt(0));
		
		generatedLevels = Integer.parseInt(saveString.substring(0, firstDelimiterIndex));
		specialLevels = Integer.parseInt(saveString.substring(firstDelimiterIndex + 1, lastDelimiterIndex));
		levelsSinceLastSpecial = Integer.parseInt(saveString.substring(lastDelimiterIndex + 1));
	}
	
	@Override
	protected Zone generateZonewithTilesAndDefaultZoneKeys()
	{
		Zone generatedZone = null;
		
		if (specialLevelShouldBeGenerated())
		{
			generatedZone = SpecialLevelManager.getInstance().generateSpecialLevel(generatedLevels);
			
			if (generatedZone != null)
			{
				specialLevels++;
				levelsSinceLastSpecial = 0;
				generatedZone.setDepth(generatedLevels);
				Logger.debug("Special level generated!");
			}
		}
		
		if (generatedZone == null)
		{
			levelsSinceLastSpecial++;
			generatedZone = new CaveGenerator().generate(zoneKey.getId(), HEIGHT, WIDTH, generatedLevels, true, false);
		}
		
		Logger.debug("just generated zone for level " + generatedLevels);
		generatedLevels++;
		return generatedZone;
	}
	
	private boolean specialLevelShouldBeGenerated()
	{
		if (generatedLevels == 0 || generatedLevels == BOTTOM_LEVEL)
			return true;
		
		int specialLevelChance = 10 * (-5 - specialLevels + levelsSinceLastSpecial);
		
		Logger.debug("Attempting to generate special level; chance is " + specialLevelChance + ".");
		
		if (specialLevelChance <= 0)
			return false;
		
		return (RPGlib.randInt(1, 100) <= specialLevelChance);
	}

	@Override
	protected ZoneType getDefaultZoneTypeForNextLevel()
	{
		return ZoneType.LABYRINTH;
	}
}
