package main.presentation.message;

import main.data.Data;
import main.data.event.EventObserver;
import main.data.event.InternalEvent;
import main.entity.actor.Actor;
import main.entity.item.Item;
import main.entity.item.ItemSource;
import main.entity.item.equipment.EquipmentSlotType;
import main.entity.tile.Tile;
import main.logic.Engine;
import main.presentation.Logger;

public class MessageGenerator implements EventObserver
{	
	private static MessageGenerator instance = null;
	private Data data = null;
	private Engine engine = null;
	
	private MessageGenerator() {}
	
	public static MessageGenerator getInstance()
	{
		if (instance == null)
			instance = new MessageGenerator();
		
		return instance;
	}
	
	@Override
	public void receiveInternalEvent(InternalEvent internalEvent)
	{
		if (internalEvent == null)
		{
			Logger.info("MessageGenerator - NULL event received.");
			return;
		}
		
		Actor actor = data.getActor(internalEvent.getFlag(0));
		ItemSource itemLocation = null;
		Item itemBeingMoved = null;

		switch (internalEvent.getInternalEventType())
		{
		case INTERRUPTION:
			MessageBuffer.addMessageIfHuman(new FormattedMessageBuilder("@1the @1is interrupted!").setSource(actor).format(), actor.getAI());
			break;
		case PICKUP:
			Tile tile = data.getCurrentZone().getTile(actor);
			Item item = tile.getItemHere();
			
			if (internalEvent.getValue().equals(ItemSource.READY.name()))	//TODO: the grammar here (and elsewhere in the file) is bad for non-players ("readys"), but fixing it requires work in FormattedMessageBuilder
				MessageBuffer.addMessage(new FormattedMessageBuilder("@1the read%1[y|ies] " + item.getNameOnGround() + ".").setSource(actor).setSourceVisibility(engine.canPlayerSeeActor(actor)).format());
			else if (item.getInventorySlot() == EquipmentSlotType.MATERIAL)
				MessageBuffer.addMessage(new FormattedMessageBuilder("@1the collect%1s " + item.getNameOnGround() + ".").setSource(actor).setSourceVisibility(engine.canPlayerSeeActor(actor)).format());
			else if (item.getInventorySlot() == EquipmentSlotType.MAGIC && actor.getMagicItems().hasEmptySlotAvailable(EquipmentSlotType.MAGIC))	//I hate that this logic is here, but for now it's how it has to be
				MessageBuffer.addMessage(new FormattedMessageBuilder("@1the pick%1s up " + item.getNameOnGround() + " and prepare%1s it.").setSource(actor).setSourceVisibility(engine.canPlayerSeeActor(actor)).format());
			else
				MessageBuffer.addMessage(new FormattedMessageBuilder("@1the store%1s " + item.getNameOnGround() + " in @1his pack.").setSource(actor).setSourceVisibility(engine.canPlayerSeeActor(actor)).format());
			
			break;
		case UNEQUIP:
			itemLocation = ItemSource.fromInt(internalEvent.getFlag(3));
			itemBeingMoved = getItemFromSourceFlags(internalEvent);
			if (itemLocation == ItemSource.PACK)
				MessageBuffer.addMessage(new FormattedMessageBuilder("@1the store%1s " + itemBeingMoved.getNameOnGround() + " in @1his pack.").setSource(actor).setSourceVisibility(engine.canPlayerSeeActor(actor)).format());
			else if (itemLocation == ItemSource.READY)
				MessageBuffer.addMessage(new FormattedMessageBuilder("@1the read%1[y|ies] " + itemBeingMoved.getNameOnGround() + " @1he @1was wielding.").setSource(actor).setSourceVisibility(engine.canPlayerSeeActor(actor)).format());
			break;
		case EQUIP:
			itemLocation = ItemSource.fromInt(internalEvent.getFlag(3));
			ItemSource originalItemLocation = ItemSource.fromInt(internalEvent.getFlag(1));
			itemBeingMoved = getItemFromSourceFlags(internalEvent);
			
			if (originalItemLocation == ItemSource.GROUND)
				itemBeingMoved = data.getCurrentZone().getTile(actor).getItemHere();
			
			if (itemLocation == ItemSource.EQUIPMENT && !(originalItemLocation == ItemSource.GROUND && itemBeingMoved.getInventorySlot() == EquipmentSlotType.ARMAMENT))
				MessageBuffer.addMessage(new FormattedMessageBuilder("@1the equip%1s " + itemBeingMoved.getNameOnGround() + ".").setSource(actor).setSourceVisibility(engine.canPlayerSeeActor(actor)).format());
			break;
		case DROP:
			itemLocation = ItemSource.fromInt(internalEvent.getFlag(1));
			itemBeingMoved = getItemFromSourceFlags(internalEvent);
			if (itemLocation == ItemSource.READY)
				MessageBuffer.addMessage(new FormattedMessageBuilder("@1the drop%1s " + itemBeingMoved.getNameOnGround() + " @1he had readied.").setSource(actor).setSourceVisibility(engine.canPlayerSeeActor(actor)).format());
			else if (!(itemLocation == ItemSource.EQUIPMENT && itemBeingMoved.getInventorySlot() == EquipmentSlotType.ARMAMENT))	//if dropping from hands, the action is instant so the message has already been displayed
				MessageBuffer.addMessage(new FormattedMessageBuilder("@1the drop%1s " + itemBeingMoved.getNameOnGround() + ".").setSource(actor).setSourceVisibility(engine.canPlayerSeeActor(actor)).format());
			break;
			//$CASES-OMITTED$
		default:
			break;
		}
	}
	
	private Item getItemFromSourceFlags(InternalEvent event)
	{
		Actor actor = data.getActor(event.getFlag(0));
		ItemSource itemSource = ItemSource.fromInt(event.getFlag(1));
		int itemIndex = event.getFlag(2);
		return actor.getItem(itemSource, itemIndex);
	}

	public void setEngine(Engine engine)
	{
		this.engine = engine;
		this.data = engine.getData();
	}
}
