package main.presentation.chateditor.renderer;

import main.entity.chat.CompareOperator;

public class OperatorListRenderer extends ChatGuiListRenderer<CompareOperator>
{
	private static final long serialVersionUID = -7280471887984546168L;

	@Override
	protected void updateText(CompareOperator value)
	{
		if (value == null)
			textString = "";
		else
			textString = "" + value.getSymbol();
	}
}