package main.entity.save;

public enum SaveTokenTag
{
	A_UID,	//actor unique identifier
	A_TYP,	//actor type
	A_NAM,	//actor name
	A_GEN,	//actor gender
	A_UNQ,	//actor unique flag
	A_ICO,	//actor icon
	A_CLR,	//actor color
	A_MHP,	//actor max hp
	A_CHP,	//actor current hp
	A_AI_,	//actor ai
	A_ATT,	//actor attributes
	A_INV,	//actor stored items (pack inventory)
	A_MAT,	//actor material inventory
	A_EQP,	//actor equipment
	A_RDY,	//actor readied items
	A_MAG,	//actor magic items
	A_TRT,	//actor traits
	A_SKL,	//actor skills
	A_NWP,	//actor default damage
	A_DAR,	//actor default armor
	A_TLK,	//actor default talk response
	A_FAC,	//actor facing
	
	C_NAM,	//fieldcoord name
	C_ICO,	//fieldcoord icon
	C_CLR,	//fieldcoord color
	C_OST,	//fieldcoord obstructs sight
	C_OMV,	//fieldcoord obstructs movement
	C_MOV,	//fieldcoord movement cost
	C_BLK,	//fieldcoord blocking message
	C_VIS,	//fieldcoord visible boolean
	C_SEN,	//fieldcoord seen boolean
	
	F_UID,	//feature unique identifier
	F_TYP,	//feature type
	F_CHP,	//feature current hp 
	F_MHP,	//feature max hp
	
	T_UID,	//tile unique identifier
	T_TYP,	//tile type
	T_AHR,	//tile actor here 
	T_FHR,	//tile feature here
	T_IHR,	//tile item here
	T_OCA,	//tile obstructs coaligned
	T_OEN,	//tile obstructs enemy
	T_RIC,	//tile remembered icon
	T_RCL,	//tile remembered color
	T_FIC,	//tile fog icon
	
	I_UID,	//item unique identifier
	I_NAM,	//item name
	I_PLR,	//item plural
	I_TYP,	//item type
	I_ICO,	//item icon
	I_CLR,	//item color
	I_DAM,	//item damage string
	I_SIZ,	//item size
	I_AMT,	//item size
	I_INV,	//item inventory slot
	I_MHP,	//item max hp
	I_CHP,	//item current hp
	I_CR_,	//item cover rating
	I_AR_,	//item armor rating
	I_MAT,	//item material
	I_UPG,	//item upgraded status
	I_UPB,	//item upgraded by
	I_TRT,	//item traits
	
	Z_UID,	//zone unique identifier
	Z_TYP,	//zone type
	Z_NAM,	//zone name
	Z_HGT, 	//zone height
	Z_WID,	//zone width
	Z_DEP,	//zone depth
	Z_TRN,	//zone last turn
	Z_TIL,	//zone tiles
	Z_ACT,	//zone actors
	Z_EVT,	//zone events
	Z_ZEK,	//zone zone entry keys
	Z_PER,	//zone should persist
	Z_CEW,	//zone can enter world
	
	W_UID,	//worldtile unique identifier
	W_TYP,	//worldtile type
	W_ZID,	//worldtile zone ID
	
	O_UID,	//overworld unique identifier
	O_PCX,	//overworld player coordinate X
	O_PCY,	//overworld player coordinate Y
	O_HGT, 	//overworld height
	O_WID,	//overworld width
	O_TIL,	//overworld tiles
	
	E_UID,	//event unique identifier
	E_TYP,	//event type
	E_TIC,	//event ticks before acting
	E_QUE,	//event queue
	E_ACT,	//event actor
	E_AC2,	//event secondary actor
	E_ITM,	//event item
	E_VAL,	//event values
}
