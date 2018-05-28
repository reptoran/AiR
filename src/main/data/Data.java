package main.data;

import java.awt.Point;

import main.data.event.Event;
import main.entity.actor.Actor;
import main.entity.actor.ActorFactory;
import main.entity.actor.ActorType;
import main.entity.feature.Feature;
import main.entity.feature.FeatureFactory;
import main.entity.feature.FeatureType;
import main.entity.tile.Tile;
import main.entity.tile.TileFactory;
import main.entity.tile.TileType;
import main.entity.world.Overworld;
import main.entity.world.WorldTile;
import main.entity.zone.Zone;
import main.entity.zone.ZoneFactory;
import main.entity.zone.ZoneKey;
import main.logic.AI.AiType;
import main.presentation.Logger;

public class Data
{
	// entities actually in the game
	// private Map<Point, String> gameZones; //we're saving space by only storing the predefined zones; note that new Points must be used for both assigning and
	// accessing so that their hashes match

	protected Zone currentZone;
	private Overworld overworld;
	private Actor player;

	private DataSaveUtils dataSaveUtils;

	private String playerName;
	private ActorTurnQueue worldTravelQueue;

	private boolean worldTravel;

	public Data()
	{
		// gameZones = new HashMap<Point, String>(); //the string is the name of the field; it's also the file name we're saving to and loading from
		dataSaveUtils = null;

		playerName = null;
		worldTravelQueue = null;

		 worldTravel = true;
//		worldTravel = false;
	}

	public void begin(String newPlayerName)
	{
		playerName = newPlayerName;
		dataSaveUtils = new DataSaveUtils(playerName);
		worldTravelQueue = new ActorTurnQueue();

		if (!dataSaveUtils.loadSavedGame(this))
			newGame();

		worldTravelQueue.add(player);
	}

	public void newGame()
	{
		// create the overworld
		overworld = new Overworld();

		// generate some actors to play around with
		Actor playerActor = createNewActor(ActorType.NO_TYPE);
		playerActor.setName(playerName);
		playerActor.setIcon('@');
		playerActor.setColor(15);
		playerActor.setAI(AiType.HUMAN_CONTROLLED);
		updatePlayerWorldCoords(2, 2);
		player = playerActor;

//		enterLocalZoneFromWorldTravel();
	}

	public Zone getZoneAtLocation(int x, int y)
	{
		WorldTile tile = overworld.getTile(x, y);
		Zone toRet = ZoneFactory.getInstance().generateNewZone(tile);

		// TODO: if there's a saved field at these coordinates, update it based on the turn cycles that have gone by; otherwise return a random map

		return toRet;
	}

	public Zone getZoneAtLocation(Point coords)
	{
		return getZoneAtLocation(coords.x, coords.y);
	}

	public Actor getPlayer()
	{
		return player;
	}

	public Tile getGenericTile(TileType tileType)
	{
		return TileFactory.generateNewTile(tileType);
	}

	public Feature getGenericFeature(FeatureType featureType)
	{
		return FeatureFactory.generateNewFeature(featureType);
	}

	public Actor getGenericActor(ActorType actorType)
	{
		return ActorFactory.generateNewActor(actorType);
	}

	public ActorTurnQueue getActorQueue()
	{
		if (isWorldTravel())
			return worldTravelQueue;

		return currentZone.getActorQueue();
	}

	public int getActorIndex(Actor actor)
	{
		if (worldTravel)
			return 0;

		return currentZone.getActorIndex(actor);
	}

	public Actor getActor(int actorIndex)
	{
		if (worldTravel)
			return player;

		return currentZone.getActor(actorIndex);
	}

	public Zone getCurrentZone()
	{
		return currentZone;
	}

	public Overworld getOverworld()
	{
		return overworld;
	}

	public boolean isWorldTravel()
	{
		return worldTravel;
	}

	public void setOverworld(Overworld world)
	{
		this.overworld = world;
	}

	public void setWorldTravel(boolean worldTravel)
	{
		this.worldTravel = worldTravel;
	}

	public void setCurrentZone(Zone zone)
	{
		this.currentZone = zone;
	}

	public void setPlayer(Actor player)
	{
		this.player = player;
	}

	private void updateZone(ZoneKey newZoneKey, Actor actor, boolean goingDown)
	{
		currentZone.removeActor(actor);
		Zone originZone = currentZone;
		
		String newZoneId = "Z" + newZoneKey.getId();
		
		if (dataSaveUtils.isZoneCached(newZoneId))
		{
			currentZone = dataSaveUtils.uncacheZone(newZoneId);
		} else {
			currentZone = ZoneFactory.getInstance().generateNewZone(newZoneKey, goingDown, currentZone);
		}
		
		//because the ZoneKey in the origin zone may be updated by generating a new level, it needs to be cached after level generation, not before
		if (originZone.shouldPersist())
		{
			dataSaveUtils.cacheZone(originZone);
		}
		
		currentZone.addActor(actor, newZoneKey.getEntryPoint());
	}

	// events are assumed to be checked and sanitized by the logic layer, so just
	// act dumbly on whatever comes through
	public void receiveEvent(Event event)
	{
		if (event == null)
		{
			System.out.println("Data - NULL event received.");
			return;
		}

		Actor actor = getActor(event.getFlag(0));
		actor.increaseTicksLeftBeforeActing(event.getActionCost());

		switch (event.getEventType())
		{
		case LOCAL_MOVE:
			updateActorLocalCoords(actor, event.getFlag(1), event.getFlag(2));
			break;

		case WORLD_MOVE:
			updatePlayerWorldCoords(event.getFlag(1), event.getFlag(2));
			break;
			
		case ZONE_TRANSITION:
			Point playerLocation = currentZone.getCoordsOfActor(actor);
			ZoneKey zoneKey = currentZone.getZoneKey(playerLocation);
			updateZone(zoneKey, actor, (event.getFlag(1) == 1));
			break;

		case ENTER_LOCAL:
			if (!worldTravel)
				break;
			enterLocalZoneFromWorldTravel();
			break;

		case ENTER_WORLD:
			worldTravel = true;
			//TODO: cache the currentZone if it's permanent, remove the player as an actor, etc.
			break;

		case SAVE_GAME:
			dataSaveUtils.saveGameData(overworld, currentZone, player, worldTravel);
			break;

		case EXIT_GAME:
			dataSaveUtils.deleteCacheDir();
			System.exit(0);
			break;
		}
	}

	private void enterLocalZoneFromWorldTravel()
	{
		Actor actor = player;
		currentZone = getZoneAtLocation(overworld.getPlayerCoords());
		currentZone.addActor(actor, new Point(20, 80));
		worldTravel = false;
	}

	private Actor createNewActor(ActorType actorType)
	{
		return getGenericActor(actorType);
	}

	private void updateActorLocalCoords(Actor actor, int x, int y)
	{
		currentZone.updateActorCoords(actor, new Point(x, y));
	}

	private void addActorToZone(Actor actor, int x, int y, Zone zone)
	{
		zone.addActor(actor, new Point(x, y));
	}

	private void updatePlayerWorldCoords(int x, int y)
	{
		Logger.debug("Data.java - Updating world coordinates to (" + x + ", " + y + ").");

		currentZone = null;
		overworld.setPlayerCoords(new Point(x, y));
	}
}