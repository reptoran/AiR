package main.presentation.curses;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import main.entity.quest.Quest;
import main.entity.quest.QuestBuilder;
import main.entity.quest.QuestManager;
import main.entity.quest.QuestNode;
import main.presentation.GuiState;

public class CursesGuiQuestDisplay extends ColorSchemeCursesGuiUtil
{
	private CursesGui parentGui;
	
	public CursesGuiQuestDisplay(CursesGui parentGui, ColorScheme colorScheme)
	{
		super(colorScheme);
		
		this.parentGui = parentGui;
	}

	@Override
	public void refresh()
	{
		clearScreen();
		drawBorders();
		listQuests();
	}

	private void drawBorders()
	{
		
		addText(0, 0, "---------------------------------[", getBorderColor());
		addText(0, 48, "]---------------------------------", getBorderColor());
		addText(0, 34, "Current Quests", getTitleColor());
		addText(24, 0, "--------------------------------------------------------------------------------", getBorderColor());
	}
	
	//TODO: eventually add pagination
	private void listQuests()
	{
//		Quest quest1 = new QuestBuilder().setName("Do the thing.").build();
//		quest1.REMOVE_THIS_addActiveNode("STEP1", "here is step 1");
//		quest1.REMOVE_THIS_addActiveNode("PARALLEL", "parallel step is to do this thing.");
//		
//		Quest quest2 = new QuestBuilder().setName("Quest with no active nodes to check line spacing").build();
//		
//		Quest quest3 = new QuestBuilder().setName("Finish the Quest Manager").build();
//		quest3.REMOVE_THIS_addActiveNode("ONLYONE", "most quests will only have one active step.");
//		
//		List<Quest> activeQuests = new ArrayList<Quest>();
//		activeQuests.add(quest1);
//		activeQuests.add(quest2);
//		activeQuests.add(quest3);
		
		//TODO: actual method start (pulling activeQuests from the quest manager)
		
		List<Quest> activeQuests = QuestManager.getInstance().getActiveQuests();
		
		int currentLine = 1;
		
		for (Quest quest : activeQuests)
		{
			if (!quest.isKnown())
				continue;
			
			addText(currentLine, 0, quest.getName(), getHighlightColor());
			
			Set<QuestNode> activeNodes = quest.getActiveNodes();
			
			for (QuestNode node : activeNodes)
			{
				currentLine++;
				addText(currentLine, 0, node.getDescription(), getTextColor());
			}
			
			currentLine += 2;	
		}
	}

	@Override
	protected void handleKey(int code, char keyChar)
	{
		if (code != KeyEvent.VK_ESCAPE)
			return;
		
		parentGui.setSingleLayer(GuiState.MAIN_GAME);
	}
}
