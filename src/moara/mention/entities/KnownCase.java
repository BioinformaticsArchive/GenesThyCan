package moara.mention.entities;

import moara.mention.MentionConstant;
import moara.util.text.StringUtil;
import java.util.ArrayList;
import java.util.Vector;
import java.util.StringTokenizer;

public class KnownCase extends Case {

	// original word
	private String originalWord;
	// type known case
	private String typeKnownCase;
	
	public KnownCase(Feature f, String category) {
		this.typeCase = MentionConstant.CASE_KNOWN;
		this.feature = f;
		this.category = category;
		this.originalWord = f.getStrValue(MentionConstant.FEATURE_WORD);
		this.typeKnownCase = MentionConstant.KNOWN_NORMAL;
	}
	
	public KnownCase(Feature f, String category, int start, int end) {
		this(f,category);
		this.start = start;
		this.end = end;
	}
	
	public String OriginalWord() {
		return this.originalWord;
	}
	
	public String TypeKnownCase() {
		return this.typeKnownCase;
	}
	
	public void setTypeKnownCase(String type) {
		this.typeKnownCase = type;
	}
	
	public ArrayList<String> getKnownWords() {
		ArrayList<String> words = new ArrayList<String>();
		words.add(this.originalWord);
		if (this.originalWord.toLowerCase().equals(this.originalWord))
			words.add(this.originalWord.toLowerCase());
		return words;
	}
	
	/*public Vector<KnownCase> getKnownWords(KnownCase kc) {
		StringUtil su = new StringUtil();
		Vector<KnownCase> cases = new Vector<KnownCase>();
		String s = kc.OriginalWord();
		int index = 0;
		// add original case
		Feature f1 = new Feature(kc.Feature());
		KnownCase kc1 = new KnownCase(f1,null,kc.Start(),kc.End());
		kc1.setTryCode(kc.TryCode());
		kc1.setTypeWordCase(MentionConstant.WORD_CASE_COMPLETE);
		cases.add(index,kc1);
		// add lower case word
		if (su.hasLowerCaseLetter(s) && !s.equals(s.toLowerCase())) {
			Feature f2 = new Feature(kc.Feature());
			KnownCase kc2 = new KnownCase(f2,null,kc.Start(),kc.End());
			kc2.setTryCode(kc.TryCode());
			kc2.setTypeWordCase(MentionConstant.WORD_CASE_COMPLETE);
			f2.setStrValue(MentionConstant.FEATURE_WORD,s.toLowerCase());
			index++;
			cases.add(index,kc2);
		}
		// add family word
		String family = getFamilyName(s);
		if (family!=null) {
			Feature f2 = new Feature(kc.Feature());
			KnownCase kc2 = new KnownCase(f2,null,kc.Start(),kc.End());
			kc2.setTryCode(kc.TryCode());
			kc2.setTypeWordCase(MentionConstant.WORD_CASE_COMPLETE);
			kc2.setTypeKnownCase(MentionConstant.KNOWN_FAMILY);
			f2.setStrValue(MentionConstant.FEATURE_WORD,family);
			index++;
			cases.add(index,kc2);
		}
		cases.trimToSize();
		return cases;
	}*/
	
	public String getFamilyName(String word) {
		StringUtil su = new StringUtil();
		// the word has a "-"
		if (word.indexOf("-")>1) {
			int index = word.indexOf("-");
			// the "-" is not the last character
			if (index<(word.length()-1)) {
				String beforeHyphen = word.substring(0,index);
				String afterHyphen = word.substring(index+1,word.length());
				// numeral after "-", not numeral before "-"
				if (su.isNumeral(afterHyphen) && !su.isNumeral(beforeHyphen))
					return (word.substring(0,index+1) + MentionConstant.SYMBOL_FORMAT_NUMBER); 
			}
		}
		return null;
	}
	
	public Vector<KnownCase> getPartsKnownWords(KnownCase kc, String reading) {
		Vector<KnownCase> cases = new Vector<KnownCase>();
		String s = kc.Feature().getStrValue(MentionConstant.FEATURE_WORD);
		int index = 0;
		// add parts of word, if any
		if (getSymbolString(s)!=null) {
			String symbol = getSymbolString(s);
			StringTokenizer st = new StringTokenizer(s,symbol,true);
			int startWord = kc.Start();
			int endWord = 0;
			while (st.hasMoreTokens()) {
				String part = st.nextToken();
				//System.err.println(part + " " + kc.Feature().getStrValue(Constant.FEATURE_WORD));
				endWord = startWord + part.length()-1;
				Feature f3 = new Feature(kc.Feature());
				KnownCase kc3 = new KnownCase(f3,null,startWord,endWord);
				//System.err.println(part + " " + startWord + " " + endWord);
				kc3.setTryCode(kc.TryCode());
				kc3.setTypeWordCase(MentionConstant.WORD_CASE_PARTIAL);
				f3.setStrValue(MentionConstant.FEATURE_WORD,part);
				if (getSymbolString(part)!=null) {
					Vector<KnownCase> cases2 = getPartsKnownWords(kc3,MentionConstant.READING_FORWARD);
					for (int i=0; i<cases2.size(); i++) {
						cases.add(index,cases2.elementAt(i));
						index++;
					}
				}
				else {
					cases.add(index,kc3);
					index++;
				}
				//System.err.println(part + " " + startWord + " " + endWord);
				startWord = endWord+1;
			}
		}
		cases.trimToSize();
		// invert order of elements, if backwards
		if (cases.size()>0 && reading.equals(MentionConstant.READING_BACKWARD)) {
			Vector<KnownCase> casesB = new Vector<KnownCase>(cases.size());
			for (int i=cases.size()-1; i>=0; i--) {
				casesB.add(cases.elementAt(i));
			}
			casesB.trimToSize();
			return casesB;
		}
		else
			return cases;
	}
	
	private String getSymbolString(String s) {
		StringUtil su = new StringUtil();
		String symbol = null;
		if (s.equals("/") || s.equals("-") || s.equals("+") || s.equals("*") || 
				s.equals("'") || s.equals("x") || s.equals(".") || s.equals("="))
			return null;
		if (s.indexOf("/")>-1)
			symbol = "/";
		else if (s.indexOf("-")>-1)
			symbol = "-";
		else if (s.indexOf("+")>-1)
			symbol = "+";
		else if (s.indexOf("*")>-1)
			symbol = "*";
		else if (s.indexOf("'")>-1)
			symbol = "'";
		else if (s.indexOf("=")>-1)
			symbol = "=";
		else if (s.indexOf(".")>-1 && s.indexOf(".")!=(s.length()-1))
			symbol = ".";
		else if (s.indexOf("x")>0 && s.indexOf("x")<(s.length()-2)) {
			int index = s.indexOf("x");
			if (su.isNumeral(s.substring(index-1,index)) && su.isNumeral(s.substring(index+1,index+2))) {
				symbol = "x";
			}
		}
		return symbol;
	}
	 
}
