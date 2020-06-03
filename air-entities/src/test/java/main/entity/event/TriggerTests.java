package main.entity.event;

import org.junit.Before;
import org.junit.Test;

public class TriggerTests
{
	@Before
	public void setup()
	{
		//
	}
	
	@Test
	public void parseJsonDetailsString_noValue()
	{
		Trigger trigger = new Trigger(TriggerType.CHANGE_HP_OF_ACTOR, "PLAYER:1");
		System.out.println(trigger.getDetails());
	}
}
