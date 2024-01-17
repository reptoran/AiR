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
	CHANGE_HP_OF_ACTOR,		//"CHANGE_HP_OF_ACTOR":"PLAYER:1"					adds the quantity (does not set); note that here, "PLAYER" is the modifier, "1" is the value
	CHANGE_ACTOR_TYPE,		//"CHANGE_ACTOR_TYPE":"RAT":"GIANT_RAT"				invokes Actor.convertToType(), replacing the original actor with a completely new one (full HP and all) based on the new type
	GIVE_ITEM_TO,			//"GIVE_ITEM_TO":"MEDIC:MEDICINAL_FUNGUS=2"			assumes player is giving
	GET_ITEM_FROM,			//"GET_ITEM_FROM":"MEDIC:HEALING_SALVE=1"			assumes player is receiving
	CONSUME_ITEM,			//"CONSUME_ITEM":"PLAYER:MEDICINAL_FUNGUS=1"		makes an item (or stack of items) owned by the actor to disappear (rather than dealing damage to those items)
	ZONE_TRANSITION,		//"ZONE_TRANSITION":"PLAYER:DOWN"					moves a player to the staircase of the level below or above
	UPGRADE_ITEM,			//"UPGRADE_ITEM":"PLAYER:KNIFE"						upgrades an item owned by a player; at present only used internally
	ACTIVATE_QUEST,			//"ACTIVATE_QUEST":"FUNGUS"							activates the specified quest for requirements checking/node completion (this is done by completing the startNode of that quest)
	DISCOVER_QUEST,			//"DISCOVER_QUEST":"FUNGUS"							makes the specified quest visible
	COMPLETE_QUEST,			//"COMPLETE_QUEST":"FUNGUS"							completes the specified quest (triggered by a cleanup node at the very end of the quest line)
	SET_QUEST_NODE_STATUS,	//"SET_QUEST_NODE_STATUS":"FUNGUS:STEP1=COMPLETE"	sets the status of a quest node (defined by a quest tag and a node tag) to the given value
	SET_ZONE_TILE,			//"SET_ZONE_TILE":"TOWN:6,32=STAIRS_DOWN"			sets the tile at specified coordinates in a zone to the specified tile
	ADD_ZONE_KEY;			//"ADD_ZONE_KEY":"TOWN:6,32=TH_BASEMENT"			for now, creates a zone key only to a special level, with a zoneType of PERMANENT, a level of 0, and null target coordinates
	
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
