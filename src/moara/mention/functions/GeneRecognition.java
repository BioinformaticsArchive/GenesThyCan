package moara.mention.functions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import moara.corpora.biocreative.dbs.DBDocumentGene;
import moara.corpora.biocreative.dbs.DBDocumentSet;
import moara.mention.MentionConstant;
import moara.mention.MentionUtil;
import moara.mention.dbs.DBKnownCase;
import moara.mention.dbs.DBUnknownCase;
import moara.mention.entities.Case;
import moara.mention.entities.Feature;
import moara.mention.entities.GeneMention;
import moara.mention.entities.KnownCase;
import moara.mention.entities.Mention;
import moara.mention.entities.TypeCase;
import moara.mention.entities.UnknownCase;
import moara.mention.entities.VariablesProcessing;
import moara.util.Constant;
import moara.util.corpora.Token;
import moara.util.lexicon.GreekLetter;
import moara.util.lexicon.Stopwords;
import moara.util.lexicon.Symbols;
import moara.util.text.StringUtil;
import moara.util.text.Tokenizer;

public class GeneRecognition {

	/**
	 * Implements a gene and protein tagger that extracts mentions of these entities 
	 * from a text. The text must be provided in free text, as showed in the example 
	 * below:
	 * 
	 * Abstract from Pubmed: id 8076837
	 * "A gene (pkt1) was isolated from the filamentous fungus Trichoderma reesei, 
	 * which exhibits high homology with the yeast YPK1 and YKR2 (YPK2) genes. 
	 * It contains a 2123-bp ORF that is interrupted by two introns, and it encodes 
	 * a 662-amino-acid protein with a calculated M(r) of 72,820. During active growth, 
	 * pkt1 is expressed as two mRNAs of 3.1 and 2.8 kb which differ in the 3' 
	 * untranslated region due to the use of two different polyadenylation sites."
	 * 
	 * @author			Mariana Neves
	 * @version			1.1.0 
	 *  
	 */
	
	// database connection
	private DBDocumentSet dbDocSet;
	private DBDocumentGene dbDocGene;
	private DBKnownCase dbKnown;
	private DBUnknownCase dbUnknown;
	// other variables
	private String[] knownFeatures = MentionConstant.KNOWN_FEATURES;
	private String[] unknownFeatures = MentionConstant.UNKNOWN_FEATURES;
	private TypeCase tc;
	private int dataSet;
	private String type;
	private String docId;
	private String originalText;
	private String typeCategory;
	
	private StringUtil su;
	private Stopwords stop;
	private Abbreviation abb;
	private PostProcessing post;
	
	public GeneRecognition() {
		this.dataSet = 0;
		this.type = Constant.CORPUS_TEST;
		this.docId = null;
		this.typeCategory = MentionConstant.CATEGORY_TRUE_FALSE;
		this.su = new StringUtil();
		this.stop = new Stopwords();
		this.abb = new Abbreviation(this.type,this.stop);
		this.post = new PostProcessing(this.stop);
	}
	
	public void setDataSet(int dataSet) {
		this.dataSet = dataSet;
	}
	
	public void setDocId(String id) {
		this.docId = id;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Extract the mentions from the given text. The user can choose one of the available models:
	 * <ul>
	 * <li>CbrBC2: BioCreative 2 Gene Mention task;</li>
	 * <li>CbrBC2y: BioCreative 2 Gene Mention task + BioCreative 1 task 1B Gene Normalization for yeast;</li>
	 * <li>CbrBC2m: BioCreative 2 Gene Mention task + BioCreative 1 task 1B Gene Normalization for mouse;</li>
	 * <li>CbrBC2f: BioCreative 2 Gene Mention task + BioCreative 1 task 1B Gene Normalization for fly;</li>
	 * <li>CbrBC2ymf: BioCreative 2 Gene Mention task + BioCreative 1 task 1B Gene Normalization for yeast, mouse and fly;</li>
	 * </ul>
	 * 
	 * @param 	model		the model used to train the tagger
	 * @param 	text		the free text from which to extract the mention
	 * @return 				an array of gene/protein mentions
	 * 
	 */
	public ArrayList<GeneMention> extract(String model, String text) {
		return findGeneMention(model,text);
	}
	
	/**
	 * Extract the mentions from the given text. The tagger used for extracting the 
	 * mentions is the one trained by the user data set:
	 *  
	 * @param 	model		the model used to train the tagger
	 * @param 	text		the free text from which to extract the mention
	 * @param	id			the identifier of the tagger
	 * @return 				an array of gene/protein mentions
	 * 
	 */
	public ArrayList<GeneMention> extract(String model, String text, String id) {
		return findGeneMention(model+"_"+id,text);
	}
	
	private ArrayList<GeneMention> findGeneMention(String organism, String text) {
		try {
			this.originalText = text.replace("\"","");
			this.dbDocSet = new DBDocumentSet("moara_mention");
			this.dbDocGene = new DBDocumentGene("moara_mention");
			this.dbKnown = new DBKnownCase(organism);
			this.dbUnknown = new DBUnknownCase(organism);
			//System.err.println("Finding gene mentions..." + this.docId);
			// default features
			// default type of case
			MentionUtil mu = new MentionUtil();
			this.tc = mu.decideTypeCase(this.typeCategory);
			this.dbKnown.setTypeCase(tc);
			this.dbUnknown.setTypeCase(tc);
			// doc id
			if (this.docId==null) {
				// save doc in database
				this.docId = "1";
				this.dbDocSet.deleteDoc(this.docId);
				this.dbDocSet.insertDocument(this.docId,Constant.CORPUS_TEST,this.originalText,0);
			}
			// delete old mentions
			deleteMentionsDoc();
			// process text
			processTextMention();
			// get results
			ArrayList<GeneMention> gms = this.dbDocGene.getOffsetDocument(this.type,
				this.docId,MentionConstant.MENTION_COMPLETE,Constant.DATA_PREDICTION);
			// final position of mentions
			OutputGeneMention err = new OutputGeneMention();
			gms = err.extractGeneMention(gms);
			// delete doc and mentions
			//this.dbDocGene.deleteMentionsDoc(id);
			//this.dbDocSet.deleteDoc(id);
			this.dbDocSet.executeCommit();
			this.dbDocGene.executeCommit();
			//System.err.println("End of processing..." + this.docId);
			return gms;
		}
		catch(SQLException e) { 
        	System.err.println(e);
        	this.dbDocSet.executeRollback();
			this.dbDocGene.executeRollback();
        }
		return null;
	}
	
	public void deleteMentionsDoc() {
		try {
			this.dbDocGene.deleteMentionsDoc(this.docId);
			this.dbDocGene.executeCommit();
		}
		catch(SQLException e) { 
        	System.err.println(e);
        }
	}
	
	private void processTextMention() {
		Tokenizer t = new Tokenizer();
		t.tokenize(this.originalText);
		ArrayList<String> words = t.getTokens();
		Vector<Token> tokens = new Vector<Token>(words.size());
		for (int i=0; i<words.size(); i++) {
			if (words.get(i).trim().length()>0) {
				tokens.add(new Token(words.get(i)));
				//System.err.println(words.get(i));
			}
		}
		tokens.trimToSize();
		// get mentions - forwards
		processTestDocuments(tokens,MentionConstant.READING_FORWARD);
		// get mentions - backwards
		processTestDocuments(tokens,MentionConstant.READING_BACKWARD);
		// verify predictions (post-processing)
		verifyPrediction(tokens);
		// verify abbreviations (post-processing)
		verifyAbbreviations(tokens);
		// verify isolated mentions
		checkIsolatedMentions(tokens);
	}
	
	private void verifyPrediction(Vector<Token> words) {
		try {
			// get full text
			Tokenizer t = new Tokenizer();
			String text = t.concatenateWords(words);
			String textSpace = t.concatenateWordsSpace(words);
			// get gene mentions
			ArrayList<GeneMention> gms;
			/*if (typeTraining.equals(Constant.DATA_NORMALIZATION))
				gms = dbDocGene.getOffsetDocument(Constant.DATA_NORMALIZATION,id,
					Constant.MENTION_COMPLETE,Constant.DATA_PREDICTION);
			else if (typeTraining.equals(Constant.DATA_TRAINING) && dataSet!=Constant.ALL_SET)
	   			gms = dbDocGene.getOffsetDocument(Constant.DATA_TRAINING,id,Constant.MENTION_COMPLETE,
	   				Constant.DATA_PREDICTION);
	   		else*/
	   		gms = this.dbDocGene.getOffsetDocument(this.type,this.docId,MentionConstant.MENTION_COMPLETE,
	   			Constant.DATA_PREDICTION);
			int lastEnd = -1;
	   		String lastWord = "";
	   		boolean openSquare = false;
	   		for (int i=0; i<gms.size(); i++) {
	   			GeneMention gm = (GeneMention)gms.get(i);
	   			//System.err.println(gm.Text() + " " + lastEnd + " " + gm.Start() + " " + lastWord + " " + open);
	   			/*System.err.println(gm.Text() + "/" + gm.Start() + "/" + gm.End() + " " +
	   				lastWord + "/" + lastEnd + " " + openSquare);*/
	   			String gapWord = new String();
	   			if (lastEnd!=-1)
	   				gapWord = text.substring(lastEnd+1,gm.Start());
	   			//System.err.println("gap " + gapWord);
	   			// verify first word of a mention
	   			if ( (lastEnd==-1 || (lastEnd+1)!=gm.Start()) && !gapWord.equals("(") && 
	   					!gapWord.equals("/") && !gapWord.equals("-")) {
	   				// remove isolated stopwords
	   				if (this.stop.isStopword(gm.Text()) && this.su.hasOnlyLowerCaseLetters(gm.Text())) {
	   					deleteGeneMention(gm.Start(),gm.End(),true,"origin02");
	   					continue;
	   				}
	   				// remove isolated roman numbers
	   				if (gm.Text().length()>2 && this.su.isRomanNumeral(gm.Text())) {
	   					deleteGeneMention(gm.Start(),gm.End(),true,"origin03");
	   					continue;
	   				}
	   			}
	   			// verify words between mentions
	   			if (lastEnd!=-1) {
	   				String wordSave;
	   				post.setGapWordSave(false);
	   				boolean bckOpen = post.Open();
	   				wordSave = post.checkGapWord(gm,gapWord,lastWord,lastEnd,text,textSpace);
	   				//System.err.println(gapWord + " " + wordSave + " " + post.GapWordSave());
	   				if ((gm.Start()-lastEnd+1)>2 && wordSave==null && bckOpen) {
	   					//System.err.println(gapWord + " " + wordSave + " " + post.GapWordSave());
	   					deleteGeneMention(post.IndexOpen(),post.IndexOpen(),false,"origin04");
	   				}
	   				// save the gap word
	   				if (post.GapWordSave() && wordSave!=null)
	   					saveGeneMention(wordSave,tc.getCategoryMiddleGeneMention(),
			   				(lastEnd+1),(lastEnd+su.getLengthWithoutSpace(wordSave)),0,
			   				MentionConstant.CASE_POST,MentionConstant.TRY_EXACT,
			   				MentionConstant.CASE_POST,false,null);
	   			}
	   			// check part of words
	   			Mention gm2 = checkPartsOfWords(words,gm);
	   			if (gm2!=null) {
	   				lastWord = gm.Text();
	   				lastEnd = gm.End();
	   				gm.setText(gm2.Text());
	   				gm.setStart(gm2.Start());
	   				gm.setEnd(gm2.End());
	   			}
	   			// verify square brackets
	   			if (gm.Text().equals("["))
	   				openSquare = true;
	   			else if (gm.Text().equals("]") && !openSquare)
	   				deleteGeneMention(gm.Start(),gm.End(),true,"origin05");
	   			// update variables
	   			lastEnd = gm.End();
	   			lastWord = gm.Text();
	   		}
	   		// close last open parenthesis
	   		if (post.Open()) {
	   			String wordSave = post.checkTextParenthesis(textSpace.length(),lastEnd,
	   				text,textSpace);
   				//System.err.println(lastEnd + " " + textSpace.length() + " " + wordSave);
	   			if (wordSave==null)
   					deleteGeneMention(post.IndexOpen(),post.IndexOpen(),false,"origin06");
   				else
   					saveGeneMention(wordSave,tc.getCategoryMiddleGeneMention(),
		   				(lastEnd+1),(lastEnd+su.getLengthWithoutSpace(wordSave)),0,
		   				MentionConstant.CASE_POST,MentionConstant.TRY_EXACT,
		   				MentionConstant.CASE_POST,
		   				false,null);
   			}
	   	}
		catch(SQLException e) { 
        	System.err.println(e);
        	this.dbDocSet.executeRollback();
        	this.dbDocGene.executeRollback();
        }
	}
	
	private void verifyAbbreviations(Vector<Token> words) {
		this.abb.setFeatures(knownFeatures,unknownFeatures);
		this.abb.setTypeCase(tc);
		this.abb.setKnownUnknown(dbKnown,dbUnknown);
		Vector<Mention> gms = this.abb.verifyAbbreviations(this.docId,words,type,dataSet);
		for (int j=0; j<gms.size(); j++) {
			Mention gm = gms.elementAt(j);
			if (gm.Operation()==MentionConstant.OPERATION_MENTION_INSERT) {
				saveGeneMention(gm.Text(),tc.getCategoryMiddleGeneMention(),
					gm.Start(),gm.End(),0,MentionConstant.CASE_ABBREVIATION,
					MentionConstant.TRY_EXACT,MentionConstant.CASE_ABBREVIATION,
					true,words);
			}
			else {
				deleteGeneMention(gm.Start(),gm.End(),true,"origin07");
			}
		}
	}
	
	private void checkIsolatedMentions(Vector<Token> words) {
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
			ArrayList<GeneMention> gms = this.dbDocGene.getOffsetDocument(type,this.docId,
					MentionConstant.MENTION_COMPLETE,Constant.DATA_PREDICTION);
	   		int lastStart = -1;
	   		int lastEnd = -1;
	   		int lastEnd2 = -1;
	   		String lastWord = "";
	   		for (int i=0; i<=gms.size(); i++) {
	   			GeneMention gm;
	   			boolean updateVariables = true;
	   			if (i<gms.size())
	   				gm = (GeneMention)gms.get(i);
	   			else {
	   				gm = new GeneMention("",-1,-1);
	   			}
	   			// for every mention
	   			// verify "-mediated" complement
	   			if (post.endsWithComplement(gm.Text())) {
	   				int index = post.checkEndComplement(gm.Text(),false);
	   				// remove original mention 
		   			deleteGeneMention(gm.Start(),gm.End(),false,"origin08");
		   			// save first part of mention
		   			if (gm.Text().substring(0,index).length()>0) {
		   				saveGeneMention(gm.Text().substring(0,index),
				   			tc.getCategoryMiddleGeneMention(),gm.Start(),gm.Start()+index-1,0,
				   			MentionConstant.CASE_POST,MentionConstant.TRY_EXACT,
				   			MentionConstant.CASE_POST,false,null);
		   				gm.setText(gm.Text().substring(0,index));
		   				gm.setEnd(gm.Start()+index-1);
		   			}
		   			else
		   				updateVariables = false;
			   	}
	   			// if last word of a mention
	   			if ((lastEnd+1)!=gm.Start()) {
	   				// verify "-dependent" end complement of last word
	   				if (post.endsWithEndComplement(lastWord)) {
	   					// get index
		   				int index = post.checkEndComplement(lastWord,true);
		   				// remove original mention 
		   				deleteGeneMention(lastEnd-lastWord.length()+1,lastEnd,false,"origin09");
		   				// save first part of mention
		   				if (lastWord.substring(0,index).length()>0) {
		   					saveGeneMention(lastWord.substring(0,index),
								tc.getCategoryMiddleGeneMention(),lastEnd-lastWord.length()+1,
								lastEnd-lastWord.length()+index,0,MentionConstant.CASE_POST,
								MentionConstant.TRY_EXACT,MentionConstant.CASE_POST,false,null);
		   					gm.setText(lastWord.substring(0,index));
		   					gm.setEnd(lastEnd-lastWord.length()+index);
		   				}
		   				else
			   				updateVariables = false;
					}
	   			}
	   			//System.err.println(lastEnd2 + " " + lastStart + "/" + lastEnd + " " + gm.Start());
	   			/*if (lastEnd!=-1 && lastEnd2!=-1)
	   				System.err.println(su.getStringWithSpace(this.originalText,lastEnd2+1,lastStart));*/
	   			// if mention is isolated word
	   			if (lastEnd!=-1 && gm.Start()!=-1 && (lastEnd2+1)!=lastStart && (lastEnd+1)!=gm.Start() && 
	   					!isAConnectingWord(su.getStringWithSpace(this.originalText,lastEnd2+1,lastStart)) &&
	   					!isAConnectingWord(su.getStringWithSpace(this.originalText,lastEnd+1,gm.Start()))) {
	   				//System.err.println(lastStart + "/" + lastEnd + " " + lastWord);
	   				// delete isolated letters
		   			if (lastWord.length()==1) {
		   				deleteGeneMention(lastStart,lastEnd,false,"origin10");
		   				//updateVariables = false;
		   			}
		   			else if (lastWord.startsWith("-") && su.isNumeral(lastWord.substring(1,lastWord.length()))) {
		   				deleteGeneMention(lastStart,lastEnd,false,"origin11");
		   				//updateVariables = false;
		   			}
		   			else {
		   				Feature fk = new Feature(this.knownFeatures);
		   				// known case
			   			fk.setStrValue(MentionConstant.FEATURE_WORD,lastWord);
		   				fk.setStrValue(MentionConstant.FEATURE_CATEGORY_BEFORE_1,tc.getCategoryNotGeneMention());
		   				KnownCase kc = new KnownCase(fk,null);
		   				kc = this.dbKnown.getMaxFreqKnownCase(kc,MentionConstant.READING_FORWARD);
		   				if (kc.Category()!=null && !tc.isGeneMentionCategory(kc.Category())) {
		   					// remove mention 
			   				deleteGeneMention(lastStart,lastEnd,false,"origin12");
			   				//updateVariables = false;
		   				}
		   				else if (kc.Category()==null) {
		   					// unknown case
		   					Feature fu = new Feature(this.unknownFeatures);
		   					fu.setStrValue(MentionConstant.FEATURE_WORD,lastWord);
		   					fu.setStrValue(MentionConstant.FEATURE_CATEGORY_BEFORE_1,tc.getCategoryNotGeneMention());
		   					String format = fu.getFormatToken(lastWord);
		   					StringTokenizer st = new StringTokenizer(format,MentionConstant.SYMBOL_FORMAT_SEPARATION);
		   					while (st.hasMoreTokens()) {
		   						String format1 = st.nextToken();
		   						fu.setFormat(format1);
		   						UnknownCase uc = new UnknownCase(fu,null);
		   						uc = this.dbUnknown.getMaxFreqUnknownCase(uc,MentionConstant.READING_FORWARD);
		   						if (uc.Category()!=null && !this.tc.isGeneMentionCategory(uc.Category())) {
				   					// remove mention 
					   				deleteGeneMention(lastStart,lastEnd,false,"origin14");
					   				//updateVariables = false;
					   				break;
				   				}
		   						else if (uc.Category()==null) {
		   							// change category before
		   							fu.setStrValue(MentionConstant.FEATURE_CATEGORY_BEFORE_1,tc.getCategoryGeneMention());
		   							uc = this.dbUnknown.getMaxFreqUnknownCase(uc,MentionConstant.READING_FORWARD);
			   						if (uc.Category()!=null && !tc.isGeneMentionCategory(uc.Category())) {
					   					// remove mention 
						   				deleteGeneMention(lastStart,lastEnd,false,"origin13");
						   				//updateVariables = false;
						   				break;
					   				}
			   						else if (uc.Category()!=null)
			   							break;
		   						}
		   						else if (uc.Category()!=null)
		   							break;
		   					}
		   				}
		   			}
		   		}
	   			if (updateVariables) {
	   				lastEnd2 = lastEnd;
		   			lastStart = gm.Start();
		   			lastEnd = gm.End();
		   			lastWord = gm.Text();
	   			}
	   		}
	   	}
		catch(SQLException e) { 
        	System.err.println(e);
        	this.dbDocSet.executeRollback();
        	this.dbDocGene.executeRollback();
        }
	}
	
	private Mention checkPartsOfWords(Vector<Token> words, GeneMention gm) {
		try {
			int index = 0;
			for (int i=0; i<words.size(); i++) {
				String word = ((Token)words.elementAt(i)).TokenText();
				// search for internal word
				if (index<=gm.Start() && (index+word.length()-1)>=gm.Start()) {
					//System.err.println(gm.Text() + " " + word + " " + gm.Start() + " " + index);
					// save the word if its major part is already gene mention
					// check word before
					//System.err.println(index + " " + gm.Start());
					if (word.substring(0,gm.Start()-index).length()>0)
						saveGeneMention(word.substring(0,gm.Start()-index),
							tc.getCategoryMiddleGeneMention(),index,gm.Start()-1,0,
							MentionConstant.CASE_POST,MentionConstant.TRY_EXACT,MentionConstant.CASE_POST,
						   	true,words);
					// check word after
					//System.err.println(gm.End()+1 + " " + (index+word.length()));
					if (word.substring(gm.End()-index+1,word.length()).length()>0) {
						if (!dbDocGene.isPartGeneMention(this.docId,gm.End()+1,index+word.length()-1)) {
							String w = word.substring(gm.End()-index+1,word.length());
							saveGeneMention(w,tc.getCategoryMiddleGeneMention(),
								gm.End()+1,index+word.length()-1,0,MentionConstant.CASE_POST,
								MentionConstant.TRY_EXACT,MentionConstant.CASE_POST,true,words);
							Mention gm2 = new Mention(w,gm.End()+1,index+word.length()-1);
							return gm2;
						}
					}
					return null;	
				}
				index += word.length();
			}
		}
		catch(SQLException e) { 
        	System.err.println(e);
        	this.dbDocSet.executeRollback();
        	this.dbDocGene.executeRollback();
        }
		return null;
	}
	
	private void processTestDocuments(Vector<Token> tokens, 
			String typeReading) {
		// for each element in the vector
		int firstIndex;
		int lastIndex;
		int i;
		int totalLength = 0;
		int start = 0;
		int end = -1;
		boolean keepLoop;
		if (typeReading.equals(MentionConstant.READING_FORWARD)) {
			firstIndex = 0;
			lastIndex = tokens.size();
			i = firstIndex;
			keepLoop = (i<lastIndex);
		}
		else {
			firstIndex = tokens.size()-1;
			lastIndex = 0;
			i = firstIndex;
			keepLoop = (i>=lastIndex);
			// start/end of mention
			// total length
			for (int j=0; j<tokens.size(); j++) {
				totalLength += (tokens.get(j)).TokenText().length();
			}
		}
		// backup of start/end
		int startBefore1 = -1;
		int startBefore2 = -1;
		int endBefore1 = -1;
		int endBefore2 = -1;
		String categoryBeforeNotMention = tc.getCategoryNotGeneMention();
		// initiate variables
		VariablesProcessing vp = new VariablesProcessing(categoryBeforeNotMention);
		int adicional = 0;
		// start loop
		while (keepLoop || adicional<=2) {
			if (!keepLoop) {
				adicional++;
				vp.word = null;
				vp.tag = null;
			}
			else {
				vp.word = ((Token)tokens.elementAt(i)).TokenText();
				vp.tag = ((Token)tokens.elementAt(i)).Tag();
				// update start/end
				if (typeReading.equals(MentionConstant.READING_FORWARD)) {
					end += vp.word.length();
				}
				else {
					start = totalLength-vp.word.length();
					end = totalLength-1;
				}
			}
			//System.err.println(vp.wordBefore2 + " " + startBefore2 + " " + endBefore2 + " " + vp.categoryBefore2);
			/*System.err.println(vp.wordBefore4 + "/" + vp.categoryBefore4 + " " +
					vp.wordBefore3 + "/" + vp.categoryBefore3 + " " + 
					vp.wordBefore2 + "/" + vp.categoryBefore2);*/
			// do not process symbols
			if (vp.wordBefore2==null || vp.wordBefore2.equals("") || 
					vp.wordBefore2.equals("\"") || vp.wordBefore2.equals("(") || 
					vp.wordBefore2.equals(")")) {
				vp.categoryBefore2 = tc.getCategoryNotGeneMention();
			}
			else if (typeReading.equals(MentionConstant.READING_FORWARD)) {
				Vector<Mention> parts = getPartGeneMention(startBefore2,endBefore2);
				// if no parts found
				if (parts.size()==0) {
					processFindCase(vp,typeReading,startBefore2,endBefore2,false);
				}
				// if exact word found
				else if (parts.size()==1 && isGeneMention(startBefore2,endBefore2)) {
					vp.categoryBefore2 = tc.getCategoryGeneMention();
				}
				// process parts of the word
				else {
					Vector<Mention> words = getPartWord(parts,vp.wordBefore2,startBefore2,
						endBefore2);
					for (int j=0; j<words.size(); j++) {
						Mention gm = (Mention)words.elementAt(j);
						vp.wordBefore2 = gm.Text();
						//System.err.println(vp.wordBefore2);
						if (isGeneMention(gm.Start(),gm.End()))
							vp.categoryBefore2 = tc.getCategoryGeneMention();
						else
							processFindCase(vp,typeReading,gm.Start(),gm.End(),false);
						vp.updateVariablesBefore();
					}
				}
			}
			else {
				processFindCase(vp,typeReading,startBefore2,endBefore2,false);
			}
			// update start/end
			startBefore2 = startBefore1;
			endBefore2 = endBefore1;
			startBefore1 = start;
			endBefore1 = end;
			// update variables
			vp.updateVariables();
			// update start/end variables
			if (keepLoop) {
				if (typeReading.equals(MentionConstant.READING_FORWARD)) {
					start += vp.word.length();
				}
				else {
					totalLength -= vp.word.length();
				}
			}
			// conditions of the loop
			if (typeReading.equals(MentionConstant.READING_FORWARD)) {
    			i++;
    			keepLoop = (i<lastIndex);
    		}
    		else {
    			i--;
    			keepLoop = (i>=lastIndex);
    		}
		}
	}
	
	private boolean isGeneMention(int start, int end) {
		boolean isGM = false;
		try {
			isGM = dbDocGene.isGeneMention(this.docId,start,end);
		}
		catch(SQLException e) { 
        	System.err.println(e);
        	this.dbDocSet.executeRollback();
        	this.dbDocGene.executeRollback();
        }
		return isGM;
	}
	
	private Vector<Mention> getPartGeneMention(int start, int end) {
		Vector<Mention> gms = new Vector<Mention>();
		try {
			gms = this.dbDocGene.getPartGeneMention(this.docId,type,start,end);
		}
		catch(SQLException e) { 
        	System.err.println(e);
        	this.dbDocSet.executeRollback();
        	this.dbDocGene.executeRollback();
        }
		return gms;
	}
	
	private Vector<Mention> getPartWord(Vector<Mention> gms, String text, int start, int end) {
		Vector<Mention> words = new Vector<Mention>();
		int startW = 0;
		//System.err.println("part " + text + " " + start + " " + end);
		for (int i=0; i<gms.size(); i++) {
			Mention gm = gms.elementAt(i);
			//System.err.println("part1 " + gm.Text() + " " + gm.Start() + " " + gm.End());
			// save between word
			 if ((start+startW)!=gm.Start()) {
				//System.err.println("part2 " + startW + " " + gm.Start() + " " + start + " " + text);
				String w = text.substring(startW,gm.Start()-start);
				//System.err.println("part2 " + w);
				Mention gmW = new Mention(w,startW+start,gm.Start()-1);
				words.add(gmW);
			}
			// save actual mention
			Mention gmW = new Mention(gm.Text(),gm.Start(),gm.End());
			words.add(gmW);
			// update variable
			startW = gm.End()+1-start;
		}
		// last part of word
		if ((startW+start)<end) {
			String w = text.substring(startW,text.length());
			//System.err.println("part3 " + w);
			Mention gmW = new Mention(w,startW+start,end);
			words.add(gmW);
		}
		words.trimToSize();
		return words;
	}
	
	private void processFindCase(VariablesProcessing vp, String typeReading,
			int start, int end, boolean checkingParts) {
		// known case
		Feature kf = vp.createCaseFeature(knownFeatures);
		KnownCase kc = new KnownCase(kf,null,start,end);
		// unknown case
		Feature uf = vp.createCaseFeature(unknownFeatures);
		UnknownCase uc = new UnknownCase(uf,null,start,end);
		// search for a case
		Case c = findCase(kc,uc,vp,typeReading);
		// case found
		if (c!=null && c.Category()!=null) {
			vp.categoryBefore2 = c.Category();
			/*System.err.println("case found " + c.Feature().getStrValue(Constant.FEATURE_WORD) +
				" " + c.Category() + " " + c.Start() + " " + c.End());*/
			if (tc.isGeneMentionCategory(vp.categoryBefore2)) {
				String mention;
				if (c.TypeWordCase().equals(MentionConstant.WORD_CASE_PARTIAL))
					mention = c.Feature().getStrValue(MentionConstant.FEATURE_WORD);
				else
					mention = c.Feature().getStrValue(MentionConstant.FEATURE_ORIGINAL_WORD);
				saveGeneMention(mention,vp.categoryBefore2,c.Start(),c.End(),
					c.Sequential(),c.TypeCase(),c.TryCode(),typeReading,false,null);
			}
		}
		else {
			Vector<KnownCase> casesPart = kc.getPartsKnownWords(kc,typeReading);
			if (checkingParts || casesPart.size()==0) {
				// decide gene mention
				//vp.categoryBefore2 = tc.getCategoryGeneMention();
				vp.categoryBefore2 = tc.getCategoryNotGeneMention();
				saveGeneMention(vp.wordBefore2,vp.categoryBefore2,start,end,0,
					MentionConstant.CASE_NO_CASE,MentionConstant.TRY_ELSE,
					typeReading,false,null);
			}
			else if (!checkingParts) {
				for (int i=0; i<casesPart.size(); i++) {
		 			KnownCase kcPart = (KnownCase)casesPart.elementAt(i);
		 			vp.wordBefore2 = kcPart.Feature().getStrValue(MentionConstant.FEATURE_WORD);
		 			processFindCase(vp,typeReading,kcPart.Start(),kcPart.End(),true);
		 			vp.updateVariablesBefore();
				}
			}
		}
	}
	
	private void saveGeneMention(String word, String category, int start, int end,
			int sequential, String typeCase, String tryCode, String typeReading,
			boolean checkWordsAfter, Vector<Token> words) {
		Symbols sym = new Symbols();
		try {
			Mention gm = new Mention(word,MentionConstant.MENTION_COMPLETE,start,end,category);
			/*if (typeTraining.equals(Constant.DATA_NORMALIZATION))
				type = Constant.DATA_NORMALIZATION;
			else if (typeTraining.equals(Constant.DATA_TRAINING) && dataSet!=Constant.ALL_SET)
				type = Constant.DATA_TRAINING;
			else
				type = Constant.DATA_TEST;*/
			this.dbDocGene.insertGeneMention(this.docId,this.type,gm,sequential,typeCase,tryCode,typeReading);
			// review classification of next words
			//System.err.println(word);
			if (checkWordsAfter) {
				int index = 0;
				boolean reached = false;
				String w = new String();
				for (int i=0; i<words.size(); i++) {
					w = ((Token)words.elementAt(i)).TokenText();
					if (index==(end+1)) {
						reached = true;
						break;
					}
					index += w.length();
				}
				if (reached && w!=null && !w.equals("\"") && !w.equals("(") && !w.equals(")") &&
						!sym.isPunctuation(w) && !sym.isStartEnd(w)) {
					Feature fk = new Feature(this.knownFeatures);
	   				fk.setStrValue(MentionConstant.FEATURE_WORD,w);
	   				fk.setStrValue(MentionConstant.FEATURE_CATEGORY_BEFORE_1,tc.getCategoryGeneMention());
	   				KnownCase kc = new KnownCase(fk,null);
	   				kc = dbKnown.getMaxFreqKnownCase(kc,MentionConstant.READING_FORWARD);
					// known case
	   				if (kc.Category()!=null && tc.isGeneMentionCategory(kc.Category()))
						saveGeneMention(w,kc.Category(),index,index+w.length()-1,kc.Sequential(),
								MentionConstant.CASE_POST_SAVE,MentionConstant.TRY_EXACT,
								MentionConstant.CASE_POST_SAVE,true,words);
	   				else if (kc.Category()==null) {
	   					// unknown case
	   					Feature fu = new Feature(this.unknownFeatures);
	   					fu.setStrValue(MentionConstant.FEATURE_WORD,w);
	   					fu.setStrValue(MentionConstant.FEATURE_CATEGORY_BEFORE_1,tc.getCategoryGeneMention());
	   					String format = fu.getFormatToken(w);
	   					StringTokenizer st = new StringTokenizer(format,MentionConstant.SYMBOL_FORMAT_SEPARATION);
	   					while (st.hasMoreTokens()) {
	   						String format1 = st.nextToken();
	   						fu.setFormat(format1);
	   						UnknownCase uc = new UnknownCase(fu,null);
	   						uc = this.dbUnknown.getMaxFreqUnknownCase(uc,MentionConstant.READING_FORWARD);
	   						if (uc.Category()!=null && tc.isGeneMentionCategory(uc.Category())) {
			   					// remove mention 
	   							saveGeneMention(w,uc.Category(),index,index+w.length()-1,uc.Sequential(),
	   									MentionConstant.CASE_POST_SAVE,MentionConstant.TRY_EXACT,
	   									MentionConstant.CASE_POST_SAVE,true,words);
				   				break;
			   				}
	   					}
	   				}
				}
			}
		}
		catch(SQLException e) { 
        	System.err.println(e);
        	this.dbDocSet.executeRollback();
        	this.dbDocGene.executeRollback();
        }
	}
	
	private void deleteGeneMention(int start, int end, 
			boolean checkWordsAfter, String origin) {
		try {
			dbDocGene.deleteGeneMention(this.docId,start,end,origin);
			// review classification of next words
			if (checkWordsAfter) {
				Mention gm = dbDocGene.isForwardGeneMention(this.docId,end+1);
				if (gm!=null) {
					VariablesProcessing vp = new VariablesProcessing(tc.getCategoryNotGeneMention());
					vp.wordBefore2 = gm.Text();
					processFindCase(vp,MentionConstant.READING_FORWARD,end+1,gm.End(),false);
					if (!tc.isGeneMentionCategory(vp.categoryBefore2))
						deleteGeneMention(end+1,gm.End(),true,"origin01");
				}
			}
		}
		catch(SQLException e) { 
        	System.err.println(e);
        	this.dbDocSet.executeRollback();
        	this.dbDocGene.executeRollback();
        }
	}
	
	private boolean isAConnectingWord(String word) {
		if (word.equals("and") || word.equals("/") || word.equals("(") || word.equals(",") 
				|| word.equals("-"))
			return true;
		return false;
	}
		
	private Case findCase(KnownCase kc, UnknownCase uc, VariablesProcessing vp, String typeReading) {
		StringUtil su = new StringUtil();
		GreekLetter greek = new GreekLetter();
		// get word and format
		String word = kc.Feature().getStrValue(MentionConstant.FEATURE_WORD);
		String format = uc.Feature().getStrValue(MentionConstant.FEATURE_FORMAT);
		//System.err.println(format);
		// try known case, except numbers
		if (!su.isNumeral(format) && !greek.hasGreekLetter(word) && !format.equals("A.")) {
			kc = (KnownCase)findKnownCase(kc,vp,typeReading);
			if (kc.Category()!=null)
				return (Case)kc;
		}
		if (kc.Category()==null && word.indexOf("-")==-1 && word.indexOf("/")==-1 &&
				word.indexOf("+")==-1 && word.indexOf("*")==-1 &&  
				(word.indexOf(".")==-1 || word.indexOf(".")==(word.length()-1)) &&
				word.indexOf("=")==-1) {
			uc = (UnknownCase)findUnknownCase(uc,vp,typeReading);
			if (uc.Category()!=null)
				return (Case)uc;
		}
		return null;
	}
	
	private Case findKnownCase(KnownCase kc, VariablesProcessing vp, String typeReading) {
		Symbols sym = new Symbols();
		try {
			// try known case, exact parameters
			kc.setTryCode(MentionConstant.TRY_EXACT);
			kc = dbKnown.getMaxFreqKnownCase(kc,typeReading);
			// if no case found, try changing category before
			if (kc.Category()==null || (!tc.isGeneMentionCategory(kc.Category()) && 
					!sym.isSymbol(vp.wordBefore2) &&
					isAConnectingWord(vp.wordBefore3) && 
					tc.isGeneMentionCategory(vp.categoryBefore4))) {
				//System.err.println(vp.wordBefore2 + "/" + kc.Category() + " " + 
				//	vp.wordBefore3 + " " + vp.wordBefore4 + "/" + vp.categoryBefore4);
				String category1 = kc.Category();
				int sequential1 = kc.Sequential();
				int frequency1 = kc.Frequency();
				// try changing category before
				kc = (KnownCase)tryAlternateCategoryBefore((Case)kc,typeReading);
				//System.err.println(kc.Category());
				if (kc.Category()==null && category1!=null) {
					kc.setCategory(category1);
					kc.setSequential(sequential1);
					kc.setFrequency(frequency1);
				}
			}
		}
		catch(SQLException e) { 
        	System.err.println(e);
        }
		return (Case)kc;
	}
	
	private Case findUnknownCase(UnknownCase uc, VariablesProcessing vp, String typeReading) {
		try {
			// try unknown case
			String format = uc.Feature().getStrValue(MentionConstant.FEATURE_FORMAT);
			StringTokenizer st = new StringTokenizer(format,MentionConstant.SYMBOL_FORMAT_SEPARATION);
			while (st.hasMoreTokens()) {
				String format1 = st.nextToken();
				uc.Feature().setFormat(format1);
				uc.setTryCode(MentionConstant.TRY_EXACT);
				uc = dbUnknown.getMaxFreqUnknownCase(uc,typeReading);
				/*System.out.println(uc.Category() + " " + vp.wordBefore2 + "/" + 
					vp.categoryBefore2 + " " + vp.wordBefore3 + "/" + 
					vp.categoryBefore3 + " " + vp.wordBefore4 + "/" + 
					vp.categoryBefore4);*/
				// if no case found
				if (uc.Category()==null || (!tc.isGeneMentionCategory(uc.Category()) && 
						isAConnectingWord(vp.wordBefore3) && 
						tc.isGeneMentionCategory(vp.categoryBefore4))) {
					String category2 = uc.Category();
					int sequential2 = uc.Sequential();
					int frequency2 = uc.Frequency();
					// try changing category before
					uc = (UnknownCase)tryAlternateCategoryBefore((Case)uc,typeReading);
					// if no case found
					if (uc.Category()==null && category2!=null) {
						uc.setCategory(category2);
						uc.setSequential(sequential2);
						uc.setFrequency(frequency2);
					}
					else if (uc.Category()==null) {
						// try suffix
						String suffixFormat = uc.Feature().getFormatSuffix(uc.Feature().getStrValue(MentionConstant.FEATURE_WORD));
						if (suffixFormat!=null) {
							uc.Feature().setFormat(suffixFormat);
							uc.setTryCode(MentionConstant.TRY_SUFFIX);
							uc = dbUnknown.getMaxFreqUnknownCase(uc,typeReading);
							if (uc.Category()==null || (isAConnectingWord(vp.wordBefore3) && 
									tc.isGeneMentionCategory(vp.categoryBefore4))) {
								String category3 = uc.Category();
								int sequential3 = uc.Sequential();
								int frequency3 = uc.Frequency();
								// try changing category before
								uc = (UnknownCase)tryAlternateCategoryBefore((Case)uc,typeReading);
								if (uc.Category()==null) {
									uc.setCategory(category3);
									uc.setSequential(sequential3);
									uc.setFrequency(frequency3);
								}
								else
									break;
							}
							else
								break;
						}
					}
					else
						break;
				}
				else
					break;
			}
			uc.Feature().setFormat(format);
		}
		catch(SQLException e) { 
        	System.err.println(e);
        }
		return (Case)uc;
	}
	
	private Case tryAlternateCategoryBefore(Case c, String typeReading) {
		try {
			String cb1 = c.Feature().getStrValue(MentionConstant.FEATURE_CATEGORY_BEFORE_1);
			if (cb1!=null && !cb1.equals("")) {
				String altCb1 = tc.getAlternateCategoryBefore(cb1);
				c.Feature().setStrValue(MentionConstant.FEATURE_CATEGORY_BEFORE_1,altCb1);
				// try changing only category before 1
				c.setTryCode(MentionConstant.TRY_ALT_B1);
				if (c.TypeCase().equals(MentionConstant.CASE_KNOWN))
					c = (Case)dbKnown.getMaxFreqKnownCase((KnownCase)c,typeReading);
				else
					c = (Case)dbUnknown.getMaxFreqUnknownCase((UnknownCase)c,typeReading);
				c.Feature().setStrValue(MentionConstant.FEATURE_CATEGORY_BEFORE_1,cb1);
			}
		}
		catch(SQLException e) { 
        	System.err.println(e);
        }
		return c;
	}
	
	/*public void tagAbner() {
		try {
			System.err.println("Tagging with Abner...");
			// remove old test predictions
			dbDocGene.deleteMentionsTest();
			// get all the test documents
			//Vector ids = dbDocSet.getAllDocumentsId(Constant.DATA_TEST,Constant.ALL_SET);
			Vector ids = new Vector();
			ids.add("BC2GM000522836");
			// for each training document
			int count = 0;
			Tagger t = new Tagger(1);
			Enumeration enumer1 = ids.elements();
			while (enumer1.hasMoreElements()) {
				String id = (String)enumer1.nextElement();
				String text = dbDocSet.getTextDocument(Constant.DATA_TEST,id);
				Vector v = t.getWords(text);
				int start = 0;
				int end = 0;
				boolean mentionFound = false;
				String mention = "";
				int startMention = 0;
				for (int i=0; i<v.size(); i++) {
					String values[][] = (String[][])v.elementAt(i);
					for (int j=0; j<values[0].length; j++) {
						String token = values[0][j];
						String tag = values[1][j];
						if (tag.equals("O")) {
							if (mentionFound) {
								// save mention
								dbDocGene.insertDocumentGene(id,Constant.DATA_TEST,mention,
									Constant.MENTION_COMPLETE,startMention,end-1);
							}
							mentionFound = false;
						}
						else {
							// mention continues
							if (mentionFound)
								mention += " " + token;
							// gene mention found
							else {
								mention = token;
								startMention = start;
								mentionFound = true;
							}
						}
						end += token.length();
						//System.err.println(token + " " + start + " " + end);
						start = end;
					}
				}
				// if there is any mention not saved
				if (mentionFound) {
					// save mention
					dbDocGene.insertDocumentGene(id,Constant.DATA_TEST,mention,Constant.MENTION_COMPLETE,
						startMention,end-1);
				}
				count++;
	    		if ((count%100)==0) {
					System.err.print(count + "...");
					db.executeCommit();
				}
			}
			db.executeCommit();
		}
		catch(SQLException e) { 
	    	System.err.println(e);
	    	db.executeRollback();
	    }
	}*/
	
	public static void main(String[] args) {
		GeneRecognition app = new GeneRecognition();
		String text = "A highly conserved 48 bp DNA element was identified " +
		"present at 26 chromosome ends of Saccharomyces cerevisiae. " +
		"Each element harbours an ideal or a mutated ATF/CREB site, " +
		"which is a well-known target sequence for bZip transcription " +
		"factors. In all cases, the sub-telomeric ATF/CREB site element " +
		"(SACE) is a direct extension of the respective sub-telomeric " +
		"coreX element. Eight SACEs are part of very long quasi-identical " +
		"regions of several kilobases, including a sub-telomeric COS open " +
		"reading frame. Three of these eight SACEs harbour an ideal " +
		"ATF/CREB site, four a triple-exchange variant (5'-ATGGTATCAT-3'; " +
		"GTA variant), and one a single exchange variant with a C to G " +
		"exchange at the left side of the center of symmetry. We analyzed " +
		"the function of the SACE of the left arm of chromosome VIII in " +
		"vivo and found its ATF/CREB site to act as UAS/URS of the COS8 " +
		"promoter, effected by the yeast bZip proteins Sko1p, Aca1p, and " +
		"Aca2p. Cos8 protein was found in proximity to the nuclear " +
		"membrane, where it accumulated, especially during cell division. " +
		"When the ATF/CREB site of the COS8 promoter was exchanged with " +
		"the GTA variant, the regulation was changed. COS8 was then " +
		"regulated by Hac1p, a bZip protein known to be involved in the " +
		"unfolded protein response of S. cerevisiae, indicating, for the " +
		"first time, a possible functional category for the Cos proteins " +
		"of S. cerevisiae.";
		/*String text = "Phenotypic analysis demonstrates that trio and Abl cooperate " +
			"in regulating axon errgrowth in the embryonic central nervous system (CNS).";*/
		// null
		ArrayList<GeneMention> gms = app.extract(null,text);
		for (int i=0; i<gms.size(); i++) {
			GeneMention gm = gms.get(i);
			System.err.println(gm.Start() + "\t" + gm.End() + "\t" + gm.Text());
		}
		// yeast
		/*gms = app.extractForUser(text,"id");
		for (int i=0; i<gms.size(); i++) {
			GeneMention gm = gms.get(i);
			System.err.println(gm.Start() + "\t" + gm.End() + "\t" + gm.Text());
		}*/
		// all
		/*gms = app.extract(text);
		for (int i=0; i<gms.size(); i++) {
			GeneMention gm = gms.get(i);
			System.err.println(gm.Start() + "\t" + gm.End() + "\t" + gm.Text());
		}*/
	}
	
}
