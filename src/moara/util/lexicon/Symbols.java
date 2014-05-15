package moara.util.lexicon;

import java.util.Hashtable;

public class Symbols {

	private String punctuation[] = {".",":",";","!","?",",","\"","~","%","'","*","/",
		"+",">","<",
		"-","=",
		"(",")","{","}","[","]"};
	private String endings[] = {")","]"};
	private String startings[] = {"(","[","-"};
	
	private Hashtable<Integer,String> basePunctuation;
	private Hashtable<Integer,String> baseEndings;
	private Hashtable<Integer,String> baseStartings;
	
	public Symbols() {
		this.basePunctuation = new Hashtable<Integer,String>();
		this.baseEndings = new Hashtable<Integer,String>();
	    this.baseStartings = new Hashtable<Integer,String>();
	    for (int i=0; i<punctuation.length; i++) {
	    	basePunctuation.put(punctuation[i].hashCode(),punctuation[i]);
	    }
	    /*for (int i=0; i<endings.length; i++) {
	    	baseEndings.put(endings[i].hashCode(),endings[i]);
	    }
	    for (int i=0; i<startings.length; i++) {
	    	baseStartings.put(startings[i].hashCode(),startings[i]);
	    }*/
	}
	
	public String getStringSymbols() {
		String symbols = "";
		for (int i=0; i<this.punctuation.length; i++)
			symbols += this.punctuation[i];
		for (int i=0; i<this.endings.length; i++)
			symbols += this.endings[i];
		for (int i=0; i<this.startings.length; i++)
			symbols += this.startings[i];
		return symbols;
	}
	
	public boolean isSymbol(String c) {
		if (isPunctuation(c) || isEnding(c) || isStarting(c))
			return true;
		return false;
	}
	
	public boolean isStartEnd(String c) {
		if (isEnding(c) || isStarting(c))
			return true;
		return false;
	}
	
	public boolean isPunctuation(String c) {
		if (this.basePunctuation.containsKey(c.hashCode()))
			return true;
		return false;
	}
	
	public boolean isEnding(String c) {
		if (this.baseEndings.containsKey(c.hashCode()))
			return true;
		return false;
	}
	
	public boolean isStarting(String c) {
		if (this.baseStartings.containsKey(c.hashCode()))
			return true;
		return false;
	}
	
	public boolean hasEndingPunctuation(String c) {
		if (isEnding(c.substring(c.length()-1)) || isPunctuation(c.substring(c.length()-1)))
			return true;
		return false;
	}
	
	public boolean hasStarting(String c) {
		if (isStarting(c.substring(0,1)))
			return true;
		return false;
	}
	 
	/*public boolean hasTerminator(String word) {
		for (int i=0; i<terminators.length; i++)
			if (word.indexOf(terminators[i])>=0)
				return true;
		return false;
	}*/
	
}
