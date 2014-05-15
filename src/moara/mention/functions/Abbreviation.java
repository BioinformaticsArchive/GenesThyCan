package moara.mention.functions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import moara.corpora.biocreative.dbs.DBDocumentGene;
import moara.mention.MentionConstant;
import moara.mention.dbs.DBKnownCase;
import moara.mention.dbs.DBUnknownCase;
import moara.mention.entities.Feature;
import moara.mention.entities.GeneMention;
import moara.mention.entities.KnownCase;
import moara.mention.entities.Mention;
import moara.mention.entities.TypeCase;
import moara.mention.entities.UnknownCase;
import moara.util.Constant;
import moara.util.corpora.Token;
import moara.util.lexicon.Stopwords;
import moara.util.lexicon.Symbols;
import moara.util.text.StringUtil;

public class Abbreviation {

	private String type;
	private DBDocumentGene dbDocGene;
	private String[] knownFeatures;
	private String[] unknownFeatures;
	private TypeCase tc;
	private DBKnownCase dbKnown;
	private DBUnknownCase dbUnknown;
	private Stopwords stop;
	
	public Abbreviation(String type, Stopwords stop) {
		this.type = type;
		this.dbDocGene = new DBDocumentGene("moara_mention");
		this.stop = stop;
	}
	
	public void setFeatures(String[] knownFeatures, String[] unknownFeatures) {
		this.knownFeatures = knownFeatures;
		this.unknownFeatures = unknownFeatures;
	}
	
	public void setTypeCase(TypeCase tc) {
		this.tc = tc;
	}
	
	public void setKnownUnknown(DBKnownCase dbKnown, DBUnknownCase dbUnknown) {
		this.dbKnown = dbKnown;
		this.dbUnknown = dbUnknown;
	}
	
	public Vector<Mention> verifyAbbreviations(String id, Vector<Token> words, String typeTraining,
			int dataSet) {
		Vector<Mention> mentions = new Vector<Mention>();
		try {
			StringUtil su = new StringUtil();
			/*if (typeTraining.equals(Constant.DATA_NORMALIZATION))
				gms = dbDocGene.getOffsetDocument(Constant.DATA_NORMALIZATION,id,
					Constant.MENTION_COMPLETE,Constant.DATA_PREDICTION);
			else if (typeTraining.equals(Constant.DATA_TRAINING) && dataSet!=Constant.ALL_SET)
	   			gms = dbDocGene.getOffsetDocument(Constant.DATA_TRAINING,id,Constant.MENTION_COMPLETE,
	   				Constant.DATA_PREDICTION);
	   		else*/
			// get gene mentions
			ArrayList<GeneMention> gms = this.dbDocGene.getOffsetDocument(this.type,id,
	   			MentionConstant.MENTION_COMPLETE,Constant.DATA_PREDICTION);
	   		String lastWord = new String();
	   		String abbreviation = new String();
	   		int startAbb = -1;
	   		boolean open = false;
	   		for (int i=(gms.size()-1); i>=0; i--) {
	   			GeneMention gm = (GeneMention)gms.get(i);
	   			// save abbreviation
	   			if (open && !gm.Text().equals("(")) {
	   				abbreviation = gm.Text() + abbreviation;
	   				startAbb = gm.Start();
	   			}
	   			// verify words before an abbreviation
	   			if (lastWord.equals(")")) {
	   				abbreviation = gm.Text();
	   				startAbb = gm.Start();
	   				open = true;
	   			}
	   			// verify abbreviation
	   			if (open && gm.Text().equals("(")) {
	   				if (su.isAbbreviation(abbreviation)) {
	   					//System.err.println(abbreviation + " " + gm.Start());
		   				// check the long form of the abbreviation
		   				Vector<Mention> gms1 = checkAbbreviation(id,words,abbreviation,startAbb);
		   				for (int j=0; j<gms1.size(); j++) {
		   					mentions.add(gms1.elementAt(j));
		   				}
		   				// check words after abbreviation
		   				Vector<Mention> gms2 = checkComplementAbbreviation(id,words,startAbb+abbreviation.length()+1);
		   				for (int j=0; j<gms2.size(); j++) {
		   					mentions.add(gms2.elementAt(j));
		   				}
	   				}
	   				open = false;
	   			}
	   			lastWord = gm.Text();
	   		}
		}
		catch(SQLException e) { 
        	System.err.println(e);
        }
		return mentions;
	}
	 
	private Vector<Mention> checkAbbreviation(String id, Vector<Token> words, 
			String abbreviation, int start) {
		Vector<Mention> gms = new Vector<Mention>();
		boolean reached = false;
		boolean wordSaved = false;
		// array for the words that comes before the abbreviation, including "(" 
		String[] longForm = new String[abbreviation.length()+1];
		int index = 0;
		for (int i=0; i<words.size(); i++) {
			String word = ((Token)words.elementAt(i)).TokenText();
			// abbreviation found
			if (index==start)
				reached = true;
			// check words before abbreviation
			if (reached) {
				// index (longForm.length) does not exist
				// index (longForm.length-1) is "("
				int startMention = index-longForm[longForm.length-1].length();
				int j = longForm.length-2;
				int abbIndex = abbreviation.length()-1;
				int coincidences = 0;
				while (j>=0 && abbIndex>=0 && longForm[j]!=null) {
					String w = new String();
					String originalWord = longForm[j];
					w = longForm[j].toLowerCase();
					char letter = ' ';
					if (abbIndex>=0)
						letter = (abbreviation.toLowerCase()).charAt(abbIndex);
					//System.err.println("***************_" + originalWord + "_" + letter);
					boolean saveWord = false;
					// there as still letters to check
					if (abbIndex>=0) {
						// letter of abbreviation corresponds to initial of word  
						if (w.length()>0 && w.charAt(0)==letter) {
							saveWord = true;
							// next letter of abbreviation
							abbIndex--;
							// next word of long form
							startMention -= w.length();
							j--;
							// add coincidences
							coincidences++;
							//System.err.println(w + " = " + letter + " " + abbIndex);
						}
						// letter of abbreviation corresponds to a letter inside the word
						else if (w.indexOf(letter)>0) {
							// next letter of abbreviation
							abbIndex--;
							// keep the same word of long form
							// add coincidences
							coincidences++;
							//System.err.println(w + " has " + letter + " " + abbIndex);
						}
						// letter of the abbreviation is an hyphen
						else if (letter=='-' && isConnectingWord(w)) {
							saveWord = true;
							// next letter of abbreviation
							abbIndex--;
							// next word of long form
							startMention -= w.length();
							j--;
							// add coincidences
							coincidences++;
							//System.err.println(w + " % " + letter + " " + abbIndex);
						}
						else if (letter=='-' || letter=='/') {
							// next letter of abbreviation
							abbIndex--;
							//System.err.println("pass " + letter + " " + abbIndex);
						}
						// word do not corresponds to mention, exit check or continues
						else
							if (wordSaved) {
								// next word of long form
								startMention -= w.length();
								j--;
								// keep letter of abbreviation
								// save word if more than 2 coincidences
								if (coincidences>2)
									saveWord = true;
							}
							else
								return gms;
					}
					// no more letters to check
					/*else {
						startMention -= w.length();
						// delete the word mention, if it exists
						System.err.println("delete " + startMention + " " + originalWord);
						deleteGeneMention(id,startMention,startMention+w.length()-1);
						// next word of long form
						j--;
					}*/
					if (saveWord && !stop.isStopword(originalWord)) {
						//System.err.println("longform " + originalWord);
						Mention gm = new Mention(originalWord,startMention,
								startMention+w.length()-1,MentionConstant.OPERATION_MENTION_INSERT);
						gms.add(gm);
						/*saveGeneMention(id,originalWord,tc.getCategoryMiddleGeneMention(),
							startMention,startMention+w.length()-1,0,Constant.CASE_ABBREVIATION,
							Constant.TRY_EXACT,Constant.CASE_ABBREVIATION,false,null);*/
						wordSaved = true;
					}
				}
				// end of words to check
				if (j<0)
					return gms;
				// end of abbreviation
				if (abbIndex<0)
					return gms;
			}
			// update long for array
			for (int j=0; j<longForm.length-1; j++) {
				longForm[j] = longForm[j+1];
				//System.err.print(longForm[j] + " ");
			}
			longForm[longForm.length-1] = word;
			//System.err.println(longForm[longForm.length-1] + " " + index);
			index += word.length();
		}
		return gms;
	}
	
	private boolean isConnectingWord(String s) {
		if (s.equals("type") || s.equals("and"))
			return true;
		return false;
	}
	
	private Vector<Mention> checkComplementAbbreviation(String id, Vector<Token> words, int start) {
		Vector<Mention> gms = new Vector<Mention>();
		int index = 0;
		for (int i=0; i<words.size(); i++) {
			String word = ((Token)words.elementAt(i)).TokenText();
			if (start==index) {
				//System.err.println(word);
				if (isAcceptedComplementAbbreviation(word)) {
					//System.err.println("complement " + word);
					Mention gm = new Mention(word,start,start+word.length()-1,
						MentionConstant.OPERATION_MENTION_INSERT);
					gms.add(gm);
					/*saveGeneMention(id,word,tc.getCategoryMiddleGeneMention(),
						start,start+word.length()-1,0,Constant.CASE_ABBREVIATION,
						Constant.TRY_EXACT,Constant.CASE_ABBREVIATION,true,words);*/
				}
				else {
					//System.err.println("delete");
					Mention gm = new Mention("",start,start+word.length()-1,
						MentionConstant.OPERATION_MENTION_DELETE);
					gms.add(gm);
					//deleteGeneMention(id,start,start+word.length()-1,true);
				}
			}
			index += word.length();
		}
		return gms;
	}
	
	private boolean isAcceptedComplementAbbreviation(String s) {
		try {
			StringUtil su = new StringUtil();
			Symbols sym = new Symbols();
			// (CPT) II
			if (su.isRomanNumeral(s))
				return true;
			// no symbols/puntuations
			if (sym.isEnding(s) || sym.isStarting(s) || sym.isPunctuation(s))
				return false;
			// (121-476)p
			if (s.length()==1)
				return true;
			// (IL)-10
			/*if (s.startsWith("-"))
				return true;*/
			// look for case base
			Feature fk = new Feature(knownFeatures);
			fk.setStrValue(MentionConstant.FEATURE_WORD,s);
			fk.setStrValue(MentionConstant.FEATURE_CATEGORY_BEFORE_1,tc.getCategoryGeneMention());
			KnownCase kc = new KnownCase(fk,null);
			kc = dbKnown.getMaxFreqKnownCase(kc,MentionConstant.READING_FORWARD);
			if (kc.Category()!=null && tc.isGeneMentionCategory(kc.Category())) {
				return true;
			}
			else if (kc.Category()==null) {
				Feature fu = new Feature(unknownFeatures);
				fu.setStrValue(MentionConstant.FEATURE_WORD,s);
				fu.setStrValue(MentionConstant.FEATURE_CATEGORY_BEFORE_1,tc.getCategoryGeneMention());
				String format = fu.getFormatToken(s);
				StringTokenizer st = new StringTokenizer(format,MentionConstant.SYMBOL_FORMAT_SEPARATION);
				while (st.hasMoreTokens()) {
					String format1 = st.nextToken();
					fu.setFormat(format1);
					UnknownCase uc = new UnknownCase(fu,null);
					uc = dbUnknown.getMaxFreqUnknownCase(uc,MentionConstant.READING_FORWARD);
					if (uc.Category()!=null && tc.isGeneMentionCategory(uc.Category())) {
						return true;
					}
				}
			}
		}
		catch(SQLException e) { 
        	System.err.println(e);
        }
		return false;
	}
}

