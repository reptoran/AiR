package main.entity.quest;

import main.data.file.JsonFileUtils;

public class QuestJsonFileUtils extends JsonFileUtils<Quest>
{
	private static QuestJsonFileUtils instance = null;

	private QuestJsonFileUtils() {}

	public static QuestJsonFileUtils getInstance()
	{
		if (instance == null)
			instance = new QuestJsonFileUtils();

		return instance;
	}
}
