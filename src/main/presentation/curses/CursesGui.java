package main.presentation.curses;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import main.data.DataSaveUtils;
import main.entity.actor.Actor;
import main.logic.Engine;
import main.logic.RPGlib;
import main.presentation.AbstractGui;
import main.presentation.GuiState;
import main.presentation.Logger;
import main.presentation.curses.inventory.CursesGuiEquipment;
import main.presentation.curses.inventory.CursesGuiInventory;
import main.presentation.curses.inventory.InventoryState;
import main.presentation.curses.terminal.CursesTerminal;
import main.presentation.curses.terminal.CursesTerminalAsciiPanelImpl;
import main.presentation.message.MessageBuffer;

public class CursesGui extends AbstractGui implements KeyListener
{
	private CursesTerminal terminal;

	private GuiState currentState = GuiState.SELECT_PROFESSION;		//default assumes new game
	
	private CursesGuiProfessionSelect professionSelectUtil;
	private CursesGuiMessages professionDescriptionUtil;
	private CursesGuiMessages messageUtil;
	private CursesGuiUtil displayUtil;
	private CursesGuiInventory inventoryUtil;
	private CursesGuiUtil equipmentUtil;

	public CursesGui(Engine engine)
	{
		super(engine);
		
//		terminal = new CursesTerminalLibjcsiImpl("Adventures in Reptoran");
		terminal = new CursesTerminalAsciiPanelImpl("Adventures in Reptoran v" + DataSaveUtils.VERSION);
		terminal.addKeyListener(this);
		
		professionSelectUtil = new CursesGuiProfessionSelect(this, engine.getData(), terminal);
		professionDescriptionUtil = new CursesGuiMessages(this, new Rectangle(0, 0, 80, 25), terminal, GuiState.PROFESSION_DESCRIPTION, GuiState.GAME_START);	//for now, this transitions right to the game, but that may change in the future (talent selection or whatever)
		messageUtil = new CursesGuiMessages(this, new Rectangle(0, 0, 80, 2), terminal, GuiState.MESSAGE, GuiState.NONE);
		displayUtil = new CursesGuiDisplay(engine, terminal);
		inventoryUtil = new CursesGuiInventory(this, engine, terminal);
		equipmentUtil = new CursesGuiEquipment(this, inventoryUtil, engine, terminal);
		
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
			displayPackContents();
			break;
		case EQUIPMENT:
			displayEquipment();
			break;
		case MESSAGE:		//fall through
		case NONE:		//fall through
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
	
	private void displayPackContents()
	{
		inventoryUtil.refresh();
	}
	
	private void displayEquipment()
	{
		equipmentUtil.refresh();
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
			inventoryUtil.handleKeyEvent(ke);
			refreshInterface();
			return;
		}
		
		if (currentState == GuiState.EQUIPMENT)
		{
			equipmentUtil.handleKeyEvent(ke);
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

		if (code == KeyEvent.VK_NUMPAD1)
		{
			handleDirection(1, -1);
		} else if (code == KeyEvent.VK_NUMPAD2)
		{
			handleDirection(1, 0);
		} else if (code == KeyEvent.VK_NUMPAD3)
		{
			handleDirection(1, 1);
		} else if (code == KeyEvent.VK_NUMPAD4)
		{
			handleDirection(0, -1);
		} else if (code == KeyEvent.VK_NUMPAD5)
		{
			handleDirection(0, 0);
		} else if (code == KeyEvent.VK_NUMPAD6)
		{
			handleDirection(0, 1);
		} else if (code == KeyEvent.VK_NUMPAD7)
		{
			handleDirection(-1, -1);
		} else if (code == KeyEvent.VK_NUMPAD8)
		{
			handleDirection(-1, 0);
		} else if (code == KeyEvent.VK_NUMPAD9)
		{
			handleDirection(-1, 1);
		} else if (keyChar == '>')
		{
			engine.receiveCommand("CHANGE_ZONE_DOWN");
		} else if (keyChar == '<')
		{
			engine.receiveCommand("CHANGE_ZONE_UP");
		} else if (keyChar == 'i')
		{
			currentState = GuiState.EQUIPMENT;
			refreshInterface();
		} else if (keyChar == 'd')
		{
			handleDrop();
		} else if (keyChar == 'g')
		{
			engine.receiveCommand("PICKUP");
		} else if (keyChar == 'S')
		{
			engine.receiveCommand("SAVE");
			engine.receiveCommand("EXIT");
			terminal.close();
		} else if (keyChar == 'Q')
		{
			engine.receiveCommand("EXIT");
			terminal.close();
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
		
		if (player.getInventory().isEmpty())
		{
			MessageBuffer.addMessage("You aren't carrying anything!");
			currentState = GuiState.MESSAGE;
			refreshInterface();
			return;
		}
		
		inventoryUtil.setState(InventoryState.DROP);
		currentState = GuiState.INVENTORY;
		refreshInterface();
	}

	private void handleDirection(int rowChange, int colChange)
	{
		if (currentState != GuiState.NONE)
			return;
		
		String command = RPGlib.convertCoordChangeToDirection(rowChange, colChange);		
		engine.receiveCommand(command);
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