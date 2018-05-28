package main.logic;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import main.data.ActorTurnQueue;
import main.data.Data;
import main.data.event.Event;
import main.entity.actor.Actor;
import main.entity.world.Overworld;
import main.entity.zone.Zone;
import main.logic.AI.ActorAI;
import main.logic.AI.AiType;
import main.logic.AI.MoveRandomAI;
import main.presentation.Logger;
import main.presentation.UiManager;

public class Engine
{
	private Data gameData; // remember that the presentation layer can't see this; it can only see what the logic layer provides

	private Map<AiType, ActorAI> gameAIs;

	private StringBuilder messageBuffer;

	private String queuedCommand;
	private long turnIndex;

	public Engine(Data theData)
	{
		gameData = theData;

		messageBuffer = new StringBuilder();

		gameAIs = new HashMap<AiType, ActorAI>();

		// define AIs
		gameAIs.put(AiType.RAND_MOVE, new MoveRandomAI());

		queuedCommand = "";
		turnIndex = 0;
	}

	public void beginGame()
	{
		while (true)
		{
			runTurn();
		}
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

	public void receiveCommand(String theCommand)
	{
		queuedCommand = theCommand;
	}

	@Deprecated	//TODO: only deprecated so I have a visual reminder to move this to Data (assuming that's best in MM, anyway)
	public String getBufferedMessages()
	{
		String messages = messageBuffer.toString();
		messageBuffer.delete(0, messageBuffer.capacity());

		return messages;
	}

	// increases the game turn counter, and lets each actor able to do so perform an action
	private void runTurn()
	{
		turnIndex++;

		ActorTurnQueue localActors = gameData.getActorQueue();
		Actor curActor = localActors.popNextActor();

		int actorIndex = gameData.getActorIndex(curActor);

		if (actorIndex == -1)
		{
			throw new IllegalStateException("localActors contained an actor not in zone");
		}

		AiType aiIndex = curActor.getAI();

		String command = "";

		if (aiIndex == AiType.HUMAN_CONTROLLED)
		{
			while (queuedCommand.equals(""))
			{
				// loop here until an input is received
			}

			command = queuedCommand;
			queuedCommand = "";
		} else
		{
			ActorAI curAI = gameAIs.get(aiIndex);
			command = curAI.getNextCommand(curActor);
		}

		executeActorCommand(actorIndex, command);
		localActors.add(curActor);

		if (aiIndex == AiType.HUMAN_CONTROLLED)
		{
			UiManager.getInstance().refreshInterface();
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
			Event event = handleMove(actorIndex, theCommand);

			if (event == null)
				return;

			gameData.receiveEvent(event);
		}
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
		Event event = null;

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

				event = Event.worldMoveEvent(actorIndex, x2, y2, actor.getMovementCost());
			}
		} else
		{
			Zone curZone = getCurrentZone();

			if (x2 >= 0 && y2 >= 0 && x2 < curZone.getHeight() && y2 < curZone.getWidth())
			{
				// Tile tile = curZone.getTile(x2, y2);
				// TODO: check for obstructions
				// TODO: base the speed on the tile itself
				// actionCost = tile.getMoveCost();

				event = Event.localMoveEvent(actorIndex, x2, y2, actor.getMovementCost());
			}
		}

		return event;
	}
}
