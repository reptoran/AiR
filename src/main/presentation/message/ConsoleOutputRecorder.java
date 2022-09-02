package main.presentation.message;

import java.util.ArrayList;
import java.util.List;

public class ConsoleOutputRecorder implements MessageRecorder
{

	@Override
	public void recordMessageLine(String line)
	{
		System.out.println("IN-GAME MESSAGE: " + line);
	}

	@Override
	public List<String> retrieveAllMessages()
	{
		return new ArrayList<String>();
	}

	@Override
	public List<String> retrieveMessageSubset(int amountToBacktrack, int linesToRetrieve)
	{
		return new ArrayList<String>();
	}
}
