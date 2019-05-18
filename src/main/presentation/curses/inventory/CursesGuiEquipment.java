package main.presentation.curses.inventory;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import main.entity.item.equipment.Equipment;
import main.entity.item.equipment.EquipmentSlot;
import main.logic.Engine;
import main.presentation.GuiState;
import main.presentation.curses.AbtractCursesGuiListInput;
import main.presentation.curses.CursesGui;
import main.presentation.curses.terminal.CursesTerminal;

public class CursesGuiEquipment extends AbtractCursesGuiListInput
{
	private CursesGui parentGui;
	private CursesGuiInventory inventoryUtil;
	private Engine engine;

	public CursesGuiEquipment(CursesGui parentGui, CursesGuiInventory inventoryUtil, Engine engine, CursesTerminal terminal)
	{
		super(terminal);
		
		this.parentGui = parentGui;
		this.inventoryUtil = inventoryUtil;
		this.engine = engine;
	}

	@Override
	public void refresh()
	{
		terminal.clear();
		terminal.print(0, 0, "Equipped Items:", COLOR_LIGHT_GREY);
		
		Equipment equipment = engine.getData().getPlayer().getEquipment();
		List<String> slotStrings = new ArrayList<String>();
		
		for (EquipmentSlot slot : equipment.getEquipmentSlots())
			slotStrings.add(" - " + slot.getName() + ": " + getItemLabel(slot));
		
		printList(slotStrings);
		
		terminal.print(0, 24, "Press [v] to view pack contents.", COLOR_LIGHT_GREY);
		
		terminal.refresh();
	}
	
	private String getItemLabel(EquipmentSlot slot)
	{
		if (slot.getItem() != null)
			return slot.getItem().getNameInPack();
		
		return "(none)";
	}
	
	@Override
	public void handleKeyEvent(KeyEvent ke)
	{
		int code = ke.getKeyCode();
		char keyChar = Character.toLowerCase(ke.getKeyChar());
		
		if (code == KeyEvent.VK_ESCAPE)
		{
			parentGui.setCurrentState(GuiState.NONE);
			return;
		}
		
		if (keyChar == 'v')
		{
			parentGui.setCurrentState(GuiState.INVENTORY);
			inventoryUtil.setState(InventoryState.VIEW);
			return;
		}
		
		int slotIndex = getSelectedIndex(ke.getKeyChar());
		
		if (slotIndex < 0 || slotIndex > (getElementCount() - 1))
			return;
		
		Equipment equipment = engine.getData().getPlayer().getEquipment();
		EquipmentSlot slot = equipment.getEquipmentSlots().get(slotIndex);
		
		if (slot.getItem() != null)
		{
			engine.receiveCommand("UNEQUIP" + slotIndex);
			refresh();
		} else
		{
			inventoryUtil.setState(InventoryState.EQUIP);
			inventoryUtil.setFilter(slot.getType());
			inventoryUtil.setEquipSlotIndex(slotIndex);
			parentGui.setCurrentState(GuiState.INVENTORY);
		}
	}
}
