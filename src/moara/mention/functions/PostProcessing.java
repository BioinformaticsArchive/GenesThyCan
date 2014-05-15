package moara.mention.functions;

import java.util.StringTokenizer;

import moara.mention.entities.GeneMention;
import moara.util.lexicon.GreekLetter;
import moara.util.lexicon.Stopwords;
import moara.util.lexicon.Symbols;
import moara.util.text.StringUtil;

public class PostProcessing {

	private String[] endComplement = {"dependent","recruiting","responsive",
		"specific", "binding", "stimulated", "deficient"};
	private String[] complement = {"mediated", "resistant", "induced", 
		"transformed", "collaborative", "independent", "repressed",
		"resistant"};
	
	// variables
	// whether the parenthesis open symbol has been found
	private boolean open;
	// the open index in the text
	private int indexOpen;
	// if the gap word must be saved
	private boolean gapWordSave;
	
	private Stopwords stop;
	
	public PostProcessing(Stopwords stop) {
		this.open = false;
		this.indexOpen = -1;
		this.gapWordSave = false;
		this.stop = stop;
	}
	
	public boolean Open() {
		return this.open;
	}
	
	public int IndexOpen() {
		return this.indexOpen;
	}
	
	public boolean GapWordSave() {
		return this.gapWordSave;
	}
	
	public void setGapWordSave(boolean gapWordSave) {
		this.gapWordSave = gapWordSave;
	}
	
	// check the word between two mentions
	// must return the gap word that must be saved
	public String checkGapWord(GeneMention gm, String gapWord, String lastWord, 
			int lastEnd, String text, String textSpace) {
		String wordSave = null;
		if (gapWord.equals("(")) {
			this.open = true;
			this.indexOpen = lastEnd+1;
			this.gapWordSave = true;
			wordSave = gapWord;
		}
		else if (this.open && gapWord.equals(")")) {
			this.open = false;
			this.gapWordSave = true;
			wordSave = gapWord;
		}
		else if (gapWord.equals("and") && isAcceptedTuple(lastWord,gm.Text())) {
			this.gapWordSave = true;
			wordSave = gapWord;
		}
		else if (gapWord.equals(",") && isAcceptedTuple(lastWord,gm.Text())) {
			this.gapWordSave = true;
			wordSave = gapWord;
		}
		else if (gapWord.equals("/") || gapWord.equals("-") || gapWord.equals(":")) {
			this.gapWordSave = true;
			wordSave = gapWord;
		}
		else if (this.open) {
			wordSave = checkTextParenthesis(gm.Start(),lastEnd,text,textSpace);
			if (wordSave!=null)
				this.gapWordSave = true;
		}
		return wordSave;
	}
	
	public String checkTextParenthesis(int start, int lastEnd, String text, String textSpace) {
		if ((start-lastEnd+1)>2) {
			String wordSave = getTextBetweenParenthesis(text,textSpace,lastEnd+1,start);
			//System.err.println(wordSave);
			if (wordSave==null) {
				this.open = false;
			}
			else {
				if (wordSave.endsWith(")"))
					this.open = false;
			}
			return wordSave;
		}
		return null;
	}
	
	private boolean isAcceptedTuple(String word1, String word2) {
		GreekLetter greek = new GreekLetter();
		StringUtil su = new StringUtil();
		//System.err.println(word1 + " " + word2);
		// A and B; 1 and 2; 1,2
		if (word1.length()==1 && word2.length()==1)
			return true;
		// IRF-1 and -2
		if (word1.indexOf("-")>0 && word2.indexOf("-")==0) {
			int index1 = word1.indexOf("-");
			String w1 = word1.substring(index1+1);
			int index2 = word2.indexOf("-");
			String w2 = word2.substring(index2+1);
			if (su.isNumeral(w1) && su.isNumeral(w2))
				return true;
		}
		// Mad1, 2
		if (su.isNumeral(word1.substring(word1.length()-1)) && su.isNumeral(word2))
			return true;
		// alpha and beta
		if (greek.isGreekLetter(word1.toLowerCase()) && greek.isGreekLetter(word2.toLowerCase()))
			return true;
		// D- and E-type
		if (word1.endsWith("-") && word2.indexOf("-")>0)
			return true;
		return false;
	}
	
	private String getTextBetweenParenthesis(String text, String textSpace, int start, int end) {
		StringUtil su = new StringUtil();
		Symbols sym = new Symbols();
		String wordSave = "";
		String textSelected;
		if (text.indexOf(")",start)!=-1 && text.indexOf(")",start)<end)
			textSelected = su.getStringWithSpace(textSpace,start,text.indexOf(")",start)+1);
		else
			textSelected = su.getStringWithSpace(textSpace,start,end);
		StringTokenizer st = new StringTokenizer(textSelected);
		//System.err.println(textSelected);
		while (st.hasMoreElements()) {
			String w = st.nextToken();
			if (stop.isStopword(w)) {
				return null;
			}
			else {
				if (sym.isPunctuation(w) || w.equals(")"))
					wordSave += w;
				else
					wordSave += " " + w;
			}
		}
		return wordSave.trim();
	}
	
	public int checkEndComplement(String word, boolean endComplement) {
		int index = -1;
		String complement;
		if (endComplement)
			complement = getEndComplement(word);
		else
			complement = getComplement(word);
		if (complement!=null)
			index = word.indexOf("-"+complement);
		return index;
	}
	
	private String getComplement(String word) {
		for (int i=0; i<complement.length; i++) {
			if (word.endsWith("-"+complement[i]))
				return complement[i];
		}
		return null;
	}
	
	private String getEndComplement(String word) {
		for (int i=0; i<endComplement.length; i++) {
			if (word.endsWith("-"+endComplement[i]))
				return endComplement[i];
		}
		return null;
	}
	
	public boolean endsWithComplement(String word) {
		for (int i=0; i<complement.length; i++) {
			if (word.endsWith("-"+complement[i]))
				return true;
		}
		return false;
	}
	
	public boolean endsWithEndComplement(String word) {
		for (int i=0; i<endComplement.length; i++) {
			if (word.endsWith("-"+endComplement[i]))
				return true;
		}
		return false;
	}
	
}

