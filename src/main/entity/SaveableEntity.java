package main.entity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.entity.save.SaveStringBuilder;
import main.entity.save.SaveToken;
import main.entity.save.SaveTokenTag;

public abstract class SaveableEntity
{
	public abstract String saveAsText();
	public abstract String loadFromText(String text) throws ParseException;
	public abstract String getUniqueId();
	
	protected abstract void setMember(SaveStringBuilder saveStringBuilder, SaveTokenTag saveTokenTag);
	
	protected String getContentsForTag(SaveStringBuilder saveStringBuilder, SaveTokenTag saveTokenTag)
	{
		SaveToken saveToken = saveStringBuilder.getToken(saveTokenTag);
		
		if (saveToken != null)
			return saveToken.getContents();
		
		return "";
	}
	
	protected int getIntContentsForTag(SaveStringBuilder saveStringBuilder, SaveTokenTag saveTokenTag)
	{
		try
		{
			return Integer.parseInt(getContentsForTag(saveStringBuilder, saveTokenTag));
		} catch (NumberFormatException nfe)
		{
			return 0;
		}
	}
	
	protected boolean getBooleanContentsForTag(SaveStringBuilder saveStringBuilder, SaveTokenTag saveTokenTag)
	{
		return Boolean.parseBoolean(getContentsForTag(saveStringBuilder, saveTokenTag));
	}
	
	protected List<String> getContentSetForTag(SaveStringBuilder saveStringBuilder, SaveTokenTag saveTokenTag)
	{
		SaveToken saveToken = saveStringBuilder.getToken(saveTokenTag);
		
		if (saveToken != null)
			return saveToken.getContentSet();
		
		return new ArrayList<String>();
	}
	
	protected Map<String, String> getContentMapForTag(SaveStringBuilder saveStringBuilder, SaveTokenTag saveTokenTag)
	{
		SaveToken saveToken = saveStringBuilder.getToken(saveTokenTag);
		
		try
		{
			if (saveToken != null)
				return saveToken.getContentMap();
		} catch (StringIndexOutOfBoundsException sioobe) {}		//thrown if the format is wrong; it's looking for ':' and can't find it
		
		return new HashMap<String, String>();
	}
	
	@Override
	public String toString()
	{
		return saveAsText();
	}
}
