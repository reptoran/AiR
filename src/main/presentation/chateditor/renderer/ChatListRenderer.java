package main.presentation.chateditor.renderer;

import main.entity.chat.Chat;

public class ChatListRenderer extends ChatGuiListRenderer<Chat>
{
	private static final long serialVersionUID = -8117301707159680351L;

	@Override
	protected void updateText(Chat value)
	{
		textString = value.getTag();
	}
}