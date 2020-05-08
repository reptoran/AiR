package main.presentation.chateditor.renderer;

import main.entity.event.Trigger;

public class TriggerListRenderer extends ChatGuiListRenderer<Trigger>
{
	private static final long serialVersionUID = 2762484469216718337L;

	@Override
	protected void updateText(Trigger value)
	{
		textString = value.getType().name();
	}
}
