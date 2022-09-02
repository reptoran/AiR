package main.presentation.curses;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.Timer;

import org.apache.commons.lang3.StringUtils;

import main.data.DataSaveUtils;
import main.data.event.ActorCommand;
import main.data.event.EventObserver;
import main.data.event.InternalEvent;
import main.data.event.InternalEventType;
import main.entity.actor.Actor;
import main.logic.Engine;
import main.presentation.AbstractGui;
import main.presentation.GuiState;
import main.presentation.Logger;
import main.presentation.curses.inventory.CursesGuiCompleteInventory;
import main.presentation.curses.terminal.CursesTerminal;
import main.presentation.curses.terminal.CursesTerminalAsciiPanelImpl;
import main.presentation.message.MessageBuffer;

public class CursesGui extends AbstractGui implements KeyListener, EventObserver, ActionListener
{	
	private Map<GuiState, CursesGuiScreen> guiScreens;
	private List<CursesGuiScreen> layers;	//layers at the end are on top, layers at the beginning are in the back

	private CursesTerminal terminal;

	public static final int SCREEN_HEIGHT = 25;
	public static final int SCREEN_WIDTH = 80;
	
	private DisplayTile[][] screenMap;
	
	private ActorCommand pendingCommand = null;
	private GuiState topLayer = null;
	
	private Animation currentAnimation = null;
	private Map<Point, DisplayTile> animationMask = new HashMap<Point, DisplayTile>();

	public CursesGui(Engine engine, GuiState initialScreen)
	{
		super(engine);

		engine.addEventObserver(this);

		terminal = new CursesTerminalAsciiPanelImpl("Adventures in Reptoran v" + DataSaveUtils.VERSION);
		terminal.addKeyListener(this);

		guiScreens = new HashMap<GuiState, CursesGuiScreen>();
		layers = new CopyOnWriteArrayList<CursesGuiScreen>();

		defineScreens();

		setSingleLayer(initialScreen);
		refreshInterface();
	}

	private void defineScreens()
	{
		ColorScheme defaultColorScheme = ColorScheme.woodenScheme();
//		ColorScheme defaultColorScheme = ColorScheme.monochromeScheme();
		
		CursesGuiProfessionSelect professionSelectUtil = new CursesGuiProfessionSelect(this, engine.getData());
		CursesGuiCompleteInventory completeInventory = new CursesGuiCompleteInventory(this, engine, defaultColorScheme);
		CursesGuiScreen mainGameUtil = new CursesGuiMainGameDisplay(this, completeInventory, engine);
		CursesGuiScreen gameStartHelper = new GameStartHelper(this, engine);
		CursesGuiChat chatUtil = new CursesGuiChat(this, defaultColorScheme);
		CursesGuiRecipeSelect recipeUtil = new CursesGuiRecipeSelect(this, engine);
		CursesGuiQuestDisplay questUtil = new CursesGuiQuestDisplay(this, defaultColorScheme);

		guiScreens.put(GuiState.SELECT_PROFESSION, professionSelectUtil);
		guiScreens.put(GuiState.MAIN_GAME, mainGameUtil);
		guiScreens.put(GuiState.GAME_START, gameStartHelper);
		guiScreens.put(GuiState.CHAT, chatUtil);
		guiScreens.put(GuiState.RECIPE, recipeUtil);
		guiScreens.put(GuiState.INVENTORY, completeInventory);
		guiScreens.put(GuiState.QUEST, questUtil);
	}

	public void initializeNewGame()
	{
		engine.getData().initializeNewGame();
	}
	
	public void endGame()
	{
		terminal.close();
	}
	
	public void runAnimation(Animation animation)
	{
		if (currentAnimation != null)
			return;
		
		Timer animationTimer = animation.getTimer();
		animationTimer.addActionListener(this);
		currentAnimation = animation;
		animation.start();
	}

	@Override
	public void refreshInterface()
	{
		if (currentAnimation != null)
			return;
		
		screenMap = new DisplayTile[SCREEN_HEIGHT][SCREEN_WIDTH];
		
		for (CursesGuiScreen layer : layers)
		{
			layer.refresh();
		}
		
		for (int i = 0; i < SCREEN_HEIGHT; i++)
		{
			for (int j = 0; j < SCREEN_WIDTH; j++)
			{
				DisplayTile tile = getTileAtLocation(i, j);
				screenMap[i][j] = tile;
				
				if (tile == null)
					continue;
				
				terminal.print(j, i, "" + tile.getIcon(), tile.getForegroundColor(), tile.getBackgroundColor());
			}
		}
		
		terminal.refresh();
	}

	private DisplayTile getTileAtLocation(int row, int col)
	{
		Point point = new Point(row, col);
		
		for (int i = layers.size() - 1; i >= 0; i--)
		{
			DisplayTile currentTile = layers.get(i).getCharacter(point);
			if (currentTile != null)
				return currentTile;
		}
		
		return new DisplayTile(' ');
	}
	
	public void setSingleLayer(GuiState guiState)
	{
		setSingleLayer(guiState, true);
	}

	public void setSingleLayer(GuiState guiState, boolean shouldRefresh)
	{
		layers.clear();
		addLayer(guiState, shouldRefresh);
	}

	public void addLayer(GuiState guiState, boolean shouldRefresh)
	{
		CursesGuiScreen newLayer = guiScreens.get(guiState);
		
		if (newLayer == null)
			throw new IllegalArgumentException("No GUI screen mapped for GuiState [" + guiState + "]");
		
		layers.add(newLayer);
		topLayer = guiState;	//message overlays aren't considered the "top layer"
		
		CursesGuiScreen messageOverlay = newLayer.getMessageHandler();
		
		if (messageOverlay != null)
			layers.add(messageOverlay);
		
		if (shouldRefresh)
			refreshInterface();
	}

	public CursesGuiScreen clearTopLayer()
	{
		if (layers.isEmpty())
			return null;

		return layers.remove(layers.size() - 1);
	}
	
	public CursesGuiScreen getTopLayer()
	{
		if (layers.isEmpty())
			return null;

		return layers.get(layers.size() - 1);
	}
	
	public GuiState getTopLayerType()
	{
		return topLayer;
	}

	@Override
	public void receiveInternalEvent(InternalEvent internalEvent)
	{
		if (internalEvent == null)
			return;
			
		Logger.debug("GUI received event: " + internalEvent);
		
		if (InternalEventType.CHAT.equals(internalEvent.getInternalEventType()) && internalEvent.getFlag(1) != -1)
		{
			CursesGuiChat chatUtil = (CursesGuiChat)guiScreens.get(GuiState.CHAT);
			Actor chatTarget = engine.getData().getActor(internalEvent.getFlag(1));
			chatUtil.setChatTarget(chatTarget);
			addLayer(GuiState.CHAT, true);
		}
		else if (InternalEventType.INTERRUPTION.equals(internalEvent.getInternalEventType()) && topLayer == GuiState.INVENTORY)
		{
			setSingleLayer(GuiState.MAIN_GAME, false);	//the refresh happens when the player's FOV updates
		}
	}

	@Override
	public void keyPressed(KeyEvent ke)
	{
		if (currentAnimation != null)
			return;
		
		CursesGuiScreen activeScreen = getTopLayer();
		if (activeScreen == null)
			return;
		
		for (int i = layers.size() - 1; i >= 0; i--)
		{
			CursesGuiScreen currentLayer = layers.get(i);
			
			if (currentLayer.delegatesKeyEventsToNextLayer())
				continue;
			
			currentLayer.handleKeyEvent(ke);
			return;
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e)	//triggered only by the animation timer
	{
		//restore the screen where the last frame was displayed (animationMask should be empty the first time this is called)
		for (Point point : animationMask.keySet())
		{
			DisplayTile tile = animationMask.get(point);
			terminal.print(point.y, point.x, "" + tile.getIcon(), tile.getForegroundColor(), tile.getBackgroundColor());
		}
		
		//wipe out the mask (and everything else if the animation is finished)
		animationMask.clear();
		if (currentAnimation.isFinished())
		{
			currentAnimation.getTimer().removeActionListener(this);
			currentAnimation = null;
			terminal.refresh();
			return;
		}
		
		//get the mask for the current frame, then print out the current frame
		Map<Point, DisplayTile> currentFrame = currentAnimation.getCurrentFrame();
		for (Point point : currentFrame.keySet())
		{
			DisplayTile frameTile = currentFrame.get(point);
			DisplayTile maskTile = null;
			
			try
			{
				maskTile = screenMap[point.x][point.y];
			} catch (ArrayIndexOutOfBoundsException exc)
			{
				continue;
			}
			
			if (maskTile == null)
				maskTile = new DisplayTile(' ', 0);
			
			animationMask.put(point, maskTile);
			terminal.print(point.y, point.x, "" + frameTile.getIcon(), frameTile.getForegroundColor(), frameTile.getBackgroundColor());
		}
		
		terminal.refresh();
	}
	
	public void promptForDirectionAndSetPendingCommand(ActorCommand command)
	{
		pendingCommand = command;
		String message = engine.getPromptForCommandType(pendingCommand.getType());
		if (StringUtils.isNotEmpty(message))
		{
			MessageBuffer.addMessage(message);
			refreshInterface();
		}
	}
	
	public ActorCommand getPendingCommand()
	{
		return pendingCommand;
	}
	
	public void setPendingCommand(ActorCommand command)
	{
		pendingCommand = command;
	}

	// unused implemented methods below //

	@Override
	public void keyTyped(KeyEvent e)
	{
		// TODO Auto-generated method stub

	}
	
	@Override
	public void keyReleased(KeyEvent e)
	{
		// TODO Auto-generated method stub

	}
}
