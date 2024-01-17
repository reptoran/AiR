package main.data;

import java.util.HashMap;
import java.util.Map;

public class GameSettings
{
	private static Map<SettingType, Object> settings = null;
	
	private static void defineMappings()
	{
		if (settings != null)
			return;
		
		settings = new HashMap<SettingType, Object>();
		settings.put(SettingType.SHOW_FOG, false);
		settings.put(SettingType.SHOW_FACING, false);
		settings.put(SettingType.ZIP_CACHED_FILES, true);
	}
	
	public static Object getSetting(SettingType key)
	{
		defineMappings();
		return settings.get(key);
	}
	
	public static boolean getBoolean(SettingType key)
	{
		try
		{
			return (Boolean) getSetting(key);
		} catch (ClassCastException cce)
		{
			return false;
		}
	}
}
