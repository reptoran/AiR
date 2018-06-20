package main.logic;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Line	//TODO: legacy code; needs refactoring
{
	private List<Point> linePoints = new ArrayList<Point>();
	int lineDist = 0;

	/**
	 * Generates a line of connected Points.  The first element is always the origin, while the last element is always the destination.
	 * 
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 */
	public Line(int startX, int startY, int endX, int endY)
	{
		double xDist = endX - startX;
		double yDist = endY - startY;
		double xAbs = Math.abs(xDist);
		double yAbs = Math.abs(yDist);
		double lineX = (xAbs != 0) ? (xDist / xAbs) : 0;
		double lineY = (yAbs != 0) ? (yDist / yAbs) : 0;
		double xStart = startX;
		double yStart = startY;
		double[][] XY;

		if (xAbs < yAbs)
		{
			lineDist = (int) yAbs;
			XY = new double[lineDist + 1][2];
			for (int lineI = 0; lineI <= yAbs; lineI++)
			{
				XY[lineI][1] = yStart;
				yStart += lineY;
				XY[lineI][0] = (int) (xStart + (.5 * (xStart / Math.abs(xStart))));
				xStart += ((xAbs / yAbs) * lineX);
			}
		} else
		{
			lineDist = (int) xAbs;
			XY = new double[lineDist + 1][2];
			for (int lineI = 0; lineI <= xAbs; lineI++)
			{
				XY[lineI][0] = xStart;
				xStart += lineX;
				XY[lineI][1] = (int) (yStart + (.5 * (yStart / Math.abs(yStart))));
				yStart += ((yAbs / xAbs) * lineY);
			}
		}

		for (int lineI = 0; lineI <= lineDist; lineI++)
		{
			int pointX = (int) XY[lineI][0];
			int pointY = (int) XY[lineI][1];

			Point point = new Point(pointX, pointY);
			linePoints.add(point);
		}
	}
	
	public int getX(int index)
	{
		return linePoints.get(index).x;
	}
	
	public int getY(int index)
	{
		return linePoints.get(index).y;
	}

	public Point getPoint(int index)
	{
		return linePoints.get(index);
	}

	public int getLength()
	{
		return lineDist + 1;	//lineDist is the distance from the origin (so a spot right next to it would be a distance of 1, even though the line has a length of 2)
	}

	public List<Point> getPoints()
	{
		return linePoints;
	}
	
	@Override
	public String toString()
	{
		return linePoints.toString();
	}
}
