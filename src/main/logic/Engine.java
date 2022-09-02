package main.logic;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import main.data.Data;
import main.data.chat.ChatManager;
import main.data.event.ActorCommand;
import main.data.event.EventObserver;
import main.data.event.GuiCommandType;
import main.data.event.InternalEvent;
import main.data.event.InternalEventType;
import main.data.event.environment.ActorTurnEvent;
import main.data.event.environment.ConsumeInventoryItemEvent;
import main.data.event.environment.CreateItemEvent;
import main.data.event.environment.EnvironmentEvent;
import main.data.event.environment.EnvironmentEventQueue;
import main.data.event.environment.EnvironmentEventType;
import main.data.event.environment.EventInterruptionManager;
import main.data.event.environment.InterruptibleTurnEvent;
import main.entity.actor.Actor;
import main.entity.actor.ActorTraitType;
import main.entity.feature.Feature;
import main.entity.item.InventorySelectionKey;
import main.entity.item.Item;
import main.entity.item.ItemFactory;
import main.entity.item.ItemSource;
import main.entity.item.ItemType;
import main.entity.item.ItemUsageEventGenerator;
import main.entity.item.equipment.EquipmentSlotType;
import main.entity.item.recipe.Recipe;
import main.entity.item.recipe.RecipeManager;
import main.entity.quest.QuestManager;
import main.entity.tile.Tile;
import main.entity.world.Overworld;
import main.entity.zone.Zone;
import main.logic.AI.ActorAI;
import main.logic.AI.AiType;
import main.presentation.Logger;
import main.presentation.UiManager;
import main.presentation.message.FormattedMessageBuilder;
import main.presentation.message.MessageBuffer;
import main.presentation.message.MessageGenerator;

public class Engine
{
	public static final int ACTOR_SIGHT_RADIUS = 12;
	
	private Data gameData; // remember that the presentation layer can't see this; it can only see what the logic layer provides
	private List<EventObserver> eventObservers;

	private boolean acceptInput = false;
	
	@SuppressWarnings("unused")
	private long turnIndex;

	public Engine(Data data)
	{
		turnIndex = 0;
		gameData = data;
		MessageGenerator.getInstance().setEngine(this);
		
		eventObservers = new ArrayList<EventObserver>();
		eventObservers.add(EventInterruptionManager.getInstance());
		eventObservers.add(MessageGenerator.getInstance());		//this MUST be before data, so it can act on the current state before Data changes it (for example, with item pickups)
		eventObservers.add(data);
		eventObservers.add(QuestManager.getInstance());			//and this must be after data, so the things actually happen before the quest manager checks for it
	}
	
	public boolean addEventObserver(EventObserver observer)
	{
		return eventObservers.add(observer);
	}

	public void beginGame()
	{
		runEnvironmentEvents();
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
	
	//TODO: if the player moves out of sight, but the target moves back into sight before the player's next turn, it's still cleared
	public Actor getTarget()
	{
		Actor target = gameData.getTarget();
		
		if (target != null && target.getCurHp() <= 0)
			gameData.setTarget(null);
		else if (!canPlayerSeeActor(target))
			gameData.setTarget(null);

		return gameData.getTarget();
	}
	
	public String getPromptForCommandType(GuiCommandType commandType)
	{
		String description;
		
		switch (commandType)
		{
		case CHAT:
			description = "Chat with someone.  ";
			break;
		case USE:
			description = "Use an item.  ";
			break;
		case REPEAT:
			description = "Rest/Run.  ";
			break;
			//$CASES-OMITTED$
		default:
			description = "";
			break;
		}
		
		return description + "Which direction?";
	}

	public Direction determineDirectionOfCommand(GuiCommandType commandType)
	{
		Zone currentZone = gameData.getCurrentZone();
		Actor player = gameData.getPlayer();
		Point playerLocation = currentZone.getCoordsOfActor(player);
		
		if (commandType == GuiCommandType.CHAT)
		{
			List<Actor> potentialTargets = gameData.getAdjacentActors(playerLocation);
			
			if (potentialTargets.size() != 1)
				return Direction.DIRNONE;
			
			Actor target = potentialTargets.get(0);
			Point targetLocation = currentZone.getCoordsOfActor(target);
			return RPGlib.convertCoordChangeToDirection(targetLocation.x - playerLocation.x, targetLocation.y - playerLocation.y);
		}
		
		return Direction.DIRNONE;
	}

	public void receiveCommand(ActorCommand command)
	{
		if (!acceptInput)
			return;
		
		acceptInput = false;
		runTurns(command);
		acceptInput = true;
	}
	
	private void sendEventToObservers(EnvironmentEvent event)
	{
		for (InternalEvent iEvent : event.trigger())
		{
			sendEventToObservers(iEvent);
		}
	}
	
	private void sendEventToObservers(InternalEvent event)
	{
		for (EventObserver observer : eventObservers)
		{
			observer.receiveInternalEvent(event);
		}
	}
	
	private void runTurns(ActorCommand command)
	{
		EnvironmentEventQueue localEvents = gameData.getEventQueue();
		
		if (!localEvents.nextEventIsHumanTurn())
		{
			Logger.info("Next actor is not human controlled; simulating AI turns now.");
			runEnvironmentEvents();
			return;
		}
		
		//must be separate logic because otherwise the event never gets put back in the queue to be saved
		if (command.getType() == GuiCommandType.SAVE)
		{
			sendEventToObservers(InternalEvent.saveInternalEvent());
			return;
		}
		
		//we've already checked that the next event is a turn event, so this is fine
		ActorTurnEvent turnEvent = (ActorTurnEvent) localEvents.popNextEvent();
		
		Actor playerActor = turnEvent.getActor();
		int actorIndex = gameData.getActorIndex(playerActor);

		if (actorIndex == -1)
			throw new IllegalStateException("localEvents contained an actor not in zone");
		
		executeActorCommand(actorIndex, command, turnEvent, localEvents);
		
		runEnvironmentEvents();
		refreshPlayerFov(playerActor);
	}
	
	private void refreshPlayerFov(Actor playerActor)
	{
		Zone currentZone = gameData.getCurrentZone();
		currentZone.resetVisible();
		ActorSightUtil.updateFieldOfView(currentZone, currentZone.getCoordsOfActor(playerActor), ACTOR_SIGHT_RADIUS);	//TODO: base this on perception, and update that as it changes
		UiManager.getInstance().refreshInterface();
	}
	
	public boolean canPlayerSeeActor(Actor actor)
	{
		if (actor == gameData.getPlayer())
			return true;
		
		Zone currentZone = gameData.getCurrentZone();
		Point actorCoords = currentZone.getCoordsOfActor(actor);
		return ActorSightUtil.losExists(currentZone, currentZone.getCoordsOfActor(gameData.getPlayer()), actorCoords, ACTOR_SIGHT_RADIUS);
	}

	private void runEnvironmentEvents()
	{
		EnvironmentEventQueue localEvents = gameData.getEventQueue();
		
		while (!localEvents.nextEventIsHumanTurn())
		{
			turnIndex++;	//TODO: not completely accurate, because this only updates every time an event triggers, rather than with every game tick
			
			EnvironmentEvent event = localEvents.popNextEvent();
			
			if (event.getType() != EnvironmentEventType.ACTOR_TURN)
			{
				for (InternalEvent iEvent : event.trigger()) 
				
				sendEventToObservers(iEvent);
				continue;
			}
			
			ActorTurnEvent turnEvent = (ActorTurnEvent) event;
			
			Actor curActor = turnEvent.getActor();
			Logger.debug("AI Actor " + curActor.getName() + " is taking a turn.");
			int actorIndex = gameData.getActorIndex(curActor);
			
			if (actorIndex == -1)
				throw new IllegalStateException("localEvents contained an actor not in zone");
			
			AiType aiType = curActor.getAI();
			ActorAI curAI = gameData.getAI(aiType);
			ActorCommand command = curAI.getNextCommand(gameData.getCurrentZone(), curActor);
			executeActorCommand(actorIndex, command, turnEvent, localEvents);
			
			if (curActor.getAI() == AiType.REPEAT_LAST_MOVE)
				refreshPlayerFov(curActor);
		}
	}

	// returns the cost of the action taken
	// Note that strings are fine here, because this only deals with actors moving
	// I'll need a different method for in-game effects that aren't actor-driven
	// Remember, this doesn't modify data, but rather sends change requests to the data layer
	private void executeActorCommand(int actorIndex, ActorCommand command, ActorTurnEvent currentTurnEvent, EnvironmentEventQueue eventQueue)
	{
		InternalEvent event = null;
		Direction direction = null;
		String directionString = null;
		InventorySelectionKey originalInventorySlot = null;
		InventorySelectionKey targetInventorySlot = null;
		
		switch (command.getType())
		{
		case MOVE:
			directionString = command.getArgument1();
			int moveCost = handleDirectionAndReturnEventDuration(actorIndex, Direction.fromString(directionString));
			currentTurnEvent.recur(moveCost);
			return;
		case SAVE:
			event = InternalEvent.saveInternalEvent();
			break;
		case EXIT:
			event = InternalEvent.exitInternalEvent();
			break;
		case PICKUP:
			event = pickupItem(actorIndex);
			break;
		case DROP:
			event = dropItem(actorIndex, InventorySelectionKey.fromKey(command.getArgument1()));
			break;
		case EQUIP:
			originalInventorySlot = InventorySelectionKey.fromKey(command.getArgument1());
			targetInventorySlot = InventorySelectionKey.fromKey(command.getArgument2());
			event = equipItem(actorIndex, originalInventorySlot, targetInventorySlot);
			break;
		case UNEQUIP:
			originalInventorySlot = InventorySelectionKey.fromKey(command.getArgument1());
			targetInventorySlot = InventorySelectionKey.fromKey(command.getArgument2());
			event = unequipItem(actorIndex, originalInventorySlot, targetInventorySlot);
			break;
		case USE:
			originalInventorySlot = InventorySelectionKey.fromKey(command.getArgument1());
			direction = Direction.fromString(command.getArgument2());
			event = useItem(actorIndex, originalInventorySlot, direction);
			break;
		case UPGRADE:
			originalInventorySlot = InventorySelectionKey.fromKey(command.getArgument1());
			targetInventorySlot = InventorySelectionKey.fromKey(command.getArgument2());
			event = upgradeItem(actorIndex, originalInventorySlot, targetInventorySlot);
			break;
		case CHAT:
			event = chatWithActor(actorIndex, Direction.fromString(command.getArgument1()));
			break;
		case RECIPE:
			event = craftRecipe(actorIndex, RecipeManager.getInstance().getRecipeForItem(ItemType.fromString(command.getArgument1())));
			break;
		case REPAIR:
			event = repairItem(actorIndex, command);
			break;
		case REPEAT:
			event = InternalEvent.setActorAiToRepeatDirectionInternalEvent(actorIndex, Direction.fromString(command.getArgument1()));
			break;
		case DEACTIVATE_REPEAT_AI:
			event = InternalEvent.changeActorAiInternalEvent(actorIndex, AiType.HUMAN_CONTROLLED);
			break;
		//$CASES-OMITTED$
		default:
			Logger.warn("Engine has no logic for ActorCommand with type " + command.getType() + ".");
			break;
		}
		
		if ((command.getType() == GuiCommandType.CHANGE_ZONE_UP || command.getType() == GuiCommandType.CHANGE_ZONE_DOWN) && canTransitionToNewZone(actorIndex))
		{
			event = InternalEvent.transitionZoneInternalEvent(actorIndex);
		} else if (command.getType() == GuiCommandType.CHANGE_ZONE_UP && !gameData.isWorldTravel() && gameData.getCurrentZone().canEnterWorld())
		{
			event = InternalEvent.enterWorldInternalEvent(actorIndex);
		} else if (command.getType() == GuiCommandType.CHANGE_ZONE_DOWN && gameData.isWorldTravel())
		{
			event = InternalEvent.enterLocalInternalEvent(actorIndex);
		}
				
		if (event == null)
		{
			currentTurnEvent.recur(0);
			return;
		}
		
		if (event.isInterruptible())
		{
			eventQueue.add(new InterruptibleTurnEvent(event, eventQueue, currentTurnEvent));
			return;
		}
		
		sendEventToObservers(event);
		
		currentTurnEvent.recur(event.getActionCost());
	}

	private InternalEvent pickupItem(int actorIndex)
	{
		Actor actor = gameData.getActor(actorIndex);
		Tile tile = getCurrentZone().getTile(actor);
		Item item = tile.getItemHere();
		
		if (item == null)
		{
			MessageBuffer.addMessageIfHuman("There is nothing to pick up.", actor.getAI());
			return null;
		}
		
		if (item.getInventorySlot().equals(EquipmentSlotType.MATERIAL) && !actor.getMaterials().hasSpaceForItem(item)) 
		{
			MessageBuffer.addMessageIfHuman("There's no room in your pouch for " + item.getNameOnGround() + ".", actor.getAI());
			return null;
		}
		else if (!actor.hasSpaceForItem(item))
		{
			MessageBuffer.addMessageIfHuman("You have no room for " + item.getNameOnGround() + ".", actor.getAI());
			return null;
		}
		
		InternalEvent event = null;
		
		if (item.getInventorySlot().equals(EquipmentSlotType.ARMAMENT) && actor.getReadiedItems().hasEmptySlotAvailable(EquipmentSlotType.ARMAMENT))
		{
			event = InternalEvent.pickupInternalEvent(actorIndex, ItemSource.READY);
			event.setActionCost(actor.getMovementCost());
		}
		else if (item.getBulk() > 1)
		{
			event = InternalEvent.pickupInternalEvent(actorIndex);
			event.setActionCost(actor.getMovementCost() * item.getBulk());
			event.setInterruptible(true);
			MessageBuffer.addMessage(new FormattedMessageBuilder("@1the start%1s to put an item into @1his pack.").setSource(actor).setSourceVisibility(canPlayerSeeActor(actor)).format());
		}
		else
		{
			event = InternalEvent.pickupInternalEvent(actorIndex);
			event.setActionCost(actor.getMovementCost());
		}
		
		return event;
	}
	
	private Item getItemFromSource(Actor actor, InventorySelectionKey source)
	{
		return getItemFromSource(actor, source.getItemSource(), source.getItemIndex());
	}
	
	private Item getItemFromSource(Actor actor, ItemSource source, int itemIndex)
	{
		switch(source)
		{
		case PACK:
			return actor.getStoredItems().get(itemIndex);
		case MATERIAL:
			return actor.getMaterials().get(itemIndex);
		case EQUIPMENT:
			return actor.getEquipment().getItem(itemIndex);
		case MAGIC:
			return actor.getMagicItems().getItem(itemIndex);
		case READY:
			return actor.getReadiedItems().getItem(itemIndex);
		case GROUND:
			//$CASES-OMITTED$
		default:
			throw new IllegalArgumentException("No drop message behavior defined for item source " + source);
		}
	}
	
	private InternalEvent dropItem(int actorIndex, InventorySelectionKey key)
	{
		Actor actor = gameData.getActor(actorIndex);
		ItemSource source = key.getItemSource();
		int itemIndex = key.getItemIndex();
		Item item = getItemFromSource(actor, source, itemIndex);
		
		InternalEvent event = InternalEvent.dropInternalEvent(actorIndex, key);
		
		//held armaments (weapons, shields) can be dropped instantly
		if (source == ItemSource.EQUIPMENT && item.getInventorySlot() == EquipmentSlotType.ARMAMENT)
		{
			MessageBuffer.addMessage(new FormattedMessageBuilder("@1the drop%1s " + item.getNameOnGround() + " @1he @1was holding.").setSource(actor).setSourceVisibility(canPlayerSeeActor(actor)).format());
			return event;
		}
		
		if (source == ItemSource.READY)
		{
			event.setActionCost(actor.getMovementCost());
			return event;
		}
		
		if (item.getBulk() > 0)
			event.setActionCost(actor.getMovementCost() * item.getBulk());
		
		if (item.getBulk() > 1)
			event.setInterruptible(true);
		
		if (source == ItemSource.EQUIPMENT)
			MessageBuffer.addMessage(new FormattedMessageBuilder("@1the start%1s to remove a piece of equipment.").setSource(actor).setSourceVisibility(canPlayerSeeActor(actor)).format());
		
		if (source == ItemSource.PACK)
			MessageBuffer.addMessage(new FormattedMessageBuilder("@1the dig%1s into @1his pack.").setSource(actor).setSourceVisibility(canPlayerSeeActor(actor)).format());
		
		return event;
	}

	private InternalEvent useItem(int actorIndex, InventorySelectionKey itemSlot, Direction direction)
	{
		Actor actor = gameData.getActor(actorIndex);
		ItemSource source = itemSlot.getItemSource();
		int itemIndex = itemSlot.getItemIndex();
		Item itemToUse = getItemFromSource(actor, source, itemIndex);
		
		Point origin = gameData.getCurrentZone().getCoordsOfActor(actor);
		Point destination = getPointInDirection(origin, direction);
		
		boolean usedItemSuccessfully = useItemOnTile(actor, itemToUse, gameData.getCurrentZone().getTile(destination));
		
		if (!usedItemSuccessfully)
			return null;
		
		return InternalEvent.waitInternalEvent(gameData.getActorIndex(actor), actor.getMovementCost());	//effects of using should be instantaneous, but actor still needs to wait until his next turn afterward
																										//actor index is refreshed because it may have been updated after using the item
	}
	
	private boolean useItemOnTile(Actor actor, Item itemToUse, Tile targetTile)
	{
		Item targetItem = targetTile.getItemHere();
		Feature targetFeature = targetTile.getFeatureHere();
		Actor targetActor = targetTile.getActorHere();
		String targetString = "";
		
		List<EnvironmentEvent> eventsToTrigger = new ArrayList<EnvironmentEvent>();
		
		if (targetActor != null)
		{
			eventsToTrigger = ItemUsageEventGenerator.getInstance().useItem(actor, itemToUse, targetActor);
			targetString = "@2the";
		}
		else if (targetItem != null)
		{
			eventsToTrigger = ItemUsageEventGenerator.getInstance().useItem(actor, itemToUse, targetItem);
			targetString = targetItem.getNameOnGround();
		}
		else if (targetFeature != null)
		{
			eventsToTrigger = ItemUsageEventGenerator.getInstance().useItem(actor, itemToUse, targetFeature);
			targetString = "the " + targetFeature.getName();
		}
		else
		{
			eventsToTrigger = ItemUsageEventGenerator.getInstance().useItem(actor, itemToUse, targetTile);
			targetString = "the " + targetTile.getName();
		}
		
		if (eventsToTrigger.isEmpty())
		{
			MessageBuffer.addMessageIfHuman("You don't know how to use that item that way.", actor.getAI());
			return false;
		}
		
		MessageBuffer.addMessage(new FormattedMessageBuilder("@1the use%1s " + itemToUse.getName() + " on " + targetString + ".").setSource(actor).setTarget(targetActor).format());	//TODO: set visibility
		
		for (EnvironmentEvent event : eventsToTrigger)
		{
			for (InternalEvent iEvent : event.trigger())
			{
				sendEventToObservers(iEvent);
			}
		}
		
		return true;
	}
	
	private InternalEvent upgradeItem(int actorIndex, InventorySelectionKey baseItemSlot, InventorySelectionKey enhancingItemSlot)
	{
		Actor actor = gameData.getActor(actorIndex);
		Item itemToUpgrade = getItemFromSource(actor, baseItemSlot);
		Item enhancingItem = getItemFromSource(actor, enhancingItemSlot);
		
		if (itemToUpgrade == null)
		{
			Logger.warn("Attempting to upgrade a null item!");
			return null;
		}
		
		if (enhancingItem == null)
		{
			Logger.warn("Attempting to upgrade with a null item!");
			return null;
		}
		
		if (itemToUpgrade.isUpgraded())
		{
			MessageBuffer.addMessageIfHuman("That item is already upgraded.", actor.getAI());
			return null;
		}
		
		ItemType enhancingItemType = enhancingItem.getType();
		
		if (itemToUpgrade.getUpgradedBy() != enhancingItemType)
		{
			MessageBuffer.addMessageIfHuman("You can't upgrade that item with that material.", actor.getAI());
			return null;
		}
		
		MessageBuffer.addMessage(new FormattedMessageBuilder("@1the enhance%1s @1his " + itemToUpgrade.getName() + " with a " + enhancingItem.getName() + ".").setSource(actor).format());	//TODO: set visibility
		
		sendEventToObservers(new ConsumeInventoryItemEvent(actor, enhancingItem, 1, null));
		sendEventToObservers(InternalEvent.upgradeWeaponInternalEvent(actorIndex, baseItemSlot));
		
		return InternalEvent.waitInternalEvent(actorIndex, actor.getMovementCost());
	}
	
	private InternalEvent equipItem(int actorIndex, InventorySelectionKey originalInventorySlot, InventorySelectionKey targetInventorySlot)
	{
		InternalEvent event = InternalEvent.equipInternalEvent(actorIndex, originalInventorySlot, targetInventorySlot);
		Actor actor = gameData.getActor(actorIndex);
		
		Tile tile = getCurrentZone().getTile(actor);
		Item item = tile.getItemHere();
		
		if (originalInventorySlot.getItemSource() != ItemSource.GROUND)
			item = actor.getItem(originalInventorySlot.getItemSource(), originalInventorySlot.getItemIndex());
		
		//scooping up an armament from the ground to wield is free to do
		if (originalInventorySlot.getItemSource() == ItemSource.GROUND && targetInventorySlot.getItemSource() == ItemSource.EQUIPMENT && item.getInventorySlot() == EquipmentSlotType.ARMAMENT)
		{
			MessageBuffer.addMessage(new FormattedMessageBuilder("@1the snatch%1e%1s up " + item.getNameOnGround() + " from the ground and wield%1s it.").setSource(actor).setSourceVisibility(canPlayerSeeActor(actor)).format());
			return event;
		}
		//moving between equipment and ready still takes time but is uninterruptible
		else if ((originalInventorySlot.getItemSource() == ItemSource.READY && targetInventorySlot.getItemSource() == ItemSource.EQUIPMENT) ||
				(originalInventorySlot.getItemSource() == ItemSource.EQUIPMENT && targetInventorySlot.getItemSource() == ItemSource.READY))
		{
			event.setActionCost(actor.getMovementCost());
		}
		else if (item.getBulk() > 0)	//note that this excludes magic items and materials
		{
			event.setActionCost(actor.getMovementCost() * item.getBulk());
			
			if (item.getBulk() > 1)
				event.setInterruptible(true);
			
			if (originalInventorySlot.getItemSource() == ItemSource.PACK)
				MessageBuffer.addMessage(new FormattedMessageBuilder("@1the dig%1s into @1his pack for an item.").setSource(actor).setSourceVisibility(canPlayerSeeActor(actor)).format());
			if (originalInventorySlot.getItemSource() == ItemSource.GROUND)
				MessageBuffer.addMessage(new FormattedMessageBuilder("@1the begin%1s to pick up an item.").setSource(actor).setSourceVisibility(canPlayerSeeActor(actor)).format());
		}
		
		return event;
	}
	
	private InternalEvent unequipItem(int actorIndex, InventorySelectionKey originalInventorySlot, InventorySelectionKey targetInventorySlot)
	{
		InternalEvent event = InternalEvent.unequipInternalEvent(actorIndex, originalInventorySlot, targetInventorySlot);
		Actor actor = gameData.getActor(actorIndex);
		
		Tile tile = getCurrentZone().getTile(actor);
		Item item = tile.getItemHere();
		
		if (targetInventorySlot.getItemSource() == ItemSource.READY)
		{
			event.setActionCost(actor.getMovementCost());
			return event;
		}
		
		if (originalInventorySlot.getItemSource() != ItemSource.GROUND)
			item = actor.getItem(originalInventorySlot.getItemSource(), originalInventorySlot.getItemIndex());
		
		if (item.getBulk() > 1)
		{
			event.setActionCost(actor.getMovementCost() * item.getBulk());
			event.setInterruptible(true);
			MessageBuffer.addMessage(new FormattedMessageBuilder("@1the start%1s to remove a piece of equipment.").setSource(actor).setSourceVisibility(canPlayerSeeActor(actor)).format());
		}
		
		return event;
	}
	
	private InternalEvent chatWithActor(int actorIndex, Direction direction)
	{
		Logger.debug("Chatting in direction " + direction);
		
		//TODO: all this will likely be extracted to a helper method once there are more interactions with adjacent actors
		Actor actor = gameData.getActor(actorIndex);
		Point origin = gameData.getCurrentZone().getCoordsOfActor(actor);
		Point destination = getPointInDirection(origin, direction);
		Actor target = gameData.getCurrentZone().getActorAtCoords(destination);
		int targetIndex = gameData.getActorIndex(target);
		
		if (target == null)
			MessageBuffer.addMessage("You don't see anyone there to talk to.");
		else if (target == gameData.getPlayer() || !ChatManager.getInstance().hasInitialChat(target.getType()))
		{
			MessageBuffer.addMessage(target.getDefaultTalkResponse());
			targetIndex = -1;
		}
		
		return InternalEvent.chatInternalEvent(actorIndex, targetIndex);
	}
	
	private InternalEvent craftRecipe(int actorIndex, Recipe recipe)
	{
		Actor actor = gameData.getActor(actorIndex);
		
		if (!recipe.sufficientSkills(actor))
		{
			MessageBuffer.addMessageIfHuman("Your skill is not high enough to craft this item.", actor.getAI());
			return null;
		}
		
		if (!recipe.sufficientComponents(actor))
		{
			MessageBuffer.addMessageIfHuman("Your don't have the necessary components to craft this item.", actor.getAI());
			return null;
		}
		
		for (ItemType component : recipe.getComponents().keySet())
		{
			int quantity = recipe.getComponents().get(component);
			sendEventToObservers(new ConsumeInventoryItemEvent(actor, ItemFactory.generateNewItem(component), quantity, gameData.getEventQueue()));
		}
		
		sendEventToObservers(new CreateItemEvent(actor, recipe.getResultingItem(), 1, gameData.getEventQueue()));
		
		Item createdItem = ItemFactory.generateNewItem(recipe.getResultingItem());
		MessageBuffer.addMessage(new FormattedMessageBuilder("@1the craft%1s a " + createdItem.getName() + ".").setSource(actor).format());	//TODO: set visibility
		
		return InternalEvent.waitInternalEvent(actorIndex, actor.getMovementCost());	//effects of crafting should be instantaneous, but actor still needs to wait until his next turn afterward
																						//TODO: not necessarily true; some items might take a while to make, and the crafting should be able to be interrupted 
	}
	
	private InternalEvent repairItem(int actorIndex, ActorCommand command)
	{
		InventorySelectionKey itemKey = InventorySelectionKey.fromKey(command.getArgument1());
		Actor actor = gameData.getActor(actorIndex);
		Item item = actor.getItem(itemKey);
		MessageBuffer.addMessage(new FormattedMessageBuilder("@1the repair%1s " + item.getNameOnGround() + ".").setSource(actor).setSourceVisibility(canPlayerSeeActor(actor)).format());
		return InternalEvent.changeInventoryItemHpInternalEvent(actorIndex, itemKey, Integer.valueOf(command.getArgument2()));
	}
	
	private Point getPointInDirection(Point origin, Direction direction)
	{
		Point coordChange = RPGlib.convertDirectionToCoordChange(direction);
		return new Point(origin.x + coordChange.x, origin.y + coordChange.y);
	}

	private int handleDirectionAndReturnEventDuration(int actorIndex, Direction direction)
	{
		InternalEvent event = handleMove(actorIndex, direction);

		if (event == null)
			return 0;
		
		if (event.getInternalEventType() == InternalEventType.ATTACK)
			return resolveAttackInternalEvent(event);
		
		sendEventToObservers(event);
		return event.getActionCost();
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

	private InternalEvent handleMove(int actorIndex, Direction direction)
	{
		Actor actor = gameData.getActor(actorIndex);
		Point origin;

		if (isWorldTravel())
			origin = gameData.getOverworld().getPlayerCoords();
		else
			origin = gameData.getCurrentZone().getCoordsOfActor(actor);
		
		if (origin == null)
		{
			Logger.error("handleMove: No origin found for actor! Actor[" + actor + "]");
			return null;
		}

		Point coordChange = RPGlib.convertDirectionToCoordChange(direction);
		
		int x2 = origin.x + coordChange.x;
		int y2 = origin.y + coordChange.y;

		// TODO: these are kept separate because we care about seamless transitions for the zone level; not so much at the overworld level
		if (isWorldTravel())
		{
			Logger.debug("Engine - handling world move of " + direction + " to (" + x2 + ", " + y2 + ").");
			Overworld overworld = getOverworld();

			if (x2 >= 0 && y2 >= 0 && x2 < overworld.getHeight() && y2 < overworld.getWidth())
			{
				// WorldTile tile = overworld.getTile(x2, y2);
				// TODO: check for obstructions
				// TODO: base the speed on the tile itself
				// TODO: world travel should take a lot longer than local travel (massively increased movement cost)
				// actionCost = tile.getMoveCost();

				return InternalEvent.worldMoveInternalEvent(actorIndex, x2, y2, actor.getMovementCost());
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
				return InternalEvent.waitInternalEvent(actorIndex, actor.getMovementCost());
			else if (actor.isPlayer() && targetActor.getAI().chatDefault())
				return chatWithActor(actorIndex, direction);
			
			return InternalEvent.attackInternalEvent(actorIndex, gameData.getActorIndex(targetActor), -1, actor.getMovementCost());
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
		
		return InternalEvent.localMoveInternalEvent(actorIndex, x2, y2, actionCost);
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
	
	private int resolveAttackInternalEvent(InternalEvent attackInternalEvent)
	{
		Actor attacker = gameData.getActor(attackInternalEvent.getFlag(0));
		Actor defender = gameData.getActor(attackInternalEvent.getFlag(1));
		
		List<Item> attackerWeapons = attacker.getWeapons();
		
		if (attacker != gameData.getPlayer() && attackerWeapons.size() > 1)
			Logger.debug("Enemy is attacking with multiple attacks: " + attackerWeapons);
		
		while (!attackerWeapons.isEmpty() && defender.getCurHp() > 0)
			handleAttack(attacker, attackerWeapons.remove(0), defender);
		
		return attacker.getMovementCost();		//TODO: for now, assume that no matter how many attacks an actor has, it only takes up one "turn" 
	}

	private void handleAttack(Actor attacker, Item attackingWeapon, Actor defender)
	{
		Actor player = gameData.getPlayer();
		if (attacker == player)
			gameData.setTarget(defender);
		if (defender == player)
			gameData.setTarget(attacker);
		
		boolean canSeeSource = canPlayerSeeActor(attacker);
		boolean canSeeTarget = canPlayerSeeActor(defender);
		
		AttackDetails attackDetails = new AttackDetails();
		
		int damageToDefender = calculateAttackAndReturnDamageToDefender(attacker, defender, attackingWeapon, canSeeSource, canSeeTarget, attackDetails);
		String attackMessage = "";
		String armorDestroyedMessage = attackDetails.getArmorDestroyedMessage();
		String weaponDestroyedMessage = attackDetails.getWeaponDestroyedMessage();
		Item armor = attackDetails.getArmorThatAbsorbedAttack();
		
		//TODO: right now an action cost of 0 is being set on the event because it's not used anyway - a constant duration is returned regardless of attack count
		if (armor != null && armor.isUpgraded())
		{
			attackMessage = "@1thes attack glances off a makeshift plate on @2thes armor, sending it flying.";
			sendEventToObservers(InternalEvent.waitInternalEvent(gameData.getActorIndex(attacker), 0));
			sendEventToObservers(InternalEvent.downgradeItemInternalEvent(gameData.getActorIndex(defender), new InventorySelectionKey(ItemSource.EQUIPMENT, defender.getIndexOfEquippedItem(armor))));
		}
		else if (damageToDefender < 0)
		{
			attackMessage = "@2the deflect%2s @1thes attack.";
			sendEventToObservers(InternalEvent.waitInternalEvent(gameData.getActorIndex(attacker), 0));
		}
		else if (damageToDefender == 0)
		{
			attackMessage = "@1the fail%1s to hurt @2the.";
			sendEventToObservers(InternalEvent.waitInternalEvent(gameData.getActorIndex(attacker), 0));
		}
		else if (attackingWeapon.isUpgraded())
		{
			attackMessage = "@1the deliver%1s a brutal blow to @2the.";
			sendEventToObservers(InternalEvent.attackInternalEvent(gameData.getActorIndex(attacker), gameData.getActorIndex(defender), damageToDefender, 0));
			sendEventToObservers(InternalEvent.downgradeItemInternalEvent(gameData.getActorIndex(attacker), new InventorySelectionKey(ItemSource.EQUIPMENT, attacker.getIndexOfEquippedItem(attackingWeapon))));
		}
		else
		{
			attackMessage = "@1the hit%1s @2the.";
			sendEventToObservers(InternalEvent.attackInternalEvent(gameData.getActorIndex(attacker), gameData.getActorIndex(defender), damageToDefender, 0));
		}
		
		MessageBuffer.addMessage(new FormattedMessageBuilder(attackMessage).setSource(attacker).setTarget(defender).setSourceVisibility(canSeeSource).setTargetVisibility(canSeeTarget).format());
		
		if (armorDestroyedMessage != null)
			MessageBuffer.addMessage(armorDestroyedMessage);
		
		if (weaponDestroyedMessage != null)
			MessageBuffer.addMessage(weaponDestroyedMessage);
		
		if (defender.getCurHp() <= 0)	//valid check because the data layer has already received and applied the damage
		{
			if (canSeeTarget)
				MessageBuffer.addMessage(new FormattedMessageBuilder("@1the is killed!").setSource(defender).setSourceVisibility(canSeeTarget).format());
			gameData.receiveInternalEvent(InternalEvent.deathInternalEvent(gameData.getActorIndex(defender)));
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
	private int calculateAttackAndReturnDamageToDefender(Actor attacker, Actor defender, Item weapon, boolean canSeeSource, boolean canSeeTarget, AttackDetails attackDetails)
	{
		boolean attackDeflected = false;
		String damageString = weapon.getDamage();
		Item shield = getShield(defender);
		Item armor;
		
		if (shield != null)		//TODO: at present, shields ALWAYS block all damage, making them an armor upgrade on steroids
		{
			attackDeflected = true;
			armor = shield;
		}
		else
		{
			armor = getArmorForAttackedBodyPart(defender);
		}
		
		attackDetails.setArmorThatAbsorbedAttack(armor);
				
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
//		damageToWeapon = damageToArmor - weapon.getDR();
		damageToWeapon = damageToArmor - getItemDR(weapon, attacker);
		damageToDefender = rawDamage - damageToArmor;
//		damageToArmor -= armor.getDR();
		damageToArmor -= getItemDR(armor, defender);
		
		if (damageToArmor < 0 || armor.isUpgraded())
			damageToArmor = 0;
		
		if (damageToWeapon < 0 || weapon.isUpgraded())
			damageToWeapon = 0;
		
		if (damageToArmor > armor.getCurHp())
		{
			damageToDefender += (damageToArmor - armor.getCurHp());
			damageToArmor = armor.getCurHp();
		}
		
		if (damageToWeapon > weapon.getCurHp())
			damageToWeapon = weapon.getCurHp();
		
		if (damageToArmor > 0)
		{
			sendEventToObservers(InternalEvent.changeInventoryItemHpInternalEvent(gameData.getActorIndex(defender), new InventorySelectionKey(ItemSource.EQUIPMENT, defender.getIndexOfEquippedItem(armor)), damageToArmor * -1));
			
			boolean armorDestroyed = (armor.getCurHp() <= 0 ? true : false);
			
			if (armorDestroyed)
			{
				sendEventToObservers(InternalEvent.deleteHeldItemInternalEvent(gameData.getActorIndex(defender), defender.getIndexOfEquippedItem(armor), 1));
				sendEventToObservers(InternalEvent.createItemOnGroundInternalEvent(ItemType.METAL_SHARD, gameData.getCurrentZone().getCoordsOfActor(defender), 1));
			}
			
			String effect = getDescriptionOfCondition(armorDestroyed, armor.getConditionModifer(), armorConditionBeforeAttack);
			
			if (effect != null)
				attackDetails.setArmorDestroyedMessage(new FormattedMessageBuilder("@2his " + armor.getName() + " is " + effect).setTarget(defender).setSourceVisibility(canSeeSource).setTargetVisibility(canSeeTarget).format());
		}
		
		if (damageToWeapon > 0)
		{
			sendEventToObservers(InternalEvent.changeInventoryItemHpInternalEvent(gameData.getActorIndex(attacker), new InventorySelectionKey(ItemSource.EQUIPMENT, attacker.getIndexOfEquippedItem(weapon)), damageToWeapon * -1));
			
			boolean weaponDestroyed = (weapon.getCurHp() <= 0 ? true : false);
			
			if (weaponDestroyed)
			{
				sendEventToObservers(InternalEvent.deleteHeldItemInternalEvent(gameData.getActorIndex(attacker), attacker.getIndexOfEquippedItem(weapon), 1));
				sendEventToObservers(InternalEvent.createItemOnGroundInternalEvent(ItemType.METAL_SHARD, gameData.getCurrentZone().getCoordsOfActor(attacker), 1));
			}
			
			String effect = getDescriptionOfCondition(weaponDestroyed, weapon.getConditionModifer(), weaponConditionBeforeAttack);
			
			if (effect != null)
				attackDetails.setWeaponDestroyedMessage(new FormattedMessageBuilder("@1his " + weapon.getName() + " is " + effect).setSource(attacker).setSourceVisibility(canSeeSource).setTargetVisibility(canSeeTarget).format());
		}
		
		if (attackDeflected)
			return -1;
			
		return damageToDefender;
	}
	
	private int getItemDR(Item item, Actor itemOwner)
	{
		int DR = item.getDR();
		
		if (itemOwner.hasTrait(ActorTraitType.DURABLE_EQ))
			DR = (int)(item.getDR() * 1.5);
		
		Logger.debug("Actor " + itemOwner.getName() + " is wielding " + item.getName() + "; base DR is " + item.getDR() + " and effective DR is " + DR + ".");
		
		return DR;
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
	
	private class AttackDetails
	{
		private String armorDestroyedMessage = null;
		private String weaponDestroyedMessage = null;
		private Item armorThatAbsorbedAttack = null;
		
		public AttackDetails() {}

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
		
		public Item getArmorThatAbsorbedAttack()
		{
			return armorThatAbsorbedAttack;
		}
		
		public void setArmorThatAbsorbedAttack(Item armor)
		{
			armorThatAbsorbedAttack = armor;
		}
	}
}
