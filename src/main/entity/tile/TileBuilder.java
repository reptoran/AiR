package main.entity.tile;

import main.entity.FieldCoordBuilder;

public class TileBuilder extends FieldCoordBuilder<Tile>
{
	private TileBuilder()
	{
		super(new Tile());
	}
	
	public static TileBuilder generateTile(TileType tileType)
	{
		return new TileBuilder().setType(tileType);
	}
	
	private TileBuilder setType(TileType type)
	{
		getFieldCoord().setType(type);
		return this;
	}
	
	public TileBuilder setObstructsCoaligned(boolean obstructsCoaligned)
	{
		getFieldCoord().setObstructsCoaligned(obstructsCoaligned);
		return this;
	}
}
