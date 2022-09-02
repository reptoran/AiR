package main.entity;

import org.apache.commons.lang3.StringUtils;

public enum CompareOperator
{
	EQUAL('=', "IS"),
	NOT_EQUAL('!', "IS NOT"),
	LESS_THAN('<', "<"),
	GREATER_THAN('>', ">"),
	LESS_THAN_OR_EQUAL((char)243, "<="),
	GREATER_THAN_OR_EQUAL((char)242, ">=");
	
	private char symbol;
	private String description;
	
	private CompareOperator(char symbol, String description)
	{
		this.symbol = symbol;
		this.description = description;
	}
	
	public char getSymbol()
	{
		return symbol;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public static CompareOperator fromSymbol(String symbolString)
	{
		if (StringUtils.isEmpty(symbolString))
			return null;
		
		return fromSymbol(symbolString.charAt(0));
	}
	
	public static CompareOperator fromSymbol(char symbolChar)
	{
		for (CompareOperator co : CompareOperator.values())
		{
			if (co.symbol == symbolChar)
				return co;
		}
		
		throw new IllegalArgumentException("No CompareOperator defined for symbol [" + symbolChar + "].");
	}
}
