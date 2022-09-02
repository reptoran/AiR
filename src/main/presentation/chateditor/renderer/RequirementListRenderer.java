package main.presentation.chateditor.renderer;

import main.entity.requirement.Requirement;

public class RequirementListRenderer extends ChatGuiListRenderer<Requirement>
{
	private static final long serialVersionUID = -6652221432921568481L;

	@Override
	protected void updateText(Requirement value)
	{
		textString = value.toString();
	}
}