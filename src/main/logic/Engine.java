package main.logic;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import main.data.ActorTurnQueue;
import main.data.Data;
import main.data.event.Event;
import main.data.event.EventType;
import main.entity.actor.Actor;
import main.entity.item.Item;
import main.entity.tile.Tile;
import main.entity.world.Overworld;
import main.entity.zone.Zone;
import main.logic.AI.ActorAI;
import main.logic.AI.AiType;
import main.logic.AI.MeleeAI;
import main.logic.AI.MoveRandomAI;
import main.logic.AI.ZombieAI;
import main.presentation.Logger;
import main.presentation.UiManager;
import main.presentation.message.FormattedMessageBuilder;
import main.presentation.message.MessageBuffer;

public class Engine
{
	public static final int ACTOR_SIGHT_RADIUS = 12;
	
	private Data gameData; // remember that the presentation layer can't see this; it can only see what the logic layer provides

	private Map<AiType, ActorAI> gameAIs;

	private boolean acceptInput = false;
	private long turnIndex;

	public Engine(Data theData)
	{
		loadAIs();
		
		turnIndex = 0;
		gameData = theData;
	}

	private void loadAIs()
	{
		gameAIs = new HashMap<AiType, ActorAI>();
		gameAIs.put(AiType.RAND_MOVE, new MoveRandomAI());
		gameAIs.put(AiType.ZOMBIE, new ZombieAI());
		gameAIs.put(AiType.MELEE, new MeleeAI());
	}

	public void beginGame()
	{
		runAiTurns();
		refreshPlayerFov(gameData.getPlayer());
		
		acceptInput = true;
	}

	public boolean isWorldTravel()
	{
		return gameData.isWorldTravel();
	}

	public Zone getCurrentZone()
	{
		return gameData.getCurrentZone();
	}

	public Overworld getOverworld()
	{
		return gameData.getOverworld();
	}

	public Data getData()
	{
		return gameData;
	}

	public void receiveCommand(String command)
	{
		if (!acceptInput)
			return;
		
		acceptInput = false;
		runTurns(command);
		acceptInput = true;
	}
	
	private void runTurns(String command)
	{
		ActorTurnQueue localActors = gameData.getActorQueue();
		Actor playerActor = localActors.getNextActor();
		
		if (!AiType.HUMAN_CONTROLLED.equals(playerActor.getAI()))
		{
			Logger.warn("Next actor is not human controlled; simulating AI turns now.");
			runAiTurns();
			return;
		}
		
		playerActor = localActors.popNextActor();
		int actorIndex = gameData.getActorIndex(playerActor);

		if (actorIndex == -1)
			throw new IllegalStateException("localActors contained an actor not in zone");
		
		executeActorCommand(actorIndex, command);
		localActors.add(playerActor);
		
		runAiTurns();
		refreshPlayerFov(playerActor);
	}
	
	private void refreshPlayerFov(Actor playerActor)
	{
		Zone currentZone = gameData.getCurrentZone();
		currentZone.resetVisible();
		ActorSightUtil.updateFieldOfView(currentZone, currentZone.getCoordsOfActor(playerActor), ACTOR_SIGHT_RADIUS);	//TODO: base this on perception, and update that as it changes
		UiManager.getInstance().refreshInterface();
	}
	
	private boolean canPlayerSeeActor(Actor actor)
	{
		if (actor == gameData.getPlayer())
			return true;
		
		Zone currentZone = gameData.getCurrentZone();
		Point actorCoords = currentZone.getCoordsOfActor(actor);
		return ActorSightUtil.losExists(currentZone, currentZone.getCoordsOfActor(gameData.getPlayer()), actorCoords, ACTOR_SIGHT_RADIUS);
	}

	private void runAiTurns()
	{
		ActorTurnQueue localActors = gameData.getActorQueue();
		
		while (!AiType.HUMAN_CONTROLLED.equals(localActors.getNextActorAi()))
		{
			turnIndex++;	//TODO: not completely accurate, because this only updates every time an actor acts, rather than with every game tick
			
			Actor curActor = localActors.popNextActor();
			int actorIndex = gameData.getActorIndex(curActor);
			
			if (actorIndex == -1)
				throw new IllegalStateException("localActors contained an actor not in zone");
			
			AiType aiType = curActor.getAI();
			ActorAI curAI = gameAIs.get(aiType);
			String command = curAI.getNextCommand(gameData.getCurrentZone(), curActor);
			executeActorCommand(actorIndex, command);
			localActors.add(curActor);
		}
	}

	// returns the cost of the action taken
	// Note that strings are fine here, because this only deals with actors moving
	// I'll need a different method for in-game effects that aren't actor-driven
	// Remember, this doesn't modify data, but rather sends change requests to the data layer
	private void executeActorCommand(int actorIndex, String theCommand)
	{
		if (theCommand.equals("SAVE"))
		{
			gameData.receiveEvent(Event.saveEvent());
		} else if (theCommand.equals("EXIT"))
		{
			gameData.receiveEvent(Event.exitEvent());
		} else if (theCommand.equals("PICKUP"))
		{
			pickupItem(actorIndex);
		} else if (theCommand.startsWith("DROP"))
		{
			dropItem(actorIndex, Integer.parseInt(theCommand.substring(4)));
		} else if ((theCommand.equals("CHANGE_ZONE_UP") || theCommand.equals("CHANGE_ZONE_DOWN")) && canTransitionToNewZone(actorIndex))
		{
			gameData.receiveEvent(Event.transitionZoneEvent(actorIndex));
		} else if (theCommand.equals("CHANGE_ZONE_UP") && !gameData.isWorldTravel() && gameData.getCurrentZone().canEnterWorld())
		{
			gameData.receiveEvent(Event.enterWorldEvent(actorIndex));
		} else if (theCommand.equals("CHANGE_ZONE_DOWN") && gameData.isWorldTravel())
		{
			gameData.receiveEvent(Event.enterLocalEvent(actorIndex));
		} else if (theCommand.substring(0, 3).equals("DIR"))
		{
			handleDirection(actorIndex, theCommand);
		}
	}

	private void pickupItem(int actorIndex)
	{
		Actor actor = gameData.getActor(actorIndex);
		Tile tile = getCurrentZone().getTile(actor);
		Item item = tile.getItemHere();
		
		if (item == null)
		{
			MessageBuffer.addMessageIfHuman("There is nothing to pick up.", actor.getAI());
			return;
		}
		
		//TODO: check for item size, etc.
		
		gameData.receiveEvent(Event.pickupEvent(actorIndex));
		MessageBuffer.addMessage(new FormattedMessageBuilder("@1the pick%1s up " + item.getNameOnGround() + ".").setSource(actor).setSourceVisibility(canPlayerSeeActor(actor)).format());
	}
	
	private void dropItem(int actorIndex, int itemIndex)
	{
		Actor actor = gameData.getActor(actorIndex);
		Item item = actor.getInventory().get(itemIndex);
		MessageBuffer.addMessage(new FormattedMessageBuilder("@1the drop%1s " + item.getNameInPack() + ".").setSource(actor).setSourceVisibility(canPlayerSeeActor(actor)).format());
		
		gameData.receiveEvent(Event.dropEvent(actorIndex, itemIndex));
	}

	private void handleDirection(int actorIndex, String theCommand)
	{
		Event event = handleMove(actorIndex, theCommand);

		if (event == null)
			return;
		
		if (event.getEventType() == EventType.ATTACK)
		{
			resolveAttackEvent(event);
			return;
		}
		
		gameData.receiveEvent(event);
	}
	
	private boolean canTransitionToNewZone(int actorIndex)
	{
		if (gameData.isWorldTravel())
			return false;
		
		Actor actor = gameData.getActor(actorIndex);
		Point currentLocation = gameData.getCurrentZone().getCoordsOfActor(actor);
		
		if (gameData.getCurrentZone().getZoneKey(currentLocation) == null)
			return false;
		
		return true;
	}

	private Event handleMove(int actorIndex, String theCommand)
	{
		Actor actor = gameData.getActor(actorIndex);
		Point origin;

		if (isWorldTravel())
			origin = gameData.getOverworld().getPlayerCoords();
		else
			origin = gameData.getCurrentZone().getCoordsOfActor(actor);

		int x2 = origin.x, y2 = origin.y;

		// received a move command
		if (theCommand.equals("DIRNW") || theCommand.equals("DIRN") || theCommand.equals("DIRNE"))
			x2--;
		if (theCommand.equals("DIRSW") || theCommand.equals("DIRS") || theCommand.equals("DIRSE"))
			x2++;
		if (theCommand.equals("DIRNW") || theCommand.equals("DIRW") || theCommand.equals("DIRSW"))
			y2--;
		if (theCommand.equals("DIRNE") || theCommand.equals("DIRE") || theCommand.equals("DIRSE"))
			y2++;

		// TODO: these are kept separate because we care about seamless transitions for the zone level; not so much at the overworld level
		if (isWorldTravel())
		{
			Logger.debug("Engine - handling world move of " + theCommand + " to (" + x2 + ", " + y2 + ").");
			Overworld overworld = getOverworld();

			if (x2 >= 0 && y2 >= 0 && x2 < overworld.getHeight() && y2 < overworld.getWidth())
			{
				// WorldTile tile = overworld.getTile(x2, y2);
				// TODO: check for obstructions
				// TODO: base the speed on the tile itself
				// TODO: world travel should take a lot longer than local travel (massively increased movement cost)
				// actionCost = tile.getMoveCost();

				return Event.worldMoveEvent(actorIndex, x2, y2, actor.getMovementCost());
			}
		}

		Zone curZone = getCurrentZone();

		if (!curZone.containsPoint(new Point(x2, y2)))
			return null;
		
		Tile destinationTile = curZone.getTile(x2, y2);
		Actor targetActor = destinationTile.getActorHere();
		
		if (targetActor != null)
		{
			if (targetActor == actor)
				return Event.waitEvent(actorIndex, actor.getMovementCost());
			
			return Event.attackEvent(actorIndex, gameData.getActorIndex(targetActor), -1, actor.getMovementCost());
		}
		
		if (destinationTile.obstructsMotion())
		{
			// we never care if an AI actor runs into an obstruction (well, unless it's confused?)
			MessageBuffer.addMessageIfHuman(destinationTile.getBlockedMessage(), actor.getAI());
			return null;
		}
		
		int actionCost = (int)(actor.getMovementCost() * destinationTile.getMoveCostModifier());
		
		//we know that the move is successful at this point, so if there's an item in the destination tile, alert the player
		addItemMessage(destinationTile, actor);
		
		return Event.localMoveEvent(actorIndex, x2, y2, actionCost);
	}
	
	private void addItemMessage(Tile destinationTile, Actor actor)
	{
		if (!AiType.HUMAN_CONTROLLED.equals(actor.getAI()))
			return;
		
		Item itemHere = destinationTile.getItemHere();
		
		if (itemHere == null)
			return;
		
		MessageBuffer.addMessage("There is " + itemHere.getNameOnGround() + " here.");
	}
	
	private void resolveAttackEvent(Event attackEvent)
	{
		Actor attacker = gameData.getActor(attackEvent.getFlag(0));
		Actor defender = gameData.getActor(attackEvent.getFlag(1));
		
		int damage = calculateAttackDamage(attacker, defender);
		
		boolean canSeeSource = canPlayerSeeActor(attacker);
		boolean canSeeTarget = canPlayerSeeActor(defender);
		
		MessageBuffer.addMessage(new FormattedMessageBuilder("@1the hit%1s @2the.").setSource(attacker).setTarget(defender).setSourceVisibility(canSeeSource).setTargetVisibility(canSeeTarget).format());
		
		gameData.receiveEvent(Event.attackEvent(gameData.getActorIndex(attacker), gameData.getActorIndex(defender), damage, attacker.getMovementCost()));
		
		if (defender.getCurHp() <= 0)	//valid check because the data layer has already received and applied the damage
		{
			MessageBuffer.addMessage(new FormattedMessageBuilder("@1the is killed!").setSource(defender).setSourceVisibility(canSeeSource).format());
			gameData.receiveEvent(Event.deathEvent(gameData.getActorIndex(defender)));
		}
	}

	private int calculateAttackDamage(Actor attacker, Actor defender)
	{
		return 1;	//TODO: check for weapons on attacker, armor on defender, etc.
	}
}
