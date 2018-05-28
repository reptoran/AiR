package test.entity.save;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.text.ParseException;

import main.entity.EntityType;
import main.entity.save.SaveStringBuilder;
import main.entity.save.SaveToken;
import main.entity.save.SaveTokenTag;

import org.junit.Before;
import org.junit.Test;

public class SaveStringBuilderTest
{
	private SaveStringBuilder actorSaveBuilder;
	private SaveStringBuilder actorLoadBuilder;
	
	private final String actorLoadString =  "<ACTOR>[A_UID]A1;[A_TYP]HUMAN;[A_WXY]1,1;[A_LXY]5,5</ACTOR>";
	private final String actorSingleTokenString = "<ACTOR>[A_TYP]RAT</ACTOR>";
	private final String invalidEntityLoadString = "<ACTER>[A_TYP]RAT</ACTOR>";
	private final String featureEntityLoadString = "<FEATURE>[F_UID]FEATURE1140220231;[F_TYP]TREE</FEATURE>";
	
	@Before
	public void setup() throws ParseException
	{
		actorSaveBuilder = new SaveStringBuilder(EntityType.ACTOR);
		actorLoadBuilder = new SaveStringBuilder(EntityType.ACTOR, actorLoadString);
	}
	
	@Test
	public void getSaveStringFromLoad_success()
	{
		String saveString = actorLoadBuilder.getSaveString();
		assertEquals(saveString, actorLoadString);
	}
	
	@Test
	public void addToken_success()
	{
		SaveToken ratToken = new SaveToken(SaveTokenTag.A_TYP, "RAT");
		
		actorSaveBuilder.addToken(ratToken);
		
		String saveString = actorSaveBuilder.getSaveString();
		assertEquals(saveString, actorSingleTokenString);
	}
	
	@Test
	public void getEntityFromSaveString_success() throws ParseException
	{
		EntityType returnedEntity = SaveStringBuilder.getEntityType(featureEntityLoadString);
		
		assertEquals(EntityType.FEATURE, returnedEntity);
	}
	
	@Test
	public void getEntityFromSaveString_exception()
	{
		EntityType returnedEntity = null;
		String exceptionMessage = "Invalid format in loading string - Entity ACTER does not exist.";
		
		try
		{
			returnedEntity = SaveStringBuilder.getEntityType(invalidEntityLoadString);
			fail("Should throw ParseException!");
		} catch (ParseException pe)
		{
			assertEquals(exceptionMessage, pe.getMessage());
		}
		
		assertNull(returnedEntity);
	}
	
	//test invalid loadstring
}
