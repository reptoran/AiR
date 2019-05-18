package main.entity.zone.generator.dungeon;

import java.util.ArrayList;
import java.util.List;

import main.entity.zone.ZoneType;
import main.logic.Line;
import main.logic.RPGlib;

public class CaveGenerator extends RandomGenerator
{
	int size = 10;
	int openness = 15;
	int density = 30;
//	int size = 25;
//	int openness = 12;
//	int density = 35;
	
	@Override
	public char[][] generateCharMap(int height, int width)
	{
		char[][] map = new char[height][width];
		
		int xMin = 0;
		int yMin = 0;
		int xMax = height - 1;
		int yMax = width - 1;

		int[][] coords = new int[size][2];

		for (int i = 0; i < size; i++)
		{
			int x = RPGlib.randInt(xMin, xMax);
			int y = RPGlib.randInt(yMin, yMax);

			coords[i][0] = x;
			coords[i][1] = y;

			for (int j = 0; j < RPGlib.randInt((int) (density * .6), (int) (density * 1.3)); j++)
			{
				Line line = new Line(x, y, RPGlib.randInt((int) (x - (openness * (5.0 / 8))), (int) (x + (openness * (5.0 / 8)))),
						RPGlib.randInt(y - openness, y + openness));
				for (int l = 0; l < line.getLength(); l++)
				{
					if (line.getX(l) > xMin && line.getX(l) < xMax && line.getY(l) > yMin && line.getY(l) < yMax)
						map[line.getX(l)][line.getY(l)] = '.';
				}
			}
		}

		char[][] temp = new char[height][width];

		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				if (i == 0 || j == 0 || i == height - 1 || j == width - 1)
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

		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
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
		int x0 = 0, y0 = 0, x1 = RPGlib.randInt(xMin, xMax), y1 = RPGlib.randInt(yMin, yMax);

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

		while (!(temp[x1][y1] == main && RPGlib.distance(x0, y0, x1, y1) > openness))
		{
			x1 = RPGlib.randInt(xMin, xMax);
			y1 = RPGlib.randInt(yMin, yMax);

			count++;

			if (count > 1000)
			{
				// mBuffer.addMsg("This staircase is really long!");
				return null;
			}
		}

		// create the dungeon with only the largest areas
		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
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

	private int floodNode(int amt, int x, int y, char sect, char[][] map)
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

	@Override
	public ZoneType getZoneType()
	{
		return ZoneType.CAVE;
	}
}
