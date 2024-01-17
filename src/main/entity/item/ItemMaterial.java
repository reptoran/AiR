package main.entity.item;

public enum ItemMaterial
{
	NATURAL(0),		//wood/cloth/leather
	METAL(1),
	CRYSTAL(2),
	VIRTUAL(1000);
	
	private int materialDR;
	
	private ItemMaterial(int DR)
	{
		materialDR = DR;
	}
	
	public int getDR()
	{
		return materialDR;
	}
}
