package moara.util.lexicon;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Hashtable;
import java.util.StringTokenizer;

import moara.util.EnvironmentVariable;

public class Stopwords {
	
	private static Hashtable<Integer,String> base;
	
	public Stopwords() {
	    if (base==null) 
	    	loadStopwords();
	}
	 
	public boolean isStopword(String c) {
	 	String c1 = c.toLowerCase();
	 	if (base.containsKey(c1.hashCode()))
	         return true;
	     return false;
	 }
	
	public boolean hasOnlyStopwords(String c) {
		StringTokenizer tokens = new StringTokenizer(c); 
		while (tokens.hasMoreTokens()) {
			String token = tokens.nextToken();
			String c1 = token.toLowerCase();
		 	if (!base.containsKey(c1.hashCode()))
		         return false;
		}
		return true;
	}
	 
	 public void loadStopwords() {
		try {
			base = new Hashtable<Integer,String>(320);
			RandomAccessFile r = new RandomAccessFile(new File(EnvironmentVariable.getMoaraHome()+"resources/stopwords.txt"),"r");
			// skip first line
			String line = r.readLine();
			// get stopwords
			line = r.readLine();
			while (line!=null) {
				base.put(line.hashCode(),line);
				line = r.readLine();
			}
			r.close();
		}
		catch(IOException ex) {
			System.err.println(ex);
		}
	 }
	 
	 public static void main (String[] args) {
		 Stopwords app = new Stopwords();
		 app.loadStopwords();
		 System.out.println(app.base.size());
	 }
	 
}
