package main.entity.chat;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ChatJsonFileUtils
{
	private static final Logger LOGGER = LogManager.getLogger(ChatJsonFileUtils.class);
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	public static List<Chat> loadChatFromDataFile(File talkFile)
	{
		List<Chat> chats = null;
		
		try
		{
			chats = objectMapper.readValue(talkFile, new TypeReference<List<Chat>>(){});
		} catch (JsonParseException e)
		{
			LOGGER.error("JsonParseException thrown while reading file " + talkFile.getName() + "; exception was " + e.getMessage());
		} catch (JsonMappingException e)
		{
			LOGGER.error("JsonMappingException thrown while reading file " + talkFile.getName() + "; exception was " + e.getMessage());
		} catch (IOException e)
		{
			LOGGER.error("IOException thrown while reading file " + talkFile.getName() + "; exception was " + e.getMessage());
		}
		
		return chats;
	}
	
	public static boolean saveChatListToFile(List<Chat> chats, File talkFile)
	{
		try
		{
			objectMapper.writeValue(talkFile, chats);
		} catch (JsonGenerationException e)
		{
			LOGGER.error("JsonGenerationException thrown while writing file " + talkFile.getName() + "; exception was " + e.getMessage());
			return false;
		} catch (JsonMappingException e)
		{
			LOGGER.error("JsonMappingException thrown while writing file " + talkFile.getName() + "; exception was " + e.getMessage());
			return false;
		} catch (IOException e)
		{
			LOGGER.error("IOException thrown while writing file " + talkFile.getName() + "; exception was " + e.getMessage());
			return false;
		}
		
		return true;
	}
}
