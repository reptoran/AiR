package main.entity.zone.generator;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import main.entity.feature.FeatureFactory;
import main.entity.feature.FeatureType;
import main.entity.tile.Tile;
import main.entity.tile.TileFactory;
import main.entity.tile.TileType;
import main.entity.zone.Zone;
import main.entity.zone.ZoneKey;
import main.entity.zone.ZoneType;
import main.logic.Line;
import main.logic.RPGlib;
import main.presentation.Logger;

public class PersistentCaveGenerator extends AbstractGenerator
{
	private static final int HEIGHT = 40;
	private static final int WIDTH = 80;

	private static final int XMIN = 0;
	private static final int YMIN = 0;
	private static final int XMAX = 39;
	private static final int YMAX = 79;

	private Point upwardLevelEntry = null;
	private Point downwardLevelEntry = null;

	private ZoneKey zoneKey;
	private boolean descendedIntoLevel;
	private Zone originZone;

	@Override
	public Zone generateZone(ZoneKey zoneKeyArg)
	{
		return generateZone(zoneKeyArg, true, null);
	}

	@Override
	public Zone generateZone(ZoneKey zoneKeyArg, boolean descendingArg, Zone originZoneArg)
	{
		Logger.debug("Generating new persistent cave: " + zoneKeyArg + "; " + descendingArg + "; " + originZoneArg);
		
		zoneKey = zoneKeyArg;
		descendedIntoLevel = descendingArg;
		originZone = originZoneArg;

		if (!originZone.shouldPersist())
			originZone = null;

		return generateLevel();
	}

	private Zone generateLevel()
	{
		String name = zoneKey.getId();
		Zone zone = new Zone(ZoneType.CAVE, name, HEIGHT, WIDTH, zoneKey.getLevel(), true, canTransitionToOverworld());

		// size, openness, density
		// char[][] mapTiles = dgen(10, 15, 30);
		char[][] mapTiles = dgen(25, 12, 35);

		for (int i = 0; i < HEIGHT; i++)
		{
			for (int j = 0; j < WIDTH; j++)
			{
				Tile newTile = TileFactory.generateNewTile(TileType.FLOOR);

				switch (mapTiles[i][j])
				{
				case '#':
					newTile.setFeatureHere(FeatureFactory.generateNewFeature(FeatureType.WALL));
					break;
				case '<':
					newTile = TileFactory.generateNewTile(TileType.STAIRS_UP);
					upwardLevelEntry = new Point(i, j);
					break;
				case '>':
					newTile = TileFactory.generateNewTile(TileType.STAIRS_DOWN);
					downwardLevelEntry = new Point(i, j);
					break;
				}

				zone.setTile(i, j, newTile);
			}
		}
		
		//TODO: extract code below into separate methods, ideally in the parent class if possible
		//probably need to pass in all the arguments

		/*
		 * So, we need to update the passed in ZoneKey with the coords of the entry staircase, the ID of the newly generated zone, and the type of PERMANENT. We
		 * also need to create a new ZoneKey to insert into the generated zone containing the coordinates of the original ZoneKey (obtained by calling
		 * originZone.getLocationOfZoneKey() with the passed in ZoneKey), and either the ID of the origin zone and the type PERMANENT, or the type of the origin
		 * zone (stored in a new field in Zone) with a null id. Then, insert that new ZoneKey into the generated zone keyed off of the coordinates of the active
		 * staircase.
		 */
		
		ZoneKey keyToAboveZone = new ZoneKey(getDefaultZoneTypeForNextLevel(), zoneKey.getLevel() - 1);
		ZoneKey keyToBelowZone = new ZoneKey(getDefaultZoneTypeForNextLevel(), zoneKey.getLevel() + 1);
		
		if (descendedIntoLevel)
			zoneKey.setEntryPoint(new Point(upwardLevelEntry.x, upwardLevelEntry.y));
		else
			zoneKey.setEntryPoint(new Point(downwardLevelEntry.x, downwardLevelEntry.y));
		
		if (originZone != null)
		{	
			Point entryIntoGeneratedZoneFromOriginZone = originZone.getLocationOfZoneKey(zoneKey);
			
			if (descendedIntoLevel)
			{
				keyToAboveZone.updateToPermanent(originZone.getName());
				keyToAboveZone.setEntryPoint(entryIntoGeneratedZoneFromOriginZone);
			}
			else
			{
				keyToBelowZone.updateToPermanent(originZone.getName());
				keyToBelowZone.setEntryPoint(entryIntoGeneratedZoneFromOriginZone);
			}
		}
		
		zone.addZoneKey(upwardLevelEntry, keyToAboveZone);
		zone.addZoneKey(downwardLevelEntry, keyToBelowZone);

		zoneKey.updateToPermanent(name);

		Logger.debug("Persistent cave generated: " + zone);
		
		return zone;
	}

	@Override
	protected ZoneType getDefaultZoneTypeForNextLevel()
	{
		return ZoneType.CAVE;
	}

	char[][] dgen(int size, int openness, int density)
	{
		char[][] map = new char[HEIGHT][WIDTH];

		// int S = size;
		// int O = openness;
		// int D = density;

		int[][] coords = new int[size][2];

		for (int i = 0; i < size; i++)
		{
			int x = RPGlib.Randint(XMIN, XMAX);
			int y = RPGlib.Randint(YMIN, YMAX);

			coords[i][0] = x;
			coords[i][1] = y;

			for (int j = 0; j < RPGlib.Randint((int) (density * .6), (int) (density * 1.3)); j++)
			{
				Line line = new Line(x, y, RPGlib.Randint((int) (x - (openness * (5.0 / 8))), (int) (x + (openness * (5.0 / 8)))),
						RPGlib.Randint(y - openness, y + openness));
				for (int l = 0; l < line.getLength(); l++)
				{
					if (line.getX(l) > XMIN && line.getX(l) < XMAX && line.getY(l) > YMIN && line.getY(l) < YMAX)
						map[line.getX(l)][line.getY(l)] = '.';
				}
			}
		}

		char[][] temp = new char[HEIGHT][WIDTH];

		for (int i = 0; i < HEIGHT; i++)
		{
			for (int j = 0; j < WIDTH; j++)
			{
				if (i == 0 || j == 0 || i == HEIGHT - 1 || j == WIDTH - 1)
				{
					temp[i][j] = ' ';
				} else
				{
					int amt = 0;
					for (int k = -1; k <= 1; k++)
					{
						for (int l = -1; l <= 1; l++)
						{
							if (map[i + k][j + l] == '.' && !(k == 0 && l == 0))
								amt++;
						}
					}
					if (amt > 4)
						temp[i][j] = '.';
					else
						temp[i][j] = ' ';
				}
			}
		}

		// now we have our dungeon; time to determine the areas in it
		char section = (char) 65;
		char main = ' ';
		int max = 0;
		List<Character> sections = new ArrayList<Character>();

		for (int i = 0; i < HEIGHT; i++)
		{
			for (int j = 0; j < WIDTH; j++)
			{
				if (temp[i][j] == '.')
				{
					int amt = floodNode(0, i, j, section, temp);

					if (amt > 75)
					{
						sections.add(section);

						if (amt > max)
						{
							main = section;
							max = amt;
						}
					}
					section++;
				}
			}
		}

		// place the stairs
		int x0 = 0, y0 = 0, x1 = RPGlib.Randint(XMIN, XMAX), y1 = RPGlib.Randint(YMIN, YMAX);

		for (int i = 0; i < size; i++)
		{
			if (temp[coords[i][0]][coords[i][1]] == main)
			{
				x0 = coords[i][0];
				y0 = coords[i][1];
				break;
			}
		}

		int count = 0;

		while (!(temp[x1][y1] == main && RPGlib.dist(x0, y0, x1, y1) > openness))
		{
			x1 = RPGlib.Randint(XMIN, XMAX);
			y1 = RPGlib.Randint(YMIN, YMAX);

			count++;

			if (count > 1000)
			{
				// mBuffer.addMsg("This staircase is really long!");
				return null;
			}
		}

		// create the dungeon with only the largest areas
		for (int i = 0; i < HEIGHT; i++)
		{
			for (int j = 0; j < WIDTH; j++)
			{
				boolean isOk = false;
				for (int z = 0; z < sections.size(); z++)
				{
					if (temp[i][j] == sections.get(z))
						isOk = true;
				}

				if (isOk)
					map[i][j] = '.';// temp[i][j];
				else
					map[i][j] = '#';
			}
		}

		map[x0][y0] = '>';
		map[x1][y1] = '<';

		return map;
	}

	int floodNode(int amt, int x, int y, char sect, char[][] map)
	{
		map[x][y] = sect;
		for (int i = -1; i <= 1; i++)
		{
			for (int j = -1; j <= 1; j++)
			{
				if (map[x + i][y + j] == '.')
					amt = floodNode(amt, x + i, y + j, sect, map);
			}
		}

		return amt + 1;
	}
}
