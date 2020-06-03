package main.presentation.curses;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.apache.commons.lang3.StringUtils;

import main.data.DataSaveUtils;
import main.data.event.ActorCommand;
import main.data.event.EventObserver;
import main.data.event.InternalEvent;
import main.data.event.InternalEventType;
import main.entity.actor.Actor;
import main.entity.item.InventorySelectionKey;
import main.entity.item.equipment.Equipment;
import main.logic.Direction;
import main.logic.Engine;
import main.logic.RPGlib;
import main.presentation.AbstractGui;
import main.presentation.GuiState;
import main.presentation.Logger;
import main.presentation.curses.inventory.CursesGuiCompleteInventory;
import main.presentation.curses.inventory.InventoryState;
import main.presentation.curses.terminal.CursesTerminal;
import main.presentation.curses.terminal.CursesTerminalAsciiPanelImpl;
import main.presentation.message.MessageBuffer;

public class CursesGui extends AbstractGui implements KeyListener, EventObserver
{
	private CursesTerminal terminal;

	private GuiState currentState = GuiState.SELECT_PROFESSION; // default assumes new game

	private CursesGuiProfessionSelect professionSelectUtil;
	private CursesGuiMessages professionDescriptionUtil;
	private CursesGuiMessages messageUtil;
	private CursesGuiUtil displayUtil;
	private CursesGuiChat chatUtil;
//	private CursesGuiInventory inventoryUtil;
//	private CursesGuiUtil equipmentUtil;
	private CursesGuiCompleteInventory completeInventory;
	
	private ActorCommand pendingCommand = null;

	public CursesGui(Engine engine)
	{
		super(engine);
		engine.addEventObserver(this);

		// terminal = new CursesTerminalLibjcsiImpl("Adventures in Reptoran");
		terminal = new CursesTerminalAsciiPanelImpl("Adventures in Reptoran v" + DataSaveUtils.VERSION);
		terminal.addKeyListener(this);

		professionSelectUtil = new CursesGuiProfessionSelect(this, engine.getData(), terminal);
		professionDescriptionUtil = new CursesGuiMessages(this, new Rectangle(0, 0, 80, 25), terminal, GuiState.PROFESSION_DESCRIPTION,
				GuiState.GAME_START); // for now, this transitions right to the game, but that may change in the future (talent selection or whatever)
		messageUtil = new CursesGuiMessages(this, new Rectangle(0, 0, 80, 2), terminal, GuiState.MESSAGE, GuiState.NONE);
		displayUtil = new CursesGuiDisplay(engine, terminal);
		chatUtil = new CursesGuiChat(this, terminal);
//		inventoryUtil = new CursesGuiInventory(this, engine, terminal);
//		equipmentUtil = new CursesGuiEquipment(this, inventoryUtil, engine, terminal);
		completeInventory = new CursesGuiCompleteInventory(this, engine, terminal, ColorScheme.woodenScheme());

		refreshInterface();
	}

	@Override
	public void refreshInterface()
	{
		Logger.debug("Refreshing interface; currentState is " + currentState);

		switch (currentState)
		{
		case GAME_START:
			beginGame();
			break;
		case SELECT_PROFESSION:
			displayProfessionSelection();
			break;
		case PROFESSION_DESCRIPTION:
			displayProfessionDescription();
			break;
		case INVENTORY:
//			displayPackContents();
//			break;
//		case EQUIPMENT:
//			displayEquipment();
			displayCompleteInventory();
			break;
		case CHAT:
			displayChat();
			break;
		case MESSAGE: // fall through
		case NONE: // fall through
		//$CASES-OMITTED$
		default:
			displayMainGameScreen();
			break;
		}
	}

	private void displayProfessionSelection()
	{
		professionSelectUtil.refresh();
	}

	private void displayProfessionDescription()
	{
		professionDescriptionUtil.refresh();
	}
	
	private void displayChat()
	{
		chatUtil.refresh();
	}

//	private void displayPackContents()
//	{
//		inventoryUtil.refresh();
//	}
//
//	private void displayEquipment()
//	{
//		equipmentUtil.refresh();
//	}
	
	private void displayCompleteInventory()
	{
		completeInventory.refresh();
	}

	private void displayMainGameScreen()
	{
		displayUtil.refresh();
		messageUtil.refresh();
	}

	public GuiState getCurrentState()
	{
		return currentState;
	}

	public void setCurrentState(GuiState currentState)
	{
		this.currentState = currentState;
		Logger.debug("Changing GuiState to " + currentState);
	}

	@Override
	public void keyPressed(KeyEvent ke)
	{
		if (currentState == GuiState.SELECT_PROFESSION)
		{
			professionSelectUtil.handleKeyEvent(ke);
			refreshInterface();
			return;
		}

		if (currentState == GuiState.PROFESSION_DESCRIPTION)
		{
			professionDescriptionUtil.handleKeyEvent(ke);
			refreshInterface();
			return;
		}

		if (currentState == GuiState.MESSAGE)
		{
			messageUtil.handleKeyEvent(ke);
			return;
		}

		if (currentState == GuiState.INVENTORY)
		{
			completeInventory.handleKeyEvent(ke);
			refreshInterface();
			attemptToUseItem();
			return;
		}
		
		if (currentState == GuiState.CHAT)
		{
			chatUtil.handleKeyEvent(ke);
			refreshInterface();
			return;
		}

		if (currentState == GuiState.GAME_START)
		{
			refreshInterface();
			return;
		}

		int code = ke.getKeyCode();
		char keyChar = ke.getKeyChar();

		Logger.info("GUI - Key press detected, code is: " + code);
		Logger.info("GUI - Key press detected, char is: " + keyChar);

		if (code == KeyEvent.VK_ESCAPE)
		{
			currentState = GuiState.NONE;
			pendingCommand = null;
			refreshInterface();
		} else if (code == KeyEvent.VK_NUMPAD1 || code == KeyEvent.VK_END)
		{
			handleDirection(Direction.DIRSW);
		} else if (code == KeyEvent.VK_NUMPAD2 || code == KeyEvent.VK_KP_DOWN || code == KeyEvent.VK_DOWN)
		{
			handleDirection(Direction.DIRS);
		} else if (code == KeyEvent.VK_NUMPAD3 || code == KeyEvent.VK_PAGE_DOWN)
		{
			handleDirection(Direction.DIRSE);
		} else if (code == KeyEvent.VK_NUMPAD4 || code == KeyEvent.VK_KP_LEFT || code == KeyEvent.VK_LEFT)
		{
			handleDirection(Direction.DIRW);
		} else if (code == KeyEvent.VK_NUMPAD5 || code == KeyEvent.VK_CLEAR)
		{
			handleDirection(Direction.DIRNONE);
		} else if (code == KeyEvent.VK_NUMPAD6 || code == KeyEvent.VK_KP_RIGHT || code == KeyEvent.VK_RIGHT)
		{
			handleDirection(Direction.DIRE);
		} else if (code == KeyEvent.VK_NUMPAD7 || code == KeyEvent.VK_HOME)
		{
			handleDirection(Direction.DIRNW);
		} else if (code == KeyEvent.VK_NUMPAD8 || code == KeyEvent.VK_KP_UP|| code == KeyEvent.VK_UP)
		{
			handleDirection(Direction.DIRN);
		} else if (code == KeyEvent.VK_NUMPAD9 || code == KeyEvent.VK_PAGE_UP)
		{
			handleDirection(Direction.DIRNE);
		} else if (code == KeyEvent.VK_F1 || code == KeyEvent.VK_F2 || code == KeyEvent.VK_F3)
		{
			handleFunctionKey(ke);
		} else if (keyChar == '>')
		{
			engine.receiveCommand(ActorCommand.changeZoneDown());
		} else if (keyChar == '<')
		{
			engine.receiveCommand(ActorCommand.changeZoneUp());
		} else if (keyChar == 'i')
		{
//			currentState = GuiState.EQUIPMENT;
			currentState = GuiState.INVENTORY;
			refreshInterface();
		} else if (keyChar == 'd')
		{
			handleDrop();
		} else if (keyChar == 'u')
		{
			handleUse();
		} else if (keyChar == 'g')
		{
			engine.receiveCommand(ActorCommand.pickUp());
		} else if (keyChar == 'C')
		{
			handleChatInput();
		}
		if (keyChar == 'S')

		{
			engine.receiveCommand(ActorCommand.save());
			engine.receiveCommand(ActorCommand.exit());
			terminal.close();
		} else if (keyChar == 'Q')
		{
			engine.receiveCommand(ActorCommand.exit());
			terminal.close();
		}
	}

	private void handleFunctionKey(KeyEvent keyEvent)
	{
		if (currentState != GuiState.NONE)
			return;
		
		completeInventory.setState(InventoryState.USE);
		completeInventory.handleKeyEvent(keyEvent);
		attemptToUseItem();
	}

	private void handleChatInput()
	{
		ActorCommand command = ActorCommand.chat(null);
		Direction direction = engine.determineDirectionOfCommand(command.getType());
		if (direction == Direction.DIRNONE)
			promptForDirection(command);
		else
			engine.receiveCommand(ActorCommand.chat(direction));
	}
	
	private void promptForDirection(ActorCommand command)
	{
		pendingCommand = command;
		currentState = GuiState.PENDING_DIRECTION;
		String message = engine.getPromptForCommandType(pendingCommand.getType());
		if (StringUtils.isNotEmpty(message))
		{
			MessageBuffer.addMessage(message);
			refreshInterface();
		}
	}

	public void initializeNewGame()
	{
		engine.getData().initializeNewGame();
	}

	public void beginGame()
	{
		currentState = GuiState.NONE;
		engine.beginGame();
		refreshInterface();
	}

	private void handleDrop()
	{
		Actor player = engine.getData().getPlayer();

		if (player.getStoredItems().isEmpty() && player.getEquipment().isEmpty() && player.getMaterials().isEmpty() && player.getReadiedItems().isEmpty() && player.getMagicItems().isEmpty())
		{
			MessageBuffer.addMessage("You aren't carrying anything!");
			currentState = GuiState.MESSAGE;
			refreshInterface();
			return;
		}

		completeInventory.setState(InventoryState.DROP);
		currentState = GuiState.INVENTORY;
		refreshInterface();
	}

	private void handleUse()
	{
		Actor player = engine.getData().getPlayer();

		if (player.getStoredItems().isEmpty() && player.getEquipment().isEmpty() && player.getMaterials().isEmpty() && player.getReadiedItems().isEmpty() && player.getMagicItems().isEmpty())
		{
			MessageBuffer.addMessage("You have no items to use!");
			currentState = GuiState.MESSAGE;
			refreshInterface();
			return;
		}

		completeInventory.setState(InventoryState.USE);
		currentState = GuiState.INVENTORY;
		refreshInterface();
	}

	private void attemptToUseItem()
	{
		InventorySelectionKey itemToUse = completeInventory.getAndClearItemToUse();
		
		if (itemToUse == null)
			return;
		
		Equipment magicItems = engine.getData().getPlayer().getMagicItems();
		
		if (magicItems.getItem(itemToUse.getItemIndex()) != null)
		{
			promptForDirection(ActorCommand.use(itemToUse, null));
			return;
		}
		
		MessageBuffer.addMessage("That slot is empty.");
		currentState = GuiState.MESSAGE;
		refreshInterface();
		return;
	}
	
	private void handleDirection(Direction direction)
	{
		Point coordChange = direction.getCoordChange();
		handleDirection(coordChange.x, coordChange.y);
	}

	private void handleDirection(int rowChange, int colChange)
	{
		if (currentState != GuiState.NONE && currentState != GuiState.PENDING_DIRECTION)
			return;
		
		if (pendingCommand == null)
			pendingCommand = ActorCommand.move(null);	//default to move if there's nothing we're prompting a direction for

		String directionString = RPGlib.convertCoordChangeToDirection(rowChange, colChange).name();
		ActorCommand command = pendingCommand.addArgument(directionString);
		pendingCommand = null;
		engine.receiveCommand(command);
	}

	@Override
	public void receiveInternalEvent(InternalEvent internalEvent)
	{
		if (internalEvent == null)
			return;
			
		Logger.debug("GUI received event: " + internalEvent);
		
		if (InternalEventType.CHAT.equals(internalEvent.getInternalEventType()) && internalEvent.getFlag(1) != -1)
		{
			Actor chatTarget = engine.getData().getActor(internalEvent.getFlag(1));
			chatUtil.setChatTarget(chatTarget);
			setCurrentState(GuiState.CHAT);
			messageUtil.clearMessageArea();
		}
	}

	// unused implemented methods below //

	@Override
	public void keyReleased(KeyEvent arg0)
	{
		// nothing to do here
	}

	@Override
	public void keyTyped(KeyEvent arg0)
	{
		// nothing to do here
	}
}