package main.presentation.chateditor;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

public abstract class AbstractSortableListModel<T> extends AbstractListModel<T>
{
	private static final long serialVersionUID = -1377258257754697973L;
	
	protected List<T> elements = new ArrayList<T>();
	
	public abstract void sort();

	@Override
	public int getSize()
	{
		return elements.size();
	}

	@Override
	public T getElementAt(int index)
	{
		if (index < 0 || index >= elements.size())
			return null;
		
		return elements.get(index);
	}

	public boolean isEmpty()
	{
		return elements.isEmpty();
	}

	public void addElement(T chat)
	{
		elements.add(chat);
		sort();
		fireIntervalAdded(this, elements.size(), elements.size());
	}

	public void removeElementAt(int index)
	{
		if (index < 0 || index >= elements.size())
			return;
		
		elements.remove(index);
		fireIntervalRemoved(this, index + 1, index + 1);
	}

	public void removeAllElements()
	{
		elements.clear();
		fireIntervalRemoved(this, 0, elements.size());
	}
	
	public void refreshView()
	{
		fireContentsChanged(this, 0, elements.size());
	}
}
