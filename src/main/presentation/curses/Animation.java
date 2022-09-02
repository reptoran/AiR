package main.presentation.curses;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Timer;

public class Animation implements ActionListener
{
	private Timer frameTimer;
	private List<AnimationFrame> frames;
	
	private int currentFrame = 0;
	private boolean isStarted = false;
	private boolean isFinished = false;
	
	private Animation(int frameDelayInMs)
	{
		frames = new ArrayList<AnimationFrame>();
		frameTimer = new Timer(frameDelayInMs, this);
	}
	
	private void addFrame(AnimationFrame frame)
	{
		frames.add(frame);
	}
	
	private class AnimationFrame
	{
		private Map<Point, DisplayTile> characterMap = new HashMap<Point, DisplayTile>();
		
		protected AnimationFrame() {}
		
		protected void addCharacter(Point point, DisplayTile character)
		{
			characterMap.put(point, character);
		}
	}
	
	public Map<Point, DisplayTile> getCurrentFrame()
	{
		if (isFinished)
			return null;
		
		return frames.get(currentFrame).characterMap;
	}
	
	public void start()
	{
		isStarted = true;
		frameTimer.start();
	}
	
	public boolean isStarted()
	{
		return isStarted;
	}
	
	public boolean isFinished()
	{
		return isFinished;
	}
	
	public int getCurrentFrameIndex()
	{
		return currentFrame;
	}
	
	public Timer getTimer()
	{
		return frameTimer;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		currentFrame++;
		if (currentFrame >= frames.size())
			isFinished = true;
	}
	
	public static Animation staticTestFireballAnimation()
	{
		Animation animation = new Animation(250);
		
		for (int i = 0; i < 10; i++)
		{
			AnimationFrame frame = animation.new AnimationFrame();
			frame.addCharacter(new Point(i, i), new DisplayTile('*', CursesGuiScreen.COLOR_DARK_RED));
			animation.addFrame(frame);
		}
		return animation;
	}
}
