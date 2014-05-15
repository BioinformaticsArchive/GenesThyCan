package moara.util.text;

import java.util.ArrayList;

public abstract class StringDistance {

	protected int type;
	
	public StringDistance(int type) {
		this.type = type;
	}
	
	public abstract void train(ArrayList<String> tokens);
	
	public abstract double score(String token1, String token2);
	
}
