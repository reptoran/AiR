package main.presentation.chateditor.renderer;

import main.entity.chat.ChatReq;

public class RequirementListRenderer extends ChatGuiListRenderer<ChatReq>
{
	private static final long serialVersionUID = -6652221432921568481L;

	@Override
	protected void updateText(ChatReq value)
	{
		textString = value.toString();
	}
}