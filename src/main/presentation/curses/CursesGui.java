package main.presentation.curses;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import main.logic.Engine;
import main.presentation.AbstractGui;
import main.presentation.curses.terminal.CursesTerminal;
import main.presentation.curses.terminal.CursesTerminalAsciiPanelImpl;

public class CursesGui extends AbstractGui implements KeyListener
{
	private CursesTerminal terminal;

	private CursesGuiState currentState = CursesGuiState.STATE_NONE;
	
	private CursesGuiMessages messageUtil;
	private CursesGuiDisplay displayUtil;

	public CursesGui(Engine engine)
	{
		super(engine);
		
//		terminal = new CursesTerminalLibjcsiImpl("Adventures in Reptoran");
		terminal = new CursesTerminalAsciiPanelImpl("Adventures in Reptoran");
		terminal.addKeyListener(this);
		
		messageUtil = new CursesGuiMessages(this, terminal);
		displayUtil = new CursesGuiDisplay(engine, terminal);
		
		refreshInterface();
	}

	@Override
	public void refreshInterface()
	{
		displayUtil.printBorders();

		// game display
		displayUtil.updateMap();
		
		//messages
		messageUtil.clearMessageArea();
		getAndShowNewMessages();
	}
	
	private void getAndShowNewMessages()
	{
		messageUtil.parseMessageBuffer(engine.getBufferedMessages());
		messageUtil.displayNextMessages();
	}

	public CursesGuiState getCurrentState()
	{
		return currentState;
	}

	public void setCurrentState(CursesGuiState currentState)
	{
		this.currentState = currentState;
	}
	
	public String getFullKeyEventValue(KeyEvent ke)
	{
		return "";
	}

	@Override
	public void keyPressed(KeyEvent ke)
	{
		if (currentState == CursesGuiState.STATE_MESSAGE)
		{
			messageUtil.clearMessageArea();
			messageUtil.displayNextMessages();
			return;
		}
		
		int code = ke.getKeyCode();
		char keyChar = ke.getKeyChar();
		
//		System.out.println("GUI - Key press detected, code is: " + code);
//		System.out.println("GUI - Key press detected, char is: " + keyChar);

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
		
//		refreshInterface();	//TODO: this can happen before the logic is executed and the data is updated, resulting in no screen change
	}

	private void handleDirection(int rowChange, int colChange)
	{
		String command = "";

		// direction with no state just means to move
		if (currentState == CursesGuiState.STATE_NONE)
		{
			command = "DIR";
			if (rowChange < 0)
				command = command + "N";
			if (rowChange > 0)
				command = command + "S";
			if (colChange < 0)
				command = command + "W";
			if (colChange > 0)
				command = command + "E";
		}

		// System.out.println("GUI - Command being sent: " + command);

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