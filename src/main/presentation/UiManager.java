package main.presentation;

public class UiManager
{
	private static UiManager uiManager = null;

	private Gui gui = null;

	public static UiManager getInstance()
	{
		if (uiManager == null)
		{
			uiManager = new UiManager();
		}

		return uiManager;
	}

	public void setGui(Gui gui)
	{
		this.gui = gui;
	}
	
	public void refreshInterface() {
		if (gui != null) {
			gui.refreshInterface();
		}
	}
}
