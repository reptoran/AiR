package main.entity.chat;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ChatReqType
{
	ACTOR_TYPE, HP_PERCENT, ACTOR_HAS_ITEM;
	
	private static Map<String, ChatReqType> FORMAT_MAP = Stream
    .of(ChatReqType.values())
    .collect(Collectors.toMap(s -> s.name(), Function.identity()));
	
	@JsonCreator
    public static ChatReqType fromString(String string) {
        if (string == null)
        	return null;
		
		return Optional
            .ofNullable(FORMAT_MAP.get(string))
            .orElseThrow(() -> new IllegalArgumentException(string));
    }
}
