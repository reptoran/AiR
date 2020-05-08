package main.presentation.chateditor;

import java.awt.Insets;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import main.entity.actor.ActorType;
import main.entity.chat.Chat;
import main.entity.chat.ChatReq;
import main.entity.chat.ChatReqType;
import main.entity.chat.ChatResponse;
import main.entity.chat.CompareOperator;
import main.entity.event.Trigger;
import main.entity.event.TriggerType;
import main.presentation.chateditor.renderer.ActorListRenderer;
import main.presentation.chateditor.renderer.ChatListRenderer;
import main.presentation.chateditor.renderer.OperatorListRenderer;
import main.presentation.chateditor.renderer.RequirementListRenderer;
import main.presentation.chateditor.renderer.ResponseListRenderer;
import main.presentation.chateditor.renderer.TriggerListRenderer;

public class GuiBuilder
{
	private static ChatEditorPanel panel;
	
	private static JList<ActorType> bActorList;
	private static JList<Chat> bChatList;
	private static JList<ChatResponse> bResponseList;
	private static JList<ChatReq> bRequirementList;
	private static JList<Trigger> bTriggerList;
	
	private static JTextArea bChatText;
	
	public static void setPanel(ChatEditorPanel panelToSet)
	{
		panel = panelToSet;
	}
	
	public static void addActorList(JList<ActorType> actorList)
	{
		bActorList = actorList;
		actorList.setName(ChatEditorPanel.ACTOR_LIST_NAME);
		actorList.setCellRenderer(new ActorListRenderer());
		actorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane listScroller = new JScrollPane(actorList);
		listScroller.setSize(200, panel.PANEL_HEIGHT - 80);
		listScroller.setLocation(10, 10);
		panel.add(listScroller);
		
		actorList.setSize(listScroller.getSize());
		actorList.setLocation(listScroller.getLocation());
		actorList.addListSelectionListener(panel);
		
		DefaultListModel<ActorType> model = (DefaultListModel<ActorType>) actorList.getModel();
		for (ActorType actor : ActorType.values())
		{
//			if (chatManager.actorHasChats(actor))	//TODO: eventually show only actors with defined chats, as well as the ability to add and remove via a dialog with a dropdown
				model.addElement(actor);
		}
	}

	public static void addActorButtons()
	{
		int buttonWidth = (bActorList.getWidth() / 3) - 3;
		
		JButton add = new JButton("Add");
		add.setActionCommand(ChatEditorPanel.ADD_ACTOR);
		add.setSize(buttonWidth, ChatEditorPanel.BUTTON_HEIGHT);
		add.setLocation(bActorList.getLocation().x, bActorList.getLocation().y + bActorList.getHeight() + ChatEditorPanel.BUTTON_PAD);
		add.addActionListener(panel);
		add.setEnabled(false);
		panel.add(add);
		
		JButton remove = new JButton("Remove");
		remove.setMargin(new Insets(0, 0, 0, 0));
		remove.setActionCommand(ChatEditorPanel.REMOVE_ACTOR);
		remove.setSize(buttonWidth, ChatEditorPanel.BUTTON_HEIGHT);
		remove.setLocation(add.getLocation().x + buttonWidth + ChatEditorPanel.BUTTON_PAD, add.getLocation().y);
		remove.addActionListener(panel);
		remove.setEnabled(false);
		panel.add(remove);
		
		JButton save = new JButton("Save");
		save.setActionCommand(ChatEditorPanel.SAVE_ACTOR);	
		save.setSize(buttonWidth, ChatEditorPanel.BUTTON_HEIGHT);
		save.setLocation(remove.getLocation().x + buttonWidth + ChatEditorPanel.BUTTON_PAD, remove.getLocation().y);
		save.addActionListener(panel);
		panel.add(save);
	}

	public static void addChatList(JList<Chat> chatList)
	{
		bChatList = chatList;
		chatList.setName(ChatEditorPanel.CHAT_LIST_NAME);
		chatList.setCellRenderer(new ChatListRenderer());
		chatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane listScroller = new JScrollPane(chatList);
		listScroller.setSize(200, 150);
		listScroller.setLocation(bActorList.getLocation().x + bActorList.getWidth() + ChatEditorPanel.BORDER_PAD, ChatEditorPanel.BORDER_PAD);
		panel.add(listScroller);
		
		chatList.setSize(listScroller.getSize());
		chatList.setLocation(listScroller.getLocation());
		chatList.addListSelectionListener(panel);
	}

	public static void addChatButtons()
	{
		int buttonWidth = (bChatList.getWidth() / 2) - 3;
		
		JButton add = new JButton("Add");
		add.setActionCommand(ChatEditorPanel.ADD_CHAT);
		add.setSize(buttonWidth, ChatEditorPanel.BUTTON_HEIGHT);
		add.setLocation(bChatList.getLocation().x, bChatList.getLocation().y + bChatList.getHeight() + ChatEditorPanel.BUTTON_PAD);
		add.addActionListener(panel);
		panel.add(add);
		
		JButton remove = new JButton("Remove");
		remove.setActionCommand(ChatEditorPanel.REMOVE_CHAT);
		remove.setSize(buttonWidth, ChatEditorPanel.BUTTON_HEIGHT);
		remove.setLocation(add.getLocation().x + buttonWidth + ChatEditorPanel.BUTTON_PAD, add.getLocation().y);
		remove.addActionListener(panel);
		panel.add(remove);
	}

	public static void addChatTextArea(JTextArea chatText)
	{
		bChatText = chatText;
		chatText.setWrapStyleWord(true);
		chatText.setLineWrap(true);
		chatText.setLocation(bChatList.getLocation().x + bChatList.getWidth() + ChatEditorPanel.BORDER_PAD, bChatList.getLocation().y);
		chatText.setSize(panel.PANEL_WIDTH - bChatList.getWidth() - bActorList.getWidth() - (int)(4.5 * ChatEditorPanel.BORDER_PAD), bChatList.getHeight()); // + panel.BUTTON_PAD + panel.BUTTON_HEIGHT);
		panel.add(chatText);
	}

	public static void addEntryCheckbox(JCheckBox entryCheck)
	{
		entryCheck.setActionCommand(ChatEditorPanel.ENTRY_CHECK);
		entryCheck.setSize(bChatText.getWidth(), ChatEditorPanel.BUTTON_HEIGHT);
		entryCheck.setLocation(bChatText.getLocation().x, bChatText.getLocation().y + bChatText.getHeight() + ChatEditorPanel.BUTTON_PAD);
		entryCheck.addActionListener(panel);
		panel.add(entryCheck);
	}

	public static void addResponseList(JList<ChatResponse> responseList)
	{
		bResponseList = responseList;
		responseList.setName(ChatEditorPanel.RESPONSE_LIST_NAME);
		responseList.setCellRenderer(new ResponseListRenderer());
		responseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane listScroller = new JScrollPane(responseList);
		listScroller.setSize(panel.PANEL_WIDTH - bActorList.getWidth() - (int)(3.5 * ChatEditorPanel.BORDER_PAD), 150);
		listScroller.setLocation(bChatList.getLocation().x, bChatList.getLocation().y + bChatList.getHeight() + ChatEditorPanel.BUTTON_PAD + ChatEditorPanel.BUTTON_HEIGHT + ChatEditorPanel.BUTTON_PAD);
		panel.add(listScroller);
		
		responseList.setSize(listScroller.getSize());
		responseList.setLocation(listScroller.getLocation());
		responseList.addListSelectionListener(panel);
	}

	public static void addResponseFields(JTextField responseText, JTextField responseFlowTo)
	{
		int buttonWidth = 60;
		
		responseText.setSize(bResponseList.getWidth() - (ChatEditorPanel.BUTTON_PAD + buttonWidth), ChatEditorPanel.BUTTON_HEIGHT);
		responseText.setLocation(bResponseList.getLocation().x, bResponseList.getLocation().y + bResponseList.getHeight() + ChatEditorPanel.BUTTON_PAD);
		panel.add(responseText);
		
		JButton update = new JButton("Update");
		update.setMargin(new Insets(0, 0, 0, 0));
		update.setActionCommand(ChatEditorPanel.UPDATE_RESPONSE_TEXT);
		update.setSize(buttonWidth, ChatEditorPanel.BUTTON_HEIGHT);
		update.setLocation(responseText.getLocation().x + responseText.getWidth() + ChatEditorPanel.BUTTON_PAD, responseText.getLocation().y);
		update.addActionListener(panel);
		panel.add(update);
		
		JButton add = new JButton("Add");
		add.setMargin(new Insets(0, 0, 0, 0));
		add.setActionCommand(ChatEditorPanel.ADD_RESPONSE);
		add.setSize(buttonWidth, ChatEditorPanel.BUTTON_HEIGHT);
		add.setLocation(responseText.getLocation().x, responseText.getLocation().y + ChatEditorPanel.BUTTON_HEIGHT + ChatEditorPanel.BUTTON_PAD);
		add.addActionListener(panel);
		panel.add(add);
		
		JButton remove = new JButton("Remove");
		remove.setMargin(new Insets(0, 0, 0, 0));
		remove.setActionCommand(ChatEditorPanel.REMOVE_RESPONSE);
		remove.setSize(buttonWidth, ChatEditorPanel.BUTTON_HEIGHT);
		remove.setLocation(add.getLocation().x + buttonWidth + ChatEditorPanel.BUTTON_PAD, add.getLocation().y);
		remove.addActionListener(panel);
		panel.add(remove);
		
		JButton flowUpdate = new JButton("Update");
		flowUpdate.setMargin(new Insets(0, 0, 0, 0));
		flowUpdate.setActionCommand(ChatEditorPanel.UPDATE_RESPONSE_FLOWTO);
		flowUpdate.setSize(buttonWidth, ChatEditorPanel.BUTTON_HEIGHT);
		flowUpdate.setLocation(update.getLocation().x, update.getLocation().y + ChatEditorPanel.BUTTON_HEIGHT + ChatEditorPanel.BUTTON_PAD);
		flowUpdate.addActionListener(panel);
		panel.add(flowUpdate);
		
		responseFlowTo.setSize(250, ChatEditorPanel.BUTTON_HEIGHT);
		responseFlowTo.setLocation(flowUpdate.getLocation().x - responseFlowTo.getWidth() - ChatEditorPanel.BUTTON_PAD, flowUpdate.getLocation().y);
		panel.add(responseFlowTo);
		
		JLabel flowToLabel = new JLabel("Flow to tag:");
		flowToLabel.setSize(75, ChatEditorPanel.BUTTON_HEIGHT);
		flowToLabel.setLocation(responseFlowTo.getLocation().x - flowToLabel.getWidth(), responseFlowTo.getLocation().y);
		panel.add(flowToLabel);
	}

	public static void addRequirementFields(JList<ChatReq> requirementList, JComboBox<ChatReqType> requirementType, JComboBox<String> requirementModifier, JComboBox<String> requirementValue, JComboBox<CompareOperator> comparator, JComboBox<String> comparison)
	{
		bRequirementList = requirementList;
		
		int elementWidth = (bResponseList.getWidth() / 2) - ChatEditorPanel.BUTTON_PAD;
		int elementHeight = bResponseList.getHeight();
		
		JLabel reqLabel = new JLabel("Requirements:");
		reqLabel.setSize(elementWidth, ChatEditorPanel.BUTTON_HEIGHT);
		reqLabel.setLocation(bResponseList.getLocation().x, bResponseList.getLocation().y + bResponseList.getHeight() + (2 * (ChatEditorPanel.BUTTON_PAD + ChatEditorPanel.BUTTON_HEIGHT)) + ChatEditorPanel.BORDER_PAD);
		panel.add(reqLabel);
		
		requirementList.setName(ChatEditorPanel.REQUIREMENT_LIST_NAME);
		requirementList.setCellRenderer(new RequirementListRenderer());
		requirementList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane listScroller = new JScrollPane(requirementList);
		listScroller.setSize(elementWidth, elementHeight);
		listScroller.setLocation(reqLabel.getLocation().x, reqLabel.getLocation().y + reqLabel.getHeight());
		panel.add(listScroller);
		
		requirementList.setSize(listScroller.getSize());
		requirementList.setLocation(listScroller.getLocation());
		requirementList.addListSelectionListener(panel);
		
		requirementType.setActionCommand(ChatEditorPanel.CHANGE_REQTYPE);
		requirementType.setSize(elementWidth, ChatEditorPanel.BUTTON_HEIGHT);
		requirementType.setLocation(requirementList.getLocation().x + requirementList.getWidth() + ChatEditorPanel.BORDER_PAD, requirementList.getLocation().y);
		requirementType.addActionListener(panel);
		panel.add(requirementType);
		
		for (ChatReqType type : ChatReqType.values())
		{
				requirementType.addItem(type);
		}
		
		requirementModifier.setActionCommand(ChatEditorPanel.CHANGE_REQVAL);
		requirementModifier.setSize(elementWidth, ChatEditorPanel.BUTTON_HEIGHT);
		requirementModifier.setLocation(requirementType.getLocation().x, requirementType.getLocation().y + requirementType.getHeight() + ChatEditorPanel.BUTTON_PAD);
		requirementModifier.addActionListener(panel);
		panel.add(requirementModifier);
		
		requirementValue.setActionCommand(ChatEditorPanel.CHANGE_REQMOD);
		requirementValue.setSize(elementWidth, ChatEditorPanel.BUTTON_HEIGHT);
		requirementValue.setLocation(requirementModifier.getLocation().x, requirementModifier.getLocation().y + requirementModifier.getHeight() + ChatEditorPanel.BUTTON_PAD);
		requirementValue.addActionListener(panel);
		panel.add(requirementValue);
		
		comparator.setRenderer(new OperatorListRenderer());
		comparator.setActionCommand(ChatEditorPanel.CHANGE_REQOP);
		comparator.setSize(50, ChatEditorPanel.BUTTON_HEIGHT);
		comparator.setLocation(requirementValue.getLocation().x, requirementValue.getLocation().y + requirementValue.getHeight() + ChatEditorPanel.BUTTON_PAD);
		comparator.addActionListener(panel);
		panel.add(comparator);
		
		comparison.setActionCommand(ChatEditorPanel.CHANEG_REQCOMP);
		comparison.setEditable(true);
		comparison.setSize(requirementValue.getWidth() - comparator.getWidth() - ChatEditorPanel.BUTTON_PAD, ChatEditorPanel.BUTTON_HEIGHT);
		comparison.setLocation(comparator.getLocation().x + comparator.getWidth() + ChatEditorPanel.BUTTON_PAD, comparator.getLocation().y);
		comparison.addActionListener(panel);
		panel.add(comparison);
		
		ReqFieldPopulator.getInstance().setFields(requirementModifier, requirementValue, comparator, comparison);
	}

	public static void addRequirementButtons()
	{
		int buttonWidth = (bRequirementList.getWidth() / 3) - 3;
		
		JButton add = new JButton("Add");
		add.setActionCommand(ChatEditorPanel.ADD_REQUIREMENT);
		add.setSize(buttonWidth, ChatEditorPanel.BUTTON_HEIGHT);
		add.setLocation(bRequirementList.getLocation().x + bRequirementList.getWidth() + ChatEditorPanel.BORDER_PAD, bRequirementList.getLocation().y + bRequirementList.getHeight() - ChatEditorPanel.BUTTON_HEIGHT);
		add.addActionListener(panel);
		panel.add(add);
		
		JButton remove = new JButton("Remove");
		remove.setMargin(new Insets(0, 0, 0, 0));
		remove.setActionCommand(ChatEditorPanel.REMOVE_REQUIREMENT);
		remove.setSize(buttonWidth, ChatEditorPanel.BUTTON_HEIGHT);
		remove.setLocation(add.getLocation().x + buttonWidth + ChatEditorPanel.BUTTON_PAD, add.getLocation().y);
		remove.addActionListener(panel);
		panel.add(remove);
		
		JButton save = new JButton("Update");
		save.setMargin(new Insets(0, 0, 0, 0));
		save.setActionCommand(ChatEditorPanel.UPDATE_REQUIREMENT);	
		save.setSize(buttonWidth, ChatEditorPanel.BUTTON_HEIGHT);
		save.setLocation(remove.getLocation().x + buttonWidth + ChatEditorPanel.BUTTON_PAD, remove.getLocation().y);
		save.addActionListener(panel);
		panel.add(save);
	}

	public static void addTriggerFields(JList<Trigger> triggerList, JComboBox<TriggerType> triggerType, JComboBox<String> triggerModifier, JComboBox<String> triggerValue, JComboBox<CompareOperator> comparator, JComboBox<String> comparison)
	{
		bTriggerList = triggerList;
		
		int labelY = bRequirementList.getLocation().y + bRequirementList.getHeight() + ChatEditorPanel.BUTTON_PAD;
		int elementWidth = bRequirementList.getWidth();
		int elementHeight = bRequirementList.getHeight();
		
		JLabel triggerLabel = new JLabel("Triggers:");
		triggerLabel.setSize(elementWidth, ChatEditorPanel.BUTTON_HEIGHT);
		triggerLabel.setLocation(bRequirementList.getLocation().x, labelY);
		panel.add(triggerLabel);
		
		triggerList.setName(ChatEditorPanel.TRIGGER_LIST_NAME);
		triggerList.setCellRenderer(new TriggerListRenderer());
		triggerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane listScroller = new JScrollPane(triggerList);
		listScroller.setSize(elementWidth, elementHeight);
		listScroller.setLocation(triggerLabel.getLocation().x, triggerLabel.getLocation().y + triggerLabel.getHeight());
		panel.add(listScroller);
		
		triggerList.setSize(listScroller.getSize());
		triggerList.setLocation(listScroller.getLocation());
		triggerList.addListSelectionListener(panel);
		
		triggerType.setActionCommand(ChatEditorPanel.CHANGE_TRIGTYPE);
		triggerType.setSize(elementWidth, ChatEditorPanel.BUTTON_HEIGHT);
		triggerType.setLocation(triggerList.getLocation().x + triggerList.getWidth() + ChatEditorPanel.BORDER_PAD, triggerList.getLocation().y);
		triggerType.addActionListener(panel);
		panel.add(triggerType);
		
		for (TriggerType type : TriggerType.values())
		{
				triggerType.addItem(type);
		}
		
		triggerModifier.setActionCommand(ChatEditorPanel.CHANGE_TRIGVAL);
		triggerModifier.setSize(elementWidth, ChatEditorPanel.BUTTON_HEIGHT);
		triggerModifier.setLocation(triggerType.getLocation().x, triggerType.getLocation().y + triggerType.getHeight() + ChatEditorPanel.BUTTON_PAD);
		triggerModifier.addActionListener(panel);
		panel.add(triggerModifier);
		
		triggerValue.setActionCommand(ChatEditorPanel.CHANGE_TRIGMOD);
		triggerValue.setSize(elementWidth, ChatEditorPanel.BUTTON_HEIGHT);
		triggerValue.setLocation(triggerModifier.getLocation().x, triggerModifier.getLocation().y + triggerModifier.getHeight() + ChatEditorPanel.BUTTON_PAD);
		triggerValue.addActionListener(panel);
		panel.add(triggerValue);
		
		comparator.setRenderer(new OperatorListRenderer());
		comparator.setActionCommand(ChatEditorPanel.CHANGE_TRIGOP);
		comparator.setSize(50, ChatEditorPanel.BUTTON_HEIGHT);
		comparator.setLocation(triggerValue.getLocation().x, triggerValue.getLocation().y + triggerValue.getHeight() + ChatEditorPanel.BUTTON_PAD);
		comparator.addActionListener(panel);
		panel.add(comparator);
		
		comparison.setActionCommand(ChatEditorPanel.CHANEG_TRIGCOMP);
		comparison.setEditable(true);
		comparison.setSize(triggerValue.getWidth() - comparator.getWidth() - ChatEditorPanel.BUTTON_PAD, ChatEditorPanel.BUTTON_HEIGHT);
		comparison.setLocation(comparator.getLocation().x + comparator.getWidth() + ChatEditorPanel.BUTTON_PAD, comparator.getLocation().y);
		comparison.addActionListener(panel);
		panel.add(comparison);
		
		TriggerFieldPopulator.getInstance().setFields(triggerModifier, triggerValue, comparator, comparison);
	}

	public static void addTriggerButtons()
	{
		int buttonWidth = (bTriggerList.getWidth() / 3) - 3;
		
		JButton add = new JButton("Add");
		add.setActionCommand(ChatEditorPanel.ADD_TRIGGER);
		add.setSize(buttonWidth, ChatEditorPanel.BUTTON_HEIGHT);
//		add.setLocation(bTriggerList.getLocation().x + bTriggerList.getWidth() + ChatEditorPanel.BORDER_PAD, bTriggerList.getLocation().y + bTriggerList.getHeight() - ChatEditorPanel.BUTTON_HEIGHT);
		add.setLocation(220 + bTriggerList.getWidth() + ChatEditorPanel.BORDER_PAD, 620 + bTriggerList.getHeight() - ChatEditorPanel.BUTTON_HEIGHT);	//TODO: shouldn't need to do this but it doesn't work the other way
		add.addActionListener(panel);
		panel.add(add);
		
		JButton remove = new JButton("Remove");
		remove.setMargin(new Insets(0, 0, 0, 0));
		remove.setActionCommand(ChatEditorPanel.REMOVE_TRIGGER);
		remove.setSize(buttonWidth, ChatEditorPanel.BUTTON_HEIGHT);
		remove.setLocation(add.getLocation().x + buttonWidth + ChatEditorPanel.BUTTON_PAD, add.getLocation().y);
		remove.addActionListener(panel);
		panel.add(remove);
		
		JButton save = new JButton("Update");
		save.setMargin(new Insets(0, 0, 0, 0));
		save.setActionCommand(ChatEditorPanel.UPDATE_TRIGGER);	
		save.setSize(buttonWidth, ChatEditorPanel.BUTTON_HEIGHT);
		save.setLocation(remove.getLocation().x + buttonWidth + ChatEditorPanel.BUTTON_PAD, remove.getLocation().y);
		save.addActionListener(panel);
		panel.add(save);
	}
}
