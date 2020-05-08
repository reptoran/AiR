package main.entity.talk;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import main.entity.chat.Chat;
import main.entity.chat.ChatJsonFileUtils;

public class ChatTests
{
	private File elderFile;
	private File testOutput;
	
	private static final Logger LOGGER = LogManager.getLogger(ChatTests.class);
	
	@Before
	public void setup()
	{
		elderFile = new File("H:\\My Projects\\Programming\\AiR\\data\\talk\\pc_physician.tlk");
		testOutput = new File("H:\\My Projects\\Programming\\AiR\\data\\talk\\test_output.tlk");
	}
	
	@Test
	public void loggingTest()
	{
		LOGGER.debug("This will be printed on debug");
		LOGGER.info("This will be printed on info");
        LOGGER.warn("This will be printed on warn");
        LOGGER.error("This will be printed on error");
        LOGGER.fatal("This will be printed on fatal");

        LOGGER.info("Appending string: {}.", "Hello, World");
	}
	
	@Test
	public void testDeserialize()
	{
		List<Chat> chats = ChatJsonFileUtils.loadChatFromDataFile(elderFile);
	}
	
	@Test
	public void testSerialize()
	{
		List<Chat> chats = ChatJsonFileUtils.loadChatFromDataFile(elderFile);
//		ChatJsonFileUtils.saveChatListToFile(chats, testOutput);
		//above is commented out so as to not output test files with every build
	}
	
	@Test
	public void testChatCloneAndEquals()
	{
		Chat chat = new Chat();
		chat.setEntry(true);
		chat.setTag("this is a tag");
		chat.setText("text chat");
		//TODO: should set arrays too, just to be thorough
		Chat clonedChat = chat.clone();
		assertTrue(clonedChat.equals(chat));
	}
}
