package main.data.event;

import main.entity.item.InventorySelectionKey;
import main.entity.item.recipe.Recipe;
import main.logic.Direction;
import main.logic.RPGlib;

public class ActorCommand
{
	private final GuiCommandType type;
	private final String argument1;
	private final String argument2;
	
	public ActorCommand(GuiCommandType type)
	{
		this(type, null, null);
	}
	
	public ActorCommand(GuiCommandType type, String argument1)
	{
		this(type, argument1, null);
	}
	
	public ActorCommand(GuiCommandType type, String argument1, String argument2)
	{
		this.type = type;
		this.argument1 = argument1;
		this.argument2 = argument2;
	}

	public GuiCommandType getType()
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
		return new ActorCommand(GuiCommandType.SAVE);
	}
	
	public static ActorCommand exit()
	{
		return new ActorCommand(GuiCommandType.EXIT);
	}
	
	public static ActorCommand move(Direction direction)
	{
		return new ActorCommand(GuiCommandType.MOVE, RPGlib.stringValue(direction));
	}
	
	public static ActorCommand chat(Direction direction)
	{
		return new ActorCommand(GuiCommandType.CHAT, RPGlib.stringValue(direction));
	}
	
	public static ActorCommand repeat(Direction direction)
	{
		return new ActorCommand(GuiCommandType.REPEAT, RPGlib.stringValue(direction));
	}

	public static ActorCommand changeZoneDown()
	{
		return new ActorCommand(GuiCommandType.CHANGE_ZONE_DOWN);
	}

	public static ActorCommand changeZoneUp()
	{
		return new ActorCommand(GuiCommandType.CHANGE_ZONE_UP);
	}

	public static ActorCommand pickUp()
	{
		return new ActorCommand(GuiCommandType.PICKUP);
	}

	public static ActorCommand drop(InventorySelectionKey key)
	{
		return new ActorCommand(GuiCommandType.DROP, RPGlib.stringValue(key));
	}

	public static ActorCommand unequip(InventorySelectionKey originalInventorySlot, InventorySelectionKey targetInventorySlot)
	{
		return new ActorCommand(GuiCommandType.UNEQUIP, RPGlib.stringValue(originalInventorySlot), RPGlib.stringValue(targetInventorySlot));
	}

	public static ActorCommand equip(InventorySelectionKey originalInventorySlot, InventorySelectionKey targetInventorySlot)
	{
		return new ActorCommand(GuiCommandType.EQUIP, RPGlib.stringValue(originalInventorySlot), RPGlib.stringValue(targetInventorySlot));
	}

	public static ActorCommand use(InventorySelectionKey inventorySlot, Direction direction)
	{
		return new ActorCommand(GuiCommandType.USE, RPGlib.stringValue(inventorySlot), RPGlib.stringValue(direction));
	}

	public static ActorCommand upgrade(InventorySelectionKey baseItemInventorySlot, InventorySelectionKey enhancingItemInventorySlot)
	{
		return new ActorCommand(GuiCommandType.UPGRADE, RPGlib.stringValue(baseItemInventorySlot), RPGlib.stringValue(enhancingItemInventorySlot));
	}
	
	public static ActorCommand repair(InventorySelectionKey inventorySlot, int hpChange)
	{
		return new ActorCommand(GuiCommandType.REPAIR, RPGlib.stringValue(inventorySlot), String.valueOf(hpChange));
	}
	
	public static ActorCommand recipe(Recipe recipe)
	{
		return new ActorCommand(GuiCommandType.RECIPE, recipe.getResultingItem().name());
	}
}
