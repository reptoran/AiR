package main.entity.requirement;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

public class Requirement extends AbstractLogicalEvaluation implements Comparable<Requirement>
{
	@JsonProperty("map")
	@JsonDeserialize(keyUsing = RequirementDeserializer.class)
	@JsonSerialize(keyUsing = RequirementSerializer.class)
	@JsonValue
	private Map<RequirementType, String> map; // map used because the keys are arbitrary, but there should only be one entry in the map

	private RequirementType type;

	@JsonCreator
	public Requirement(Map<RequirementType, String> map)
	{
		setMap(map);
	}
	
	public Requirement(RequirementType type)
	{
		map = new HashMap<RequirementType, String>();
		this.type = type;
	}

	public Requirement(RequirementType type, String details)
	{
		map = new HashMap<RequirementType, String>();
		this.type = type;
		this.details = details;
		parseJsonDetailsString();
		removeRedundantEquals();
	}

	@Override
	protected void setTypeAndDetailsFromMap()
	{
		Set<RequirementType> keySet = map.keySet();

		for (RequirementType key : keySet)
		{
			if (map.get(key) != null)
			{
				type = key;
				details = map.get(key);
			}
		}
	}

	public void setType(RequirementType type)
	{
		this.type = type;
	}

	public Map<RequirementType, String> getMap()
	{
		return map;
	}

	public void setMap(Map<RequirementType, String> map)
	{
		this.map = map;
		setTypeAndDetailsFromMap();
		parseJsonDetailsString();
	}

	public RequirementType getType()
	{
		return type;
	}

	private static class RequirementDeserializer extends KeyDeserializer
	{
		@Override
		public RequirementType deserializeKey(String key, DeserializationContext ctxt) throws IOException, JsonProcessingException
		{
			return RequirementType.fromString(key);
		}
	}

	private static class RequirementSerializer extends JsonSerializer<RequirementType>
	{
		private ObjectMapper mapper = new ObjectMapper();

		@Override
		public void serialize(RequirementType value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException
		{

			StringWriter writer = new StringWriter();
			mapper.writeValue(writer, value.name());
			gen.writeFieldName(writer.toString());
		}
	}

	@Override
	public Requirement clone()
	{
		Set<RequirementType> keys = map.keySet();
		Map<RequirementType, String> cloneMap = new HashMap<RequirementType, String>();

		for (RequirementType key : keys)
			cloneMap.put(key, map.get(key));

		Requirement requirements = new Requirement(cloneMap);

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
		Requirement other = (Requirement) obj;
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
	public int compareTo(Requirement req)
	{
		return Integer.compare(hashCode(), req.hashCode());
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
