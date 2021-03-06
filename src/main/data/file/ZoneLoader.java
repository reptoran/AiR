package main.data.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import main.entity.actor.ActorType;
import main.entity.feature.FeatureType;
import main.entity.tile.TileType;
import main.entity.zone.Zone;
import main.entity.zone.ZoneBuilder;
import main.presentation.Logger;

public class ZoneLoader extends FileHandler
{
	private static final String STATE_KEY_PLAN = "[PLAN]";
	private static final String STATE_KEY_TILES = "[TILES]";
	private static final String STATE_KEY_ACTORS = "[ACTORS]";
	private static final String STATE_KEY_ITEMS = "[ITEMS]";
	
	private static int loadedZoneCount = 0;

	private ZoneDataState currentState = null;
	private ZoneBuilder zoneBuilder = null;

	private String dataPath = ROOT_PATH + "data" + File.separator + "zones" + File.separator;
	
	public List<Zone> loadAllPredefinedZones()
	{
		List<Zone> loadedZones = new ArrayList<Zone>();
		
		//TODO: unzip a .dat file that's just an archive of everything, then search the created folder
		
		File folder = new File(dataPath);

		for (File file : folder.listFiles())
		{
			loadedZones.add(loadZoneFromDataFile(file.getName()));
		}
		
		return loadedZones;
	}

	private Zone loadZoneFromDataFile(String zoneName)
	{
		String path = dataPath + zoneName;
		List<String> lines = loadFile(path);
		
		zoneBuilder = new ZoneBuilder(++loadedZoneCount);
		Zone zone = generateZoneFromData(lines);
		zoneBuilder = null;
		
		return zone;
	}

	private Zone generateZoneFromData(List<String> lines)
	{
		for (String line : lines)
		{
			if (line.isEmpty())
				continue;
			
			if (STATE_KEY_PLAN.equals(line))
			{
				currentState = ZoneDataState.PLAN;
				continue;
			} else if (STATE_KEY_TILES.equals(line))
			{
				currentState = ZoneDataState.TILES;
				continue;
			} else if (STATE_KEY_ACTORS.equals(line))
			{
				currentState = ZoneDataState.ACTORS;
				continue;
			} else if (STATE_KEY_ITEMS.equals(line))
			{
				currentState = ZoneDataState.ITEMS;
				continue;
			}
			
			List<String> lineElements = tokenizeLine(line);

			if (currentState == ZoneDataState.PLAN)
				interpretPlanData(lineElements);
			else if (currentState == ZoneDataState.TILES)
				interpretTileData(lineElements);
			else if (currentState == ZoneDataState.ACTORS)
				interpretActorData(lineElements);
			else if (currentState == ZoneDataState.ITEMS)
				interpretItemData(lineElements);
		}

		return zoneBuilder.build();
	}

	private void interpretPlanData(List<String> lineElements)
	{
		zoneBuilder.defineMapLine(lineElements.get(0));
	}

	private void interpretTileData(List<String> lineElements)
	{
		char tileIcon = lineElements.get(0).charAt(0);
		TileType tileType = TileType.valueOf(lineElements.get(1));
		
		if (lineElements.size() > 2)
		{
			FeatureType featureType = FeatureType.valueOf(lineElements.get(2));
			zoneBuilder.defineTile(tileIcon, tileType, featureType);
		} else {
			zoneBuilder.defineTile(tileIcon, tileType);
		}
	}

	private void interpretActorData(List<String> lineElements)
	{
		int row = Integer.valueOf(lineElements.get(0));
		int col = Integer.valueOf(lineElements.get(1));
		ActorType actorType = ActorType.valueOf(lineElements.get(2));
		
		zoneBuilder.defineActor(row, col, actorType);
	}

	private void interpretItemData(List<String> lineElements)
	{
		Logger.warn("Items not yet implemented; cannot parse item information from data file.");
	}

	@SuppressWarnings("resource")
	private List<String> tokenizeLine(String line)
	{
		List<String> tokens = new ArrayList<String>();
		Scanner scanner = new Scanner(line).useDelimiter(":");
		while (scanner.hasNext())
		{
			tokens.add(scanner.next());
		}

		scanner.close();
		return tokens;
	}

	private enum ZoneDataState
	{
		PLAN, TILES, ACTORS, ITEMS;
	}
}
