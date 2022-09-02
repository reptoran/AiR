package main.entity.chat;

import main.data.file.JsonFileUtils;

public class ChatJsonFileUtils extends JsonFileUtils<Chat>
{
	private static ChatJsonFileUtils instance = null;

	private ChatJsonFileUtils() {}

	public static ChatJsonFileUtils getInstance()
	{
		if (instance == null)
			instance = new ChatJsonFileUtils();

		return instance;
	}
}
