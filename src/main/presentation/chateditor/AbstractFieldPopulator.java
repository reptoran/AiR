package main.presentation.chateditor;

import javax.swing.JComboBox;

import main.entity.actor.ActorType;
import main.entity.chat.CompareOperator;
import main.entity.item.ItemType;

public abstract class AbstractFieldPopulator
{
	protected JComboBox<String> modifiers = new JComboBox<String>();
	protected JComboBox<String> values = new JComboBox<String>();
	protected JComboBox<CompareOperator> operators = new JComboBox<CompareOperator>();
	protected JComboBox<String> comparison = new JComboBox<String>();
	
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
	}
}
