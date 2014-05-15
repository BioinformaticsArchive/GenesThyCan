package moara.util.data;

public interface WindowTextItem {

	public void init();
	
	public WindowTextItem newObject();
	
	public void print();
	
	public WindowTextItem copy();
	
	public boolean check(String value);
	
	public String getName();
	
	public void addToName(String name);
	
}
