package main.entity;

import org.apache.commons.lang3.StringUtils;

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

	public int getModifierInt()
	{
		return getIntValueOfString(modifier);
	}

	public String getValue()
	{
		return value;
	}
	
	public int getValueInt()
	{
		return getIntValueOfString(value);
	}

	public CompareOperator getOperator()
	{
		return operator;
	}

	public String getComparison()
	{
		return comparison;
	}
	
	public int getComparisonInt()
	{
		return getIntValueOfString(comparison);
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
		removeRedundantEquals();
	}
	
	protected void removeRedundantEquals()
	{
		if (StringUtils.isEmpty(details) || details.charAt(0) != '=')
			return;
		
		details = details.substring(1);
		operator = null;
		putDetailsInMap();
	}

	protected void parseJsonDetailsString()
	{
		int modifierIndex = details.indexOf(":");
		int operatorIndex = -1;

		for (CompareOperator co : CompareOperator.values())
		{
			int index = details.indexOf(co.getSymbol());
			if (index != -1)
			{
				operatorIndex = index;
				operator = co;
				break;
			}
		}
		
		if (modifierIndex != -1)
			modifier = details.substring(0, modifierIndex);
		
		if (operatorIndex != -1)
		{
			value = details.substring(modifierIndex + 1, operatorIndex);
			comparison = details.substring(operatorIndex + 1);
		}
		else
		{
			value = details.substring(modifierIndex + 1);
			operator = null;
			comparison = null;
		}
		
		stringRepresentation = generateStringRepresentation();
	}
	
	private String generateStringRepresentation()
	{
		String detailString = value;
		
		if (!StringUtils.isEmpty(modifier))
			detailString = modifier + ":" + detailString;
		
		if (operator != null)
			detailString = detailString + " " + operator.getSymbol() + " " + comparison;
		
		return getTypeName() + ": " + detailString;
	}
	
	private int getIntValueOfString(String value)
	{
		try
		{
			return Integer.parseInt(value);
		} catch (NumberFormatException nfe)
		{
			return 0;
		}
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
