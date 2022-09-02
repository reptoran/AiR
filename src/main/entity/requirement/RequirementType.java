package main.entity.requirement;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum RequirementType
{
	ACTOR_TYPE,
	HP_PERCENT,					//"HP_PERCENT":"<100"
	ACTOR_HAS_ITEM,				//"ACTOR_HAS_ITEM":"PLAYER:MEDICINAL_FUNGUS>1"
	PLAYER_ENTERS_ZONE,			//"PLAYER_ENTERS_ZONE":"TH_BASEMENT>=1"
	QUEST_NODE_ACTIVE,			//"QUEST_NODE_ACTIVE":"FUNGUS:START"
	QUEST_NOT_STARTED;			//"QUEST_NOT_STARTED":"FUNGUS"
	
	private static Map<String, RequirementType> FORMAT_MAP = Stream
    .of(RequirementType.values())
    .collect(Collectors.toMap(s -> s.name(), Function.identity()));
	
	@JsonCreator
    public static RequirementType fromString(String string) {
        if (string == null)
        	return null;
		
		return Optional
            .ofNullable(FORMAT_MAP.get(string))
            .orElseThrow(() -> new IllegalArgumentException(string));
    }
}
