package main.presentation.chateditor;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;

import main.data.file.QuestLoader;
import main.entity.CompareOperator;
import main.entity.actor.ActorType;
import main.entity.item.ItemType;
import main.entity.quest.Quest;
import main.entity.quest.QuestNode;
import main.entity.quest.QuestNodeStatus;
import main.entity.zone.ZoneAttribute;
import main.entity.zone.predefined.PredefinedZone;
import main.entity.zone.predefined.PredefinedZoneLoader;

public abstract class AbstractFieldPopulator
{
	protected JComboBox<String> modifiers = new JComboBox<String>();
	protected JComboBox<String> values = new JComboBox<String>();
	protected JComboBox<CompareOperator> operators = new JComboBox<CompareOperator>();
	protected JComboBox<String> comparison = new JComboBox<String>();
	
	private List<String> zoneNames = null;
	private List<String> questNodeTags = null;
	private List<String> questTags = null;
	
	public void setFields(JComboBox<String> modifiersArg, JComboBox<String> valuesArg, JComboBox<CompareOperator> operatorsArg, JComboBox<String> comparisonArg)
	{
		modifiers = modifiersArg;
		values = valuesArg;
		operators = operatorsArg;
		comparison = comparisonArg;
	}
	
	protected void enableModifiers()
	{
		modifiers.setEnabled(true);
	}
	
	protected void disableModifiers()
	{
		modifiers.removeAllItems();
		modifiers.setEnabled(false);
	}

	protected void addActorModifiers()
	{
		modifiers.removeAllItems();
		
		for (ActorType actor : ActorType.values())
		{
			modifiers.addItem(actor.name());
		}
	}

	protected void addQuestNodeTagModifiers()
	{
		loadQuestAndNodeTags();
		
		modifiers.removeAllItems();
		
		for (String questNodeTag : questNodeTags)
		{
			modifiers.addItem(questNodeTag);
		}
	}

	protected void enableValues()
	{
		values.setEnabled(true);
	}

	protected void addFreeformValues()
	{
		values.removeAllItems();
		values.setEditable(true);
	}
	
	protected void disableValues()
	{
		values.removeAllItems();
		values.setEnabled(false);
		values.setEditable(false);
	}

	protected void addItemValues()
	{
		values.removeAllItems();
		
		for (ItemType item : ItemType.values())
		{
			values.addItem(item.name());
		}
		
		values.setSelectedItem(ItemType.NO_TYPE);
	}
	
	protected void addQuestTagValues()
	{
		loadQuestAndNodeTags();
		
		values.removeAllItems();
		
		for (String questTag : questTags)
		{
			values.addItem(questTag);
		}
		
		values.setSelectedItem(questTags.get(0));
	}
	
	protected void addQuestNodeStatusValues()
	{
		values.removeAllItems();
		
		for (QuestNodeStatus status : QuestNodeStatus.values())
		{
			values.addItem(status.name());
		}
		
		values.setSelectedItem(QuestNodeStatus.ACTIVE);
	}
	
	protected void enableComparisons()
	{
		operators.removeAllItems();
		operators.setEnabled(true);
		comparison.setEnabled(true);
	}
	
	protected void disableComparisons()
	{
		operators.removeAllItems();
		operators.setEnabled(false);
		comparison.removeAllItems();
		comparison.setEnabled(false);
	}
	
	protected void clearComparisons()
	{
		comparison.removeAllItems();
	}

	protected void addActorTypeComparisons()
	{
		comparison.removeAllItems();
		
		for (ActorType actor : ActorType.values())
		{
			comparison.addItem(actor.name());
		}
		
		values.setSelectedItem(ActorType.NO_TYPE);
	}
	
	protected void addZoneNameComparisons()
	{
		loadZoneNames();
		
		comparison.removeAllItems();
		
		for (String zoneName : zoneNames)
		{
			comparison.addItem(zoneName);
		}
	}
	
	protected void addQuestNodeComparisons()
	{
		loadQuestAndNodeTags();
		
		comparison.removeAllItems();
		
		for (String nodeTag : questNodeTags)
		{
			comparison.addItem(nodeTag);
		}
	}
	
	protected void addQuestComparisons()
	{
		loadQuestAndNodeTags();
		
		comparison.removeAllItems();
		
		for (String questTag : questTags)
		{
			comparison.addItem(questTag);
		}
	}

	protected void addIntOperators()
	{
		operators.removeAllItems();
		
		operators.addItem(CompareOperator.EQUAL);
		operators.addItem(CompareOperator.NOT_EQUAL);
		operators.addItem(CompareOperator.LESS_THAN);
		operators.addItem(CompareOperator.GREATER_THAN);
	}
	
	protected void addStringOperators()
	{
		operators.removeAllItems();
		
		operators.addItem(CompareOperator.EQUAL);
		operators.addItem(CompareOperator.NOT_EQUAL);
	}
	
	protected void addExactOperator()
	{
		operators.removeAllItems();
		operators.addItem(CompareOperator.EQUAL);
		operators.setEnabled(false);
	}
	
	private void loadZoneNames()
	{
		if (zoneNames != null)
			return;
		
		zoneNames = new ArrayList<String>();
		List<PredefinedZone> zones = PredefinedZoneLoader.getInstance().loadAllPredefinedZones();
		
		for (PredefinedZone zone : zones)
		{
			zoneNames.add(zone.getAttribute(ZoneAttribute.NAME));
		}
	}
	
	private void loadQuestAndNodeTags()
	{
		if (questNodeTags != null && questTags != null)
			return;
		
		questNodeTags = new ArrayList<String>();
		questTags = new ArrayList<String>();
		List<Quest> quests = QuestLoader.getInstance().defineQuests();
		
		for (Quest quest : quests)
		{
			for (String nodeKey : quest.getNodes().keySet())
			{
				QuestNode node = quest.getNodes().get(nodeKey);
				
				questNodeTags.add(quest.getTag() + "|" + node.getTag());
			}
			
			questTags.add(quest.getTag());
		}
	}
}
