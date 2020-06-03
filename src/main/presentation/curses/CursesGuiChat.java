package main.presentation.curses;

import java.awt.event.KeyEvent;
import java.util.List;

import main.data.chat.ChatManager;
import main.data.chat.EventTriggerExecutor;
import main.entity.actor.Actor;
import main.entity.actor.ActorType;
import main.entity.chat.Chat;
import main.entity.chat.ChatResponse;
import main.logic.RPGlib;
import main.presentation.GuiState;
import main.presentation.Logger;
import main.presentation.curses.terminal.CursesTerminal;
import main.presentation.message.FormattedMessageBuilder;
import main.presentation.message.MessageBuffer;

public class CursesGuiChat extends ColorSchemeCursesGuiUtil
{
	private static final int CHAT_WIDTH = 62;
	private static final int CHAT_HEIGHT = 13;
	
	private CursesGui parentGui;
	private String chatTitle;
	private ActorType chatTarget;
	private Chat currentChatNode;
	private int selectedResponse;
	private int totalResponses;
	
	public CursesGuiChat(CursesGui parentGui, CursesTerminal terminal)
	{
		super(terminal, ColorScheme.woodenScheme());
		
		this.parentGui = parentGui;
		reset();	//sets all fields to default values
	}

	@Override
	public void refresh()
	{
		drawConversationWindow();
		drawText();
		drawResponses();
		terminal.refresh();
	}
	
	public void reset()
	{
		chatTitle = "";
		chatTarget = null;
		currentChatNode = null;
		selectedResponse = 0;
		totalResponses = 0;
	}
	
	public void setChatTarget(Actor actor)
	{
		if (actor == null)
			return;
		
		FormattedMessageBuilder formatter = new FormattedMessageBuilder("Talking with @1the");
		formatter.setSource(actor);
		chatTitle = RPGlib.padString(formatter.format(), CHAT_WIDTH - 2, ' ');
		chatTarget = actor.getType();
		currentChatNode = ChatManager.getInstance().getInitialTalkForActor(chatTarget);
		newChat();
	}
	
	public void newChat()
	{
		totalResponses = currentChatNode.getResponse().size();
		selectedResponse = 0;
	}
	
	public void endChat()
	{
		reset();
		parentGui.setCurrentState(GuiState.NONE);
	}

	private void drawConversationWindow()
	{
		int start = (80 - CHAT_WIDTH) / 2 - 1;
		int end = start + CHAT_WIDTH + 1;
		
		for (int i = start; i < end; i++)
	    {
	        for (int j = 1; j < 23; j++)
	        {
	            String icon = " ";

	            if (i == start || i == end - 1 || j == 1 || j == 22 || j == 3 || j == 17)
	                icon = "#";

	            terminal.print(i, j, icon, getBorderColor());
	        }
	    }
		
		terminal.print(start + 1, 2, chatTitle, getTitleColor());
	}

	private void drawText()
	{
		MessageBuffer.reset();
		MessageBuffer.addMessage(currentChatNode.getText());
		
		List<String> messageLines = MessageBuffer.parseMessageBuffer(CHAT_WIDTH, CHAT_HEIGHT);
		
		for (int i = 4; i < 17; i++)
		{
			if (!messageLines.isEmpty())
			{
				String messageLine = messageLines.remove(0);
				terminal.print(9, i, messageLine, getTextColor());
			}
		}
	}

	private void drawResponses()
	{
		List<ChatResponse> responses = currentChatNode.getResponse();
		
		for (int i = 0; i < totalResponses; i++)
		{
			String text = responses.get(i).getText();
			
			if (text.length() > 61)
				Logger.error("Response text is too long by " + (text.length() - 61) + " characters; string is \n[" + text + "]");
			
			int color = getTextColor();
			if (i == selectedResponse)
				color = getHighlightColor();
			
			terminal.print(9, 18 + i, text, color);
		}
	}

	@Override
	public void handleKeyEvent(KeyEvent ke)
	{	
		int code = ke.getKeyCode();
		
		if (code == KeyEvent.VK_ESCAPE || (currentChatNode != null && currentChatNode.getResponse().isEmpty()))
		{
			endChat();
			return;
		} else if (code == KeyEvent.VK_NUMPAD8 || code == KeyEvent.VK_KP_UP|| code == KeyEvent.VK_UP)
		{
			if (selectedResponse > 0)
				selectedResponse--;
		} else if (code == KeyEvent.VK_NUMPAD2 || code == KeyEvent.VK_KP_DOWN || code == KeyEvent.VK_DOWN)
		{
			if (selectedResponse < totalResponses - 1)
				selectedResponse++;
		} else if (code == KeyEvent.VK_ENTER)
		{
			selectResponse();
		}
	}

	private void selectResponse()
	{
		ChatResponse highlightedResponse = currentChatNode.getResponse().get(selectedResponse);
		EventTriggerExecutor.getInstance().executeChatResponseTriggers(highlightedResponse);
		
		String flowToTag = highlightedResponse.getFlowTo();
		
		if (flowToTag != null)
		{
			currentChatNode = ChatManager.getInstance().getTalkByTag(flowToTag);
			newChat();
			return;
		}
		
		endChat();
	}
}
