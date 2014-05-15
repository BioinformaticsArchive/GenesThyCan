package moara.util.corpora;

import java.lang.Comparable;

public abstract class NormalizedNamedEntity implements Comparable<NormalizedNamedEntity> {

	private String identifier;
	private double score;
	private boolean selected;
		
	public NormalizedNamedEntity() {
		
	}
	
	public String Identifier() {
		return this.identifier;
	}
	
	public double Score() {
		return this.score;
	}
	
	public boolean Selected() {
		return this.selected;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public void setScore(double score) {
		this.score = score;
	}
	
	public void setSelected(boolean set) {
		this.selected = set;
	}
	
	public abstract String getNamespace();
	
	public int compareTo(NormalizedNamedEntity nne) {
		if (this.score>nne.Score())
			return 1;
		else if (this.score<nne.Score())
			return -1;
		else
			return 0;
	}
	
}
