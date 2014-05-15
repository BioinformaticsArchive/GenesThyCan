package moara.normalization.entities;

import java.util.StringTokenizer;
import java.util.Arrays;
import java.util.ArrayList;
import moara.normalization.NormalizationConstant;
import moara.util.lexicon.GreekLetter;
import moara.util.text.StringUtil;

public class FeatureSynonym {

	// gene id
	private String geneId;
	// synonym
	private String synonym;
	// length of the gram
	private int lengthGram;
	// bigram
	private ArrayList<String> bigram;
	// trigram
	private ArrayList<String> trigram;
	// prefix
	private String prefix;
	// suffix
	private String suffix;
	// number
	private String number;
	// Greek letter
	private String greekLetter;
	// shape
	private String shape;
	// tokens
	private String tokens;
	
	public FeatureSynonym(String geneId, String synonym) {
		this.geneId = geneId;
		this.synonym = synonym;
		this.prefix = "";
		this.suffix = "";
		this.number = "";
		this.greekLetter = "";
		this.lengthGram = 0;
		this.shape = "";
		this.tokens = "";
		this.bigram = new ArrayList<String>(0);
		this.trigram = new ArrayList<String>(0);
	}
	
	public FeatureSynonym(String geneId, String synonym, String[] features) {
		this(geneId,synonym);
		getFeatures(features);
	}
	
	public String GeneId() {
		return this.geneId;
	}
	
	public String Synonym() {
		return this.synonym;
	}
	
	public ArrayList<String> Bigram() {
		return this.bigram;
	}
	
	public ArrayList<String> Trigram() {
		return this.trigram;
	}
	
	public String Prefix() {
		return this.prefix;
	}
	
	public String Suffix() {
		return this.suffix;
	}
	
	public String Number() {
		return this.number;
	}
	
	public String GreekLetter() {
		return this.greekLetter;
	}
	
	public String Shape() {
		return this.shape;
	}
	
	public String Tokens() {
		return this.tokens;
	}
	
	public int LengthGram() {
		return this.lengthGram;
	}
	
	public void setBigram(ArrayList<String> grams) {
		this.bigram = grams;
	}
	
	public void setTrigram(ArrayList<String> grams) {
		this.trigram = grams;
	}
	
	public void setPrefix(String p) {
		this.prefix = p;
	}
	
	public void setSuffix(String s) {
		this.suffix = s;
	}
	
	public void setNumber(String n) {
		this.number = n;
	}
	
	public void setGreekLetter(String g) {
		this.greekLetter = g;
	}
	
	public void setShape(String s) {
		this.shape = s;
	}
	
	public void setTokens(String t) {
		this.tokens = t;
	}
	
	public void setLengthGram(int n) {
		this.lengthGram = n;
	}
	
	private void getFeatures(String[] features) {
		StringUtil su = new StringUtil();
		for (int i=0; i<features.length; i++) {
			String f = features[i];
			String s = quitSymbols(this.synonym);
			if (f==null)
				break;
			if (f.equals(NormalizationConstant.FEATURE_BIGRAM))
				this.bigram = getGrams(s,2);
			else if (f.equals(NormalizationConstant.FEATURE_TRIGRAM))
				this.trigram = getGrams(s,3);
			if (f.equals(NormalizationConstant.FEATURE_PREFIX3)) {
				if (s.length()>=3)
					this.prefix = s.substring(0,3);
				else
					this.prefix = s;
			}
			if (f.equals(NormalizationConstant.FEATURE_SUFFIX3)) {
				if (s.length()>=3)
					this.suffix = s.substring(s.length()-3,s.length());
				else
					this.suffix = s;
			}
			if (f.equals(NormalizationConstant.FEATURE_NUMBER))
				this.number = getNumber();
			if (f.equals(NormalizationConstant.FEATURE_GREEK_LETTER))
				this.greekLetter = getGreekLetter();
			if (f.equals(NormalizationConstant.FEATURE_SHAPE))
				this.shape = su.getShape(s);
			if (f.equals(NormalizationConstant.FEATURE_TOKEN))
				this.tokens = getTokens();
		}
	}
	
	private String quitSymbols(String synonym) {
		// replace parenthesis and hyphens
		String s = synonym.replace("(","");
		s = s.replace(")","");
		s = s.replace("-","");
		return s;
	}
	
	private ArrayList<String> getGrams(String s, int n) {
		this.lengthGram = n;
		String s1 = quitSymbols(s);
		String[] grams = new String[s.length()];;
		int index = 0;
		// separate tokens
		StringTokenizer tokens = new StringTokenizer(s1);
		while (tokens.hasMoreTokens()) {
			String s2 = tokens.nextToken();
			if (s2.length()<=n) {
				grams[index] = s2;
				index++;
			}
			else {
				for (int i=0; i<=(s2.length()-n); i++) {
					grams[index] = s2.substring(i,i+n);
					index++;
				}
			}
		}
		while (index<grams.length) {
			grams[index] = "";
			index++;
		}
		Arrays.sort(grams);
		//return (ArrayList<String>)Arrays.asList(grams);
		ArrayList<String> array = new ArrayList<String>(grams.length);
		for (int i=0; i<grams.length; i++) {
			if (grams[i].length()!=0)
				array.add(grams[i]);
		}
		return array;
	}
	
	/*private ArrayList<String> getGrams(int n) {
		this.lengthGram = n;
		StringTokenizer tokens = new StringTokenizer(this.synonym);
		String[] grams;
		if (this.synonym.length()<=n)
			grams = new String[tokens.countTokens()];
		else
			grams = new String[this.synonym.length()-n+1];
		for (int i=0; i<grams.length; i++)
			grams[i] = "";
		int index = 0;
		while (tokens.hasMoreTokens()) {
			String token = tokens.nextToken();
			if (token.length()<n)
				grams[index++] = token;
			else {
				int start = 0;
				int end = n;
				while (end<=token.length()) {
					grams[index++] = token.substring(start,end);
					start++;
					end++;
				}
			}
		}
		Arrays.sort(grams);
		//return (ArrayList<String>)Arrays.asList(grams);
		ArrayList<String> array = new ArrayList<String>(grams.length);
		for (int i=0; i<grams.length; i++)
			array.add(grams[i]);
		return array;
	}*/
	
	private String getNumber() {
		StringUtil su = new StringUtil();
		String n = "";
		boolean numberFound = false;
		for (int i=0; i<this.synonym.length(); i++) {
			char c = this.synonym.charAt(i);
			if (!su.isNumeral(c) && numberFound)
				break;
			else if (su.isNumeral(c) && !numberFound)
				numberFound = true;
			if (numberFound)
				n += c;
		}
		return n;
	}
	
	private String getGreekLetter() {
		GreekLetter greek = new GreekLetter();
		return greek.getGreekLetter(this.synonym);
	}
	
	private String getTokens() {
		StringTokenizer tks = new StringTokenizer(this.synonym," -/()[],");
		String[] tokensArr = new String[tks.countTokens()];
		int index = 0; 
		while (tks.hasMoreTokens()) {
			tokensArr[index] = tks.nextToken();
			index++;
		}
		Arrays.sort(tokensArr);
		for (int i=0; i<tokensArr.length; i++)
			tokens += tokensArr[i] + " ";
		return tokens.trim();
	}
	
}

