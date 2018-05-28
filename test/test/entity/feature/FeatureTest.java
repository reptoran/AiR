package test.entity.feature;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import main.entity.feature.Feature;
import main.entity.feature.FeatureFactory;
import main.entity.feature.FeatureType;
import main.entity.save.EntityMap;

import org.junit.Before;
import org.junit.Test;

public class FeatureTest
{
	private final String defaultFeatureUid = "FEATURE1945729261";

	private final String treeSaveString1 = "<FEATURE>[F_UID]F1;[F_TYP]TREE</FEATURE>";
	private final String treeSaveString2 = "<FEATURE>[F_UID]F1;[F_TYP]TREE;[F_CHP]96</FEATURE>";
	
	private Feature defaultFeature;
	private Feature treeFeature;
	
	@Before
	public void setup()
	{
		EntityMap.clearMappings();
		
		defaultFeature = new Feature();
		treeFeature = FeatureFactory.generateNewFeature(FeatureType.TREE);
	}
	
	@Test
	public void clone_success()
	{
		Feature treeFeatureClone = treeFeature.clone();
		
		assertEquals(treeFeature, treeFeatureClone);
		assertEquals(treeFeature.hashCode(), treeFeatureClone.hashCode());
	}
	
	@Test
	public void getUniqueID_success()
	{
		String uid = defaultFeature.getUniqueId();
		
		assertEquals(defaultFeatureUid, uid);
	}
	
	@Test
	public void saveAsText_unchangedSuccess()
	{
		String saveString = treeFeature.saveAsText();
		
		assertEquals(treeSaveString1, saveString);
	}
	
	@Test
	public void saveAsText_changedHpSuccess()
	{
		treeFeature.damage(4);
		String saveString = treeFeature.saveAsText();
		
		assertEquals(treeSaveString2, saveString);
	}
	
	@Test
	public void loadFromText_unchangedSuccess() throws ParseException
	{
		Feature newTreeFeature = new Feature();
		
		newTreeFeature.loadFromText(treeSaveString1);
		
		assertEquals(treeFeature, newTreeFeature);
	}
}

