package main.presentation.chateditor.renderer;

import main.entity.chat.ChatResponse;

public class ResponseListRenderer extends ChatGuiListRenderer<ChatResponse>
{
	private static final long serialVersionUID = -3681611317775803954L;

	@Override
	protected void updateText(ChatResponse value)
	{
		textString = value.getText();
	}
}