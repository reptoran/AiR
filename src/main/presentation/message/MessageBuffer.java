package main.presentation.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import main.logic.AI.AiType;

public class MessageBuffer
{
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
	
	public static List<String> parseMessageBuffer(int messageWidth)
	{
		String bufferedMessages = messageBuffer.toString();
		List<String> messageLines = new ArrayList<String>();
		Scanner splitter = new Scanner(bufferedMessages);		//TODO: delimiter?
		
		String currentLine = "";	//TODO: StringBuffer?
		String toAdd = "";
		
		while (splitter.hasNext())
		{
			toAdd = splitter.next();
			int newLength = toAdd.length() + currentLine.length();
			
			if (newLength > messageWidth - 2)
			{
				messageLines.add(currentLine);
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
