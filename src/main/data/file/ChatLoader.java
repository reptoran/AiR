package main.data.file;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.entity.actor.ActorType;
import main.entity.chat.Chat;
import main.entity.chat.ChatJsonFileUtils;

public class ChatLoader extends FileHandler
{
	private static ChatLoader instance = null;
	
	private ChatLoader() {}
	
	public static ChatLoader getInstance()
	{
		if (instance == null)
			instance = new ChatLoader();
		
		return instance;
	}
	
	public Map<ActorType, List<Chat>> loadAllChats()
	{
		Map<ActorType, List<Chat>> loadedChats = new HashMap<ActorType, List<Chat>>();
		
		//TODO: unzip a .dat file that's just an archive of everything, then search the created folder
		
		File folder = new File(getDataPath());

		for (File file : folder.listFiles())
		{
			if (!getFileExtension(file).equals(getExtension()))
				continue;
			
			List<Chat> elements = ChatJsonFileUtils.getInstance().loadFromFile(file);
			String key = getFileNameNoExtension(file);
			loadedChats.put(ActorType.fromString(key), elements);
		}
		
		return loadedChats;
	}
	
	public boolean saveChatForActor(ActorType actor, List<Chat> chats)
	{
		if (actor == null)
			return false;
		
		String fileName = actor.name().toLowerCase() + "." + getExtension();
		File file = new File(getDataPath() + File.separator + fileName);
		
		if (file.exists())
			file.delete();
		
		if (chats == null)
			return false;
		if (chats.isEmpty())
			return false;
		
		return ChatJsonFileUtils.getInstance().saveToFile(chats, file);
	}
	
	@Override
	protected String getExtension()
	{
		return "tlk";
	}

	@Override
	protected String getDataPath()
	{
		return ROOT_PATH + "data" + File.separator + "talk" + File.separator;
	}
}
