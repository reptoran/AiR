package main.logic;

import java.awt.Point;
import java.util.HashMap;
import java.util.List;
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
		} else if (theCommand.startsWith("EQUIP"))
		{
			equipItem(actorIndex, Integer.parseInt(theCommand.substring(5, 6)), Integer.parseInt(theCommand.substring(6)));
		} else if (theCommand.startsWith("UNEQUIP"))
		{
			unequipItem(actorIndex, Integer.parseInt(theCommand.substring(7)));
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
	
	private void equipItem(int actorIndex, int slotIndex, int itemIndex)
	{
		gameData.receiveEvent(Event.equipEvent(actorIndex, slotIndex, itemIndex));
	}
	
	private void unequipItem(int actorIndex, int slotIndex)
	{
		gameData.receiveEvent(Event.unequipEvent(actorIndex, slotIndex));
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
		
		List<Item> attackerWeapons = attacker.getWeapons();
		
		if (attacker != gameData.getPlayer() && attackerWeapons.size() > 1)
			Logger.debug("Enemy is attacking with multiple attacks: " + attackerWeapons);
		
		while (!attackerWeapons.isEmpty() && defender.getCurHp() > 0)
			handleAttack(attacker, attackerWeapons.remove(0), defender);
	}

	private void handleAttack(Actor attacker, Item attackingWeapon, Actor defender)
	{
		boolean canSeeSource = canPlayerSeeActor(attacker);
		boolean canSeeTarget = canPlayerSeeActor(defender);
		
		ItemDestructionMessageStorer messageStorer = new ItemDestructionMessageStorer();
		
		int damageToDefender = calculateAttackAndReturnDamageToDefender(attacker, defender, attackingWeapon, canSeeSource, canSeeTarget, messageStorer);
		String attackMessage = "";
		String armorDestroyedMessage = messageStorer.getArmorDestroyedMessage();
		String weaponDestroyedMessage = messageStorer.getWeaponDestroyedMessage();
		
		if (damageToDefender < 0)
		{
			attackMessage = "@2the deflect%2s @1thes attack.";
			gameData.receiveEvent(Event.waitEvent(gameData.getActorIndex(attacker), attacker.getMovementCost()));
		}
		else if (damageToDefender == 0)
		{
			attackMessage = "@1the miss%1e%1s @2the.";
			gameData.receiveEvent(Event.waitEvent(gameData.getActorIndex(attacker), attacker.getMovementCost()));
		}
		else
		{
			attackMessage = "@1the hit%1s @2the.";
			gameData.receiveEvent(Event.attackEvent(gameData.getActorIndex(attacker), gameData.getActorIndex(defender), damageToDefender, attacker.getMovementCost()));
		}
		
		MessageBuffer.addMessage(new FormattedMessageBuilder(attackMessage).setSource(attacker).setTarget(defender).setSourceVisibility(canSeeSource).setTargetVisibility(canSeeTarget).format());
		
		if (armorDestroyedMessage != null)
			MessageBuffer.addMessage(armorDestroyedMessage);
		
		if (weaponDestroyedMessage != null)
			MessageBuffer.addMessage(weaponDestroyedMessage);
		
		if (defender.getCurHp() <= 0)	//valid check because the data layer has already received and applied the damage
		{
			MessageBuffer.addMessage(new FormattedMessageBuilder("@1the is killed!").setSource(defender).setSourceVisibility(canSeeSource).format());
			gameData.receiveEvent(Event.deathEvent(gameData.getActorIndex(defender)));
		}
	}

	//TODO: right now, there's only one armor piece (maximum), but later, with multiple pieces of armor, something might determine which piece gets hit
	// 		this is because armor takes damage from attacks, and it seems foolish to damage all pieces of armor with each attack
	private Item getArmorForAttackedBodyPart(Actor defender)
	{
		List<Item> defenderArmor = defender.getArmor();
		return defenderArmor.get(RPGlib.randInt(0, defenderArmor.size() - 1));
	}
	
	private Item getShield(Actor defender)
	{
		List<Item> defenderShields = defender.getShields();
		
		if (defenderShields.isEmpty())
			return null;
		
		Item shield = defenderShields.get(RPGlib.randInt(0, defenderShields.size() - 1));
		
		if (RPGlib.percentage(shield.getCR()))
			return null;
		
		return shield;
	}

	//TODO: remember to adjust effective AR based on condition
	//	note that the armor still takes damage as though it had full AR, provided there's enough damage for it
	//		attack of 1 against armor with 2/4 AR deals 1 to armor and 0 to defender
	//		attack of 2 against armor with 2/4 AR deals 2 to armor and 0 to defender
	//		attack of 3 against armor with 2/4 AR deals 3 to armor and 1 to defender
	//		attack of 4 against armor with 2/4 AR deals 4 to armor and 2 to defender
	//		attack of 5 against armor with 2/4 AR deals 4 to armor and 3 to defender
	private int calculateAttackAndReturnDamageToDefender(Actor attacker, Actor defender, Item weapon, boolean canSeeSource, boolean canSeeTarget, ItemDestructionMessageStorer messageStorer)
	{
		boolean attackDeflected = false;
		String damageString = weapon.getDamage();
		Item shield = getShield(defender);
		Item armor;
		
		if (shield != null)
		{
			attackDeflected = true;
			armor = shield;
		}
		else
		{
			armor = getArmorForAttackedBodyPart(defender);
		}
				
		int rawDamage = RPGlib.roll(damageString);
		int damageToArmor = 0;
		int damageToWeapon = 0;
		int damageToDefender = 0;
		
		//TODO: maybe this is considered a critical hit?
		if (RPGlib.percentage(armor.getCR()))
			return rawDamage;
		
		double armorConditionBeforeAttack = armor.getConditionModifer();
		double weaponConditionBeforeAttack = weapon.getConditionModifer();
		
		damageToArmor = rawDamage > armor.getAR() ? armor.getAR() : rawDamage;
		damageToWeapon = damageToArmor - weapon.getDR();
		damageToDefender = rawDamage - damageToArmor;
		damageToArmor -= armor.getDR();
		
		if (damageToArmor < 0)
			damageToArmor = 0;
		
		if (damageToWeapon < 0)
			damageToWeapon = 0;
		
		if (damageToArmor > armor.getCurHp())
		{
			damageToDefender += (damageToArmor - armor.getCurHp());
			damageToArmor = armor.getCurHp();
		}
		
		if (damageToArmor > 0)
		{
			gameData.receiveEvent(Event.changeHeldItemHpEvent(gameData.getActorIndex(defender), defender.getIndexOfEquippedItem(armor), damageToArmor * -1));
			
			boolean armorDestroyed = (damageToArmor == armor.getCurHp() ? true : false);
			String effect = getDescriptionOfCondition(armorDestroyed, armor.getConditionModifer(), armorConditionBeforeAttack);
			
			if (effect != null)
				messageStorer.setArmorDestroyedMessage(new FormattedMessageBuilder("@2his " + armor.getName() + " is " + effect).setTarget(defender).setSourceVisibility(canSeeSource).setTargetVisibility(canSeeTarget).format());
		}
		
		if (damageToWeapon > 0)
		{
			gameData.receiveEvent(Event.changeHeldItemHpEvent(gameData.getActorIndex(attacker), attacker.getIndexOfEquippedItem(weapon), damageToWeapon * -1));
			
			boolean weaponDestroyed = (damageToWeapon == weapon.getCurHp() ? true : false);
			String effect = getDescriptionOfCondition(weaponDestroyed, weapon.getConditionModifer(), weaponConditionBeforeAttack);
			
			if (effect != null)
				messageStorer.setWeaponDestroyedMessage(new FormattedMessageBuilder("@1his " + weapon.getName() + " is " + effect).setSource(attacker).setSourceVisibility(canSeeSource).setTargetVisibility(canSeeTarget).format());
		}
		
		if (attackDeflected)
			return -1;
			
		return damageToDefender;
	}
	
	private String getDescriptionOfCondition(boolean destroyed, double currentCondition, double conditionBeforeAttack)
	{
		if (destroyed)
			return "destroyed!";
		else if (currentCondition <= .25 && conditionBeforeAttack > .25)
			return "heavily damaged.";	//"cracked.";
		else if (currentCondition <= .5 && conditionBeforeAttack > .5)
			return "moderately damaged.";	//"warped.";
		else if (currentCondition <= .75 && conditionBeforeAttack > .75)
			return "slightly damaged.";	//"dented.";
		
		return null;
	}
	
	private class ItemDestructionMessageStorer
	{
		private String armorDestroyedMessage = null;
		private String weaponDestroyedMessage = null;
		
		public ItemDestructionMessageStorer() {}

		public String getArmorDestroyedMessage()
		{
			return armorDestroyedMessage;
		}

		public void setArmorDestroyedMessage(String armorDestroyedMessage)
		{
			this.armorDestroyedMessage = armorDestroyedMessage;
		}

		public String getWeaponDestroyedMessage()
		{
			return weaponDestroyedMessage;
		}

		public void setWeaponDestroyedMessage(String weaponDestroyedMessage)
		{
			this.weaponDestroyedMessage = weaponDestroyedMessage;
		}
	}
}
