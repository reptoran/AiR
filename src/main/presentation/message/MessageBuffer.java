package main.presentation.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import main.logic.AI.AiType;

public class MessageBuffer
{
	private static final String MORE_PROMPT = "(more)";
	public static final String NEWLINE = "%NL%";
	
	private static StringBuffer messageBuffer = new StringBuffer();
	
	public static void addMessage(String message)
	{
		messageBuffer.append(" ");
		messageBuffer.append(message);
	}
	
	public static void addMessageIfHuman(String message, AiType ai)
	{
		if (!AiType.HUMAN_CONTROLLED.equals(ai))
			return;
		
		addMessage(message);
	}
	
	public static List<String> parseMessageBuffer(int messageWidth, int messageHeight)
	{
		String bufferedMessages = messageBuffer.toString();
		List<String> messageLines = new ArrayList<String>();
		Scanner splitter = new Scanner(bufferedMessages);		//TODO: delimiter?
		
		String currentLine = "";	//TODO: StringBuffer?
		String toAdd = "";
		
		int rowOnScreen = 1;
		
		while (splitter.hasNext())
		{
			toAdd = splitter.next();
			int newLength = toAdd.length() + currentLine.length();
			
			int maxLineLength = messageWidth - 2;
			
			if (rowOnScreen == messageHeight && splitter.hasNext())
				maxLineLength = maxLineLength - MORE_PROMPT.length();
			
			if (newLength > maxLineLength || NEWLINE.equals(toAdd))
			{
				if (rowOnScreen == messageHeight && splitter.hasNext())
				{
					currentLine = currentLine + MORE_PROMPT;
					rowOnScreen = 1;
				}
				else
				{
					rowOnScreen++;
				}
				
				messageLines.add(currentLine);
				
				//if the NEWLINE string is detected, pull it out instead of adding it to the next line
				if (NEWLINE.equals(toAdd))
					currentLine = "";
				else
					currentLine = toAdd + " ";
			}
			else
			{
				currentLine = currentLine + toAdd + " ";
			}
		}
		
		
		if (currentLine.length() > 0)
			messageLines.add(currentLine);
		
		splitter.close();
		messageBuffer.delete(0, messageBuffer.capacity());
		
		return messageLines;
	}
}
