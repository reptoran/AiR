package main.entity.chat;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import main.entity.event.Trigger;
import main.entity.requirement.Requirement;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ChatResponse implements Comparable<ChatResponse>
{
	private String text;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String flowTo;

	@JsonProperty("reqs")
	private List<Requirement> reqs;
	
	@JsonProperty("triggers")
	private List<Trigger> triggers;

	public ChatResponse()
	{
		text = null;
		flowTo = null;
		reqs = new ArrayList<Requirement>();
		triggers = new ArrayList<Trigger>();
	}
	
	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public String getFlowTo()
	{
		return flowTo;
	}

	public void setFlowTo(String flowTo)
	{
		this.flowTo = flowTo;
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
	
	@Override
	public ChatResponse clone()
	{
		ChatResponse response = new ChatResponse();
		
		response.text = text;
		response.flowTo = flowTo;
		
		for (Requirement req : reqs)
			response.reqs.add(req.clone());
		
		for (Trigger trigger : triggers)
			response.triggers.add(trigger.clone());
		
		return response;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((flowTo == null) ? 0 : flowTo.hashCode());
		result = prime * result + ((reqs == null) ? 0 : reqs.hashCode());
		result = prime * result + ((triggers == null) ? 0 : triggers.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}

	//consider doing null-safe collections checks (empty trigger list and null trigger list should still be equal, for example)
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChatResponse other = (ChatResponse) obj;
		if (flowTo == null)
		{
			if (other.flowTo != null)
				return false;
		} else if (!flowTo.equals(other.flowTo))
			return false;
		if (reqs == null)
		{
			if (other.reqs != null)
				return false;
		} else if (!reqs.equals(other.reqs))
			return false;
		if (triggers == null)
		{
			if (other.triggers != null)
				return false;
		} else if (!triggers.equals(other.triggers))
			return false;
		if (StringUtils.isEmpty(text))
		{
			if (!StringUtils.isEmpty(other.text))
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}

	@Override
	public int compareTo(ChatResponse response)
	{
		return this.getText().compareTo(response.getText());
	}
}
