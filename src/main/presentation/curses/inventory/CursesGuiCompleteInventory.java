package main.presentation.curses.inventory;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import main.data.event.ActorCommand;
import main.data.event.ActorCommandType;
import main.entity.actor.Actor;
import main.entity.item.Inventory;
import main.entity.item.Item;
import main.entity.item.InventorySelectionKey;
import main.entity.item.ItemSource;
import main.entity.item.equipment.Equipment;
import main.entity.item.equipment.EquipmentSlot;
import main.entity.item.equipment.EquipmentSlotType;
import main.logic.Engine;
import main.logic.RPGlib;
import main.presentation.GuiState;
import main.presentation.Logger;
import main.presentation.curses.AbstractCursesGuiListInput;
import main.presentation.curses.ColorScheme;
import main.presentation.curses.CursesGui;
import main.presentation.curses.terminal.CursesTerminal;

public class CursesGuiCompleteInventory extends AbstractCursesGuiListInput
{
	private static final String EMPTY_LABEL = "(empty)";
	private static final int GROUND_KEY_INDEX = -52;
	private static final int ZERO_KEY_INDEX = -49;

	private CursesGui parentGui;
	private Engine engine;

	private InventoryState state = InventoryState.VIEW;
	private InventoryState previousState = null;
	private EquipmentSlotType filter = null;

	private List<InventorySelectionKey> slotSelectionMapping = new ArrayList<InventorySelectionKey>();
	private int materialSlotCount = 0;
	private boolean groundIsSelectable = false;
	private int selectedEquipmentIndex = -1;
	
	private InventorySelectionKey itemToUse = null;

	private static final int BACKPACK_SIZE = 12; // probably should be defined and constraint in Inventory, but this is good for now

	public CursesGuiCompleteInventory(CursesGui parentGui, Engine engine, CursesTerminal terminal, ColorScheme colorScheme)
	{
		super(terminal, colorScheme);
		this.parentGui = parentGui;
		this.engine = engine;
	}

	@Override
	public void refresh()
	{
		slotSelectionMapping.clear();
		groundIsSelectable = false;

		terminal.clear();
		drawBorders();
		populateInventory();
		displayStateMessage();

		switch (state)
		{
		case VIEW:
			labelEquipment();
			break;
		case EQUIP:
			labelAvailableItemsForSelectedSlot();
			break;
		case DROP:
		case USE:
			labelEquipment();
			labelMaterials();
			labelMagic();
			break;
		}
	}

	public void setState(InventoryState state)
	{
		this.state = state;
	}

	@Override
	public void handleKeyEvent(KeyEvent ke)
	{
		Actor player = engine.getData().getPlayer();

		int code = ke.getKeyCode();
		char keyChar = ke.getKeyChar();

		if (code == KeyEvent.VK_ESCAPE)
		{
			switch (state)
			{
			// all of these intentionally fall through
			case VIEW:
			case USE:
			case DROP:
				if (previousState == null)
					parentGui.setCurrentState(GuiState.NONE);
				else
					state = InventoryState.VIEW;
			case EQUIP:
				filter = null;
				state = InventoryState.VIEW;
			}
			
			selectedEquipmentIndex = -1;
			previousState = null;
			
			return;
		}
		
		if (state == InventoryState.VIEW && keyChar == 'u')
		{
			previousState = state;
			state = InventoryState.USE;
			return;
		}
		
		if (state == InventoryState.VIEW && keyChar == '-')
		{
			previousState = state;
			state = InventoryState.DROP;
			return;
		}

		int selectionIndex = getSelectedIndex(keyChar);

		if (!selectionIndexIsValid(selectionIndex))
			return;

		InventorySelectionKey selection = null;

		if (selectionIndex == GROUND_KEY_INDEX)
		{
			selection = new InventorySelectionKey(ItemSource.GROUND, 0);
		} else if (selectionIndexSpecifiesNumber(selectionIndex))
		{
			selection = getMaterialSelection(selectionIndex);
		} else
		{
			selection = slotSelectionMapping.get(selectionIndex);
		}

		int selectionSlotIndex = selection.getItemIndex();

		switch (state)
		{
		case VIEW:
			Equipment equipment = player.getEquipment();
			EquipmentSlot slot = equipment.getEquipmentSlots().get(selectionSlotIndex);

			if (slot.getItem() != null)
			{
				unequipItem(slot.getItem(), selection);
			} else
			{
				state = InventoryState.EQUIP;
				filter = slot.getType();
				selectedEquipmentIndex = selectionSlotIndex;
			}

			break;
		case EQUIP:
			engine.receiveCommand(ActorCommand.equip(selection, new InventorySelectionKey(ItemSource.EQUIPMENT, selectedEquipmentIndex)));
			state = InventoryState.VIEW;
			selectedEquipmentIndex = -1;
			break;
		case DROP:
			engine.receiveCommand(ActorCommand.drop(selection));
			state = InventoryState.VIEW;
			parentGui.setCurrentState(GuiState.NONE);
			break;
		case USE:
			itemToUse = selection;
			state = InventoryState.VIEW;
			parentGui.setCurrentState(GuiState.NONE);
			break;
		}
	}
	
	public InventorySelectionKey getAndClearItemToUse()
	{
		InventorySelectionKey returnVal = itemToUse;
		itemToUse = null;
		return returnVal;
	}

	private void unequipItem(Item item, InventorySelectionKey selection)
	{
		Inventory inventory = engine.getData().getPlayer().getStoredItems();

		if (inventory.hasSpaceForItem(item))
		{
			engine.receiveCommand(ActorCommand.unqeuip(selection, new InventorySelectionKey(ItemSource.PACK, -1)));
			refresh();
			return;
		}
		
		if (getItemOnGround() == null)
		{
			engine.receiveCommand(ActorCommand.drop(selection)); 
			refresh();
			return;
		}
		
		//if there's no space in the pack or on the current tile, the player can't drop directly from equipped
	}

	private boolean selectionIndexIsValid(int selectionIndex)
	{
		Logger.debug("Checking for validity of selection index " + selectionIndex);

		//null values are for fixed slots that can't be selected (equipment)
		if (selectionIndex >= 0 && selectionIndex < slotSelectionMapping.size() && slotSelectionMapping.get(selectionIndex) != null)
			return true;

		if (selectionIndex == GROUND_KEY_INDEX && groundIsSelectable)
			return true;
		
		if (selectionIndexSpecifiesNumber(selectionIndex) && convertMaterialSelectionToIndex(selectionIndex) < materialSlotCount)	//TODO: this might not handle 0 well
			return true;

		// TODO: add whatever keys magic items map to once that's a thing

		return false;
	}
	
	private boolean selectionIndexSpecifiesNumber(int selectionIndex)
	{
		if (selectionIndex >= ZERO_KEY_INDEX && selectionIndex <= ZERO_KEY_INDEX + 9)
			return true;
		
		return false;
	}
	
	private int convertMaterialSelectionToIndex(int selectionIndex)
	{
		int selectionNumber = selectionIndex + 48;
		
		if (selectionNumber == -1)
			selectionNumber = 9;
		
		return selectionNumber;
	}
	
	private InventorySelectionKey getMaterialSelection(int selectionIndex)
	{
		return new InventorySelectionKey(ItemSource.MATERIAL, convertMaterialSelectionToIndex(selectionIndex));
	}

	private void drawBorders()
	{
		// equipment letter borders
		for (int i = 1; i <= 10; i++)
			terminal.print(0, i, "[ ]", getBorderColor());

		// material letter borders
		for (int i = 12; i <= 21; i++)
			terminal.print(0, i, "[ ]", getBorderColor());

		// readied letter borders
		for (int i = 1; i <= 4; i++)
			terminal.print(43, i, "|[ ]", getBorderColor());

		// stored letter borders
		for (int i = 6; i <= 17; i++)
			terminal.print(43, i, "|[ ]", getBorderColor());

		// magic letter borders
		for (int i = 19; i <= 21; i++)
			terminal.print(43, i, "|[  ]", getBorderColor());

		// ground letter border
		terminal.print(43, 23, "|[ ]", getBorderColor());

		terminal.print(0, 0, "-----------------[", getBorderColor());
		terminal.print(18, 0, "Equipped", getTitleColor());
		terminal.print(26, 0, "]----------------+--------------[", getBorderColor());
		terminal.print(59, 0, "Readied", getTitleColor());
		terminal.print(66, 0, "]-------------", getBorderColor());

		terminal.print(0, 11, "----------------[", getBorderColor());
		terminal.print(17, 11, "Materials", getTitleColor());
		terminal.print(26, 11, "]----------------+", getBorderColor());

		/*
		terminal.print(0, 22, "------------------[", getBorderColor());
		terminal.print(19, 22, "Ground", getTitleColor());
//		terminal.print(25, 22, "]-----------------+", getBorderColor());
		terminal.print(25, 22, "]-----------------+------------------------------------", getBorderColor());
		*/
		terminal.print(0, 22, "-------------------------------------------+-------------[", getBorderColor());
		terminal.print(58, 22, "On Ground", getTitleColor());
		terminal.print(67, 22, "]------------", getBorderColor());
		

		terminal.print(43, 5, "+----------[", getBorderColor());
		terminal.print(55, 5, "Stored (  /  )", getTitleColor());
		terminal.print(69, 5, "]----------", getBorderColor());
		terminal.print(66, 5, String.valueOf(BACKPACK_SIZE), getHighlightColor());

		terminal.print(43, 18, "+---------------[", getBorderColor());
		terminal.print(60, 18, "Magic", getTitleColor());
		terminal.print(65, 18, "]--------------", getBorderColor());

		terminal.print(0, 24, "-------------------------------------------+------------------------------------", getBorderColor());
	}
	
	private void displayStateMessage()
	{
		String action = "";
		String message = "";
		switch(state)
		{
		case VIEW:
			message = "[a-j,F1-F3] (Un)Equip, [u] Use, [-] Drop";
			break;
		case DROP:
			message = "[a-z] Drop Item, [ESC] Cancel";
			action = "DROP: ";
			break;
		case EQUIP:
			message = "[a-l] Wear/Wield Item, [ESC] Cancel";
			action = "EQUIP: ";
			break;
		case USE:
//			message = "[0-9] Use Material, [ESC] Cancel";	//TODO: maybe this should be 'c' for "craft" instead?
			message = "[a-z,0-9] Use Item, [ESC] Cancel";
			action = "USE: ";
		default:
			break;
		}
		
		if (message.length() > 41)
			message = message.substring(0, 41);
		
		terminal.print(1, 23, action, getTitleColor());
		terminal.print(action.length() + 1, 23, message, getHighlightColor());
	}

	private void populateInventory()
	{
		populatePack();
		populateEquipment();
		populateMaterials();
		populateMagic();
		populateGround();
	}

	private void populatePack()
	{
		Inventory inventory = engine.getData().getPlayer().getStoredItems();
		List<Item> itemsInInventory = inventory.getItemsForSlot(null);

		int packSlots = itemsInInventory.size() + (BACKPACK_SIZE - inventory.getBulk());

		for (int i = 0; i < packSlots; i++)
		{
			String label = EMPTY_LABEL;

			if (i < itemsInInventory.size())
				label = itemsInInventory.get(i).getNameInPack();

			terminal.print(48, 6 + i, label, getTextColor());
		}
		
		String bulkString = String.valueOf(inventory.getBulk());
		terminal.print(63, 5, RPGlib.padStringLeft(bulkString, 2, '0'), getHighlightColor());
	}

	private void populateEquipment()
	{
		List<EquipmentSlot> slots = engine.getData().getPlayer().getEquipment().getEquipmentSlots();

		for (int i = 0; i < slots.size(); i++)
		{
			EquipmentSlot slot = slots.get(i);
			String slotName = RPGlib.padStringRight(slot.getShortName() + ": ", 6, ' ');
			terminal.print(4, 1 + i, slotName + getItemLabel(slot), getTextColor());
		}
	}

	private void populateMaterials()
	{
		Inventory inventory = engine.getData().getPlayer().getMaterials();
		List<Item> itemsInInventory = inventory.getItemsForSlot(null);

		int packSlots = 10;

		for (int i = 0; i < packSlots; i++)
		{
			String label = EMPTY_LABEL;

			if (i < itemsInInventory.size())
				label = itemsInInventory.get(i).getNameInPack();

			terminal.print(4, 12 + i, label, getTextColor());
		}
	}

	private void populateMagic()
	{
		List<EquipmentSlot> slots = engine.getData().getPlayer().getMagicItems().getEquipmentSlots();

		int magicSize = 3;
		
		for (int i = 0; i < magicSize; i++)
		{
			String label = EMPTY_LABEL;

			if (i < slots.size())
				label = getItemLabel(slots.get(i));

			terminal.print(49, 19 + i, label, getTextColor());
		}
	}

	private void populateGround()
	{
		Item itemOnGround = getItemOnGround();
		String label = EMPTY_LABEL;

		if (itemOnGround != null)
			label = itemOnGround.getNameInPack();

		terminal.print(48, 23, label, getTextColor());
	}

	private String getItemLabel(EquipmentSlot slot)
	{
		if (slot.getItem() != null)
			return slot.getItem().getNameInPack();

		return EMPTY_LABEL;
	}

	private void labelEquipment()
	{
		List<EquipmentSlot> slots = engine.getData().getPlayer().getEquipment().getEquipmentSlots();
		for (int i = 0; i < slots.size(); i++)
		{
			boolean selectable = true;

			EquipmentSlot slot = slots.get(i);
			Item slotItem = slot.getItem();

			if (slotItem == null && state != InventoryState.DROP && !itemAvailableForSlot(slot.getType())) // empty slots, but nothing can go there
				selectable = false;
			
			if (slotItem == null && state == InventoryState.DROP)
				selectable = false;

			if (slotItem != null && state != InventoryState.DROP && !spaceToRemoveItem(slotItem))	//TODO: temporary fix because removing an item can only put it on the player's tile, but drop can "bubble out" - perhaps make it so removing can't drop the item? 
				selectable = false;

			if (selectable)
			{
				terminal.print(1, 1 + i, getLetterForSelectionIndex(i), getTitleColor());
				slotSelectionMapping.add(new InventorySelectionKey(ItemSource.EQUIPMENT, i));
			}
			else
			{
				slotSelectionMapping.add(null);
			}
		}
	}

	private void labelMaterials()
	{
		Inventory inventory = engine.getData().getPlayer().getMaterials();
		List<Item> itemsInInventory = inventory.getItemsForSlot(null);
		materialSlotCount = itemsInInventory.size();

		for (int i = 0; i < itemsInInventory.size(); i++)
		{
			terminal.print(1, 12 + i, getNumberForSelectionIndex(i), getTitleColor());
		}
	}

	private void labelMagic()
	{
		List<EquipmentSlot> slots = engine.getData().getPlayer().getMagicItems().getEquipmentSlots();
		for (int i = 0; i < slots.size(); i++)
		{
			boolean selectable = true;

			EquipmentSlot slot = slots.get(i);
			Item slotItem = slot.getItem();

			if (slotItem == null && state != InventoryState.DROP && !itemAvailableForSlot(slot.getType())) // empty slots, but nothing can go there
				selectable = false;
			
			if (slotItem == null && state == InventoryState.DROP)
				selectable = false;

			if (slotItem != null && state != InventoryState.DROP && !spaceToRemoveItem(slotItem))	//TODO: temporary fix because removing an item can only put it on the player's tile, but drop can "bubble out" - perhaps make it so removing can't drop the item? 
				selectable = false;

			if (selectable)
			{
				terminal.print(45, 19 + i, "F" + (i + 1), getTitleColor());
				slotSelectionMapping.add(new InventorySelectionKey(ItemSource.MAGIC, i));
			}
			else
			{
				slotSelectionMapping.add(null);
			}
		}
	}

	private void labelAvailableItemsForSelectedSlot()
	{
		if (selectedEquipmentIndex != -1)
			terminal.print(1, 1 + selectedEquipmentIndex, ">", getHighlightColor());

		Inventory inventory = engine.getData().getPlayer().getStoredItems();
		List<Item> itemsInInventory = inventory.getItemsForSlot(null);

		// TODO: include readied items

		for (int i = 0; i < itemsInInventory.size(); i++)
		{
			Item inventoryItem = itemsInInventory.get(i);

			if (state != InventoryState.DROP && inventoryItem.getInventorySlot() != filter)
				continue;

			terminal.print(45, 6 + i, getLetterForSelectionIndex(slotSelectionMapping.size()), getTitleColor());
			slotSelectionMapping.add(new InventorySelectionKey(ItemSource.PACK, i));
		}

		Item groundItem = getItemOnGround();
		if (state != InventoryState.DROP && groundItem != null && groundItem.getInventorySlot() == filter)
		{
			terminal.print(45, 23, "-", getTitleColor());
			groundIsSelectable = true;
		}
	}

	private String getLetterForSelectionIndex(int selectionIndex)
	{
		return "" + (char) (65 + selectionIndex); // 97 for lowercase
	}

	private String getNumberForSelectionIndex(int selectionIndex)
	{
		if (selectionIndex == 9)
			return "0";
		
		return "" + (selectionIndex + 1);
	}

	private Item getItemOnGround()
	{
		return engine.getData().getCurrentZone().getTile(engine.getData().getPlayer()).getItemHere();
	}

	private List<Item> getStoredItemsOfType(EquipmentSlotType type)
	{
		Inventory inventory = engine.getData().getPlayer().getStoredItems();
		List<Item> items = inventory.getItemsForSlot(type);
		inventory.resetFilters();
		return items;
	}

	private boolean itemAvailableForSlot(EquipmentSlotType type)
	{
		// TODO: include Readied Items in the check
		if (!getStoredItemsOfType(type).isEmpty())
			return true;

		Item groundItem = getItemOnGround();

		if (groundItem == null)
			return false; // if the was something eligible in the pack, we wouldn't have gotten here

		return groundItem.getInventorySlot() == type;
	}

	private boolean spaceToRemoveItem(Item item)
	{
		Inventory inventory = engine.getData().getPlayer().getStoredItems();

		if (inventory.hasSpaceForItem(item))
			return true;

		if (getItemOnGround() == null)
			return true;

		return false;
	}
}
