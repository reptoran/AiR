package main.entity;

public abstract class FieldCoordBuilder<T>
{
	protected FieldCoord fieldCoord;
	
	@SuppressWarnings("unchecked")
	protected T getFieldCoord()
	{
		return (T) fieldCoord;
	}
	
	protected FieldCoordBuilder(FieldCoord fieldCoord)
	{
		this.fieldCoord = fieldCoord;
	}
	
	@SuppressWarnings("unchecked")
	public T build()
	{
		return (T) fieldCoord;
	}
	
	public FieldCoordBuilder<T> setName(String name)
	{
		fieldCoord.setName(name);
		return this;
	}
	
	public FieldCoordBuilder<T> setIcon(char icon)
	{
		fieldCoord.setIcon(icon);
		return this;
	}
	
	public FieldCoordBuilder<T> setColor(int color)
	{
		fieldCoord.setColor(color);
		return this;
	}

	public FieldCoordBuilder<T> setObstructsSight(boolean obstructsSight)
	{
		fieldCoord.setObstructsSight(obstructsSight);
		return this;
	}

	public FieldCoordBuilder<T> setObstructsMotion(boolean obstructsMotion)
	{
		fieldCoord.setObstructsMotion(obstructsMotion);
		return this;
	}

	public FieldCoordBuilder<T> setVisible(boolean visible)
	{
		fieldCoord.setVisible(visible);
		return this;
	}

	public FieldCoordBuilder<T> setSeen(boolean seen)
	{
		fieldCoord.setSeen(seen);
		return this;
	}

	public FieldCoordBuilder<T> setMoveCostModifier(double moveCostModifier)
	{
		fieldCoord.setMoveCostModifier(moveCostModifier);
		return this;
	}

	public FieldCoordBuilder<T> setBlockedMessage(String blockedMessage)
	{
		fieldCoord.setBlockedMessage(blockedMessage);
		return this;
	}
}
