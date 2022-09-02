package main.data;

public interface SaveableDataManager
{
	String saveState();
	void loadState(String saveString);
}
