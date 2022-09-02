package main.data.event.environment;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import main.data.DataAccessor;
import main.data.event.InternalEvent;
import main.entity.actor.Actor;
import main.entity.tile.Tile;
import main.entity.tile.TileType;
import main.entity.zone.Zone;
import main.entity.zone.ZoneKey;

public class ZoneTransitionEvent extends AbstractEnvironmentEvent
{
	public ZoneTransitionEvent(Actor actor, boolean descending, EnvironmentEventQueue eventQueue)
	{
		this.actor = actor;
		this.values[0] = (descending ? 1 : 0);
		this.eventQueue = eventQueue;
	}
	
	@Override
	public List<InternalEvent> trigger()
	{
		List<InternalEvent> eventList = new ArrayList<InternalEvent>();
		
		Zone zone = DataAccessor.getInstance().getCurrentZone();
		Set<ZoneKey> zoneKeys = zone.getAllZoneKeys();
		
		Point stairCoords = null;
		
		//this only checks the first stairs, so if there are multiple down stairs (like after revealing the town hall basement), it won't always go where you want
		for (ZoneKey zoneKey : zoneKeys)
		{
			Point coords = zone.getLocationOfZoneKey(zoneKey);
			Tile tile = zone.getTile(coords);
			
			if (tile.getType() == TileType.STAIRS_DOWN && isDescending())
			{
				stairCoords = new Point(coords.x, coords.y);
				break;
			}
			
			if (tile.getType() == TileType.STAIRS_UP && !isDescending())
			{
				stairCoords = new Point(coords.x, coords.y);
				break;
			}
		}
		
		if (stairCoords == null)	//note that this means zone transition events only work if there is a stair pointing in that direction
			return eventList;
		
		int actorIndex = DataAccessor.getInstance().getIndexOfActor(actor);
		
		InternalEvent moveEvent = InternalEvent.localMoveInternalEvent(actorIndex, stairCoords.x, stairCoords.y, 0);
		InternalEvent zoneChangeEvent = InternalEvent.transitionZoneInternalEvent(actorIndex, isDescending());
		eventList.add(moveEvent);
		eventList.add(zoneChangeEvent);
		
		return eventList;
	}
	
	private boolean isDescending()
	{
		return (values[0] == 1);
	}

	@Override
	public EnvironmentEventType getType()
	{
		return EnvironmentEventType.ZONE_TRANSITION;
	}
}
