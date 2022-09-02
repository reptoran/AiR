package main.logic.AI;

import java.awt.Point;
import java.util.List;

import main.data.event.ActorCommand;
import main.entity.actor.Actor;
import main.entity.item.Inventory;
import main.entity.item.InventorySelectionKey;
import main.entity.item.Item;
import main.entity.item.ItemSource;
import main.entity.item.equipment.EquipmentSlot;
import main.entity.tile.Tile;
import main.entity.zone.Zone;
import main.logic.Direction;
import main.logic.RPGlib;

public class BlacksmithAI extends CoalignedAI
{
	private static final int MAX_ITEM_DISTANCE = 2;
	
	@Override
	public ActorCommand getNextCommand(Zone zone, Actor actor)
	{
		ActorCommand nextCommand = null;
		nextCommand = dropRepairedItem(actor);
		
		if (nextCommand != null)
			return nextCommand;
		
		nextCommand = repairDamagedItem(actor);
		
		if (nextCommand != null)
			return nextCommand;
		
		nextCommand = getDamagedItem(zone, actor);
		
		if (nextCommand != null)
			return nextCommand;
		
		nextCommand = moveTowardNearestDamagedItem(zone, actor);
		
		if (nextCommand != null)
			return nextCommand;
		
		return getRandomLegalMoveCommand(zone, actor);
	}

	private ActorCommand dropRepairedItem(Actor actor)							//if carrying an unequipped, undamaged item, drop it
	{
		List<EquipmentSlot> readiedItems = actor.getReadiedItems().getEquipmentSlots();
		
		for (int i = 0; i < readiedItems.size(); i++)
		{
			Item item = readiedItems.get(i).getItem();
			
			if (item == null)
				continue;
			
			if (item.getCurHp() == item.getMaxHp())
				return ActorCommand.drop(new InventorySelectionKey(ItemSource.READY, i));
		}
		
		Inventory storedItems = actor.getStoredItems();
		
		for (int i = 0; i < storedItems.size(); i++)
		{
			Item item = storedItems.get(i);
			
			if (item == null)
				continue;
			
			if (item.getCurHp() == item.getMaxHp())
				return ActorCommand.drop(new InventorySelectionKey(ItemSource.PACK, i));
		}
		
		return null;
	}

	private ActorCommand repairDamagedItem(Actor actor)							//if carrying a damaged item, repair it to full hp
	{
		List<EquipmentSlot> readiedItems = actor.getReadiedItems().getEquipmentSlots();
		
		for (int i = 0; i < readiedItems.size(); i++)
		{
			Item item = readiedItems.get(i).getItem();
			
			if (item == null)
				continue;
			
			if (item.getCurHp() < item.getMaxHp())
				return ActorCommand.repair(new InventorySelectionKey(ItemSource.READY, i), item.getMaxHp() - item.getCurHp());
		}
		
		Inventory storedItems = actor.getStoredItems();
		
		for (int i = 0; i < storedItems.size(); i++)
		{
			Item item = storedItems.get(i);
			
			if (item == null)
				continue;
			
			if (item.getCurHp() < item.getMaxHp())
				return ActorCommand.repair(new InventorySelectionKey(ItemSource.PACK, i), item.getMaxHp() - item.getCurHp());
		}
		
		return null;
	}

	private ActorCommand getDamagedItem(Zone zone, Actor actor)					//if there is a damaged item here, pick it up
	{
		Tile tile = zone.getTile(actor);
		Item item = tile.getItemHere();
		
		if (item == null)
			return null;
		
		if (item.getCurHp() == item.getMaxHp())
			return null;
		
		return ActorCommand.pickUp();
	}

	private ActorCommand moveTowardNearestDamagedItem(Zone zone, Actor actor)	//if there's a damaged item on a visible floor tile, move toward the nearest one
	{
		Point actorCoords = zone.getCoordsOfActor(actor);
		
		for (int radius = 1; radius <= MAX_ITEM_DISTANCE; radius++)
		{
			int min = -1 * radius;
			int max = radius;
			
			for (int i = min; i <= max; i++)
			{
				for (int j = min; j <= max; j++)
				{
					if (i > min && i < max && j > min && j < max)
						continue;
					
					Point targetCoords = RPGlib.addPoints(actorCoords, new Point(i, j));
					Tile tile = zone.getTile(targetCoords);
					
					if (tile == null)
						continue;
					
					if (tile.obstructsCoaligned())
						continue;
					
					if (tile.getActorHere() != null)
						continue;
					
					Item item = tile.getItemHere();
					
					if (item == null)
						continue;
					
					if (item.getCurHp() == item.getMaxHp())
						continue;
					
					ActorCommand nextMove = moveTowardPoint(zone, actor, targetCoords);
					
					if (Direction.fromString(nextMove.getArgument1()) == Direction.DIRNONE)
						return null;
					
					return nextMove;
				}
			}
		}
		
		return null;
	}
}
