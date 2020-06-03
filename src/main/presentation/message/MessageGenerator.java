package main.presentation.message;

import main.data.Data;
import main.data.event.EventObserver;
import main.data.event.InternalEvent;
import main.entity.actor.Actor;
import main.entity.item.Item;
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

		switch (internalEvent.getInternalEventType())
		{
		case PICKUP:
			Tile tile = data.getCurrentZone().getTile(actor);
			Item item = tile.getItemHere();
			MessageBuffer.addMessage(new FormattedMessageBuilder("@1the pick%1s up " + item.getNameOnGround() + ".").setSource(actor).setSourceVisibility(engine.canPlayerSeeActor(actor)).format());
			break;
			//$CASES-OMITTED$
		default:
			break;
		}
	}

	public void setEngine(Engine engine)
	{
		this.engine = engine;
		this.data = engine.getData();
	}
}
