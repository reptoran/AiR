package main.entity.feature;

import main.entity.FieldCoordBuilder;

public class FeatureBuilder extends FieldCoordBuilder<Feature>
{
	private FeatureBuilder()
	{
		super(new Feature());
	}
	
	public static FeatureBuilder generateFeature(FeatureType featureType)
	{
		return new FeatureBuilder().setType(featureType);
	}
	
	private FeatureBuilder setType(FeatureType type)
	{
		getFieldCoord().setType(type);
		return this;
	}
}
