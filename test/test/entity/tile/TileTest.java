package test.entity.tile;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import main.entity.actor.Actor;
import main.entity.actor.ActorFactory;
import main.entity.actor.ActorType;
import main.entity.feature.Feature;
import main.entity.feature.FeatureFactory;
import main.entity.feature.FeatureType;
import main.entity.save.EntityMap;
import main.entity.tile.Tile;
import main.entity.tile.TileFactory;
import main.entity.tile.TileType;

import org.junit.Before;
import org.junit.Test;

public class TileTest
{
	private final String defaultTileUid = "TILE1600325603";

	private final String grassSaveString1 = "<TILE>[T_UID]T1;[T_TYP]GRASS</TILE>";
	private final String grassSaveString2 = "<TILE>[T_UID]T1;[T_TYP]GRASS;[T_FHR]1</TILE>";
	private final String grassSaveString3 = "<TILE>[T_UID]T2;[T_TYP]GRASS;[T_AHR]1</TILE>";
	
	private Tile defaultTile;
	private Tile grassTile;
	
	@Before
	public void setup()
	{
		EntityMap.clearMappings();
		
		defaultTile = new Tile();
		grassTile = TileFactory.generateNewTile(TileType.GRASS);
	}
	
	@Test
	public void clone_success()
	{
		Tile grassTileClone = grassTile.clone();
		
		assertEquals(grassTile, grassTileClone);
		assertEquals(grassTile.hashCode(), grassTileClone.hashCode());
	}
	
	@Test
	public void getUniqueID_success()
	{
		String uid = defaultTile.getUniqueId();
		
		assertEquals(defaultTileUid, uid);
	}
	
	@Test
	public void saveAsText_unchangedSuccess()
	{
		String saveString = grassTile.saveAsText();
		
		assertEquals(grassSaveString1, saveString);
	}
	
	@Test
	public void saveAsText_changedFeatureSuccess()
	{
		Feature feature = FeatureFactory.generateNewFeature(FeatureType.TREE);
		
		grassTile.setFeatureHere(feature);
		String saveString = grassTile.saveAsText();
		
		assertEquals(grassSaveString2, saveString);
	}
	
	@Test
	public void loadFromText_unchangedSuccess() throws ParseException
	{
		Tile newGrassTile = new Tile();
		
		newGrassTile.loadFromText(grassSaveString1);
		
		assertEquals(grassTile, newGrassTile);
	}
	
	@Test
	public void loadFromText_changedActorSuccess() throws ParseException
	{
		Actor actor = ActorFactory.generateNewActor(ActorType.HUMAN);
		EntityMap.put("A1", actor);
		
		grassTile.setActorHere(actor);
		
		Tile newGrassTile = new Tile();
		newGrassTile.loadFromText(grassSaveString3);
		
		assertEquals(grassTile, newGrassTile);
	}
}
