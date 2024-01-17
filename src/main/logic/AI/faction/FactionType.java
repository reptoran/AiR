package main.logic.AI.faction;

public enum FactionType
{
	COALIGNED,	//generally good; actors that won't attack you, that you can often chat with
	WILD,		//animals who will attack any non-animals if they're not too strong
	EVIL,		//intelligent beings who are aggressive to intruders
	SHADOW,		//spawned from shadow energy; only interested in defeating anyone of the COALIGNED faction
	UNALIGNED,	//ancients, elementals, and other entities aggressive but removed from the morality of the world
	NONE		//anything that doesn't exhibit intentional aggression or bias
}
