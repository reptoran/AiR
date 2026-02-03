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
		settings.put(SettingType.SHOW_AWARENESS, false);
		settings.put(SettingType.ZIP_CACHED_FILES, true);
		settings.put(SettingType.MAX_NAME_LENGTH, 11);
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
	
	public static int getInteger(SettingType key)
	{
		try
		{
			return (Integer) getSetting(key);
		} catch (ClassCastException cce)
		{
			return 0;
		}
	}
	
	public static void updateSetting(SettingType setting, Object value)
	{
		settings.put(setting, value);
	}
}
