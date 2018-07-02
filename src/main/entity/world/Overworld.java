package main.entity.world;

import java.awt.Point;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import main.entity.EntityType;
import main.entity.SaveableEntity;
import main.entity.save.EntityMap;
import main.entity.save.SaveStringBuilder;
import main.entity.save.SaveToken;
import main.entity.save.SaveTokenTag;
import main.entity.zone.ZoneType;
import main.logic.RPGlib;

public class Overworld extends SaveableEntity
{
	public static final int OVERWORLD_HEIGHT = 20;
	public static final int OVERWORLD_WIDTH = 40;
	
	private int height;
	private int width;

	private WorldTile[][] tiles;
	private Point playerCoords;
	
	public Overworld()
	{
		this.height = OVERWORLD_HEIGHT;
		this.width = OVERWORLD_WIDTH;
		
		tiles = new WorldTile[height][width];
		
		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				if (i == 0 || j == 0 || i == height - 1 || j == width - 1)
				{
					tiles[i][j] = WorldTileFactory.generateNewTile(ZoneType.OCEAN);
				}
				else
				{
					ZoneType tileType = ZoneType.PLAINS;
					
					int type = RPGlib.randInt(1, 3);
					
					if (type == 2)
						tileType = ZoneType.DESERT;
					if (type == 3)
						tileType = ZoneType.FOREST;
					
					tiles[i][j] = WorldTileFactory.generateNewTile(tileType);
				}
			}
		}
		
		tiles[2][2] = WorldTileFactory.generateNewTile(ZoneType.LABYRINTH);
		
		playerCoords = new Point(2, 2);
	}
	
	public WorldTile getTile(int row, int column)
	{
		if (row < 0 || row >= height || column < 0 || column >= width)
		{
			return null;
		}
		
		return tiles[row][column];
	}
	
	public WorldTile getTile(Point coords)
	{
		return getTile(coords.x, coords.y);
	}

	public int getHeight()
	{
		return height;
	}

	public int getWidth()
	{
		return width;
	}
	
	public Point getPlayerCoords()
	{
		return new Point(playerCoords.x, playerCoords.y);
	}

	public void setPlayerCoords(Point newCoords)
	{
		this.playerCoords = new Point(newCoords.x, newCoords.y);
	}
	
	@Override
	public String saveAsText()
	{
		SaveStringBuilder ssb = new SaveStringBuilder(EntityType.OVERWORLD);
		List<String> worldTileList = new ArrayList<String>();
		
		String overworldUid = getUniqueId();
		
		if (EntityMap.getOverworld(overworldUid) == null)
			overworldUid = EntityMap.put(overworldUid, this);
		else
			overworldUid = EntityMap.getSimpleKey(overworldUid);
		
		//will be saved with every overworld
		ssb.addToken(new SaveToken(SaveTokenTag.O_UID, overworldUid));
		
		if (playerCoords != null)
		{
			ssb.addToken(new SaveToken(SaveTokenTag.O_PCX, String.valueOf(playerCoords.x)));
			ssb.addToken(new SaveToken(SaveTokenTag.O_PCY, String.valueOf(playerCoords.y)));
		}
		
		ssb.addToken(new SaveToken(SaveTokenTag.O_HGT, String.valueOf(height)));
		ssb.addToken(new SaveToken(SaveTokenTag.O_WID, String.valueOf(width)));

		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				String tileUid = tiles[i][j].getUniqueId();
				
				if (EntityMap.getWorldTile(tileUid) == null)
					tileUid = EntityMap.put(tileUid, tiles[i][j]);
				else
					tileUid = EntityMap.getSimpleKey(tileUid);
				
				worldTileList.add(tileUid.substring(1));
			}
		}
		
		ssb.addToken(new SaveToken(SaveTokenTag.O_TIL, worldTileList));
		
		return ssb.getSaveString();
	}

	@Override
	public String loadFromText(String text) throws ParseException
	{
		SaveStringBuilder ssb = new SaveStringBuilder(EntityType.OVERWORLD, text);
		
		String toRet = getContentsForTag(ssb, SaveTokenTag.O_UID);	//assumed to be defined
		

		setMember(ssb, SaveTokenTag.O_PCX);
		setMember(ssb, SaveTokenTag.O_PCY);
		setMember(ssb, SaveTokenTag.O_HGT);
		setMember(ssb, SaveTokenTag.O_WID);
		setMember(ssb, SaveTokenTag.O_TIL);
		
		return toRet;
	}

	@Override
	public String getUniqueId()
	{
		return "OVERWORLD";	//TODO: update this if there's ever more than one overworld
	}

	@Override
	protected void setMember(SaveStringBuilder ssb, SaveTokenTag saveTokenTag)
	{
		String contents = getContentsForTag(ssb, saveTokenTag);
		SaveToken saveToken = null;
		List<String> strVals = null;
		String referenceKey = "";
		
		if (contents.equals("")) return;
		
		switch (saveTokenTag)
		{		
			case O_HGT:
				saveToken = ssb.getToken(saveTokenTag);
				this.height = Integer.parseInt(saveToken.getContents());
				break;
				
			case O_PCX:
				saveToken = ssb.getToken(saveTokenTag);

				if (playerCoords == null)
					playerCoords = new Point(-1, -1);
				
				playerCoords.x = Integer.parseInt(saveToken.getContents());
				break;
				
			case O_PCY:
				saveToken = ssb.getToken(saveTokenTag);

				if (playerCoords == null)
					playerCoords = new Point(-1, -1);
				
				playerCoords.y = Integer.parseInt(saveToken.getContents());
				break;
				
			case O_WID:
				saveToken = ssb.getToken(saveTokenTag);
				this.width = Integer.parseInt(saveToken.getContents());
				break;
			
			case O_TIL:
				saveToken = ssb.getToken(saveTokenTag);
				strVals = saveToken.getContentSet();
				for (int i = 0; i < height; i++)
				{
					for (int j = 0; j < width; j++)
					{
						referenceKey = "W" + strVals.remove(0);
						WorldTile tile = EntityMap.getWorldTile(referenceKey).clone();
						tiles[i][j] = tile;
					}
				}
				break;
				
			default:
				throw new IllegalArgumentException("Overworld - Unhandled token: " + saveTokenTag.toString());
		}
		
		return;
	}
}
