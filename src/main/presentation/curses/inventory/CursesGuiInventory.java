package main.presentation.curses.inventory;

import java.awt.event.KeyEvent;
import java.util.List;

import main.entity.actor.Actor;
import main.entity.item.Item;
import main.entity.item.equipment.EquipmentSlotType;
import main.logic.Engine;
import main.presentation.GuiState;
import main.presentation.Logger;
import main.presentation.curses.CursesGui;
import main.presentation.curses.CursesGuiUtil;
import main.presentation.curses.terminal.CursesTerminal;

public class CursesGuiInventory extends CursesGuiUtil
{
	private CursesGui parentGui;
	private Engine engine;
	
	private InventoryState state = InventoryState.VIEW;
	private EquipmentSlotType filter = null;
	
	private int itemsInPack = 0;
	private int equipSlotIndex = -1;
	
	public CursesGuiInventory(CursesGui parentGui, Engine engine, CursesTerminal terminal)
	{
		super(terminal);
		
		this.parentGui = parentGui;
		this.engine = engine;
	}
	
	@Override
	public void refresh()
	{
		terminal.clear();
		terminal.print(0, 0, getScreenTitle(), COLOR_LIGHT_GREY);
		
		Actor player = engine.getData().getPlayer();
		
		itemsInPack = 0;
		
		List<Item> itemsInInventory = player.getInventory().getItemsOfType(filter);
		
		for (Item item : itemsInInventory)
		{
			itemsInPack++;
			terminal.print(1, itemsInPack, (char)(96 + itemsInPack) + ") " + item.getNameInPack(), COLOR_LIGHT_GREY);
		}
		
		if (itemsInPack == 0)
			terminal.print(1, 1, "(empty)", COLOR_LIGHT_GREY);
		
		terminal.refresh();
	}

	@Override
	public void handleKeyEvent(KeyEvent ke)
	{
		int code = ke.getKeyCode();
		char keyChar = Character.toLowerCase(ke.getKeyChar());
		
		if (code == KeyEvent.VK_ESCAPE)
		{
			filter = null;
			
			if (state == InventoryState.EQUIP || state == InventoryState.VIEW)	//viewing also comes from the equipment screen, so return there
				parentGui.setCurrentState(GuiState.EQUIPMENT);
			else
				parentGui.setCurrentState(GuiState.NONE);
			
			return;
		}
		
		if (state == InventoryState.VIEW)
			return;		//no commands other than exiting if we're just viewing the pack contents
		
		int itemIndex = (int)(keyChar) - 97;
		
		if (itemIndex < 0 || itemIndex > (itemsInPack - 1))
			return;
		
		Logger.info("Key " + keyChar + " pressed; this translates to an item index of " + itemIndex + ".");
		
		filter = null;	//any valid selection should always clear the filter, since even if you're doing an "inspect" or some such command, you won't be returned to the inventory
		
		if (state == InventoryState.DROP)
		{
			engine.receiveCommand("DROP" + itemIndex);
			parentGui.setCurrentState(GuiState.NONE);
		} else if (state == InventoryState.EQUIP && equipSlotIndex >= 0)
		{
			engine.receiveCommand("EQUIP" + equipSlotIndex + itemIndex);
			parentGui.setCurrentState(GuiState.EQUIPMENT);
			equipSlotIndex = -1;
		}
		
		state = InventoryState.VIEW;
	}
	
	public void setState(InventoryState state)
	{
		this.state = state;
	}
	
	public void setFilter(EquipmentSlotType filter)
	{
		this.filter = filter;
		
		if (filter == EquipmentSlotType.ANY)
			this.filter = null;
	}

	public void setEquipSlotIndex(int equipSlotIndex)
	{
		this.equipSlotIndex = equipSlotIndex;
	}
	
	private String getScreenTitle()
	{
		if (state == InventoryState.DROP)
			return "Choose an item to drop:";
		else if (state == InventoryState.EQUIP)
			return "Choose an item to equip:";
		
		return "Pack Contents:";
	}
}