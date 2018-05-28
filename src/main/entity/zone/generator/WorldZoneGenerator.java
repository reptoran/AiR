package main.entity.zone.generator;

public abstract class WorldZoneGenerator extends AbstractGenerator
{
	@Override
	protected boolean canTransitionToOverworld()
	{
		return true;
	}
}
