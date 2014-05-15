package moara.util.lexicon;

import java.util.Hashtable;

public class GreekLetter {

	 private Hashtable<Integer,String> base;
	 private String letters[] = {"alpha","beta","gamma","delta","epsilon","zeta","theta","kappa",
	 	"lambda","omicron","sigma","upsilon","omega"};
	 /*private String letters[] = {"alpha","beta","gamma","delta","epsilon","zeta","eta","theta","theta",
			"iota","kappa","lambda","mu","nu","xi","omicron","pi","rho","sigma","tau","upsilon","phi",
			"chi","psi","omega"};*/
	 
	 public GreekLetter() {
	     this.base = new Hashtable<Integer,String>();
	     for (int i=0; i<letters.length; i++) {
	     	base.put(letters[i].hashCode(),letters[i]);
	     }
	 }
	 
	 public boolean isGreekLetter(String c) {
	 	String c1 = c.toLowerCase();
	 	if (base.containsKey(c1.hashCode()))
	         return true;
	     return false;
	 }
	 
	 public int getPosGreekLetter(String c) {
	 	String c1 = c.toLowerCase();
	 	for (int i=0; i<letters.length; i++) {
	 		int letterLength = letters[i].length();
	 		if (c1.length()>=letterLength)
	    		if (c1.substring(0,letterLength).equals(letters[i]))
	    			return letterLength;
	 	}
	 	return -1;
	 }
	 
	 public boolean hasGreekLetter(String s) {
		if (!getGreekLetter(s).equals(""))
			return true;
		return false;
	}
	 
	 public String getGreekLetter(String c) {
	 	String c1 = c.toLowerCase();
	 	for (int i=0; i<letters.length; i++) {
	 		if (c1.indexOf(letters[i])>=0)
	    		return letters[i];
	 	}
	 	return "";
	 }

}


