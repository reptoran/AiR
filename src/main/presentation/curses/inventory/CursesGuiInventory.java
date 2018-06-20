package main.presentation.curses.inventory;

import java.awt.event.KeyEvent;
	
import main.entity.actor.Actor;
import main.entity.item.Item;
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
	
	int itemsInPack = 0;
	
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
		
		for (Item item : player.getInventory())
		{
			itemsInPack++;
			terminal.print(1, itemsInPack, (char)(96 + itemsInPack) + ") " + item.getNameInPack(), COLOR_LIGHT_GREY);
		}
		
		if (itemsInPack == 0)
			terminal.print(1, 1, "(empty)", COLOR_LIGHT_GREY);
		
		terminal.refresh();
	}

	public void handleKeyEvent(KeyEvent ke)
	{
		int code = ke.getKeyCode();
		char keyChar = Character.toLowerCase(ke.getKeyChar());
		
		if (code == KeyEvent.VK_ESCAPE)
		{
			parentGui.setCurrentState(GuiState.NONE);
		}
		
		if (state == InventoryState.VIEW)
			return;		//no commands other than exiting if we're just viewing the pack contents
		
		int itemIndex = (int)(keyChar) - 97;
		
		if (itemIndex < 0 || itemIndex > (itemsInPack - 1))
			return;
		
		Logger.debug("Key " + keyChar + " pressed; this translates to an item index of " + itemIndex + ".");
		
		if (state == InventoryState.DROP)
		{
			engine.receiveCommand("DROP" + itemIndex);
			parentGui.setCurrentState(GuiState.NONE);
			state = InventoryState.VIEW;
		}
	}
	
	public void setState(InventoryState state)
	{
		this.state = state;
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