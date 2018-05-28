package main.presentation;

import main.logic.Engine;

public abstract class AbstractGui implements Gui
{
	protected Engine engine;
	
	public AbstractGui(Engine engine)
	{
		this.engine = engine;
	}
	
	@Override
	public abstract void refreshInterface();
}
