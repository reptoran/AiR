package main.logic.AI;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import main.data.event.ActorCommand;
import main.data.event.GuiCommandType;
import main.entity.actor.Actor;
import main.entity.actor.ActorTraitType;
import main.entity.tile.Tile;
import main.entity.tile.TileType;
import main.entity.zone.Zone;
import main.logic.Direction;
import main.logic.RPGlib;
import main.presentation.message.MessageBuffer;

public class RepeatLastMoveAi extends ActorAI
{
	private List<AiType> enemyAiTypes = new ArrayList<AiType>();
	private Direction directionToRepeat = null;
	private boolean isFirstMove = true;
	private int actorLastHp = -1;
	
	public RepeatLastMoveAi()
	{
		for (AiType aiType : AiType.values())
			enemyAiTypes.add(aiType);
		
		enemyAiTypes.remove(AiType.COALIGNED);
		enemyAiTypes.remove(AiType.FROZEN_CA);
		enemyAiTypes.remove(AiType.PHYSICIAN);
		enemyAiTypes.remove(AiType.REPEAT_LAST_MOVE);
		enemyAiTypes.remove(AiType.RAND_MOVE);
	}
	
	public void initializeNewRepeat(Direction direction)
	{
		directionToRepeat = direction;
		actorLastHp = -1;
		isFirstMove = true;
	}
	
	@Override	//TODO: make this smarter: if not resting, check possible directions.  if there's only one that wasn't where you came from, move there.  if there's more than one (room, T intersection), stop
				//		this will need to still allow to run across rooms, of course; it should just stop at doorways
	public ActorCommand getNextCommand(Zone zone, Actor actor)
	{
		//if the actor doesn't regenerate hitpoints, don't let them rest
		if (isResting() && !actor.hasTrait(ActorTraitType.HP_REGEN))
		{
			MessageBuffer.addMessage("You don't have the medical skills to recover from resting.");
			return new ActorCommand(GuiCommandType.DEACTIVATE_REPEAT_AI);
		}
		
		setActorTargets(zone, actor);
		
		//enemy is visible
		if (nearestEnemy != null && visibleActors.contains(nearestEnemy))
			return new ActorCommand(GuiCommandType.DEACTIVATE_REPEAT_AI);
		
		//resting but HP is at full
		if (isResting() && actor.getCurHp() == actor.getMaxHp())
			return new ActorCommand(GuiCommandType.DEACTIVATE_REPEAT_AI);
		
		//actor has been damaged since last move
		if (actor.getCurHp() < actorLastHp)
			return new ActorCommand(GuiCommandType.DEACTIVATE_REPEAT_AI);
		
		//actor can't move in the specified direction
		if (!isResting() && targetTileIsBlocked(zone, actor))
			return new ActorCommand(GuiCommandType.DEACTIVATE_REPEAT_AI);
		
		if (hasMovedOntoInterestingTile(zone, actor))
			return new ActorCommand(GuiCommandType.DEACTIVATE_REPEAT_AI);
		
		actorLastHp = actor.getCurHp();
		isFirstMove = false;
		
		return ActorCommand.move(directionToRepeat);
	}
	
	private boolean hasMovedOntoInterestingTile(Zone zone, Actor actor)
	{
		if (isFirstMove)
			return false;
		
		if (isResting())
			return false;		
		
		//stop if you go onto a tile with an item (but not if you're resting there)
		if (zone.getTile(actor).getItemHere() != null)
			return true;
		
		//stop at the stairs (but not if you're resting there)
		if (zone.getTile(actor).getType() == TileType.STAIRS_DOWN || zone.getTile(actor).getType() == TileType.STAIRS_UP)
			return true;
		
		return false;
	}

	private boolean isResting()
	{
		return directionToRepeat == Direction.DIRNONE;
	}

	private boolean targetTileIsBlocked(Zone zone, Actor actor)
	{
		Point origin = zone.getCoordsOfActor(actor);
		Point coordChange = directionToRepeat.getCoordChange();
		Point target = RPGlib.addPoints(origin, coordChange);
		
		Tile targetTile = zone.getTile(target);
		
		if (targetTile == null)
			return true;
		
		if (targetTile.obstructsMotion())
			return true;
		
		if (targetTile.getActorHere() != null)
			return true;
		
		return false;
	}

	@Override
	protected List<AiType> getEnemyAiTypes()
	{
		return enemyAiTypes;
	}
}
