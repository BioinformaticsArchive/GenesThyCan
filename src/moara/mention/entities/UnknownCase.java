package moara.mention.entities;

import moara.mention.MentionConstant;

public class UnknownCase extends Case {

	// example of word that has the format
	private String example;
	
	public UnknownCase(Feature f, String category) {
		this.typeCase = MentionConstant.CASE_UNKNOWN;
		this.feature = f;
		this.category = category;
		this.example = f.getStrValue(MentionConstant.FEATURE_WORD);
	}
	
	public UnknownCase(Feature f, String category, int start, int end) {
		this(f,category);
		this.start = start;
		this.end = end;
	}
	
	public String Example() {
		return this.example;
	}
	 
}



