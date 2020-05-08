package main.entity.event;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TriggerType
{
	CHANGE_HP,				//"CHANGE_HP":"10000"								adds the quantity (does not set); assumes change is to player (and distinct from the environment event type HP_CHANGE for that reason)
	CHANGE_HP_OF_ACTOR,		//"CHANGE_HP_OF_ACTOR":"PLAYER=1"					adds the quantity (does not set); note that here, "PLAYER" is the value, not the modifier
	GIVE_ITEM_TO,			//"GIVE_ITEM_TO":"MEDIC:MEDICINAL_FUNGUS=2"			assumes player is giving
	GET_ITEM_FROM,			//"GET_ITEM_FROM":"MEDIC:HEALING_SALVE=1"			assumes player is receiving
	CONSUME_ITEM;			//"CONSUME_ITEM":"PLAYER:MEDICINAL_FUNGUS=1"		makes an item (or stack of items) owned by the actor to disappear (rather than dealing damage to those items)
	
	private static Map<String, TriggerType> FORMAT_MAP = Stream
    .of(TriggerType.values())
    .collect(Collectors.toMap(s -> s.name(), Function.identity()));
	
	@JsonCreator
    public static TriggerType fromString(String string) {
        if (string == null)
        	return null;
		
		return Optional
            .ofNullable(FORMAT_MAP.get(string))
            .orElseThrow(() -> new IllegalArgumentException(string));
    }
}
