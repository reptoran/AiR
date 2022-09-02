package test.misc;

import java.util.Scanner;

import org.junit.Test;

import main.data.Data;
import main.entity.actor.ActorFactory;
import main.entity.actor.ActorType;
import main.logic.RPGlib;
import main.presentation.message.FormattedMessageBuilder;

public class MiscTests
{
	@Test
	public void scanner_experimentation()
	{
		String textToScan = "aa,ba.ca,dd.1e,21.e3";
		Scanner s = new Scanner(textToScan).useDelimiter("[.]");
		
		while (s.hasNext())
		{
			String next = s.next();
			System.out.println(next);
		}
	}
	
	@Test
	public void replace_experimentation()
	{
		String text = "a@bcdef";
		text = text.replace("@bc", "Z");
		System.out.println(text);
	}
	
	@Test
	public void spiral_logic_test()
	{
		Data data = new Data();
		
//		data.getClosestOpenTileCoords(new Point(0, 0));
	}
	
	@Test
	public void truncate_experimentation()
	{
		System.out.println(RPGlib.truncateDouble(123.45678, 2));
		System.out.println(RPGlib.truncateDouble(123.45678, 5));
		System.out.println(RPGlib.truncateDouble(123.45678, 7));
	}
	
	@Test
	public void roll_experimentation()
	{
		RPGlib.roll("3D12+14");
		RPGlib.roll("2D4");
		RPGlib.roll("4D3-10");
	}
	
	@Test
	public void test_message_format_sequence_replace()
	{
		System.out.println(new FormattedMessageBuilder("test returns %1[hello|goodbye]").setSource(ActorFactory.generateNewActor(ActorType.PLAYER)).format());
		System.out.println(new FormattedMessageBuilder("test returns %1[hello|goodbye]").setSource(ActorFactory.generateNewActor(ActorType.BANDIT)).format());
	}
}
