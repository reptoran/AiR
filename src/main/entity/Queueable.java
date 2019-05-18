package main.entity;

public interface Queueable
{
	public void reduceTicksLeftBeforeActing(int amount);
	public void increaseTicksLeftBeforeActing(int amount);
	public int getTicksLeftBeforeActing();
}
