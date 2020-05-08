package main.data.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.data.Data;
import main.entity.actor.ActorType;
import main.entity.chat.Chat;
import main.entity.chat.ChatReq;
import main.entity.chat.ChatReqType;
import main.entity.chat.ChatResponse;
import main.entity.chat.CompareOperator;
import main.presentation.Logger;

public class ChatManager
{
	private Map<ActorType, List<Chat>> chats = new HashMap<ActorType, List<Chat>>();
	private Map<String, Chat> chatsByTag = new HashMap<String, Chat>();
	private Map<String, ActorType> chatOwners = new HashMap<String, ActorType>();
	
	private Data data = null;
	
	private static ChatManager instance = null;
	
	private ChatManager() {}
	
	public static ChatManager getInstance()
	{
		if (instance == null)
			instance = new ChatManager();
		
		return instance;
	}
	
	public void setData(Data data)
	{
		this.data = data;
	}
	
	public void populateChats(Map<ActorType, List<Chat>> originalTalkMap)
	{
		for (ActorType actorType : ActorType.values())
		{
			List<Chat> originalTalks = originalTalkMap.get(actorType);
			
			if (originalTalks == null)
				continue;
			
			List<Chat> newTalks = new ArrayList<Chat>();
			
			for (Chat origTalk : originalTalks)
			{
				Chat newTalk = origTalk.clone();		//just in case I make it so conversation elements can be modified (like a one-time flag or something)
				newTalks.add(newTalk);
				chatsByTag.put(newTalk.getTag(), newTalk);
				chatOwners.put(newTalk.getTag(), actorType);
			}
			chats.put(actorType, newTalks);
		}
	}

	public void removeChat(Chat chatToRemove)
	{
		if (chatToRemove == null)
			return;
		
		String chatTag = chatToRemove.getTag();
		ActorType actor = chatOwners.get(chatToRemove.getTag());
		
		if (actor == null)
		{
			Logger.warn("Chat " + chatTag + " is not associated with an actor; cannot remove.");
			return;
		}
		
		chatsByTag.remove(chatTag);
		chatOwners.remove(chatToRemove.getTag());
		chats.get(actor).remove(chatToRemove);
		
		if (chats.get(actor).isEmpty())
			chats.remove(actor);
	}

	public void addNewChat(Chat newChat, ActorType actor)
	{
//		Chat newChat = chat.clone();
		
		chatsByTag.put(newChat.getTag(), newChat);
		chatOwners.put(newChat.getTag(), actor);
		
		List<Chat> chatsForActor = chats.get(actor);
		if (chatsForActor == null)
		{
			chatsForActor = new ArrayList<Chat>();
			chatsForActor.add(newChat);
			chats.put(actor, chatsForActor);
		}
		else
		{
			chatsForActor.add(newChat);
		}
	}
	
	public void updateChat(Chat newChat)
	{
//		Chat oldChat = chatsByTag.get(newChat.getTag());
//		ActorType chatOwner = chatOwners.get(oldChat);
//		chatsByTag.put(newChat.getTag(), newChat);
//		chatOwners.remove(oldChat);
//		chatOwners.put(newChat, chatOwner);
//		chats.get(chatOwner).remove(oldChat);
		
		Chat oldChat = chatsByTag.get(newChat.getTag());
		
		if (oldChat == null)
		{
			Logger.warn("Chat " + newChat.getTag() + " isn't in ChatManager; use addNewChat instead to include it.");
			return;
		}
		
		ActorType chatOwner = chatOwners.get(oldChat.getTag());
		removeChat(oldChat);
		addNewChat(newChat, chatOwner);
	}
	
	public Chat filterChatResponsesByRequirement(Chat chat)
	{
		if (chat == null)
			return null;
		
		Chat chatWithResponseReqs = chat.clone();
		chatWithResponseReqs.getResponse().clear();
		
		for (ChatResponse response : chat.getResponse())
		{
			if (requirementsMet(response.getReqs()))
				chatWithResponseReqs.getResponse().add(response);
		}
		
		return chatWithResponseReqs;
	}
	
	public boolean actorHasChats(ActorType actor)
	{
		List<Chat> actorChats = chats.get(actor);
		
		if (actorChats == null)
			return false;
		
		if (actorChats.isEmpty())
			return false;
		
		return true;
	}
	
	public List<Chat> getAllChatsForActor(ActorType actor)
	{
		return chats.get(actor);
	}
	
	public Chat getTalkByTag(String tag)
	{
		return filterChatResponsesByRequirement(chatsByTag.get(tag));
	}
	
	public boolean hasInitialChat(ActorType actorType)
	{
		Chat chat = getInitialTalkForActor(actorType);
		if (chat == null)
			return false;
		
		return true;
	}
	
	public Chat getInitialTalkForActor(ActorType actorType)
	{
		List<Chat> potentialInitialChats = chats.get(actorType);
		List<Chat> validInitialChats = new ArrayList<Chat>();
		
		if (potentialInitialChats == null)
			return null;
		
		for (Chat chat : potentialInitialChats)
		{
			if (chat.getEntry() && requirementsMet(chat.getReqs()))
				validInitialChats.add(chat);
		}
		
		if (validInitialChats.isEmpty())
			return null;
		
		return filterChatResponsesByRequirement(validInitialChats.get(0));
	}
	
	private boolean requirementsMet(List<ChatReq> requirements)
	{	
		for (ChatReq requirement : requirements)
		{
			try
			{
				validateRequirement(requirement);
			} catch (ValidationException e)
			{
				return false;
			}	
		}
		
		return true;
	}
	
	private void validateRequirement(ChatReq requirement) throws ValidationException
	{
		ChatReqType reqType = requirement.getType();
		CompareOperator operator = requirement.getOperator();
		String modifier = requirement.getModifier();
		String value = requirement.getValue();
		String requiredValue = requirement.getComparison();
		
		String valueToCheck = "";
		
		switch (reqType)
		{
		case ACTOR_TYPE:
			valueToCheck = data.getPlayer().getType().name();
			break;
		case HP_PERCENT:
			valueToCheck = String.valueOf(data.getPlayer().getHpPercent());
			break;
		case ACTOR_HAS_ITEM:
			valueToCheck = RequirementValidator.getInstance().getValueToCheckForActorHasItem(modifier, value);
			break;
		default:
			return;
		}
		
		if (!RequirementValidator.getInstance().checkRequirement(operator, requiredValue, valueToCheck))
			throw new ValidationException();
	}

	@SuppressWarnings("serial")
	private class ValidationException extends Exception {}
}
