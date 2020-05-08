package main.entity.item.equipment;

public enum EquipmentSlotType
{
	NONE(0),
	ANY(1),
	ARMOR(10),
	ARMAMENT(4),
	MATERIAL(0),
	MAGIC(1);
	
	private int bulk;
	
	private EquipmentSlotType(int bulk)
	{
		this.bulk = bulk;
	}
	
	public int getBulk()
	{
		return bulk;
	}
}
