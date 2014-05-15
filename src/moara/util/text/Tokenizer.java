package moara.util.text;

import java.util.StringTokenizer;
import java.util.Vector;
import java.util.ArrayList;

import moara.util.corpora.Token;
import moara.util.lexicon.Stopwords;
import moara.util.lexicon.Symbols;
import moara.util.lexicon.Units;
import moara.wrapper.porter.Stemmer;

public class Tokenizer {

	private Symbols sym;
	private ArrayList<String> tokens;
	private String tokenized;
	private boolean ignoreSymbols;
	private boolean ignoreStopwords;
	private boolean ignoreNumerals;
	private boolean doStemming;
	private int minNumLetter;
	private int maxNumLetter;
	private boolean ignoreUnits;
		
	public Tokenizer() {
		this.sym = new Symbols();
		this.tokens =  new ArrayList<String>();
		this.tokenized = "";
		this.ignoreSymbols = false;
		this.ignoreStopwords = false;
		this.doStemming = false;
		this.ignoreNumerals = false;
		this.minNumLetter = 0;
		this.maxNumLetter = 1000;
		this.ignoreUnits = false;
	}
	
	public ArrayList<String> getTokens() {
		return tokens;
	}
	
	public String getTokenizedText() {
		return this.tokenized;
	}
	
	public void setIgnoreSymbols(boolean ignoreSymbols) {
		this.ignoreSymbols = ignoreSymbols;
	}
	
	public void setIgnoreStopwords(boolean ignoreStopwords) {
		this.ignoreStopwords = ignoreStopwords;
	}
	
	public void setIgnoreNumerals(boolean ignoreNumerals) {
		this.ignoreNumerals = ignoreNumerals;
	}
	
	public void setDoStemming(boolean doStemming) {
		this.doStemming = doStemming;
	}
	
	public void setMinNumLetter(int minNumLetter) {
		this.minNumLetter = minNumLetter;
	}
	
	public void setMaxNumLetter(int maxNumLetter) {
		this.maxNumLetter = maxNumLetter;
	}
	
	public void setIgnoreUnits(boolean ignoreUnits) {
		this.ignoreUnits = ignoreUnits;
	}
	
	public String tokenize(String original) {
		this.tokens =  new ArrayList<String>();
		this.tokenized = "";
		StringTokenizer tokens;
		if (this.ignoreSymbols)
			tokens = new StringTokenizer(original," \n"+this.sym.getStringSymbols(),false);
		else
			tokens = new StringTokenizer(original," \n"+this.sym.getStringSymbols(),true);
		while (tokens.hasMoreTokens()) {
			String token = tokens.nextToken().trim();
			// check endings and startings
			if (token.length()>0)
				this.tokenized += checkStartingEndings(token) + " ";
		}
		this.tokens.trimToSize();
		return this.tokenized.trim();
	}
	
	private boolean checkToken(String token) {
		// stopwords
		if (this.ignoreStopwords) {
			Stopwords stop = new Stopwords();
			if (stop.isStopword(token))
				return false;
		}
		// numerals
		if (this.ignoreNumerals) {
			StringUtil su = new StringUtil();
			if (!su.hasLetter(token) || su.isOrdinalNumber(token) || 
					su.isRomanNumeral(token))
				return false;
		}
		// minimum number of letters
		if (token.length()<this.minNumLetter)
			return false;
		// maximum number of letters
		if (token.length()>this.maxNumLetter)
			return false;
		// units
		if (this.ignoreUnits) {
			Units u = new Units();
			if (u.hasUnit(token))
				return false;
		}
		// ignore html symbols
		if (token.equals("&amp") || token.equals("&apos") || token.equals("&gt") ||
			token.equals("&lt") && token.equals("&quot"))
			return false;
		return true;
	}
	
	private String editToken(String token) {
		// replace letters
		token.replace("ô","o");
		if (this.doStemming)
			token = executeStemming(token);
		// final hyphen
		if (this.ignoreSymbols) {
			while (token.substring(token.length()-1).equals("-") ||
					token.substring(token.length()-1).equals("(")) {
				token = token.substring(0,token.length()-1);
			}
		}
		return token;
	}
	
	private String checkStartingEndings(String original) {
		String tknz = "";
		String token = original;
		String start = "";
		String end = "";
		while (sym.hasStarting(token) || sym.hasEndingPunctuation(token)) {
			if (sym.hasStarting(token)) {
				String symbol = token.substring(0,1);
				start += symbol + " "; 
				token = token.substring(1);
			}
			if (token.length()>0 && sym.hasEndingPunctuation(token)) {
				String symbol = token.substring(token.length()-1);
				end += " " + symbol; 
				token = token.substring(0,token.length()-1);
			}
			if (token.length()==0)
				break;
		}
		// start
		if (!this.ignoreSymbols && start.length()>0) {
			tknz += start.trim();
			StringTokenizer starts = new StringTokenizer(start.trim());
			while (starts.hasMoreTokens())
				this.tokens.add(starts.nextToken());
		}
		// token
		if (checkToken(token)) {
			token = editToken(token);
			tknz += " " + token;
			this.tokens.add(token);
		}
		// end
		if (!this.ignoreSymbols && end.length()>0) {
			tknz += " " + end.trim();
			StringTokenizer ends = new StringTokenizer(end.trim());
			while (ends.hasMoreTokens())
				this.tokens.add(ends.nextToken());
		}
		return tknz.trim();
	}
	
	private String executeStemming(String word) {
		Stemmer s = new Stemmer();
		for (int i=0; i<word.length(); i++) {
			char c = word.charAt(i);
			s.add(c);
		}
		s.stem();
		return s.toString();
	}
	
	public String concatenateWords(Vector<Token> words) {
		String text = "";
		for (int i=0; i<words.size(); i++) {
			Token token = (Token)words.elementAt(i);
			text += token.TokenText();
		}
		return text;
	}
	
	public String concatenateWordsSpace(Vector<Token> words) {
		String text = "";
		for (int i=0; i<words.size(); i++) {
			Token token = (Token)words.elementAt(i);
			text += " " + token.TokenText();
		}
		return text;
	}
	
}
