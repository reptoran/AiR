package main.entity.save;

public enum SaveTokenTag
{
	A_UID,	//actor unique identifier
	A_TYP,	//actor type
	A_NAM,	//actor name
	A_ICO,	//actor icon
	A_CLR,	//actor color
	A_MHP,	//actor max hp
	A_CHP,	//actor current hp
	A_SPD,	//actor speed counter
	A_AI_,	//actor ai
	A_ATT,	//actor attributes
	
	C_NAM,	//fieldcoord name
	C_ICO,	//fieldcoord icon
	C_CLR,	//fieldcoord color
	C_OST,	//fieldcoord obstructs sight
	C_OMV,	//fieldcoord obstructs movement
	C_MOV,	//fieldcoord movement cost
	C_BLK,	//fieldcoord blocking message
	
	F_UID,	//feature unique identifier
	F_TYP,	//feature type
	F_CHP,	//feature current hp 
	F_MHP,	//feature max hp
	
	T_UID,	//tile unique identifier
	T_TYP,	//tile type
	T_AHR,	//tile actor here 
	T_FHR,	//tile feature here
	
	Z_UID,	//zone unique identifier
	Z_TYP,	//zone type
	Z_NAM,	//zone name
	Z_HGT, 	//zone height
	Z_WID,	//zone width
	Z_DEP,	//zone depth
	Z_TRN,	//zone last turn
	Z_TIL,	//zone tiles
	Z_ACT,	//zone actors
	Z_ZEK,	//zone zone entry keys
	Z_PER,	//zone should persist
	Z_CEW,	//zone can enter world
	
	W_UID,	//worldtile unique identifier
	W_TYP,	//worldtile type
	
	O_UID,	//overworld unique identifier
	O_PCX,	//overworld player coordinate X
	O_PCY,	//overworld player coordinate Y
	O_HGT, 	//overworld height
	O_WID,	//overworld width
	O_TIL	//overworld tiles
}
