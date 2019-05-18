package main.entity.zone.predefined;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import main.data.file.FileHandler;
import main.entity.actor.ActorType;
import main.entity.feature.FeatureType;
import main.entity.item.ItemType;
import main.entity.tile.TileType;

public class PredefinedZoneLoader extends FileHandler
{
	private static final String STATE_KEY_PLAN = "[PLAN]";
	private static final String STATE_KEY_TILES = "[TILES]";
	private static final String STATE_KEY_ACTORS = "[ACTORS]";
	private static final String STATE_KEY_ITEMS = "[ITEMS]";
	private static final String STATE_KEY_ATTRIBUTES = "[ATTRIBUTES]";
	
	private static int loadedZoneCount = 0;

	private ZoneDataState currentState = null;
	private PredefinedZoneBuilder predefinedZoneBuilder = null;
	
	public List<PredefinedZone> loadAllPredefinedZones()
	{
		List<PredefinedZone> loadedZones = new ArrayList<PredefinedZone>();
		
		//TODO: unzip a .dat file that's just an archive of everything, then search the created folder
		
		File folder = new File(getDataPath());

		for (File file : folder.listFiles())
		{
			if (!getFileExtension(file).equals(getExtension()))
				continue;
			
			loadedZones.add(loadZoneFromDataFile(file.getName()));
		}
		
		return loadedZones;
	}

	private PredefinedZone loadZoneFromDataFile(String zoneName)
	{
		String path = getDataPath() + zoneName;
		List<String> lines = loadFile(path);
		
		predefinedZoneBuilder = new PredefinedZoneBuilder(++loadedZoneCount);
		PredefinedZone zone = generateZoneFromData(lines);
		predefinedZoneBuilder = null;
		
		return zone;
	}

	private PredefinedZone generateZoneFromData(List<String> lines)
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
			} else if (STATE_KEY_ATTRIBUTES.equals(line))
			{
				currentState = ZoneDataState.ATTRIBUTES;
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
			else if (currentState == ZoneDataState.ATTRIBUTES)
				interpretAttributeData(lineElements);
		}

		return predefinedZoneBuilder.build();
	}

	private void interpretPlanData(List<String> lineElements)
	{
		predefinedZoneBuilder.defineMapLine(lineElements.get(0));
	}

	private void interpretTileData(List<String> lineElements)
	{
		char tileIcon = lineElements.get(0).charAt(0);
		TileType tileType = TileType.valueOf(lineElements.get(1));
		
		if (lineElements.size() > 2)
		{
			FeatureType featureType = FeatureType.valueOf(lineElements.get(2));
			predefinedZoneBuilder.defineTile(tileIcon, tileType, featureType);
		} else {
			predefinedZoneBuilder.defineTile(tileIcon, tileType);
		}
	}

	private void interpretActorData(List<String> lineElements)
	{
		int row = Integer.valueOf(lineElements.get(0));
		int col = Integer.valueOf(lineElements.get(1));
		ActorType actorType = ActorType.valueOf(lineElements.get(2));
		
		predefinedZoneBuilder.defineActor(row, col, actorType);
	}

	private void interpretItemData(List<String> lineElements)
	{
		int row = Integer.valueOf(lineElements.get(0));
		int col = Integer.valueOf(lineElements.get(1));
		ItemType itemType = ItemType.valueOf(lineElements.get(2));
		
		if (lineElements.size() > 3)
		{
			int amount = Integer.valueOf(lineElements.get(3));
			predefinedZoneBuilder.defineItem(row, col, itemType, amount);
		} else {
			predefinedZoneBuilder.defineItem(row, col, itemType);
		}
	}

	private void interpretAttributeData(List<String> lineElements)
	{
		String key = lineElements.get(0);
		String value = lineElements.get(1);
		
		predefinedZoneBuilder.defineAttribute(key, value);
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
		PLAN, TILES, ACTORS, ITEMS, ATTRIBUTES;
	}

	@Override
	protected String getExtension()
	{
		return "zon";
	}

	@Override
	protected String getDataPath()
	{
		return ROOT_PATH + "data" + File.separator + "zones" + File.separator;
	}
}
