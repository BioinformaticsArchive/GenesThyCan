package moara.mention.entities;

import java.util.HashMap;
import java.util.Iterator;

public class CollectionCases {

	private HashMap<String,Case> tokens;
	
	public CollectionCases() {
		this.tokens = new HashMap<String,Case>();
	}
	
	public void add(Case c, int start, int end) {
		String index = start + "," + end;
		this.tokens.put(index,c);
	}
	
	public Iterator<String> getIndexes() {
		return this.tokens.keySet().iterator();
	}
	
	public Case get(String index) {
		return this.tokens.get(index);
	}
	
}
