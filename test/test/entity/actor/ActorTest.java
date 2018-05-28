package test.entity.actor;

import static org.junit.Assert.assertEquals;

import java.awt.Point;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import main.entity.actor.Actor;
import main.entity.actor.ActorFactory;
import main.entity.actor.ActorType;
import main.entity.save.EntityMap;

public class ActorTest
{
	private final String humanSaveString1 = "<ACTOR>[A_UID]A1;[A_TYP]HUMAN</ACTOR>";
	private final String humanSaveString2 = "<ACTOR>[A_UID]A1;[A_TYP]HUMAN;[A_ATT]20,20,30,20,20</ACTOR>";
	private final String humanSaveString3 = "<ACTOR>[A_UID]A2;[A_TYP]HUMAN;[A_CHP]18</ACTOR>";
	
	private Actor humanActor;
	private Actor ratActor;
	
	@Before
	public void setup()
	{
		EntityMap.clearMappings();
		
		humanActor = ActorFactory.generateNewActor(ActorType.HUMAN);
		ratActor = ActorFactory.generateNewActor(ActorType.RAT);
	}
	
	@Test
	public void mapsKeyedToPoints()
	{
		Map<Point, String> map = new HashMap<Point, String>();
		
		Point p1 = new Point(1, 1);
		String s1 = "Number 1";
		
		map.put(p1, s1);
		
		Point p2 = new Point(1, 1);
		
		assertEquals("Number 1", map.get(p2));
	}
	
	@Test
	public void clone_success()
	{
		Actor ratActorClone = ratActor.clone();
		
		assertEquals(ratActor, ratActorClone);
		assertEquals(ratActor.hashCode(), ratActorClone.hashCode());
	}
	
	@Test
	public void saveAsText_unchangedSuccess()
	{
		String saveString = humanActor.saveAsText();
		
		assertEquals(humanSaveString1, saveString);
	}
	
	@Test
	public void saveAsText_changedAttributesSuccess()
	{
		humanActor.setAttribute(Actor.ATT_MAG, 30);
		String saveString = humanActor.saveAsText();
		
		assertEquals(humanSaveString2, saveString);
	}
	
	@Test
	public void loadFromText_changedHpSuccess() throws ParseException
	{
		Actor hurtHumanActor = ActorFactory.generateNewActor(ActorType.HUMAN);
		hurtHumanActor.damage(2);
		
		humanActor.loadFromText(humanSaveString3);
		
		assertEquals(hurtHumanActor, humanActor);
	}
}
