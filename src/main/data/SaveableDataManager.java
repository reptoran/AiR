package main.data;

public interface SaveableDataManager
{
	public static final String DELIMITER = ";";
	
	String saveState();
	void loadState(String saveString);
}
