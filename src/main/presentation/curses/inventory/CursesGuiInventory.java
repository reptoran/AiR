package main.presentation.curses.inventory;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import main.data.event.ActorCommand;
import main.entity.actor.Actor;
import main.entity.item.InventorySelectionKey;
import main.entity.item.Item;
import main.entity.item.ItemSource;
import main.entity.item.equipment.EquipmentSlotType;
import main.logic.Engine;
import main.presentation.GuiState;
import main.presentation.curses.AbstractCursesGuiListInput;
import main.presentation.curses.ColorScheme;
import main.presentation.curses.CursesGui;
import main.presentation.curses.terminal.CursesTerminal;

public class CursesGuiInventory extends AbstractCursesGuiListInput
{
	private CursesGui parentGui;
	private Engine engine;
	
	private InventoryState state = InventoryState.VIEW;
	private EquipmentSlotType filter = null;
	
	private int equipSlotIndex = -1;
	
	public CursesGuiInventory(CursesGui parentGui, Engine engine, CursesTerminal terminal)
	{
		super(terminal, ColorScheme.monochromeScheme());
		
		this.parentGui = parentGui;
		this.engine = engine;
	}
	
	@Override
	public void refresh()
	{
		terminal.clear();
		terminal.print(0, 0, getScreenTitle(), COLOR_LIGHT_GREY);
		
		Actor player = engine.getData().getPlayer();
		
		List<Item> itemsInInventory = player.getStoredItems().getItemsForSlot(filter);
		List<String> itemStrings = new ArrayList<String>();
		
		if (itemsInInventory.isEmpty())
		{
			terminal.print(1, 1, "(empty)", COLOR_LIGHT_GREY);
			terminal.refresh();
			return;
		}
		
		for (Item item : itemsInInventory)
			itemStrings.add(") " + item.getNameInPack());
		
		printList(itemStrings);
	}

	@Override
	public void handleKeyEvent(KeyEvent ke)
	{
		Actor player = engine.getData().getPlayer();
		
		int code = ke.getKeyCode();
		
		if (code == KeyEvent.VK_ESCAPE)
		{
			filter = null;
			player.getStoredItems().resetFilters();
			
			if (state == InventoryState.EQUIP || state == InventoryState.VIEW)	//viewing also comes from the equipment screen, so return there
				parentGui.setCurrentState(GuiState.EQUIPMENT);
			else
				parentGui.setCurrentState(GuiState.NONE);
			
			return;
		}
		
		if (state == InventoryState.VIEW)
			return;		//no commands other than exiting if we're just viewing the pack contents
		
		int itemIndex = getSelectedIndex(ke.getKeyChar());
		
		if (itemIndex < 0 || itemIndex > (getElementCount() - 1))
			return;
		
		filter = null;	//any valid selection should always clear the filter, since even if you're doing an "inspect" or some such command, you won't be returned to the inventory
		
		if (state == InventoryState.DROP)
		{
			InventorySelectionKey key = new InventorySelectionKey(ItemSource.PACK, itemIndex);
			engine.receiveCommand(ActorCommand.drop(key));
			parentGui.setCurrentState(GuiState.NONE);
		} else if (state == InventoryState.EQUIP && equipSlotIndex >= 0)
		{
			InventorySelectionKey originKey = new InventorySelectionKey(ItemSource.PACK, itemIndex);
			InventorySelectionKey targetKey = new InventorySelectionKey(ItemSource.EQUIPMENT, equipSlotIndex);
			engine.receiveCommand(ActorCommand.equip(originKey, targetKey));
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