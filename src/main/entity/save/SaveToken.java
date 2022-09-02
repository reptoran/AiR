package main.entity.save;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class SaveToken
{
	public static final int TAG_LENGTH = 5;
	
	private SaveTokenTag tag;
	private String contents;
	
	//takes the arguments in the form of TAG, "contents"
	public SaveToken(SaveTokenTag tag, String contents)
	{
		this.tag = tag;
		this.contents = contents;
	}
	
	//takes the arguments in the form of TAG, {"1", "2", "3", "4", "5"}
	public SaveToken(SaveTokenTag tag, List<String> contents)
	{
		createTokenFromList(tag, contents);
	}
	
	//takes the arguments in the form of TAG, {"KEY1:VAL1", "KEY2:VAL2"}
	public SaveToken(SaveTokenTag tag, Map<String, String> contentMap)
	{
		List<String> contentList = new ArrayList<String>();
		
		for (String key : contentMap.keySet())
		{
			String value = contentMap.get(key);
			
			contentList.add(key + ":" + value);
		}
		
		createTokenFromList(tag, contentList);
	}
	
	//takes the argument in the form of "[TAG]content"
	public SaveToken(String token)
	{
		String tempTag = token.substring(1, TAG_LENGTH + 1);
		contents = token.substring(TAG_LENGTH + 2);
		
		tag = SaveTokenTag.valueOf(tempTag.toUpperCase());
	}
	
	private void createTokenFromList(SaveTokenTag stt, List<String> contentList)
	{
		if (contentList.isEmpty())
		{
			this.contents = "";
			this.tag = null;
			return;
		}
		
		String compiledContents = "";
		
		for (String s : contentList)
		{
			compiledContents = compiledContents + s + ",";
		}
		
		//trim the last comma
		this.contents = compiledContents.substring(0, compiledContents.length() - 1);
		this.tag = stt;
	}
	
	@Override
	public String toString()
	{
		return "[" + tag.name() + "]" + contents;
	}
	
	public String getContents()
	{
		return contents;
	}
	
	public SaveTokenTag getTag()
	{
		return tag;
	}
	
	public List<String> getContentSet()
	{
		List<String> toRet = new ArrayList<String>();
		
		@SuppressWarnings("resource")
		Scanner s = new Scanner(contents).useDelimiter(",");
		
		while (s.hasNext())
		{
			toRet.add(s.next());
		}
		
		return toRet;
	}
	
	public Map<String, String> getContentMap()
	{
		List<String> contentList = new ArrayList<String>();
		Map<String, String> contentMap = new HashMap<String, String>();
		
		@SuppressWarnings("resource")
		Scanner s = new Scanner(contents).useDelimiter(",");
		
		while (s.hasNext())
		{
			contentList.add(s.next());
		}
		
		for (String entry : contentList)
		{
			int delimiterIndex = entry.indexOf(':');
			String key = entry.substring(0, delimiterIndex);
			String value = entry.substring(delimiterIndex + 1);
			contentMap.put(key, value);
		}
		
		return contentMap;
	}
}