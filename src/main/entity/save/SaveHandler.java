package main.entity.save;

import java.io.File;
import java.io.IOException;
import java.util.List;

import main.data.file.FileHandler;
import main.entity.actor.Actor;
import main.entity.feature.Feature;
import main.entity.tile.Tile;
import main.entity.world.Overworld;
import main.entity.world.WorldTile;
import main.entity.zone.Zone;
import main.presentation.Logger;

public class SaveHandler extends FileHandler
{
	private static final String CACHE_DIR_NAME = "cache";

	private static final String OVERWORLD_PATH = "worlds.dat";
	private static final String WORLD_TILE_PATH = "wTiles.dat";
	private static final String ZONE_PATH = "zones.dat";
	private static final String TILE_PATH = "tiles.dat";
	private static final String FEATURE_PATH = "features.dat";
	private static final String ACTOR_PATH = "actors.dat";
	private static final String GAMEDATA_PATH = "game.dat";

	private String savePath = ROOT_PATH + "save" + File.separator;
	private String cachePath = savePath + CACHE_DIR_NAME;
	private String playerPath;
	private String playerName;

	public SaveHandler(String playerName)
	{
		this.playerPath = savePath + playerName;
		this.playerName = playerName;
	}

	public File getSaveFile()
	{
		File saveFile = new File(playerPath + ".sav");
		if (saveFile.exists())
			return saveFile;

		return null;
	}

	public void createCacheDir()
	{
		createSaveDirectory(CACHE_DIR_NAME);
	}

	public void createZoneCacheDir(String zoneId)
	{
		createSaveDirectory(CACHE_DIR_NAME + File.separator + zoneId);
	}

	public void createSaveDir()
	{
		createSaveDirectory(playerName);
	}

	public void deleteCacheDir()
	{
		deleteSaveDirectory(CACHE_DIR_NAME);
	}

	public void deleteZoneCacheDir(String zoneId)
	{
		deleteSaveDirectory(CACHE_DIR_NAME + File.separator + zoneId);
	}

	public void deleteSaveDir()
	{
		deleteSaveDirectory(playerName);
	}

	private void createSaveDirectory(String directory)
	{
		createDirectory(savePath + directory);
	}

	private void deleteSaveDirectory(String directory)
	{
		deleteDirectory(savePath + directory);
	}

	public void copyCacheToSaveDir()
	{
		File cacheFolder = new File(cachePath);
		for (File file : cacheFolder.listFiles())
		{
			copyFileAndCatchException(file, playerPath);
		}
	}

	private void copyCacheableFilesToCacheDir()
	{
		File saveFolder = new File(playerPath);
		for (File file : saveFolder.listFiles())
		{
			String extension = getFileExtension(file);

			if (canCacheExtension(extension))
				copyFileAndCatchException(file, cachePath);
		}
	}

	private boolean canCacheExtension(String extension)
	{
		return "zn".equals(extension);
	}
	
	public boolean isZoneCached(String zoneId)
	{
		File cacheFile = new File(cachePath + File.separator + zoneId + ".zn");
		if (cacheFile.exists())
			return true;

		return false;
	}

	public void zipZoneCache(String zoneId) throws IOException
	{
		zipDirectory(cachePath + File.separator + zoneId, ".zn");
	}

	public void zipSaveDir()
	{
		try
		{
			zipDirectory(playerPath, ".sav");
		} catch (IOException e)
		{
			Logger.error("Could not compress save directory!");
		}
	}

	public void unzipZoneCache(String zoneId) throws IOException
	{
		unzipSaveDir(cachePath + File.separator + zoneId, ".zn");
	}

	public void unzipSaveDir() throws IOException
	{
		unzipSaveDir(playerPath, ".sav");
		copyCacheableFilesToCacheDir();
	}

	public List<String> loadGameDataElements()
	{
		return loadFile(playerPath + File.separator + GAMEDATA_PATH);
	}

	public List<String> loadActors()
	{
		return loadFile(playerPath + File.separator + ACTOR_PATH);
	}

	public List<String> loadFeatures()
	{
		return loadFile(playerPath + File.separator + FEATURE_PATH);
	}

	public List<String> loadTiles()
	{
		return loadFile(playerPath + File.separator + TILE_PATH);
	}

	public List<String> loadZones()
	{
		return loadFile(playerPath + File.separator + ZONE_PATH);
	}

	public List<String> loadWorldTiles()
	{
		return loadFile(playerPath + File.separator + WORLD_TILE_PATH);
	}

	public List<String> loadOverworlds()
	{
		return loadFile(playerPath + File.separator + OVERWORLD_PATH);
	}

	public boolean saveGameDataElement(String element)
	{
		return writeLine(playerPath + File.separator + GAMEDATA_PATH, element);
	}

	public boolean saveActor(Actor actor)
	{
		if (EntityMap.getActor(actor.getUniqueId()) == null)
			EntityMap.put(actor.getUniqueId(), actor);
		return writeLine(playerPath + File.separator + ACTOR_PATH, actor.saveAsText());
	}

	public boolean saveFeature(Feature feature)
	{
		if (EntityMap.getFeature(feature.getUniqueId()) == null)
			EntityMap.put(feature.getUniqueId(), feature);
		return writeLine(playerPath + File.separator + FEATURE_PATH, feature.saveAsText());
	}

	public boolean saveTile(Tile tile)
	{
		if (EntityMap.getTile(tile.getUniqueId()) == null)
			EntityMap.put(tile.getUniqueId(), tile);
		return writeLine(playerPath + File.separator + TILE_PATH, tile.saveAsText());
	}

	public boolean saveZone(Zone zone)
	{
		if (EntityMap.getZone(zone.getUniqueId()) == null)
			EntityMap.put(zone.getUniqueId(), zone);
		return writeLine(playerPath + File.separator + ZONE_PATH, zone.saveAsText());
	}

	public boolean saveWorldTile(WorldTile tile)
	{
		if (EntityMap.getWorldTile(tile.getUniqueId()) == null)
			EntityMap.put(tile.getUniqueId(), tile);
		return writeLine(playerPath + File.separator + WORLD_TILE_PATH, tile.saveAsText());
	}

	public boolean saveOverworld(Overworld overworld)
	{
		if (EntityMap.getOverworld(overworld.getUniqueId()) == null)
			EntityMap.put(overworld.getUniqueId(), overworld);
		return writeLine(playerPath + File.separator + OVERWORLD_PATH, overworld.saveAsText());
	}

	public boolean cacheZone(Zone zone, String zoneId)
	{
		if (EntityMap.getZone(zone.getUniqueId()) == null)
			EntityMap.put(zone.getUniqueId(), zone);
		return writeLine(cachePath + File.separator + zoneId + File.separator + ZONE_PATH, zone.saveAsText());
	}

	public boolean cacheZoneActor(Actor actor, String zoneId)
	{
		if (EntityMap.getActor(actor.getUniqueId()) == null)
			EntityMap.put(actor.getUniqueId(), actor);
		return writeLine(cachePath + File.separator + zoneId + File.separator + ACTOR_PATH, actor.saveAsText());
	}

	public boolean cacheZoneFeature(Feature feature, String zoneId)
	{
		if (EntityMap.getFeature(feature.getUniqueId()) == null)
			EntityMap.put(feature.getUniqueId(), feature);
		return writeLine(cachePath + File.separator + zoneId + File.separator + FEATURE_PATH, feature.saveAsText());
	}

	public boolean cacheZoneTile(Tile tile, String zoneId)
	{
		if (EntityMap.getTile(tile.getUniqueId()) == null)
			EntityMap.put(tile.getUniqueId(), tile);
		return writeLine(cachePath + File.separator + zoneId + File.separator + TILE_PATH, tile.saveAsText());
	}

	public List<String> uncacheZones(String zoneId)
	{
		return loadFile(cachePath + File.separator + zoneId + File.separator + ZONE_PATH);
	}

	public List<String> uncacheZoneActors(String zoneId)
	{
		return loadFile(cachePath + File.separator + zoneId + File.separator + ACTOR_PATH);
	}

	public List<String> uncacheZoneFeatures(String zoneId)
	{
		return loadFile(cachePath + File.separator + zoneId + File.separator + FEATURE_PATH);
	}

	public List<String> uncacheZoneTiles(String zoneId)
	{
		return loadFile(cachePath + File.separator + zoneId + File.separator + TILE_PATH);
	}
}
