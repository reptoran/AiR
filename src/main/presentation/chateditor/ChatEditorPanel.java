package main.presentation.chateditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import main.data.chat.ChatManager;
import main.data.file.ChatLoader;
import main.entity.CompareOperator;
import main.entity.actor.ActorType;
import main.entity.chat.Chat;
import main.entity.chat.ChatResponse;
import main.entity.event.Trigger;
import main.entity.event.TriggerType;
import main.entity.requirement.Requirement;
import main.entity.requirement.RequirementType;

@SuppressWarnings("serial")
public class ChatEditorPanel extends JPanel implements ListSelectionListener, ActionListener
{
	public final int PANEL_HEIGHT;
	public final int PANEL_WIDTH;
	
	private static final int MAX_RESPONSE_LENGTH = 61;
	
	public static final int BORDER_PAD = 10;
	public static final int BUTTON_PAD = 5;
	
	public static final int BUTTON_HEIGHT = 25;

	public static final String ACTOR_LIST_NAME = "actors";
	public static final String CHAT_LIST_NAME = "chats";
	public static final String RESPONSE_LIST_NAME = "responses";
	public static final String REQUIREMENT_LIST_NAME = "requirements";
	public static final String TRIGGER_LIST_NAME = "triggers";
	
	public static final String ADD_ACTOR = "add actor";
	public static final String REMOVE_ACTOR = "remove actor";
	public static final String SAVE_ACTOR = "save actor";
	public static final String ADD_CHAT = "add chat";
	public static final String REMOVE_CHAT = "remove chat";
	public static final String ENTRY_CHECK = "entry";
	public static final String ADD_RESPONSE = "add response";
	public static final String REMOVE_RESPONSE = "remove response";
	public static final String UPDATE_RESPONSE_TEXT = "update response text";
	public static final String UPDATE_RESPONSE_FLOWTO = "update response flowto";
	public static final String ADD_REQUIREMENT = "add requirement";
	public static final String REMOVE_REQUIREMENT = "remove requirement";
	public static final String UPDATE_REQUIREMENT = "update requirement";
	public static final String ADD_TRIGGER = "add trigger";
	public static final String REMOVE_TRIGGER = "remove trigger";
	public static final String UPDATE_TRIGGER = "update trigger";
	public static final String CHANGE_REQTYPE = "req type";
	public static final String CHANGE_REQVAL = "req value";
	public static final String CHANGE_REQMOD = "req modifier";
	public static final String CHANGE_REQOP = "req operator";
	public static final String CHANEG_REQCOMP = "req comparison";
	public static final String CHANGE_TRIGTYPE = "trig type";
	public static final String CHANGE_TRIGVAL = "trig value";
	public static final String CHANGE_TRIGMOD = "trig modifier";
	public static final String CHANGE_TRIGOP = "trig operator";
	public static final String CHANEG_TRIGCOMP = "trig comparison";
	
	private boolean refreshingRequirement = false;
	private boolean refreshingTrigger = false;
	
	private ChatLoader chatLoader;
	private ChatManager chatManager;
	
	private Chat currentChat = null;
	private ChatResponse currentResponse = null;
	private Requirement currentRequirement = null;
	private Trigger currentTrigger = null;
	
	private JList<ActorType> actorList = new JList<ActorType>(new DefaultListModel<ActorType>());
	private JList<Chat> chatList = new JList<Chat>(new ChatListModel());
	private JList<ChatResponse> responseList = new JList<ChatResponse>(new ResponseListModel());
	private JList<Requirement> requirementList = new JList<Requirement>(new RequirementListModel());
	private JList<Trigger> triggerList = new JList<Trigger>(new TriggerListModel());
	
	private JComboBox<RequirementType> requirementType = new JComboBox<RequirementType>();
	private JComboBox<String> requirementModifiers = new JComboBox<String>();
	private JComboBox<String> requirementValues = new JComboBox<String>();
	private JComboBox<CompareOperator> requirementComparator = new JComboBox<CompareOperator>();
	private JComboBox<String> requirementComparison = new JComboBox<String>();
	
	private JComboBox<TriggerType> triggerType = new JComboBox<TriggerType>();
	private JComboBox<String> triggerModifiers = new JComboBox<String>();
	private JComboBox<String> triggerValues = new JComboBox<String>();
	private JComboBox<CompareOperator> triggerComparator = new JComboBox<CompareOperator>();
	private JComboBox<String> triggerComparison = new JComboBox<String>();
	
	private JTextArea chatText = new JTextArea();
	private JCheckBox entryCheck = new JCheckBox("Entry Chat?");
	private JTextField responseText = new JTextField();				//TODO: make this whatever class can enforce a character limit
	private JTextField responseFlowTo = new JTextField();

	public ChatEditorPanel(int heightArg, int widthArg)
	{
		PANEL_HEIGHT = heightArg;
		PANEL_WIDTH = widthArg;

		chatLoader = ChatLoader.getInstance();
		chatManager = ChatManager.getInstance();

		chatManager.populateChats(chatLoader.loadAllChats());

		addGuiElements();
	}

	private void addGuiElements()
	{
		setLayout(null);
		GuiBuilder.setPanel(this);
		GuiBuilder.addActorList(actorList);
		revalidate();
		repaint();
		GuiBuilder.addActorButtons();
		revalidate();
		repaint();
		GuiBuilder.addChatList(chatList);
		revalidate();
		repaint();
		GuiBuilder.addChatButtons();
		revalidate();
		repaint();
		GuiBuilder.addChatTextArea(chatText);
		revalidate();
		repaint();
		GuiBuilder.addEntryCheckbox(entryCheck);
		revalidate();
		repaint();
		GuiBuilder.addResponseList(responseList);
		revalidate();
		repaint();
		GuiBuilder.addResponseFields(responseText, responseFlowTo);
		revalidate();
		repaint();
		GuiBuilder.addRequirementFields(requirementList, requirementType, requirementModifiers, requirementValues, requirementComparator, requirementComparison);
		revalidate();
		repaint();
		GuiBuilder.addRequirementButtons();
		revalidate();
		repaint();
		GuiBuilder.addTriggerFields(triggerList, triggerType, triggerModifiers, triggerValues, triggerComparator, triggerComparison);
		revalidate();
		repaint();
		GuiBuilder.addTriggerButtons();
		revalidate();
		repaint();
	}
	
	private void updateActor(ActorType actorType)
	{
		ChatListModel model = (ChatListModel) chatList.getModel();
		model.removeAllElements();
		
		List<Chat> chatsForActor = chatManager.getAllChatsForActor(actorType);
		
		if (chatsForActor == null)
			return;
		
		//ConcurrentModificationException here
//		for (Chat chat : chatsForActor)
//			model.addElement(chat);
		
		//should avoid ConcurrentModificationException thrown by above block
		Iterator<Chat> iter = null;
		
		do {
			try {
				iter = chatsForActor.iterator();
		
				while (iter.hasNext()) {
					model.addElement(iter.next());
				}
			} catch (ConcurrentModificationException cme)
			{
				iter = null;
			}
		} while (iter == null);
		
		if (!model.isEmpty())
			chatList.setSelectedIndex(0);
	}

	private void saveCurrentChat()
	{
		if (currentChat == null)
			return;
		
		currentChat.setText(chatText.getText());
		currentChat.setEntry(entryCheck.isSelected());
		chatManager.updateChat(currentChat);
	}
	
	private void changeSelectedChat(Chat chat)
	{
		saveCurrentChat();
		currentChat = chat;
		
		String text;
		boolean entry;
		
		if (chat == null)
		{
			text = "";
			entry = false;
		}
		else
		{	
			text = chat.getText();
			entry = chat.getEntry();
		}
		
		chatText.setText(text);
		entryCheck.setSelected(entry);
		updateChatResponses(chat);
	}

	private void updateChatResponses(Chat chat)
	{
		ResponseListModel model = (ResponseListModel) responseList.getModel();
		model.removeAllElements();
		
		if (chat == null)
			return;
		
		List<ChatResponse> responses = chat.getResponse();
		
		if (responses == null)
			return;
		
		for (ChatResponse response : responses)
		{
			model.addElement(response);
		}
		
		if (!model.isEmpty())
			responseList.setSelectedIndex(0);
	}
	
	private void changeSelectedResponse(ChatResponse response)
	{
		saveCurrentChat();	//this SHOULD work, assuming we're editing a reference to the response that the chat owns
		
		String text;
		String flowTo;
		
		if (response == null)
		{
			text = "";
			flowTo = "";
		}
		else
		{	
			text = response.getText();
			flowTo = response.getFlowTo();
		}
		
		responseText.setText(text);
		responseFlowTo.setText(flowTo);
		updateResponseRequirements(response);
		updateResponseTriggers(response);
		
		currentResponse = response;
	}
	
	private void updateResponseText()
	{
		if (currentResponse == null)
			return;
		
		String newText = responseText.getText();
		if (newText.length() > MAX_RESPONSE_LENGTH)
			newText = newText.substring(0, MAX_RESPONSE_LENGTH);
		
		currentResponse.setText(newText);
		saveCurrentChat();
		
		ResponseListModel model = (ResponseListModel)responseList.getModel();
		model.refreshView();
	}
	
	private void updateResponseFlowTo()
	{
		if (currentResponse == null)
			return;
		
		currentResponse.setFlowTo(responseFlowTo.getText());
		saveCurrentChat();
		
		ResponseListModel model = (ResponseListModel)responseList.getModel();
		model.refreshView();
	}

	private void updateResponseRequirements(ChatResponse response)
	{
		RequirementListModel model = (RequirementListModel) requirementList.getModel();
		model.removeAllElements();
		
		if (response == null)
			return;
		
		List<Requirement> requirements = response.getReqs();
		
		if (requirements == null)
			return;
		
		for (Requirement requirement : requirements)
		{
			model.addElement(requirement);
		}
		
		if (!model.isEmpty())
		{
			requirementList.setSelectedIndex(0);
			enableRequirementFields();
		}
		
		refreshRequirementFields();
	}

	private void setCurrentRequirementType()
	{
		if (currentRequirement == null)
			return;
		
		RequirementType type = RequirementType.fromString(requirementType.getSelectedItem().toString());
		currentRequirement.setType(type);
	}
	
	//TODO: this might not work if the reference somehow isn't maintained in the response's requirement list
	private void updateCurrentRequirementFromFields()
	{
		if (currentRequirement == null)
			return;
		
		setCurrentRequirementType();
		
		String modifier = (String)requirementModifiers.getSelectedItem();
		String value = (String)requirementValues.getSelectedItem();
		CompareOperator operator = (CompareOperator)requirementComparator.getSelectedItem();
		String compareValue = (String)requirementComparison.getSelectedItem();
		
		currentRequirement.setDetails(modifier, value, operator, compareValue);
		
		RequirementListModel model = (RequirementListModel)requirementList.getModel();
		model.refreshView();
	}

	private void setCurrentTriggerType()
	{
		if (currentTrigger == null)
			return;
		
		TriggerType type = TriggerType.fromString(triggerType.getSelectedItem().toString());
		currentTrigger.setType(type);		//a side effect of this is that the other fields might not have sensible values, but it's probably better to let the user just update them rather than wiping them
//		currentTrigger = new Trigger(type);
	}
	
	//TODO: this might not work if the reference somehow isn't maintained in the response's trigger list
	private void updateCurrentTriggerFromFields()
	{
		if (currentTrigger == null)
			return;
		
		setCurrentTriggerType();
		
		String modifier = (String)triggerModifiers.getSelectedItem();
		String value = (String)triggerValues.getSelectedItem();
		CompareOperator operator = (CompareOperator)triggerComparator.getSelectedItem();
		String compareValue = (String)triggerComparison.getSelectedItem();
		
		currentTrigger.setDetails(modifier, value, operator, compareValue);
		
		TriggerListModel model = (TriggerListModel)triggerList.getModel();
		model.refreshView();
	}
	
	private void changeSelectedRequirement(Requirement requirement)
	{
//		updateCurrentRequirementFromFields();
		refreshRequirementFields();
		saveCurrentChat();
		
		currentRequirement = requirement;
	}
	
	private void changeSelectedTrigger(Trigger trigger)
	{
//		updateCurrentTriggerFromFields();
		refreshTriggerFields();
		saveCurrentChat();
		
		currentTrigger = trigger;
	}

	private void updateResponseTriggers(ChatResponse response)
	{
		TriggerListModel model = (TriggerListModel) triggerList.getModel();
		model.removeAllElements();
		
		if (response == null)
			return;
		
		List<Trigger> triggers = response.getTriggers();
		
		if (triggers == null)
			return;
		
		for (Trigger trigger : triggers)
		{
			model.addElement(trigger);
		}
		
		if (!model.isEmpty())
		{
			triggerList.setSelectedIndex(0);
			enableTriggerFields();
		}
		
		refreshTriggerFields();
	}

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		@SuppressWarnings("rawtypes")
		JList list = (JList)e.getSource();
		DefaultListSelectionModel model = (DefaultListSelectionModel)list.getSelectionModel();
		
		switch (list.getName())
		{
		case ACTOR_LIST_NAME:
			if (model.isSelectionEmpty())
				updateActor(null);
			else
				updateActor(actorList.getSelectedValue());
			
			return;
		case CHAT_LIST_NAME:
			if (model.isSelectionEmpty())
				changeSelectedChat(null);
			else
				changeSelectedChat(chatList.getSelectedValue());
			
			return;
		case RESPONSE_LIST_NAME:
			if (model.isSelectionEmpty())
				changeSelectedResponse(null);
			else
				changeSelectedResponse(responseList.getSelectedValue());
			return;
			
		case REQUIREMENT_LIST_NAME:
			if (model.isSelectionEmpty())
				changeSelectedRequirement(null);
			else
				changeSelectedRequirement(requirementList.getSelectedValue());
			return;
			
		case TRIGGER_LIST_NAME:
			if (model.isSelectionEmpty())
				changeSelectedTrigger(null);
			else
				changeSelectedTrigger(triggerList.getSelectedValue());
			return;
		}
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		String actionCommand = event.getActionCommand();
		
		switch (actionCommand)
		{
		case SAVE_ACTOR:
			saveChatsForActor();
			break;
		case ADD_CHAT:
			addNewChat();
			break;
		case REMOVE_CHAT:
			removeSelectedChat();
			break;
		case ADD_RESPONSE:
			addNewResponse();
			break;
		case REMOVE_RESPONSE:
			removeSelectedResponse();
			break;
		case UPDATE_RESPONSE_TEXT:
			updateResponseText();
			break;
		case UPDATE_RESPONSE_FLOWTO:
			updateResponseFlowTo();
			break;
		case ADD_REQUIREMENT:
			addNewRequirement();
			break;
		case REMOVE_REQUIREMENT:
			removeSelectedRequirement();
			break;
		case UPDATE_REQUIREMENT:
			updateCurrentRequirementFromFields();
			break;
		case ADD_TRIGGER:
			addNewTrigger();
			break;
		case REMOVE_TRIGGER:
			removeSelectedTrigger();
			break;
		case UPDATE_TRIGGER:
			updateCurrentTriggerFromFields();
			break;
		case CHANGE_REQTYPE:
			if (!refreshingRequirement)
			{
				setCurrentRequirementType();
				refreshRequirementFields();
			}
			break;
		case CHANGE_TRIGTYPE:
			if (!refreshingTrigger)
			{
				setCurrentTriggerType();
				refreshTriggerFields();
			}
			break;
		}
	}

	//TODO: eventually keep a list of changed actors and save all of those; currently only saves the selected one
	private void saveChatsForActor()
	{
		ActorType currentActor = actorList.getSelectedValue();
		
		if (currentActor == null)
			return;
		
		saveCurrentChat();
		
		List<Chat> actorChats = chatManager.getAllChatsForActor(currentActor);
		chatLoader.saveChatForActor(currentActor, actorChats);
	}

	private void addNewChat()
	{
		ActorType currentActor = actorList.getSelectedValue();
		
		if (currentActor == null)
			return;
		
		String chatTag = JOptionPane.showInputDialog("Enter new chat tag.");
		
		if (chatTag == null)
			return;
		
		Chat newChat = new Chat();
		newChat.setTag(chatTag.toUpperCase());
		chatManager.addNewChat(newChat, currentActor);
		ChatListModel model = (ChatListModel) chatList.getModel();
		model.addElement(newChat);
		chatList.setSelectedValue(newChat, true);
		currentChat = newChat;
	}

	private void removeSelectedChat()
	{
		chatManager.removeChat(currentChat);
		currentChat = null;
		
		int index = chatList.getSelectedIndex();
		ChatListModel model = (ChatListModel) chatList.getModel();
		model.removeElementAt(index);
		chatList.setSelectedIndex(index);
		
		if (chatList.getSelectedIndex() == -1 && !model.isEmpty())
			chatList.setSelectedIndex(index - 1);
		
		if (chatList.getSelectedIndex() != -1)
			changeSelectedChat(model.getElementAt(chatList.getSelectedIndex()));
	}

	private void addNewResponse()
	{
		if (currentChat == null)
			return;
		
		String text = JOptionPane.showInputDialog("Enter new response text.");
		
		if (text == null)
			return;
		
		if (text.length() > MAX_RESPONSE_LENGTH)
			text = text.substring(0, MAX_RESPONSE_LENGTH);
		
		ChatResponse newResponse = new ChatResponse();
		newResponse.setText(text);
		currentChat.getResponse().add(newResponse);
		saveCurrentChat();
		
		ResponseListModel model = (ResponseListModel) responseList.getModel();
		model.addElement(newResponse);
		responseList.setSelectedValue(newResponse, true);
	}

	private void removeSelectedResponse()
	{
		if (currentChat == null || currentResponse == null)
			return;
		
		currentChat.getResponse().remove(currentResponse);
		currentResponse = null;
		
		int index = responseList.getSelectedIndex();
		ResponseListModel model = (ResponseListModel) responseList.getModel();
		model.removeElementAt(index);
		responseList.setSelectedIndex(index);
		
		if (responseList.getSelectedIndex() == -1 && !model.isEmpty())
			responseList.setSelectedIndex(index - 1);
		
		if (responseList.getSelectedIndex() != -1)
			changeSelectedResponse(model.getElementAt(responseList.getSelectedIndex()));
	}

	private void addNewRequirement()
	{
		if (currentResponse == null)
			return;
		
		Requirement requirement = new Requirement(RequirementType.ACTOR_TYPE, "");
		currentResponse.getReqs().add(requirement);
		currentRequirement = requirement;
		
		RequirementListModel model = (RequirementListModel) requirementList.getModel();
		model.addElement(requirement);
		requirementList.setSelectedValue(requirement, true);
		
		refreshRequirementFields();
	}

	private void removeSelectedRequirement()
	{
		if (currentChat == null || currentResponse == null || currentRequirement == null)
			return;
		
		currentResponse.getReqs().remove(currentRequirement);
		currentRequirement = null;
		
		int index = requirementList.getSelectedIndex();
		RequirementListModel model = (RequirementListModel) requirementList.getModel();
		model.removeElementAt(index);
		requirementList.setSelectedIndex(index);
		
		if (requirementList.getSelectedIndex() == -1 && !model.isEmpty())
			requirementList.setSelectedIndex(index - 1);
		
		if (requirementList.getSelectedIndex() != -1)
			changeSelectedRequirement(model.getElementAt(requirementList.getSelectedIndex()));
	}

	private void refreshRequirementFields()
	{
		if (currentRequirement == null)
		{
			disableRequirementFields();
			ReqFieldPopulator.getInstance().updateFields(null);
			return;
		}
		
		refreshingRequirement = true;
		
		enableRequirementFields();
		ReqFieldPopulator.getInstance().updateFields(currentRequirement.getType());
		
		requirementType.setSelectedItem(currentRequirement.getType());
		requirementModifiers.setSelectedItem(currentRequirement.getModifier());
		requirementValues.setSelectedItem(currentRequirement.getValue());
		requirementComparator.setSelectedItem(currentRequirement.getOperator());
		requirementComparison.setSelectedItem(currentRequirement.getComparison());
		
		refreshingRequirement = false;
	}
	
	private void enableRequirementFields()
	{
		requirementType.setEnabled(true);
		requirementModifiers.setEnabled(true);
		requirementValues.setEnabled(true);
		requirementComparator.setEnabled(true);
		requirementComparison.setEnabled(true);
	}
	
	private void disableRequirementFields()
	{
		requirementType.setEnabled(false);
		requirementModifiers.setEnabled(false);
		requirementValues.setEnabled(false);
		requirementComparator.setEnabled(false);
		requirementComparison.setEnabled(false);
	}

	private void addNewTrigger()
	{
		if (currentResponse == null)
			return;
		
		Trigger trigger = new Trigger(TriggerType.CHANGE_HP, "");
		currentResponse.getTriggers().add(trigger);
		currentTrigger = trigger;
		
		TriggerListModel model = (TriggerListModel) triggerList.getModel();
		model.addElement(trigger);
		triggerList.setSelectedValue(trigger, true);
		
		refreshTriggerFields();
	}

	private void removeSelectedTrigger()
	{
		if (currentChat == null || currentResponse == null || currentTrigger == null)
			return;
		
		currentResponse.getTriggers().remove(currentTrigger);
		currentTrigger = null;
		
		int index = triggerList.getSelectedIndex();
		TriggerListModel model = (TriggerListModel) triggerList.getModel();
		model.removeElementAt(index);
		requirementList.setSelectedIndex(index);
		
		if (triggerList.getSelectedIndex() == -1 && !model.isEmpty())
			triggerList.setSelectedIndex(index - 1);
		
		if (triggerList.getSelectedIndex() != -1)
			changeSelectedTrigger(model.getElementAt(triggerList.getSelectedIndex()));
	}

	private void refreshTriggerFields()
	{
		if (currentTrigger == null)
		{
			disableTriggerFields();
			TriggerFieldPopulator.getInstance().updateFields(null);
			return;
		}
		
		refreshingTrigger = true;
		
		enableTriggerFields();
		TriggerFieldPopulator.getInstance().updateFields(currentTrigger.getType());
		
		triggerType.setSelectedItem(currentTrigger.getType());
		triggerModifiers.setSelectedItem(currentTrigger.getModifier());
		triggerValues.setSelectedItem(currentTrigger.getValue());
		triggerComparator.setSelectedItem(currentTrigger.getOperator());
		triggerComparison.setSelectedItem(currentTrigger.getComparison());
		
		refreshingTrigger = false;
	}
	
	private void enableTriggerFields()
	{
		triggerType.setEnabled(true);
		triggerModifiers.setEnabled(true);
		triggerValues.setEnabled(true);
		triggerComparator.setEnabled(true);
		triggerComparison.setEnabled(true);
	}
	
	private void disableTriggerFields()
	{
		triggerType.setEnabled(false);
		triggerModifiers.setEnabled(false);
		triggerValues.setEnabled(false);
		triggerComparator.setEnabled(false);
		triggerComparison.setEnabled(false);
	}
	
	private class ChatListModel extends AbstractSortableListModel<Chat>
	{
		@Override
		public void sort()
		{
		    Collections.sort(elements);
		    refreshView();
		}
	}
	
	private class ResponseListModel extends AbstractSortableListModel<ChatResponse>
	{
		@Override
		public void sort()
		{
		    Collections.sort(elements);
		    refreshView();
		}
	}
	
	private class RequirementListModel extends AbstractSortableListModel<Requirement>
	{
		@Override
		public void sort()
		{
		    Collections.sort(elements);
		    refreshView();
		}
	}
	
	private class TriggerListModel extends AbstractSortableListModel<Trigger>
	{
		@Override
		public void sort()
		{
		    Collections.sort(elements);
		    refreshView();
		}
	}
}
