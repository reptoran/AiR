package main.data;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import main.entity.actor.Actor;
import main.entity.actor.ActorFactory;
import main.entity.feature.Feature;
import main.entity.feature.FeatureFactory;
import main.entity.item.Item;
import main.entity.item.ItemFactory;
import main.entity.save.EntityMap;
import main.entity.save.SaveHandler;
import main.entity.tile.Tile;
import main.entity.tile.TileFactory;
import main.entity.world.Overworld;
import main.entity.world.WorldTile;
import main.entity.world.WorldTileFactory;
import main.entity.zone.Zone;
import main.entity.zone.ZoneFactory;
import main.entity.zone.generator.LabyrinthGenerator;
import main.presentation.Logger;

public class DataSaveUtils
{
	public static final String VERSION = "0.6.0";
	
	private SaveHandler saveHandler;

	public DataSaveUtils(String playerName)
	{
		saveHandler = new SaveHandler(playerName);

		// saveHandler.deleteCacheDir();
		saveHandler.createCacheDir(); // TODO: this doesn't seem to work if the cache directory was just deleted (that is, if it existed on game startup), so
										// I've commented the delete out
	}

	public boolean loadSavedGame(Data data)
	{
		File saveFile = saveHandler.getSaveFile();

		if (saveFile == null)
			return false;

		try
		{
			loadGameData(data);
		} catch (ParseException e)
		{
			Logger.error("Data - " + e.getMessage());
		}

		saveFile.delete();
		return true;
	}

	public boolean isZoneCached(String newZoneId)
	{
		if (newZoneId == null)
			return false;
		
		return saveHandler.isZoneCached(newZoneId);
	}

	public void cacheZone(Zone zone)
	{
		cacheZone(zone, true);
	}
	
	public void cacheZone(Zone zone, boolean clearMappings)
	{
		if (clearMappings)
			EntityMap.clearMappings();	//caching the zone will put in all of its mappings, so we don't need anything in the map
		
		List<String> keys;

		String zoneId = zone.getUniqueId();
		saveHandler.createZoneCacheDir(zoneId);

		saveHandler.cacheZone(zone, zoneId); // this also adds keys to the save the actors and tiles on the zones, so if we save those in order next, they
												// should all be there.

		// save actors
		keys = EntityMap.getActorKeys();

		for (String key : keys)
		{
			Actor actor = EntityMap.getActor(key);
			saveHandler.cacheZoneActor(actor, zoneId);
		}

		// save tiles
		keys = EntityMap.getTileKeys();

		for (String key : keys)
		{
			Tile tile = EntityMap.getTile(key);
			saveHandler.cacheZoneTile(tile, zoneId);
		}

		// save features
		keys = EntityMap.getFeatureKeys();

		for (String key : keys)
		{
			Feature feature = EntityMap.getFeature(key);
			saveHandler.cacheZoneFeature(feature, zoneId);
		}

		// save items (should be stored from both the actors and the tiles)
		keys = EntityMap.getItemKeys();

		for (String key : keys)
		{
			Item item = EntityMap.getItem(key);
			saveHandler.cacheZoneItem(item, zoneId);
		}
		
		if (clearMappings)
			EntityMap.clearMappings();	//all the mappings have been used and recorded, so we can clear them

		// zip the individual files into a single save file
		try
		{
			saveHandler.zipZoneCache(zoneId);
		} catch (IOException e)
		{
			Logger.error("Could not compress cache directory for " + zoneId + "!");
		}

		saveHandler.deleteZoneCacheDir(zoneId);
	}

	public Zone uncacheZone(String zoneId)
	{
		return uncacheZone(zoneId, true);
	}

	public Zone uncacheZone(String zoneId, boolean clearMappings)
	{
		Zone uncachedZone = null;
		
		if (clearMappings)
			EntityMap.clearMappings();	//we don't need the mappings to just pull the data strings from the cache files, and the factories will add the mappings as we go
		
		try
		{
			saveHandler.unzipZoneCache(zoneId);
		} catch (IOException e)
		{
			Logger.error("Could not uncompress cache directory for " + zoneId + "!");
		}

		List<String> entityLines;
		
		// uncache items
		entityLines = saveHandler.uncacheZoneItems(zoneId);

		for (String saveString : entityLines)
		{
			ItemFactory.loadAndMapItemFromSaveString(saveString); // the factory sticks it in EntityMap, so we don't need to save it anywhere else for now
		}

		// uncache actors
		entityLines = saveHandler.uncacheZoneActors(zoneId);

		for (String saveString : entityLines)
		{
			ActorFactory.loadAndMapActorFromSaveString(saveString); // the factory sticks it in EntityMap, so we don't need to save it anywhere else for now
		}

		// uncache features
		entityLines = saveHandler.uncacheZoneFeatures(zoneId);

		for (String saveString : entityLines)
		{
			FeatureFactory.loadAndMapFeatureFromSaveString(saveString); // the factory sticks it in EntityMap
		}

		// uncache tiles
		entityLines = saveHandler.uncacheZoneTiles(zoneId);

		for (String saveString : entityLines)
		{
			TileFactory.loadAndMapTileFromSaveString(saveString); // the factory sticks it in EntityMap
		}

		// uncache zone
		entityLines = saveHandler.uncacheZones(zoneId);

		for (String saveString : entityLines) // likely unnecessary, since we'll only ever have one zone per file now, but it keeps the pattern
		{
			uncachedZone = ZoneFactory.getInstance().loadAndMapZoneFromSaveString(saveString); // the factory sticks it in EntityMap
		}
		
		if (clearMappings)
			EntityMap.clearMappings();	//all the mappings have been used and recorded, so we can clear them

		saveHandler.deleteZoneCacheDir(zoneId);
		return uncachedZone;
	}

	public void saveGameData(Overworld overworld, Zone currentZone, Actor player, boolean worldTravel)
	{
		saveHandler.createSaveDir();

		EntityMap.clearMappings();
		List<String> keys;

		// save world
		saveHandler.saveOverworld(overworld);

		// save world tiles
		keys = EntityMap.getWorldTileKeys();

		for (String key : keys)
		{
			WorldTile tile = EntityMap.getWorldTile(key);
			saveHandler.saveWorldTile(tile);
		}

		String currentZoneName = null;
		String currentZoneId = null;
		
		if (currentZone != null)
		{
			cacheZone(currentZone, false);
			saveHandler.copyCacheToSaveDir();
			currentZoneName = currentZone.getUniqueId();
			currentZoneId = EntityMap.getSimpleKey(currentZone.getUniqueId());
		} else
		{
			saveHandler.saveActor(player);	//required because the player is not in the actor list of any zone, since he's in overworld travel
		}

		// save game information
		saveHandler.saveGameDataElement(VERSION);
		saveHandler.saveGameDataElement(EntityMap.getSimpleKey(player.getUniqueId())); // player
		saveHandler.saveGameDataElement(Boolean.toString(worldTravel)); // whether or not the player is using world travel
		saveHandler.saveGameDataElement(currentZoneName); // current zone name
		saveHandler.saveGameDataElement(currentZoneId); // current zone ID
		saveHandler.saveGameDataElement(String.valueOf(ZoneFactory.getGeneratedMapCount())); //determines the number of the next zone to be generated
		saveHandler.saveGameDataElement(LabyrinthGenerator.saveState());
		saveHandler.saveGameDataElement(SpecialLevelManager.saveState());
		//TODO: consider saving the current random seed (and loading it later in the appropriate method)

		saveHandler.zipSaveDir();	// zip the individual files into a single save file
		saveHandler.deleteSaveDir();
		EntityMap.clearMappings();
	}

	public void loadGameData(Data data) throws ParseException
	{
		try
		{
			saveHandler.unzipSaveDir();
		} catch (IOException e)
		{
			System.out.println("Data - Error occured while uncompressing save directory!");
		}

		// load game information
		List<String> entityLines = saveHandler.loadGameDataElements();

		String saveGameVersion = entityLines.get(0);
		
		if (!VERSION.equalsIgnoreCase(saveGameVersion))
		{
			Logger.popup("Version mismatch; cannot load game.");
			saveHandler.zipSaveDir();
			saveHandler.deleteSaveDir();
			saveHandler.deleteCacheDir();
			System.exit(0);
		}
		
		String currentPlayerId = entityLines.get(1);
		String worldTravel = entityLines.get(2);
		String currentZoneName = entityLines.get(3);
		String currentZoneId = entityLines.get(4);
		ZoneFactory.setGeneratedMapCount(Integer.parseInt(entityLines.get(5)));
		LabyrinthGenerator.loadState(entityLines.get(6));
		SpecialLevelManager.loadState(entityLines.get(7));

		data.setWorldTravel(Boolean.valueOf(worldTravel));
		data.setCurrentZone(null);
		
		SpecialLevelManager.populateSpecialZonesForLevels(data.getPredefinedZones());
		
		EntityMap.clearMappings();

		// load world tiles
		entityLines = saveHandler.loadWorldTiles();

		for (String wtSaveString : entityLines)
		{
			WorldTileFactory.loadAndMapTileFromSaveString(wtSaveString); // the factory sticks it in EntityMap
		}

		// load world
		String saveString = saveHandler.loadOverworlds().get(0);
		Overworld world = new Overworld();
		world.loadFromText(saveString);
		data.setOverworld(world);
		
		if (!data.isWorldTravel())
		{
			uncacheZone(currentZoneName, false);
			data.setCurrentZone(EntityMap.getZone(currentZoneId));
		} else 
		{
			entityLines = saveHandler.loadActors();
			ActorFactory.loadAndMapActorFromSaveString(entityLines.get(0));		//assuming only one actor: the player
		}

		data.setPlayer(EntityMap.getActor(currentPlayerId));
		
		saveHandler.deleteSaveDir();
		EntityMap.clearMappings();
	}

	public void deleteCacheDir()
	{
		saveHandler.deleteCacheDir();
	}
}
