package moara.mention.entities;

import moara.mention.MentionConstant;
import moara.util.lexicon.AminoAcid;
import moara.util.lexicon.GreekLetter;
import moara.util.lexicon.NucleicAcid;
import moara.util.lexicon.Stopwords;
import moara.util.lexicon.Symbols;
import moara.util.text.StringUtil;

public class Feature {
	
	// original word of the case
	private String originalWord;
	// word of the case
	private String word;
	// POS tag of the word
	private String posTag;
	// word before 1 (-1)
	private String wordBefore1;
	// category of the word before 1 (-1)
	private String categoryBefore1;
	// POS tag of the word before 1 (-1)
	private String posTagBefore1;
	// word after 1 (+1)
	private String wordAfter1;
	// POS tag of the word after 1 (+1)
	private String posTagAfter1;
	// word before 2 (-2)
	private String wordBefore2;
	// category of the word before 2 (-2)
	private String categoryBefore2;
	// POS tag of the word before 2 (-2)
	private String posTagBefore2;
	// word after 2 (+2)
	private String wordAfter2;
	// POS tag of the word after 2 (+2)
	private String posTagAfter2;
	// format of the word
	private String format;
	// format of the word before 1 (-1)
	private String formatBefore1;
	// format of the word before 2 (-2)
	private String formatBefore2;
	// format of the word after 1 (+1)
	private String formatAfter1;
	// format of the word after 2 (+2)
	private String formatAfter2;
	// suffix of 3 last letters
	private String suffix3;
	// suffix of 4 last letters
	private String suffix4;
	// prefix of 3 first letters
	private String prefix3;
	// prefix of 4 first letters
	private String prefix4;
		
	// known feature set
	private String[] featureSet;
	
	public Feature() {
		
	}
	 
	public Feature(String[] featureSet) {
		// initiate variables
		this.originalWord = null;
		this.word = null;
		this.wordBefore1 = null;
		this.wordBefore2 = null;
		this.wordAfter1 = null;
		this.wordAfter2 = null;
		this.categoryBefore1 = null;
		this.categoryBefore2 = null;
		this.posTag = null;
		this.posTagBefore1 = null;
		this.posTagBefore2 = null;
		this.posTagAfter1 = null;
		this.posTagAfter2 = null;
		this.format = null;
		this.formatBefore1 = null;
		this.formatBefore2 = null;
		this.formatAfter1 = null;
		this.formatAfter2 = null;
		this.suffix3 = null;
		this.suffix4 = null;
		this.prefix3 = null;
		this.prefix4 = null;
		this.featureSet = featureSet;
	}
	
	public Feature(Feature f) {
		this.featureSet = f.FeatureSet();
		for (int i=0; i<this.featureSet.length; i++) {
			String feature = f.FeatureSet()[i];
			this.setStrValue(feature,f.getStrValue(feature));
		}
	}
	
	public String[] FeatureSet() {
		return this.featureSet;
	}
	
	public String getStrValue(String featureName) {
		if (featureName.equals(MentionConstant.FEATURE_ORIGINAL_WORD))
			return this.originalWord;
		if (featureName.equals(MentionConstant.FEATURE_WORD))
			return this.word;
		if (featureName.equals(MentionConstant.FEATURE_WORD_BEFORE_1))
			return this.wordBefore1;
		if (featureName.equals(MentionConstant.FEATURE_WORD_BEFORE_2))
			return this.wordBefore2;
		if (featureName.equals(MentionConstant.FEATURE_WORD_AFTER_1))
			return this.wordAfter1;
		if (featureName.equals(MentionConstant.FEATURE_WORD_AFTER_2))
			return this.wordAfter2;
		if (featureName.equals(MentionConstant.FEATURE_CATEGORY_BEFORE_1))
			return this.categoryBefore1;
		if (featureName.equals(MentionConstant.FEATURE_CATEGORY_BEFORE_2))
			return this.categoryBefore2;
		if (featureName.equals(MentionConstant.FEATURE_POSTAG))
			return this.posTag;
		if (featureName.equals(MentionConstant.FEATURE_POSTAG_BEFORE_1))
			return this.posTagBefore1;
		if (featureName.equals(MentionConstant.FEATURE_POSTAG_BEFORE_2))
			return this.posTagBefore2;
		if (featureName.equals(MentionConstant.FEATURE_POSTAG_AFTER_1))
			return this.posTagAfter1;
		if (featureName.equals(MentionConstant.FEATURE_POSTAG_AFTER_2))
			return this.posTagAfter2;
		if (featureName.equals(MentionConstant.FEATURE_FORMAT))
			return this.format;
		if (featureName.equals(MentionConstant.FEATURE_FORMAT_BEFORE_1))
			return this.formatBefore1;
		if (featureName.equals(MentionConstant.FEATURE_FORMAT_BEFORE_2))
			return this.formatBefore2;
		if (featureName.equals(MentionConstant.FEATURE_FORMAT_AFTER_1))
			return this.formatAfter1;
		if (featureName.equals(MentionConstant.FEATURE_FORMAT_AFTER_2))
			return this.formatAfter2;
		if (featureName.equals(MentionConstant.FEATURE_SUFFIX3))
			return this.suffix3;
		if (featureName.equals(MentionConstant.FEATURE_SUFFIX4))
			return this.suffix4;
		if (featureName.equals(MentionConstant.FEATURE_PREFIX3))
			return this.prefix3;
		if (featureName.equals(MentionConstant.FEATURE_PREFIX4))
			return this.prefix4;
		return null;
	}
	
	public void setStrValue(String featureName, String value) {
		if (featureName.equals(MentionConstant.FEATURE_ORIGINAL_WORD))
			this.originalWord = value;
		else if (featureName.equals(MentionConstant.FEATURE_WORD))
			this.word = value;
		else if (featureName.equals(MentionConstant.FEATURE_WORD_BEFORE_1))
			this.wordBefore1 = value;
		else if (featureName.equals(MentionConstant.FEATURE_WORD_BEFORE_2))
			this.wordBefore2 = value;
		else if (featureName.equals(MentionConstant.FEATURE_WORD_AFTER_1))
			this.wordAfter1 = value;
		else if (featureName.equals(MentionConstant.FEATURE_WORD_AFTER_2))
			this.wordAfter2 = value;
		else if (featureName.equals(MentionConstant.FEATURE_CATEGORY_BEFORE_1))
			this.categoryBefore1 = value;
		else if (featureName.equals(MentionConstant.FEATURE_CATEGORY_BEFORE_2))
			this.categoryBefore2 = value;
		else if (featureName.equals(MentionConstant.FEATURE_POSTAG))
			this.posTag = value;
		else if (featureName.equals(MentionConstant.FEATURE_POSTAG_BEFORE_1))
			this.posTagBefore1 = value;
		else if (featureName.equals(MentionConstant.FEATURE_POSTAG_BEFORE_2))
			this.posTagBefore2 = value;
		else if (featureName.equals(MentionConstant.FEATURE_POSTAG_AFTER_1))
			this.posTagAfter1 = value;
		else if (featureName.equals(MentionConstant.FEATURE_POSTAG_AFTER_2))
			this.posTagAfter2 = value;
		else if (featureName.equals(MentionConstant.FEATURE_FORMAT))
			this.format = getFormatToken(value);
		else if (featureName.equals(MentionConstant.FEATURE_FORMAT_BEFORE_1))
			this.formatBefore1 = getFormatToken(value);
		else if (featureName.equals(MentionConstant.FEATURE_FORMAT_BEFORE_2))
			this.formatBefore2 = getFormatToken(value);
		else if (featureName.equals(MentionConstant.FEATURE_FORMAT_AFTER_1))
			this.formatAfter1 = getFormatToken(value);
		else if (featureName.equals(MentionConstant.FEATURE_FORMAT_AFTER_2))
			this.formatAfter2 = getFormatToken(value);
		else if (featureName.equals(MentionConstant.FEATURE_SUFFIX3))
			this.suffix3 = value.substring(value.length()-3);
		else if (featureName.equals(MentionConstant.FEATURE_SUFFIX4))
			this.suffix4 = value.substring(value.length()-4);
		else if (featureName.equals(MentionConstant.FEATURE_PREFIX3))
			this.prefix3 = value.substring(0,3);
		else if (featureName.equals(MentionConstant.FEATURE_PREFIX4))
			this.prefix4 = value.substring(0,4);
	}
	
	public void setFormat(String format) {
		this.format = format;
	}
	
	public void setFormatBefore1(String format) {
		this.formatBefore1 = format;
	}
	
	public String getFormatToken(String token) {
		// Codes:
	 	// upper case: A
	 	// lower case: a
	 	// number: 1
	 	// bioword: B
	 	// greek letters: g
	 	// suffixes: $
		// terminators: .
		// stopwords: p
		// chemical compounds: q
		// DNA nucleotides: d
	 	String formatedToken = "";
	 	String stringSuffix = "";
	 	String stringPrefix = "";
	 	String lastCode = "";
	 	GreekLetter greek = new GreekLetter();
	 	Stopwords stop = new Stopwords();
	 	StringUtil su = new StringUtil();
	 	AminoAcid aa = new AminoAcid();
	 	Symbols sym = new Symbols();
	 	//CollectionTerminators colTerm = new CollectionTerminators();
	 	for (int i=0; i<token.length(); i++) {
	 		// identification of stopwords
	 		if (stop.isStopword(token)) {
	 			formatedToken = formatedToken + MentionConstant.SYMBOL_FORMAT_STOPWORDS;
	 			i = token.length();
	 			lastCode = MentionConstant.SYMBOL_FORMAT_STOPWORDS;
	 		}
	 		// identification of symbols/punctuation
	 		else if (sym.isSymbol(token)) {
	 			formatedToken = formatedToken + MentionConstant.SYMBOL_FORMAT_PUNCTUATION;
	 			i = token.length();
	 			lastCode = MentionConstant.SYMBOL_FORMAT_PUNCTUATION;
	 		}
	 		// identification of decimal interval
	 		else if (su.isDecimalInterval(token)) {
	 			formatedToken = "1.1-1.1";
	 			i = token.length();
	 			lastCode = MentionConstant.SYMBOL_FORMAT_NUMBER;
	 		}
	 		// number interval
	 		else if (su.isNumberInterval(token)) {
	 			formatedToken = "1-1";
	 			i = token.length();
	 			lastCode = MentionConstant.SYMBOL_FORMAT_NUMBER;
	 		}
	 		// identification of amino acids
	 		if ((token.length()-i)>=3 && aa.isAminoAcidCode(token.substring(i,i+3))) {
	 			formatedToken = formatedToken + MentionConstant.SYMBOL_FORMAT_AMINO_ACID;
	 			i += 3;
	 			lastCode = MentionConstant.SYMBOL_FORMAT_AMINO_ACID;
	 		}
	 		// identification of nucleic acids sequences
	 		if (formatedToken.startsWith("1'-")) {
	 			int lengthSequence = getNucleicAcid(token.substring(i,token.length()));
	 			if (lengthSequence>0) {
	 				formatedToken = formatedToken + MentionConstant.SYMBOL_FORMAT_NUCLEIC_ACID;
		 			i += lengthSequence;
		 			lastCode = MentionConstant.SYMBOL_FORMAT_NUCLEIC_ACID;
	 			}
	 		}
	 		// identification of Greek letters
	 		int greekLength = greek.getPosGreekLetter(token.substring(i,token.length()));
	 		if (greekLength>0) {
	 			formatedToken = formatedToken + MentionConstant.SYMBOL_FORMAT_GREEK_LETTER;
	 			i += greekLength;
	 			lastCode = MentionConstant.SYMBOL_FORMAT_GREEK_LETTER;
	 		}
	 		// suffix, only for words bigger than 4, composed only by letters
	 		if (token.length()>MentionConstant.MIN_TOKEN_LENGTH_SUFFIX && 
 					i==(token.length()-MentionConstant.MAX_SUFFIX_LENGTH) && 
 					(formatedToken.equals("a") || formatedToken.equals("a-a") || 
 					formatedToken.equals("a-") || formatedToken.equals("A-a") ||
 					formatedToken.equals("Aa")) && 
 					su.hasSuffix(token,MentionConstant.MAX_SUFFIX_LENGTH)) {
	 			String originalFormat = formatedToken;
	 			for (int k=0; k<MentionConstant.array_suffix_length.length; k++) {
	 				int suffixLength = MentionConstant.array_suffix_length[k];
		 			String suffix = token.substring(token.length()-suffixLength,token.length());
			 		String formatSuffix = originalFormat + MentionConstant.SYMBOL_FORMAT_SUFFIX + suffix;
			 		//System.err.println(formatSuffix);
			 		stringSuffix += MentionConstant.SYMBOL_FORMAT_SEPARATION + formatSuffix;
			 	}
	 			i = token.length();
		 		lastCode = MentionConstant.SYMBOL_FORMAT_SUFFIX;
	 		}
	 		// identification of special characters
	 		if (i<token.length()) {
	 			String letter = token.substring(i,i+1);
	    		Character charac = token.charAt(i);
	    		// upper case
		 		if (Character.isUpperCase(charac)) {
					formatedToken = formatedToken + MentionConstant.SYMBOL_FORMAT_UPPER_CASE;
					lastCode = MentionConstant.SYMBOL_FORMAT_UPPER_CASE;
				}
				// numbers
				else if (Character.isDigit(charac)) {
					formatedToken = formatedToken + MentionConstant.SYMBOL_FORMAT_NUMBER;
					lastCode = MentionConstant.SYMBOL_FORMAT_NUMBER;
				}
				// letters
				else if (Character.isLetter(charac) && !lastCode.equals(MentionConstant.SYMBOL_FORMAT_LOWER_CASE)) {
					formatedToken = formatedToken + MentionConstant.SYMBOL_FORMAT_LOWER_CASE;
					lastCode = MentionConstant.SYMBOL_FORMAT_LOWER_CASE;
				}
				// others - symbols
				else if (!Character.isLetter(charac)) {
					formatedToken = formatedToken + letter;
	    			lastCode = letter;
				}
			}	
	 	}
	 	// verify prefixes
	 	//System.err.println(token + " " + formatedToken);
	 	if (token.length()>MentionConstant.MIN_TOKEN_LENGTH_PREFIX && 
 				su.hasPrefix(token,MentionConstant.MAX_PREFIX_LENGTH) &&
	 			(formatedToken.equals("a") || formatedToken.equals("a-a")) ) {
	 		for (int k=0; k<MentionConstant.array_prefix_length.length; k++) {
		 		int prefixLength = MentionConstant.array_prefix_length[k];
		 		String prefix = token.substring(0,prefixLength);
	 			String formatPrefix = prefix + MentionConstant.SYMBOL_FORMAT_PREFIX + MentionConstant.SYMBOL_FORMAT_LOWER_CASE;
	 			//System.err.println(formatPrefix);
	 			stringPrefix += MentionConstant.SYMBOL_FORMAT_SEPARATION + formatPrefix;
	 			//System.err.println(token + " " + formatedToken);
		 	}
	 	}
	 	if (!stringPrefix.equals(""))
	 		formatedToken =  stringPrefix + MentionConstant.SYMBOL_FORMAT_SEPARATION + formatedToken;
	 	if (!stringSuffix.equals(""))
	 		formatedToken =  stringSuffix + MentionConstant.SYMBOL_FORMAT_SEPARATION + formatedToken;
	 	//System.err.println(formatedToken);
	 	return formatedToken;
	}
	
	public String getFormatSuffix(String word) {
		if (word.length()>MentionConstant.MIN_TOKEN_LENGTH_SUFFIX) {
 			word = word.toLowerCase();
			String suffix = word.substring(word.length()-MentionConstant.SUFFIX_LENGTH,word.length());
 			return (MentionConstant.SYMBOL_FORMAT_LOWER_CASE + MentionConstant.SYMBOL_FORMAT_SUFFIX + suffix);
 		}
		return null;
	}
	
	private int getNucleicAcid(String s) {
		NucleicAcid na = new NucleicAcid();
		if (s.indexOf("-")>0) {
			int index = s.indexOf("-");
			String sequence = s.substring(0,index);
			//System.err.println(sequence);
			if (sequence.length()>0 && na.isNucleicAcid(sequence))
				return sequence.length();
		}
		return -1;
	}
	 
}

