package main.entity.quest;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum QuestNodeStatus
{
	INACTIVE, ACTIVE, COMPLETE;
	
	private static Map<String, QuestNodeStatus> FORMAT_MAP = Stream
    .of(QuestNodeStatus.values())
    .collect(Collectors.toMap(s -> s.name(), Function.identity()));
	
	@JsonCreator
    public static QuestNodeStatus fromString(String string) {
        if (string == null)
        	return null;
		
		return Optional
            .ofNullable(FORMAT_MAP.get(string))
            .orElseThrow(() -> new IllegalArgumentException(string));
    }
}