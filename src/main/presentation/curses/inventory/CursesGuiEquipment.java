package main.presentation.curses.inventory;

import java.awt.event.KeyEvent;

import main.entity.item.equipment.Equipment;
import main.entity.item.equipment.EquipmentSlot;
import main.logic.Engine;
import main.presentation.GuiState;
import main.presentation.Logger;
import main.presentation.curses.CursesGui;
import main.presentation.curses.CursesGuiUtil;
import main.presentation.curses.terminal.CursesTerminal;

public class CursesGuiEquipment extends CursesGuiUtil
{
	private CursesGui parentGui;
	private CursesGuiInventory inventoryUtil;
	private Engine engine;
	
	private int equipmentSlots = 0;

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
		
		equipmentSlots = 0;
		
		for (EquipmentSlot slot : equipment.getEquipmentSlots())
		{
			equipmentSlots++;
			char indexLetter = (char)(96 + equipmentSlots);
			terminal.print(1, equipmentSlots, indexLetter + " - " + slot.getName() + ": " + getItemLabel(slot), COLOR_LIGHT_GREY);
		}
		
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
		
		int slotIndex = (int)(keyChar) - 97;
		
		if (slotIndex < 0 || slotIndex > (equipmentSlots - 1))
			return;
		
		Logger.info("Key " + keyChar + " pressed; this translates to a slot index of " + slotIndex + ".");
		
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
