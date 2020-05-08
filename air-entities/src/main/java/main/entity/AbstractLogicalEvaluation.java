package main.entity;

import org.apache.commons.lang3.StringUtils;

import main.entity.chat.CompareOperator;

public abstract class AbstractLogicalEvaluation
{
	protected String details;

	protected String modifier;
	protected String value;
	protected CompareOperator operator;
	protected String comparison;

	protected String stringRepresentation;

	public String getDetails()
	{
		return details;
	}

	public String getModifier()
	{
		return modifier;
	}

	public String getValue()
	{
		return value;
	}

	public CompareOperator getOperator()
	{
		return operator;
	}

	public String getComparison()
	{
		return comparison;
	}

	public void setDetails(String modifier, String value, CompareOperator operator, String comparison)
	{
		details = "";

		if (!StringUtils.isEmpty(modifier))
			details = modifier + ":";
		
		if (!StringUtils.isEmpty(value))
			details = details + value;

		if (operator != null)
			details = details + operator.getSymbol();
		else
			details = details + "=";

		if (!StringUtils.isEmpty(comparison))
			details = details + comparison;

		putDetailsInMap();
		parseJsonDetailsString();
	}

	protected void parseJsonDetailsString()
	{
		int modifierIndex = details.indexOf(":");
		String operatorString = "";
		int operatorIndex = -1;

		for (CompareOperator co : CompareOperator.values())
		{
			int index = details.indexOf(co.getSymbol());
			if (index != -1)
			{
				operatorString = " " + co.getSymbol() + " ";
				operatorIndex = index;
				operator = co;
				break;
			}
		}

		String detailString = ": " + details;
		
		if (modifierIndex != -1)
		{
			modifier = details.substring(0, modifierIndex);
			detailString = modifier + ":" + detailString;
		}
		
		if (operatorIndex != -1)
		{
			value = details.substring(modifierIndex + 1, operatorIndex);
			comparison = details.substring(operatorIndex + 1);

			if (StringUtils.isEmpty(value))
				detailString = operatorString + comparison;
			else if (StringUtils.isEmpty(modifier))
				detailString = ": " + value + operatorString + comparison;
			else
				detailString = ": " + modifier + ":" + value + operatorString + comparison;
		}

		stringRepresentation = getTypeName() + detailString;
	}
	
	protected abstract void setTypeAndDetailsFromMap();
	protected abstract void putDetailsInMap();
	protected abstract String getTypeName();

	@Override
	public String toString()
	{
		return stringRepresentation;
	}
}
