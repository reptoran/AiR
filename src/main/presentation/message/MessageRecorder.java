package main.presentation.message;

import java.util.List;

public interface MessageRecorder
{
	void recordMessageLine(String line);
	List<String> retrieveAllMessages();
	List<String> retrieveMessageSubset(int amountToBacktrack, int linesToRetrieve);
}
