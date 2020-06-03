package main.data.event;

import main.entity.item.InventorySelectionKey;
import main.logic.Direction;
import main.logic.RPGlib;

public class ActorCommand
{
	private final ActorCommandType type;
	private final String argument1;
	private final String argument2;
	
	public ActorCommand(ActorCommandType type)
	{
		this(type, null, null);
	}
	
	public ActorCommand(ActorCommandType type, String argument1)
	{
		this(type, argument1, null);
	}
	
	public ActorCommand(ActorCommandType type, String argument1, String argument2)
	{
		this.type = type;
		this.argument1 = argument1;
		this.argument2 = argument2;
	}

	public ActorCommandType getType()
	{
		return type;
	}

	public String getArgument1()
	{
		return argument1;
	}

	public String getArgument2()
	{
		return argument2;
	}
	
	public ActorCommand addArgument(String argument)
	{
		if (argument2 != null)
			throw new IllegalStateException("Cannot add argument to an Actor command that already has two arguments defined.");
		if (argument1 != null)
			return new ActorCommand(type, argument1, argument);
		return new ActorCommand(type, argument);
	}
	
	@Override
	public String toString()
	{
		return type.name() + "[" + argument1 + "," + argument2 + "]";
	}
	
	public static ActorCommand save()
	{
		return new ActorCommand(ActorCommandType.SAVE);
	}
	
	public static ActorCommand exit()
	{
		return new ActorCommand(ActorCommandType.EXIT);
	}
	
	public static ActorCommand move(Direction direction)
	{
		return new ActorCommand(ActorCommandType.MOVE, RPGlib.stringValue(direction));
	}
	
	public static ActorCommand chat(Direction direction)
	{
		return new ActorCommand(ActorCommandType.CHAT, RPGlib.stringValue(direction));
	}

	public static ActorCommand changeZoneDown()
	{
		return new ActorCommand(ActorCommandType.CHANGE_ZONE_DOWN);
	}

	public static ActorCommand changeZoneUp()
	{
		return new ActorCommand(ActorCommandType.CHANGE_ZONE_UP);
	}

	public static ActorCommand pickUp()
	{
		return new ActorCommand(ActorCommandType.PICKUP);
	}

	public static ActorCommand drop(InventorySelectionKey key)
	{
		return new ActorCommand(ActorCommandType.DROP, RPGlib.stringValue(key));
	}

	public static ActorCommand unqeuip(InventorySelectionKey originalInventorySlot, InventorySelectionKey targetInventorySlot)
	{
		return new ActorCommand(ActorCommandType.UNEQUIP, RPGlib.stringValue(originalInventorySlot), RPGlib.stringValue(targetInventorySlot));
	}

	public static ActorCommand equip(InventorySelectionKey originalInventorySlot, InventorySelectionKey targetInventorySlot)
	{
		return new ActorCommand(ActorCommandType.EQUIP, RPGlib.stringValue(originalInventorySlot), RPGlib.stringValue(targetInventorySlot));
	}

	public static ActorCommand use(InventorySelectionKey inventorySlot, Direction direction)
	{
		return new ActorCommand(ActorCommandType.USE, RPGlib.stringValue(inventorySlot), RPGlib.stringValue(direction));
	}
}
