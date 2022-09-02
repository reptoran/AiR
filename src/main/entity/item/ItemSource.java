package main.entity.item;

public enum ItemSource
{
	EQUIPMENT('E'), PACK('I'), READY('R'), GROUND('G'), MATERIAL('M'), MAGIC('m'), NONE('~');
	
	private char sourceCharacter;
	
	private ItemSource(char sourceCharacter)
	{
		this.sourceCharacter = sourceCharacter;
	}
	
	public static ItemSource fromChar(char sourceChar)
	{
		for (ItemSource itemSource : values())
		{
			if (itemSource.sourceCharacter == sourceChar)
				return itemSource;
		}
		
		throw new IllegalArgumentException("No ItemSource found for character '" + sourceChar + "'");
	}
	
	public static ItemSource fromInt(int sourceInt)
	{
		return fromChar((char)sourceInt);
	}
	
	public static ItemSource fromString(String sourceString)
	{
		return fromChar(sourceString.charAt(0));
	}
	
	public String stringValue()
	{
		return "" + sourceCharacter;
	}
	
	public char charValue()
	{
		return sourceCharacter;
	}
	
	public int intValue()
	{
		return (int)sourceCharacter;
	}
}
