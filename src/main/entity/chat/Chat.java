package main.entity.chat;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import main.entity.event.Trigger;
import main.entity.requirement.Requirement;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Chat implements Comparable<Chat>
{
	private String tag;
	private String text;
	private boolean entry;
	
	//Note that while requirements and triggers are both defined here, and requirements are technically checked in the ChatManager, both of
	//those should be managed by responses, since it's an instant flow anyway, so including them in both is redundant. 
	
	@JsonProperty("reqs")
	private List<Requirement> reqs;
	
	@JsonProperty("triggers")
	private List<Trigger> triggers;
	
	@JsonProperty("response")
	private List<ChatResponse> response;	

	public Chat()
	{
		tag = null;
		text = null;
		entry = false;
		reqs = new ArrayList<Requirement>();
		triggers = new ArrayList<Trigger>();
		response = new ArrayList<ChatResponse>();
	}
	
	public String getTag()
	{
		return tag;
	}

	public void setTag(String tag)
	{
		this.tag = tag;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}
	
	public boolean getEntry()
	{
		return entry;
	}
	
	public void setEntry(boolean entry)
	{
		this.entry = entry;
	}

	public List<Requirement> getReqs()
	{
		return reqs;
	}

	public void setReqs(List<Requirement> reqs)
	{
		if (reqs == null)
			this.reqs = new ArrayList<Requirement>();
		else
			this.reqs = reqs;
	}

	public List<Trigger> getTriggers()
	{
		return triggers;
	}

	public void setTriggers(List<Trigger> triggers)
	{
		if (triggers == null)
			this.triggers = new ArrayList<Trigger>();
		else
			this.triggers = triggers;
	}

	public List<ChatResponse> getResponse()
	{
		return response;
	}

	public void setResponse(List<ChatResponse> response)
	{
		if (reqs == null)
			this.response = new ArrayList<ChatResponse>();
		else
			this.response = response;
	}

	@Override
	public Chat clone()
	{
		Chat chat = new Chat();
		
		chat.tag = tag;
		chat.text = text;
		chat.entry = entry;

		for (Requirement req : reqs)
			chat.reqs.add(req.clone());
		
		for (Trigger trigger : triggers)
			chat.triggers.add(trigger.clone());
		
		for (ChatResponse resp : response)
			chat.response.add(resp.clone());
		
		return chat;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (entry ? 1231 : 1237);
		result = prime * result + ((reqs == null) ? 0 : reqs.hashCode());
		result = prime * result + ((response == null) ? 0 : response.hashCode());
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + ((triggers == null) ? 0 : triggers.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Chat other = (Chat) obj;
		
		//Right now, only compare on tag, since that should be immutable
		if (!StringUtils.equals(tag, other.tag))
			return false;
		
		return true;
	}

	@Override
	public int compareTo(Chat chat)
	{
		return this.getTag().compareTo(chat.getTag());
	}
	
	@Override
	public String toString()
	{
		return tag;
	}
}
