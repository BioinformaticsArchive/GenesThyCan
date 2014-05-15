package moara.util.data;

public interface EditDistanceItem {

	public int getLenght();
	
	public double getInclusionCost(int index);
	
	public double getSubstitutionCost(int index1, EditDistanceItem item2, int index2);
	
	public double getExclusionCost(int index);
	
	public void clear();
	
}
