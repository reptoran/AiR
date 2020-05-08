package main.data.file;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.presentation.Logger;
import main.presentation.message.MessageBuffer;

public class TextLoader extends FileHandler
{
	private static final String STATE_KEY_NAME = "[NAME]";
	private static final String STATE_KEY_TEXT = "[TEXT]";

	private TextDataState currentState = null;
	private Map<String, String> loadedText = new HashMap<String, String>();
	
	private static TextLoader instance = null;
	
	private TextLoader() {}
	
	public static TextLoader getInstance()
	{
		if (instance == null)
			instance = new TextLoader();
		
		return instance;
	}

	public Map<String, String> loadAllGameText()
	{
		// TODO: unzip a .dat file that's just an archive of everything, then search the created folder

		File folder = new File(getDataPath());

		for (File file : folder.listFiles())
		{
			if (!getFileExtension(file).equals(getExtension()))
				continue;

			addGameTextEntry(file.getName());
		}

		return loadedText;
	}

	private void addGameTextEntry(String entryFileName)
	{
		String path = getDataPath() + entryFileName;
		List<String> lines = loadFile(path);

		String key = null;
		StringBuffer text = new StringBuffer(0);

		for (String line : lines)
		{
			if (line.isEmpty())
				continue;

			if (STATE_KEY_NAME.equals(line))
			{
				currentState = TextDataState.NAME;
				continue;
			} else if (STATE_KEY_TEXT.equals(line))
			{
				currentState = TextDataState.TEXT;
				continue;
			}

			if (currentState == TextDataState.NAME)
			{
				key = line;
				continue;
			} else if (currentState == TextDataState.TEXT)
			{
				if (text.toString().isEmpty())
					text.append(line);
				else
					text.append(" " + MessageBuffer.NEWLINE + " " + line);	//if the file specifically breaks the text out into lines, take this into account
			}
		}
		
		if (key == null)
//			throw new IllegalArgumentException("No key name given for game text in file " + entryFileName + ".");
			Logger.warn("No key name given for game text in file " + entryFileName + "; entry will not be added.");
		
		loadedText.put(key, text.toString());
	}

	private enum TextDataState
	{
		NAME, TEXT;
	}

	@Override
	protected String getExtension()
	{
		return "gt";
	}

	@Override
	protected String getDataPath()
	{
		return ROOT_PATH + "data" + File.separator + "text" + File.separator;
	}
}
