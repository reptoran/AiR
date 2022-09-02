package main.presentation.curses.inventory;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import main.data.event.ActorCommand;
import main.entity.actor.Actor;
import main.entity.item.Inventory;
import main.entity.item.InventorySelectionKey;
import main.entity.item.Item;
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
import main.presentation.message.MessageBuffer;

public class CursesGuiCompleteInventory extends AbstractCursesGuiListInput
{
	private static final String EMPTY_LABEL = "(empty)";
	private static final int GROUND_KEY_INDEX = -52;
	private static final int ZERO_KEY_INDEX = -49;
	private static final int FUNCTION_KEY_MASK = 1000;

	private CursesGui parentGui;
	private Engine engine;

	private InventoryState state = InventoryState.VIEW;
	private InventoryState previousState = null;
	private EquipmentSlotType filter = null;

	private List<InventorySelectionKey> slotSelectionMapping = new ArrayList<InventorySelectionKey>();
	private int materialSlotCount = 0;
	private boolean groundIsSelectable = false;
	
	private InventorySelectionKey selectedEquipment = null;	
	private InventorySelectionKey itemToUseOrUpgrade = null;

	public CursesGuiCompleteInventory(CursesGui parentGui, Engine engine, ColorScheme colorScheme)
	{
		super(colorScheme);
		this.parentGui = parentGui;
		this.engine = engine;
	}

	@Override
	public void refresh()
	{
		slotSelectionMapping.clear();
		groundIsSelectable = false;

		clearScreen();
		drawBorders();
		populateInventory();
		displayStateMessage();

		switch (state)
		{
		case VIEW:
			labelEquipment(true);
			labelReadiedItems(true);
			labelMagic();
			break;
		case EQUIP:
			labelAvailableItemsForSelectedEquippableSlot();
			break;
		case MOVE_READY_ITEM:
			labelAvailableSlotsToMoveReadyItemTo();
			break;
		case UPGRADE_MATERIAL_SELECT:
			labelMaterials();
			break;
		case DROP:		//intentionally falls through
		case USE:
			labelEquipment(false);
			labelReadiedItems(false);
			labelAllStoredItems();
			labelMaterials();
			labelMagic();
			break;
		case UPGRADE_BASE_ITEM_SELECT:
			labelEquipment(false);
			labelReadiedItems(false);
			labelAllStoredItems();
			labelMagic();
			break;
		}
	}

	public void setState(InventoryState state)
	{
		this.state = state;
	}

	@Override
	public void handleKey(int code, char keyChar)
	{
		Actor player = engine.getData().getPlayer();

		if (code == KeyEvent.VK_ESCAPE)
		{
			switch (state)
			{
			// all of these intentionally fall through
			case VIEW:
			case UPGRADE_MATERIAL_SELECT:
			case UPGRADE_BASE_ITEM_SELECT:	//maybe this can be triggered from outside the inventory screen with 'U' (instead of 'u' for use)
			case USE:						// also, instead of blindly calling the base item's upgrade method, perhaps check for a recipe first to see
			case DROP:						// if a completely new item should be created instead
				if (previousState == null)
					parentGui.setSingleLayer(GuiState.MAIN_GAME);
				else
					state = InventoryState.VIEW;
			case EQUIP:
			case MOVE_READY_ITEM:
				filter = null;
				state = InventoryState.VIEW;
			}
			
			selectedEquipment = null;
			previousState = null;
			parentGui.refreshInterface();
			return;
		}
		
		if (state == InventoryState.VIEW && keyChar == 'u' && playerHasMaterials())
		{
			previousState = state;
			state = InventoryState.UPGRADE_BASE_ITEM_SELECT;
			parentGui.refreshInterface();
			return;
		}
		
		if (state == InventoryState.VIEW && keyChar == '-')
		{
			previousState = state;
			state = InventoryState.DROP;
			parentGui.refreshInterface();
			return;
		}
		
		int selectionIndex = getSelectedIndex(keyChar);
		
		if (isFunctionKey(code))
			selectionIndex = FUNCTION_KEY_MASK + code;
		

		if (!selectionIndexIsValid(selectionIndex))
			return;

		InventorySelectionKey selection = null;

		if (selectionIndex == GROUND_KEY_INDEX)
		{
			selection = new InventorySelectionKey(ItemSource.GROUND, 0);
		} else if (selectionIndexSpecifiesNumber(selectionIndex))
		{
			selection = getMaterialSelection(selectionIndex);
		} else if (selectionIndexSpecifiesValidFunctionKey(selectionIndex))
		{
			selection = getMagicSelection(selectionIndex);
		} else
		{
			selection = slotSelectionMapping.get(selectionIndex);
		}

		switch (state)
		{
		case VIEW:
			Equipment equipment = getEquipmentForSelection(player, selection);
			EquipmentSlot slot = equipment.getEquipmentSlots().get(selection.getItemIndex());

			if (slot.getItem() != null && (selection.getItemSource() == ItemSource.EQUIPMENT || selection.getItemSource() == ItemSource.MAGIC))
			{
				unequipItem(slot.getItem(), selection);
			} else if (slot.getItem() != null && selection.getItemSource() == ItemSource.READY)
			{
				state = InventoryState.MOVE_READY_ITEM;
				selectedEquipment = selection.clone();
				parentGui.refreshInterface();
			} else
			{
				state = InventoryState.EQUIP;
				filter = slot.getType();
				selectedEquipment = selection.clone();
				parentGui.refreshInterface();
			}

			break;
		case EQUIP:
			engine.receiveCommand(ActorCommand.equip(selection, selectedEquipment));
			
			state = InventoryState.VIEW;
			selectedEquipment = null;
			
			//TODO: unfortunately, this and the ones in MOVE_READY_ITEM and DROP are required in case the action is interrupted.  not elegant, but good enough
			if (parentGui.getTopLayerType() == GuiState.INVENTORY)
				parentGui.refreshInterface();
			
			return;
		case MOVE_READY_ITEM:
			if (selection.getItemSource() == ItemSource.EQUIPMENT)
				engine.receiveCommand(ActorCommand.equip(selectedEquipment, selection));
			else if (selection.getItemSource() == ItemSource.PACK)
				engine.receiveCommand(ActorCommand.unequip(selectedEquipment, selection));
			else if (selection.getItemSource() == ItemSource.GROUND)
				engine.receiveCommand(ActorCommand.drop(selectedEquipment));
			
			state = InventoryState.VIEW;
			selectedEquipment = null;
			
			if (parentGui.getTopLayerType() == GuiState.INVENTORY)
				parentGui.refreshInterface();
			
			return;
		case DROP:
			engine.receiveCommand(ActorCommand.drop(selection));
			state = InventoryState.VIEW;
			
			//TODO: if the player drops from the main screen (with 'd'), it should return there
			if (parentGui.getTopLayerType() == GuiState.INVENTORY)
				parentGui.refreshInterface();
			
			return;
		case USE:
			itemToUseOrUpgrade = selection;
			state = InventoryState.VIEW;
			attemptToUseItem();
			return;
		case UPGRADE_BASE_ITEM_SELECT:
			itemToUseOrUpgrade = selection;
			state = InventoryState.UPGRADE_MATERIAL_SELECT;
			
			if (parentGui.getTopLayerType() == GuiState.INVENTORY)
				parentGui.refreshInterface();
			return;
		case UPGRADE_MATERIAL_SELECT:
			state = InventoryState.VIEW;
			engine.receiveCommand(ActorCommand.upgrade(itemToUseOrUpgrade, selection));
			
			if (parentGui.getTopLayerType() == GuiState.INVENTORY)
				parentGui.refreshInterface();
			return;
		}
	}

	private Equipment getEquipmentForSelection(Actor player, InventorySelectionKey selection)
	{
		switch (selection.getItemSource())
		{
		case EQUIPMENT:
			return player.getEquipment();
		case MAGIC:
			return player.getMagicItems();
		case READY:
			return player.getReadiedItems();
		//$CASES-OMITTED$
		default:
			Logger.error("getEquipmentForSelection() called for ItemSource " + selection.getItemSource() + "; returning null");
			return null;
		}
	}

	private boolean isFunctionKey(int code)
	{
		if (code >= KeyEvent.VK_F1 && code <= KeyEvent.VK_F12)
			return true;
		
		return false;
	}

	public InventorySelectionKey getAndClearItemToUse()
	{
		InventorySelectionKey returnVal = itemToUseOrUpgrade;
		itemToUseOrUpgrade = null;
		return returnVal;
	}

	private void unequipItem(Item item, InventorySelectionKey selection)
	{
		Inventory inventory = engine.getData().getPlayer().getStoredItems();
		Equipment readiedItems = engine.getData().getPlayer().getReadiedItems();

		if (item.getInventorySlot() == EquipmentSlotType.ARMAMENT && readiedItems.hasEmptySlotAvailable(EquipmentSlotType.ARMAMENT))
		{
			int firstAvailableSlotIndex = readiedItems.getIndexOfFirstSlotAvailable(EquipmentSlotType.ARMAMENT);
			engine.receiveCommand(ActorCommand.unequip(selection, new InventorySelectionKey(ItemSource.READY, firstAvailableSlotIndex)));
			return;
		}
		
		if (inventory.hasSpaceForItem(item))
		{
			engine.receiveCommand(ActorCommand.unequip(selection, new InventorySelectionKey(ItemSource.PACK)));
			return;
		}
		
		if (getItemOnGround() == null)
		{
			engine.receiveCommand(ActorCommand.drop(selection)); 
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

		if (selectionIndexSpecifiesValidFunctionKey(selectionIndex))
			return true;

		return false;
	}
	
	private boolean selectionIndexSpecifiesValidFunctionKey(int selectionIndex)
	{
		int slotIndex = getSlotIndexFromFunctionKeySelectionIndex(selectionIndex);
		
		if (slotIndex == -1)
			return false;
		
		Actor player = engine.getData().getPlayer();
		int maxMagicItems = player.getMagicItems().getEquipmentSlots().size();
		
		if (slotIndex >= maxMagicItems)
			return false;
		
		Item selectedMagicItem = player.getMagicItems().getItem(slotIndex);
		
		if (selectedMagicItem == null && state == InventoryState.UPGRADE_BASE_ITEM_SELECT)
			return false;
		
		return true;
	}
	
	private int getSlotIndexFromFunctionKeySelectionIndex(int selectionIndex)
	{
		int unmaskedCode = selectionIndex - FUNCTION_KEY_MASK;
		
		if (!isFunctionKey(unmaskedCode))
			return -1;
		
		return unmaskedCode - KeyEvent.VK_F1;	//so F1 is 0, F2 is 1, etc.
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
	
	private InventorySelectionKey getMagicSelection(int selectionIndex)
	{
		int magicIndex = getSlotIndexFromFunctionKeySelectionIndex(selectionIndex);
		return new InventorySelectionKey(ItemSource.MAGIC, magicIndex);
	}

	private void drawBorders()
	{
		// equipment letter borders
		for (int i = 1; i <= 10; i++)
			addText(i, 0, "[ ]", getBorderColor());

		// material letter borders
		for (int i = 12; i <= 21; i++)
			addText(i, 0, "[ ]", getBorderColor());

		// readied letter borders
		for (int i = 1; i <= 4; i++)
			addText(i, 43, "|[ ]", getBorderColor());

		// stored letter borders
		for (int i = 6; i <= 17; i++)
			addText(i, 43, "|[ ]", getBorderColor());

		// magic letter borders
		for (int i = 19; i <= 21; i++)
			addText(i, 43, "|[  ]", getBorderColor());

		// ground letter border
		addText(23, 43, "|[ ]", getBorderColor());

		addText(0, 0, "-----------------[", getBorderColor());
		addText(0, 18, "Equipped", getTitleColor());
		addText(0, 26, "]----------------+--------------[", getBorderColor());
		addText(0, 59, "Readied", getTitleColor());
		addText(0, 66, "]-------------", getBorderColor());

		addText(11, 0, "----------------[", getBorderColor());
		addText(11, 17, "Materials", getTitleColor());
		addText(11, 26, "]----------------+", getBorderColor());

		addText(22, 0, "-------------------------------------------+-------------[", getBorderColor());
		addText(22, 58, "On Ground", getTitleColor());
		addText(22, 67, "]------------", getBorderColor());
		

		addText(5, 43, "+----------[", getBorderColor());
		addText(5, 55, "Stored <  /  >", getTitleColor());
		addText(5, 69, "]----------", getBorderColor());
		addText(5, 66, String.valueOf(Inventory.MAX_BULK), getHighlightColor());

		addText(18, 43, "+---------------[", getBorderColor());
		addText(18, 60, "Magic", getTitleColor());
		addText(18, 65, "]--------------", getBorderColor());

		addText(24, 0, "-------------------------------------------+------------------------------------", getBorderColor());
	}
	
	private void displayStateMessage()
	{
		String action = "";
		String message = "";
		switch(state)
		{
		case VIEW:
			if (playerHasMaterials())
				message = "[a-j,F1-F3] Equip, [u] Upgrade, [-] Drop";
			else
				message = "[a-j,F1-F3] Equip, [-] Drop, [ESC] Exit";
			break;
		case DROP:
			message = "[a-z] Drop Item, [ESC] Cancel";
			action = "DROP: ";
			break;
		case EQUIP:
			if (selectedEquipment.getItemSource() == ItemSource.READY)
			{
				message = "[a-l] Ready Item, [ESC] Cancel";
				action = "READY: ";
				break;
			}
			if (selectedEquipment.getItemSource() == ItemSource.MAGIC)
			{
				message = "[a-l] Prepare Item, [ESC] Cancel";
				action = "MAGIC: ";
				break;
			}
			
			message = "[a-l] Wear/Wield Item, [ESC] Cancel";
			action = "EQUIP: ";
			break;
		case MOVE_READY_ITEM:
			message = "[a-c] Swap/Store Item, [ESC] Cancel";
			action = "READY: ";
			break;
		case USE:
			message = "[a-z,0-9] Use Item, [ESC] Cancel";
			action = "USE: ";
			break;
		case UPGRADE_BASE_ITEM_SELECT:
			message = "[a-z] Pick Base Item, [ESC] Cancel";		//note that magic items can't be upgraded (no "F1-F3"), which is probably fine, at least for now
			action = "UPGRADE:";
			break;
		case UPGRADE_MATERIAL_SELECT:
			message = "[0-9] Pick Enhancer, [ESC] Cancel";
			action = "UPGRADE:";
			break;
		default:
			break;
		}
		
		if (message.length() > 41)
			message = message.substring(0, 41);
		
		addText(23, 1, action, getTitleColor());
		addText(23, action.length() + 1, message, getHighlightColor());
	}
	
	private boolean playerHasMaterials()
	{
		Inventory inventory = engine.getData().getPlayer().getMaterials();
		List<Item> itemsInInventory = inventory.getItemsForSlot(null);
		return !itemsInInventory.isEmpty();
	}

	private void populateInventory()
	{
		populatePack();
		populateEquipment();
		populateReadyItems();
		populateMaterials();
		populateMagic();
		populateGround();
	}

	private void populatePack()
	{
		Inventory inventory = engine.getData().getPlayer().getStoredItems();
		List<Item> itemsInInventory = inventory.getItemsForSlot(null);

		int packSlots = itemsInInventory.size() + (Inventory.MAX_BULK - inventory.getBulk());

		for (int i = 0; i < packSlots; i++)
		{
			String label = EMPTY_LABEL;
			String itemBulk = "";

			if (i < itemsInInventory.size())
			{
				label = itemsInInventory.get(i).getNameInPack();
				itemBulk = "<" + itemsInInventory.get(i).getBulk() + ">";
			}

			addText(6 + i, 48, label, getTextColor());
			addText(6 + i, 80 - itemBulk.length(), itemBulk, getHighlightColor());
		}
		
		String bulkString = String.valueOf(inventory.getBulk());
		addText(5, 63, RPGlib.padStringLeft(bulkString, 2, '0'), getHighlightColor());
	}

	private void populateEquipment()
	{
		List<EquipmentSlot> slots = engine.getData().getPlayer().getEquipment().getEquipmentSlots();

		for (int i = 0; i < slots.size(); i++)
		{
			EquipmentSlot slot = slots.get(i);
			String slotName = RPGlib.padStringRight(slot.getShortName() + ": ", 6, ' ');
			addText(1 + i, 4, slotName + getItemLabel(slot), getTextColor());
		}
	}

	private void populateReadyItems()
	{
		List<EquipmentSlot> slots = engine.getData().getPlayer().getReadiedItems().getEquipmentSlots();

		for (int i = 0; i < slots.size(); i++)
		{
			EquipmentSlot slot = slots.get(i);
			addText(1 + i, 48, getItemLabel(slot), getTextColor());
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

			addText(12 + i, 4, label, getTextColor());
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

			addText(19 + i, 49, label, getTextColor());
		}
	}

	private void populateGround()
	{
		Item itemOnGround = getItemOnGround();
		String label = EMPTY_LABEL;

		if (itemOnGround != null)
			label = itemOnGround.getNameInPack();

		addText(23, 48, label, getTextColor());
	}

	private String getItemLabel(EquipmentSlot slot)
	{
		if (slot.getItem() != null)
			return slot.getItem().getNameInPack();

		return EMPTY_LABEL;
	}

	private void labelEquipment(boolean includeEmptySlots)
	{
		int existingLabels = slotSelectionMapping.size();
		
		List<EquipmentSlot> slots = engine.getData().getPlayer().getEquipment().getEquipmentSlots();
		for (int i = 0; i < slots.size(); i++)
		{
			boolean selectable = true;

			EquipmentSlot slot = slots.get(i);
			Item slotItem = slot.getItem();

			if (slotItem == null && !includeEmptySlots)
				selectable = false;
			
			if (slotItem == null && state == InventoryState.DROP)
				selectable = false;	
			
			if (slotItem == null && state != InventoryState.DROP && !itemAvailableForEquipmentSlot(slot.getType())) // empty slot, but nothing can go there
				selectable = false;		

			if (slotItem != null && state != InventoryState.DROP && !spaceToRemoveEquippedItem(slotItem))	//TODO: basically, if you're removing an item but there's no place to put it.  This is a temporary fix because removing an
				selectable = false;																			//		item can only put it on the player's tile, but drop can "bubble out" - perhaps make it so removing can't drop the item?

			if (selectable)
			{
				addText(1 + i, 1, getLetterForSelectionIndex(existingLabels + i), getTitleColor());
				slotSelectionMapping.add(new InventorySelectionKey(ItemSource.EQUIPMENT, existingLabels + i));
			}
			else
			{
				slotSelectionMapping.add(null);
			}
		}
	}
	
	private void labelReadiedItems(boolean includeEmptySlots)
	{
		int existingLabels = slotSelectionMapping.size();
		
		List<EquipmentSlot> slots = engine.getData().getPlayer().getReadiedItems().getEquipmentSlots();
		for (int i = 0; i < slots.size(); i++)
		{
			boolean selectable = true;
			
			EquipmentSlot slot = slots.get(i);
			Item slotItem = slot.getItem();
			
			if (slotItem == null && !includeEmptySlots)
				selectable = false;
			
			if (slotItem == null && state == InventoryState.DROP)
				selectable = false;
			
			if (slotItem == null && state != InventoryState.DROP && !itemAvailableForReadySlot()) // empty slot, but nothing can go there
				selectable = false;
			
			//note that as long as a ready item slot has an item, it will always have a valid target, because it can swap with the equipment
			
			if (selectable)
			{
				addText(1 + i, 45, getLetterForSelectionIndex(existingLabels + i), getTitleColor());
				slotSelectionMapping.add(new InventorySelectionKey(ItemSource.READY, i));
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
			addText(12 + i, 1, getNumberForSelectionIndex(i), getTitleColor());
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

			if (slotItem == null && state != InventoryState.DROP && !itemAvailableForEquipmentSlot(slot.getType())) // empty slots, but nothing can go there
				selectable = false;
			
			if (slotItem == null && state == InventoryState.DROP)
				selectable = false;

			if (slotItem != null && state != InventoryState.DROP && !spaceToRemoveEquippedItem(slotItem))	//TODO: temporary fix because removing an item can only put it on the player's tile, but drop can "bubble out" - perhaps make it so removing can't drop the item? 
				selectable = false;

			if (selectable)
			{
				addText(19 + i, 45, "F" + (i + 1), getTitleColor());
				slotSelectionMapping.add(new InventorySelectionKey(ItemSource.MAGIC, i));
			}
			else
			{
				slotSelectionMapping.add(null);
			}
		}
	}
	
	private void labelAllStoredItems()
	{
		for (int i = 0; i < engine.getData().getPlayer().getStoredItems().size(); i++)
		{
			addText(6 + i, 45, getLetterForSelectionIndex(slotSelectionMapping.size()), getTitleColor());
			slotSelectionMapping.add(new InventorySelectionKey(ItemSource.PACK, i));
		}
	}

	private void labelAvailableItemsForSelectedEquippableSlot()
	{
		Actor player = engine.getData().getPlayer();
		List<EquipmentSlot> equippedItems = player.getEquipment().getEquipmentSlots();
		List<EquipmentSlot> readiedItems = player.getReadiedItems().getEquipmentSlots();
		Inventory inventory = player.getStoredItems();
		List<Item> itemsInInventory = inventory.getItemsForSlot(null);
		
		if (selectedEquipment != null)
		{
			int selectedEquipmentIndex = selectedEquipment.getItemIndex();
			
			switch (selectedEquipment.getItemSource())
			{
			case EQUIPMENT:
				addText(1 + selectedEquipmentIndex, 1, ">", getHighlightColor());
				break;
			case READY:
				addText(1 + selectedEquipmentIndex, 45, ">", getHighlightColor());
				break;
			case MAGIC:
				addText(19 + selectedEquipmentIndex, 45, "->", getHighlightColor());
				break;
				//$CASES-OMITTED$
			default:
				break;
			}
		}
		
		if (selectedEquipment.getItemSource() == ItemSource.READY)
		{
			for (int i = 0; i < equippedItems.size(); i++)
			{
				if (equippedItems.get(i).getItem() == null)
					continue;
				
				if (equippedItems.get(i).getType() != EquipmentSlotType.ARMAMENT)
					continue;
				
				addText(1 + i, 1, getLetterForSelectionIndex(slotSelectionMapping.size()), getTitleColor());
				slotSelectionMapping.add(new InventorySelectionKey(ItemSource.EQUIPMENT, i));
			}
		}
		
		if (filter == EquipmentSlotType.ARMAMENT && selectedEquipment.getItemSource() != ItemSource.READY)
		{
			for (int i = 0; i < readiedItems.size(); i++)
			{
				if (readiedItems.get(i).getItem() == null)
					continue;
				
				addText(1 + i, 45, getLetterForSelectionIndex(slotSelectionMapping.size()), getTitleColor());
				slotSelectionMapping.add(new InventorySelectionKey(ItemSource.READY, i));
			}
		}

		for (int i = 0; i < itemsInInventory.size(); i++)
		{
			Item inventoryItem = itemsInInventory.get(i);

			if (state != InventoryState.DROP && inventoryItem.getInventorySlot() != filter)
				continue;

			addText(6 + i, 45, getLetterForSelectionIndex(slotSelectionMapping.size()), getTitleColor());
			slotSelectionMapping.add(new InventorySelectionKey(ItemSource.PACK, i));
		}

		Item groundItem = getItemOnGround();
		if (state != InventoryState.DROP && groundItem != null && groundItem.getInventorySlot() == filter)
		{
			addText(23, 45, "-", getTitleColor());
			groundIsSelectable = true;
		}
	}
	
	private void labelAvailableSlotsToMoveReadyItemTo()
	{
		Actor player = engine.getData().getPlayer();
		List<EquipmentSlot> equipment = player.getEquipment().getEquipmentSlots();
		
		for (int i = 0; i < equipment.size(); i++)
		{
			if (equipment.get(i).getType() == EquipmentSlotType.ARMAMENT)
			{
				addText(1 + i, 1, getLetterForSelectionIndex(slotSelectionMapping.size()), getTitleColor());
				slotSelectionMapping.add(new InventorySelectionKey(ItemSource.EQUIPMENT, i));
			}
		}
		
		int selectedReadyItemIndex = selectedEquipment.getItemIndex();
		Item selectedReadyItem = player.getReadiedItems().getItem(selectedReadyItemIndex);
		
		labelFirstAvailablePackSlot(selectedReadyItem);

		if (getItemOnGround() == null)
		{
			addText(23, 45, "-", getTitleColor());
			groundIsSelectable = true;
		}
	}
	
	private void labelFirstAvailablePackSlot(Item itemToStore)
	{
		Inventory inventory = engine.getData().getPlayer().getStoredItems();
		
		if (inventory.hasSpaceForItem(itemToStore))
		{
			int indexInPack = inventory.size();
			addText(6 + indexInPack, 45, getLetterForSelectionIndex(slotSelectionMapping.size()), getTitleColor());
			slotSelectionMapping.add(new InventorySelectionKey(ItemSource.PACK, indexInPack));
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

	private boolean itemAvailableForEquipmentSlot(EquipmentSlotType type)
	{
		if (type == EquipmentSlotType.ARMAMENT && !engine.getData().getPlayer().getReadiedItems().isEmpty())
			return true;
		
		if (!getStoredItemsOfType(type).isEmpty())
			return true;

		Item groundItem = getItemOnGround();

		if (groundItem == null)
			return false; // if the was something eligible in the pack, we wouldn't have gotten here

		return groundItem.getInventorySlot() == type;
	}

	private boolean itemAvailableForReadySlot()
	{
		List<EquipmentSlot> equipmentSlots = engine.getData().getPlayer().getEquipment().getEquipmentSlots();
		int totalEquippedArmaments = 0;
		
		for (EquipmentSlot slot : equipmentSlots)
		{
			if (slot.getType() == EquipmentSlotType.ARMAMENT && slot.getItem() != null)
				totalEquippedArmaments++;
		}
		
		if (totalEquippedArmaments > 0)
			return true;
		
		if (!getStoredItemsOfType(EquipmentSlotType.ARMAMENT).isEmpty())
			return true;

		Item groundItem = getItemOnGround();

		if (groundItem == null)
			return false; // if the was something eligible in the pack, we wouldn't have gotten here

		return groundItem.getInventorySlot() == EquipmentSlotType.ARMAMENT;
	}

	private boolean spaceToRemoveEquippedItem(Item item)
	{
		Equipment readiedItems = engine.getData().getPlayer().getReadiedItems();
		Inventory inventory = engine.getData().getPlayer().getStoredItems();

		if (readiedItems.hasEmptySlotAvailable(EquipmentSlotType.ARMAMENT))
			return true;
		
		if (inventory.hasSpaceForItem(item))
			return true;

		if (getItemOnGround() == null)
			return true;

		return false;
	}
	
	public void attemptToUseItem()
	{
		InventorySelectionKey pendingItemToUseSelectionKey = getAndClearItemToUse();
		if (pendingItemToUseSelectionKey == null)
			return;
		
		Actor player = engine.getData().getPlayer();
		int itemIndex = pendingItemToUseSelectionKey.getItemIndex();
		Item pendingItemToUse = null;
		
		if (pendingItemToUseSelectionKey.getItemSource() == ItemSource.MATERIAL)
		{
			Inventory materials = player.getMaterials();
			pendingItemToUse = materials.get(itemIndex);
		}
		else if (pendingItemToUseSelectionKey.getItemSource() == ItemSource.PACK)
		{
			Inventory storedItems = player.getStoredItems();
			pendingItemToUse = storedItems.get(itemIndex);
		}
		else
		{
			Equipment equipment = getEquipmentForSelection(player, pendingItemToUseSelectionKey);
			pendingItemToUse = equipment.getItem(itemIndex);
		}
		
		if (pendingItemToUse != null)
		{
			parentGui.setSingleLayer(GuiState.MAIN_GAME);
			parentGui.promptForDirectionAndSetPendingCommand(ActorCommand.use(pendingItemToUseSelectionKey, null));
			return;
		}
		
		MessageBuffer.addMessage("You can't use anything in that slot.");
		parentGui.setSingleLayer(GuiState.MAIN_GAME);
		return;
	}
}
