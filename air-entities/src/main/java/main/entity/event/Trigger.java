package main.entity.event;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import main.entity.AbstractLogicalEvaluation;
import main.entity.chat.CompareOperator;

public class Trigger extends AbstractLogicalEvaluation implements Comparable<Trigger>
{
	@JsonProperty("map")
	@JsonDeserialize(keyUsing = TriggerDeserializer.class)
	@JsonSerialize(keyUsing = TriggerSerializer.class)
	@JsonValue
	private Map<TriggerType, String> map;	//map used because the keys are arbitrary, but there should only be one entry in the map
	
	private TriggerType type;

	@JsonCreator
	public Trigger(Map<TriggerType, String> map)
	{
		setMap(map);
	}
	
	public Trigger(TriggerType type)
	{
		map = new HashMap<TriggerType, String>();
		this.type = type;
	}
	
	public Trigger(TriggerType type, String details)
	{
		map = new HashMap<TriggerType, String>();
		this.type = type;
		this.details = details;
		parseJsonDetailsString();
		removeRedundantEquals();
	}
	
	@Override
	public void setDetails(String modifier, String value, CompareOperator operator, String comparison)
	{
		super.setDetails(modifier, value, operator, comparison);
		removeRedundantEquals();
	}

	private void removeRedundantEquals()
	{
		if (StringUtils.isEmpty(details) || details.charAt(0) != '=')
			return;
		
		details = details.substring(1);
		operator = null;
		putDetailsInMap();
	}

	@Override
	protected void setTypeAndDetailsFromMap()
	{
		Set<TriggerType> keySet = map.keySet();

		for (TriggerType key : keySet)
		{
			if (map.get(key) != null)
			{
				type = key;
				details = map.get(key);
			}
		}
		
		//this is so that equals operators are not required for values with no comparators
		if (!detailsContainsOperator())
		{
			details = "=" + details;
			map.put(type, details);
		}
	}
	
	private boolean detailsContainsOperator()
	{
		for (CompareOperator co : CompareOperator.values())
		{
			int index = details.indexOf(co.getSymbol());
			if (index != -1)
				return true;
		}
		
		return false;
	}

	public void setType(TriggerType type)
	{
		this.type = type;
	}

	public Map<TriggerType, String> getMap()
	{
		return map;
	}

	public void setMap(Map<TriggerType, String> map)
	{
		this.map = map;
		setTypeAndDetailsFromMap();
		parseJsonDetailsString();
	}
	
	public TriggerType getType()
	{
		return type;
	}

	private static class TriggerDeserializer extends KeyDeserializer
	{
		@Override
		public TriggerType deserializeKey(String key, DeserializationContext ctxt) throws IOException, JsonProcessingException
		{
			return TriggerType.fromString(key);
		}
	}
	
	private static class TriggerSerializer extends JsonSerializer<TriggerType>
	{
		private ObjectMapper mapper = new ObjectMapper();

		@Override
		public void serialize(TriggerType value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException
		{

			StringWriter writer = new StringWriter();
			mapper.writeValue(writer, value.name());
			gen.writeFieldName(writer.toString());
		}
	}
	
	@Override
	public Trigger clone()
	{
		Set<TriggerType> keys = map.keySet();
		Map<TriggerType, String> cloneMap = new HashMap<TriggerType, String>();
		
		for (TriggerType key : keys)
			cloneMap.put(key, map.get(key));
		
		Trigger requirements = new Trigger(cloneMap);
		
		return requirements;
	}

	//value, operator, comparison are all "helper" fields encapsulated by details, so as long as details is being checked, everything should be fine
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((details == null) ? 0 : details.hashCode());
		result = prime * result + ((map == null) ? 0 : map.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Trigger other = (Trigger) obj;
		if (details == null)
		{
			if (other.details != null)
				return false;
		} else if (!details.equals(other.details))
			return false;
		if (map == null)
		{
			if (other.map != null)
				return false;
		} else if (!map.equals(other.map))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public int compareTo(Trigger trigger)
	{
		return Integer.compare(hashCode(), trigger.hashCode());
	}

	@Override
	protected void putDetailsInMap()
	{
		if (type != null)
			map.put(type, details);
	}

	@Override
	protected String getTypeName()
	{
		return type.name();
	}
}
