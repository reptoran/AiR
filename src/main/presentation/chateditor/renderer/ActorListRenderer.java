package main.presentation.chateditor.renderer;

import main.entity.actor.ActorFactory;
import main.entity.actor.ActorType;

public class ActorListRenderer extends ChatGuiListRenderer<ActorType>
{
	private static final long serialVersionUID = -7280471887984546168L;

	@Override
	protected void updateText(ActorType value)
	{
		textString = ActorFactory.generateNewActor(value).getName();
	}
}