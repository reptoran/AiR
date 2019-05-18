package main.entity.zone;

import java.awt.Point;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import main.data.event.environment.EnvironmentEvent;
import main.data.event.environment.EnvironmentEventQueue;
import main.data.event.environment.SaveableEnvironmentEvent;
import main.entity.EntityType;
import main.entity.SaveableEntity;
import main.entity.actor.Actor;
import main.entity.item.Item;
import main.entity.save.EntityMap;
import main.entity.save.SaveStringBuilder;
import main.entity.save.SaveToken;
import main.entity.save.SaveTokenTag;
import main.entity.tile.Tile;
import main.presentation.Logger;

public class Zone extends SaveableEntity
{
	private int height;
	private int width;
	private Tile[][] tiles;
	private String name; // this is what the file holding a saved instance of this is called

	private List<Actor> actors;
	private EnvironmentEventQueue eventQueue;

	private Map<Point, Actor> actorAtPoint;
	private Map<Actor, Point> pointOfActor;
	private Map<Point, ZoneKey> zoneEntryKeys; 

	boolean shouldPersist;
	boolean canEnterWorld;
	
	private ZoneType type;

	private int depth;
	
	private int lastTurn; // this is the turn counter that the map was left; when loading it, simulate the missed turns
	
	public Zone(ZoneType type, String name, int height, int width, int depth, boolean shouldPersist, boolean canEnterWorld)
	{
		this.height = height;
		this.width = width;
		this.depth = depth;

		tiles = new Tile[height][width];

		this.name = name;
		this.shouldPersist = shouldPersist;
		this.canEnterWorld = canEnterWorld;
		lastTurn = -1; // this means it hasn't been visited yet

		actors = new ArrayList<Actor>();
		eventQueue = new EnvironmentEventQueue();

		actorAtPoint = new HashMap<Point, Actor>();
		pointOfActor = new ActorMap();
		zoneEntryKeys = new HashMap<Point, ZoneKey>();
		
		this.type = type;
	}

	public Zone(ZoneType type, String name, int height, int width, boolean shouldPersist, boolean canEnterWorld)
	{
		this(type, name, height, width, 0, shouldPersist, canEnterWorld);
	}

	public Zone()
	{
		this(ZoneType.NO_TYPE, "Unnamed Zone", 40, 160, 0);
	}

	public Zone(ZoneType type, String name, int height, int width, int depth)
	{
		this(type, name, height, width, depth, false, false);
	}

	public void setTurn(int turn)
	{
		lastTurn = turn;
	}

	public String getName()
	{
		return name;
	}

	public ZoneType getType()
	{
		return type;
	}

	public int getHeight()
	{
		return height;
	}

	public int getWidth()
	{
		return width;
	}
	
	public int getDepth()
	{
		return depth;
	}
	
	public void setDepth(int depth)
	{
		this.depth = depth;
	}

	public Tile getTile(int row, int column)
	{
		if (row < 0 || row >= height || column < 0 || column >= width)
		{
			return null;
		}

		return tiles[row][column];
	}

	public Tile getTile(Point coords)
	{
		return getTile(coords.x, coords.y);
	}

	public Tile getTile(Actor actor)
	{
		Point point = getCoordsOfActor(actor);
		return getTile(point);
	}

	public void setTile(int row, int column, Tile tile)
	{
		tiles[row][column] = tile.clone();
	}

	public void setTile(Point coords, Tile tile)
	{
		setTile(coords.x, coords.y, tile);
	}
	
	public void resetVisible()
	{
		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				tiles[i][j].setVisible(false);
			}
		}
	}
	
	public boolean containsPoint(Point point)
	{
		if (point == null)
			return false;
		
		if (point.x < 0 || point.y < 0 || point.x >= height || point.y >= width)
			return false;
		
		return true;
	}
	
	public void setItem(Item item, Point coords)
	{
		if (coords == null)
			throw new IllegalArgumentException("Cannot add item with null coordinates.");
		
		getTile(coords).setItemHere(item);
	}

	//TODO: verify this and the remove method both work still after the event overhaul
	public int addActor(Actor actor, Point coords)
	{
		if (actor == null)
			throw new IllegalArgumentException("Cannot add null actor.");
		
		Logger.info("Attempting to add actor [" + actor.getName() + "] to zone at [" + coords + "]");
		
		if (coords == null)
			throw new IllegalArgumentException("Cannot add actor with null coordinates.");
		
		if (coords.x < 0 || coords.y < 0 || coords.x >= height || coords.y >= width)
			throw new IllegalArgumentException("Cannot add actor outside the bounds of the zone; attempted coordinates were: " + coords);
			
		if (actors.contains(actor))
			throw new IllegalArgumentException("Actor is already present in the zone.");

		actors.add(actor);
		eventQueue.add(actor);
		actorAtPoint.put(new Point(coords.x, coords.y), actor);
		pointOfActor.put(actor, new Point(coords.x, coords.y));
		getTile(coords).setActorHere(actor);

		return actors.size() - 1; // the actor index
	}
	
	public void removeActor(Actor actor)
	{
		if (actor == null || !actors.contains(actor))
			return;
		
		actors.remove(actor);
		eventQueue.remove(actor);
		
		Point actorLocation = pointOfActor.get(actor);
		pointOfActor.remove(actor);
		actorAtPoint.remove(actorLocation);
		getTile(actorLocation).setActorHere(null);
	}

	public Actor getActor(int index)
	{
		if (index < 0)
			return null;
		
		return actors.get(index);
	}

	public int getActorIndex(Actor actor)
	{
		int index = -1;
		boolean matchFound = false;

		for (Actor checkedActor : actors)
		{
			Logger.info("Zone - Comparing " + actor + " with " + checkedActor);
			
			index++;

			if (checkedActor == actor)
			{ // these should be the same object, so no need for equals()
				matchFound = true;
				Logger.info("Zone - Match found: " + actor + " matches " + checkedActor);
				break;
			}
		}
		
		if (!matchFound)
			Logger.error("Zone - No actor match found!");
		
		Logger.debug("Zone - Getting actor index for actor " + actor.getName() + ", returning index of [" + index + "].  Total actors: " + actors.size());

		return index;
	}

	public int getTotalActors()
	{
		return actors.size();
	}

	public EnvironmentEventQueue getEventQueue()
	{
		return eventQueue;
	}

	public List<Actor> getActors()
	{
		return actors;
	}

	public Point getCoordsOfActor(Actor actor)
	{
		if (actor == null)
		{
			return null;
		}

		return pointOfActor.get(actor);
	}

	public void updateActorCoords(Actor actor, Point newCoords)
	{
		if (actor == null)
		{
			return;
		}

		Point oldCoords = null;

		if (pointOfActor.containsKey(actor))
		{
			oldCoords = pointOfActor.remove(actor);

			if (isInZoneBoundaries(oldCoords) && getTile(oldCoords).getActorHere() == actor)
			{
				getTile(oldCoords).setActorHere(null);
			}
		}

		pointOfActor.put(actor, new Point(newCoords.x, newCoords.y));

		if (actorAtPoint.containsKey(oldCoords))
		{
			actorAtPoint.remove(oldCoords);
		}

		if (newCoords != null)
		{
			actorAtPoint.put(newCoords, actor);
			getTile(newCoords).setActorHere(actor);
		}
	}

	private boolean isInZoneBoundaries(Point point)
	{
		if (point.x >= 0 && point.y >= 0 && point.x < height && point.y < width)
			return true;

		return false;
	}

	public Actor getActorAtCoords(Point coords)
	{
		if (coords == null)
		{
			return null;
		}

		return actorAtPoint.get(coords);
	}

	public boolean shouldPersist()
	{
		return shouldPersist;
	}

	public boolean canEnterWorld()
	{
		return canEnterWorld;
	}
	
	public ZoneKey getZoneKey(Point point)
	{
		return zoneEntryKeys.get(point);
	}
	
	public void addZoneKey(Point point, ZoneKey zoneKey)
	{
		if (zoneEntryKeys.containsKey(point))
			zoneEntryKeys.remove(point);
		
		zoneEntryKeys.put(point, zoneKey);
	}
	
	public Point getLocationOfZoneKey(ZoneKey zoneKey)
	{
		for (Entry<Point, ZoneKey> entry : zoneEntryKeys.entrySet()) {
	        if (Objects.equals(zoneKey, entry.getValue())) {
	            return entry.getKey();
	        }
	    }
		
		return null;
	}

	@Override
	public String saveAsText()
	{
		SaveStringBuilder ssb = new SaveStringBuilder(EntityType.ZONE);
		List<String> tileList = new ArrayList<String>();
		List<String> actorList = new ArrayList<String>();
		List<String> eventList = new ArrayList<String>();
		List<String> zoneKeyList = new ArrayList<String>();

		String zoneUid = getUniqueId();

		if (EntityMap.getZone(zoneUid) == null)
			zoneUid = EntityMap.put(zoneUid, this);
		else
			zoneUid = EntityMap.getSimpleKey(zoneUid);

		// will be saved with every zone
		ssb.addToken(new SaveToken(SaveTokenTag.Z_UID, zoneUid));
		ssb.addToken(new SaveToken(SaveTokenTag.Z_NAM, name));
		ssb.addToken(new SaveToken(SaveTokenTag.Z_TYP, type.name()));
		ssb.addToken(new SaveToken(SaveTokenTag.Z_PER, String.valueOf(shouldPersist)));
		ssb.addToken(new SaveToken(SaveTokenTag.Z_CEW, String.valueOf(canEnterWorld)));
		ssb.addToken(new SaveToken(SaveTokenTag.Z_TRN, String.valueOf(lastTurn)));
		ssb.addToken(new SaveToken(SaveTokenTag.Z_HGT, String.valueOf(height)));
		ssb.addToken(new SaveToken(SaveTokenTag.Z_WID, String.valueOf(width)));
		ssb.addToken(new SaveToken(SaveTokenTag.Z_DEP, String.valueOf(depth)));

		for (Actor actor : actors)
		{
			String actorUid = actor.getUniqueId();

			if (EntityMap.getActor(actorUid) == null)
				actorUid = EntityMap.put(actorUid, actor);
			else
				actorUid = EntityMap.getSimpleKey(actorUid);

			actorList.add(actorUid.substring(1));
		}

		ssb.addToken(new SaveToken(SaveTokenTag.Z_ACT, actorList));
		
		for (EnvironmentEvent event : eventQueue.getQueueContents())
		{
			SaveableEnvironmentEvent saveEvent = event.asSaveableEvent();
			String eventUid = saveEvent.getUniqueId();

			if (EntityMap.getEvent(eventUid) == null)
				eventUid = EntityMap.put(eventUid, saveEvent);
			else
				eventUid = EntityMap.getSimpleKey(eventUid);

			eventList.add(eventUid.substring(1));
		}
		
		ssb.addToken(new SaveToken(SaveTokenTag.Z_EVT, eventList));
		
		Set<Point> zoneEntryPoints = zoneEntryKeys.keySet();
		for (Point point : zoneEntryPoints)
		{
			ZoneKey zoneKey = zoneEntryKeys.get(point);
			zoneKeyList.add(convertZoneEntryKeyValueToString(point, zoneKey));
		}
		
		ssb.addToken(new SaveToken(SaveTokenTag.Z_ZEK, zoneKeyList));

		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				String tileUid = tiles[i][j].getUniqueId();

				if (EntityMap.getTile(tileUid) == null)
					tileUid = EntityMap.put(tileUid, tiles[i][j]);
				else
					tileUid = EntityMap.getSimpleKey(tileUid);

				tileList.add(tileUid.substring(1));
			}
		}

		ssb.addToken(new SaveToken(SaveTokenTag.Z_TIL, tileList));

		return ssb.getSaveString();
	}

	private String convertZoneEntryKeyValueToString(Point point, ZoneKey zoneKey)
	{
		return String.valueOf(point.x) + "." + String.valueOf(point.y) + "|" + zoneKey.toString();
	}

	@Override
	public String loadFromText(String text) throws ParseException
	{
		SaveStringBuilder ssb = new SaveStringBuilder(EntityType.ZONE, text);

		String toRet = getContentsForTag(ssb, SaveTokenTag.Z_UID); // assumed to be defined

		setMember(ssb, SaveTokenTag.Z_TYP);
		setMember(ssb, SaveTokenTag.Z_NAM);
		setMember(ssb, SaveTokenTag.Z_PER);
		setMember(ssb, SaveTokenTag.Z_CEW);
		setMember(ssb, SaveTokenTag.Z_TRN);
		setMember(ssb, SaveTokenTag.Z_HGT);
		setMember(ssb, SaveTokenTag.Z_WID);
		setMember(ssb, SaveTokenTag.Z_DEP);
		setMember(ssb, SaveTokenTag.Z_ACT);
		setMember(ssb, SaveTokenTag.Z_EVT);
		setMember(ssb, SaveTokenTag.Z_ZEK);
		setMember(ssb, SaveTokenTag.Z_TIL);

		return toRet;
	}

	@Override
	protected void setMember(SaveStringBuilder ssb, SaveTokenTag saveTokenTag)
	{
		String contents = getContentsForTag(ssb, saveTokenTag);
		SaveToken saveToken = null;
		List<String> strVals = null;
		String referenceKey = "";

		if (contents.equals(""))
			return;

		switch (saveTokenTag)
		{
		case Z_NAM:
			saveToken = ssb.getToken(saveTokenTag);
			this.name = saveToken.getContents();
			break;
			
		case Z_TYP:
			saveToken = ssb.getToken(saveTokenTag);
			String typeString = saveToken.getContents();
			this.type = ZoneType.valueOf(typeString);
			break;

		case Z_PER:
			saveToken = ssb.getToken(saveTokenTag);
			this.shouldPersist = Boolean.parseBoolean(saveToken.getContents());
			break;

		case Z_CEW:
			saveToken = ssb.getToken(saveTokenTag);
			this.canEnterWorld = Boolean.parseBoolean(saveToken.getContents());
			break;

		case Z_TRN:
			saveToken = ssb.getToken(saveTokenTag);
			this.lastTurn = Integer.parseInt(saveToken.getContents());
			break;

		case Z_HGT:
			saveToken = ssb.getToken(saveTokenTag);
			this.height = Integer.parseInt(saveToken.getContents());
			break;

		case Z_WID:
			saveToken = ssb.getToken(saveTokenTag);
			this.width = Integer.parseInt(saveToken.getContents());
			break;

		case Z_DEP:
			saveToken = ssb.getToken(saveTokenTag);
			this.depth = Integer.parseInt(saveToken.getContents());
			break;

		case Z_ACT:
			saveToken = ssb.getToken(saveTokenTag);
			strVals = saveToken.getContentSet();
			actors.clear();
			for (String val : strVals)
			{
				referenceKey = "A" + val;
				Actor actor = EntityMap.getActor(referenceKey);
				actors.add(actor);
			}
			break;

		case Z_EVT:
			saveToken = ssb.getToken(saveTokenTag);
			strVals = saveToken.getContentSet();
			eventQueue = null;
			for (String val : strVals)
			{
				referenceKey = "E" + val;
				EnvironmentEvent event = EntityMap.getEvent(referenceKey);
				
				if (eventQueue == null)
					eventQueue = event.getEventQueue();
				
				eventQueue.add(event);
				Logger.debug("Zone - Loading events into queue, total queue size is " + eventQueue.size());
			}
			break;

		case Z_ZEK:
			saveToken = ssb.getToken(saveTokenTag);
			strVals = saveToken.getContentSet();
			zoneEntryKeys.clear();
			for (String val : strVals)
			{
				int splitIndex = val.indexOf('|');
				String keyString = val.substring(0, splitIndex);
				String valueString = val.substring(splitIndex + 1);
				
				Point key = new Point(-1, -1);
				int pointSplitIndex = keyString.indexOf('.');
				key.x = Integer.parseInt(keyString.substring(0, pointSplitIndex));
				key.y = Integer.parseInt(keyString.substring(pointSplitIndex + 1));
				
				ZoneKey value = new ZoneKey(valueString);
				
				zoneEntryKeys.put(key, value);	//TODO: this may not be working
			}
			break;

		case Z_TIL:
			saveToken = ssb.getToken(saveTokenTag);
			strVals = saveToken.getContentSet();
			for (int i = 0; i < height; i++)
			{
				for (int j = 0; j < width; j++)
				{
					referenceKey = "T" + strVals.remove(0);
					Tile tile = EntityMap.getTile(referenceKey).clone();
					tiles[i][j] = tile;

					Actor actor = tile.getActorHere();

					if (actor != null)
					{
						updateActorCoords(actor, new Point(i, j));
					}
				}
			}
			break;

		//$CASES-OMITTED$
		default:
			throw new IllegalArgumentException("Zone - Unhandled token: " + saveTokenTag.toString());
		}

		return;
	}

	@Override
	public String getUniqueId()
	{
		return "Z" + name;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
			return false;
		if (!(obj instanceof Zone))
			return false;
		if (!super.equals(obj))
			return false;

		Zone zone = (Zone) obj;

		// as in the hashing method, we're not checking the tiles here; hopefully the name should be sufficient (not checking zone entry keys either)
		if (!type.equals(zone.type) || !name.equals(zone.name) || height != zone.height || width != zone.width || depth != zone.depth || lastTurn != zone.lastTurn || canEnterWorld != zone.canEnterWorld || shouldPersist != zone.shouldPersist)
			return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = 7;

		hash = 31 * hash + name.hashCode();
		hash = 31 * hash + height;
		hash = 31 * hash + width;
		hash = 31 * hash + depth;
		hash = 31 * hash + lastTurn;

		// TODO: I *really* don't want to grab the hashes of all 6400 tiles, so figure out another way to force uniqueness.
		// Honestly, it might just be the name. Each randomly generated map should get a generation number ("RANDMAP6713"), and
		// that number could be saved in the overworld object or even just a generic "worldData.dat" file along with other
		// game-specific values.

		return hash;
	}

	@Override
	public Zone clone()
	{
		throw new UnsupportedOperationException(); // a zone should never have to be cloned
	}

	private class ActorMap implements Map<Actor, Point>
	{
		List<Actor> keys = new ArrayList<Actor>();
		List<Point> values = new ArrayList<Point>();

		@Override
		public void clear()
		{
			keys.clear();
			values.clear();
		}

		@Override
		public boolean containsKey(Object key)
		{
			return keys.contains(key);
		}

		@Override
		public boolean containsValue(Object value)
		{
			return values.contains(value);
		}

		@Override
		public Set<java.util.Map.Entry<Actor, Point>> entrySet()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public Point get(Object key)
		{
			int index = keys.indexOf(key);

			if (index == -1)
				return null;

			return values.get(index);
		}

		@Override
		public boolean isEmpty()
		{
			return keys.isEmpty();
		}

		@Override
		public Set<Actor> keySet()
		{
			return new HashSet<Actor>(keys);
		}

		@Override
		public Point put(Actor key, Point value)
		{
			if (!keys.contains(key))
			{
				keys.add(key);
				values.add(value);
				return value;
			}

			int index = keys.indexOf(key);
			return values.set(index, value);
		}

		@Override
		public void putAll(Map<? extends Actor, ? extends Point> m)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public Point remove(Object key)
		{
			if (!keys.contains(key))
			{
				return null;
			}

			int index = keys.indexOf(key);
			keys.remove(index);
			return values.remove(index);
		}

		@Override
		public int size()
		{
			return keys.size();
		}

		@Override
		public Collection<Point> values()
		{
			return values;
		}
	}
}
