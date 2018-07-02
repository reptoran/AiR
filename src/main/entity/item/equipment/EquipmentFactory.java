package main.entity.item.equipment;

public class EquipmentFactory
{
	private EquipmentFactory() {}
	
	public static Equipment generateEquipment(EquipmentType equipmentType)
	{
		switch (equipmentType)
		{
		case NONE:
			return new EmptyEquipmentImpl();
		case BASIC:
			return new BasicEquipmentImpl();
		default:
			throw new IllegalArgumentException("No equipment definition for equipment type: " + equipmentType);
		}
	}
}
