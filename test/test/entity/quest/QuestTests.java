package test.entity.quest;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import main.data.file.QuestLoader;
import main.entity.quest.Quest;
import main.entity.quest.QuestJsonFileUtils;

public class QuestTests
{
	private File testOutput;
	
	@Before
	public void setup()
	{
		testOutput = new File("H:\\My Projects\\Programming\\AiR\\data\\quests\\fungus_finder.qst");
	}
	
	@Test
	public void testSerialize()
	{
		List<Quest> quests = QuestLoader.getInstance().defineQuests();
		QuestJsonFileUtils.getInstance().saveToFile(quests, testOutput);
		//above is commented out so as to not output test files with every build
	}
}
