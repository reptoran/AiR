package main.data.event.environment;

import java.text.ParseException;

import main.data.event.InternalEvent;
import main.entity.EntityType;
import main.entity.save.SaveStringBuilder;
import main.entity.save.SaveToken;
import main.entity.save.SaveTokenTag;

public class SaveableEnvironmentEvent extends AbstractEnvironmentEvent
{
	private int queueHash = -1;

	@Override
	public InternalEvent trigger()
	{
		throw new UnsupportedOperationException("A SaveableEnvironmentEvent object should never be triggered.");
	}

	@Override
	public EnvironmentEventType getType()
	{
		return type;
	}
	
	@Override
	public String loadFromText(String text) throws ParseException
	{
		SaveStringBuilder ssb = new SaveStringBuilder(EntityType.EVENT, text);
		setMember(ssb, SaveTokenTag.E_QUE);
		
		return super.loadFromText(text);
	}
	
	@Override
	protected void setMember(SaveStringBuilder ssb, SaveTokenTag saveTokenTag)
	{
		if (saveTokenTag == SaveTokenTag.E_QUE)
		{
			SaveToken saveToken = ssb.getToken(saveTokenTag);
			queueHash = Integer.parseInt(saveToken.getContents());
			return;
		}
		
		super.setMember(ssb, saveTokenTag);
	}
	
	public int getQueueHash()
	{
		return queueHash;
	}
}
