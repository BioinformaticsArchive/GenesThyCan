package moara.mention.functions;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import moara.mention.MentionConstant;
import moara.mention.MentionUtil;
import moara.mention.dbs.DBKnownCase;
import moara.mention.dbs.DBUnknownCase;
import moara.mention.entities.Feature;
import moara.mention.entities.GeneMention;
import moara.mention.entities.KnownCase;
import moara.mention.entities.Token;
import moara.mention.entities.TypeCase;
import moara.mention.entities.UnknownCase;
import moara.mention.entities.VariablesProcessing;
import moara.util.Constant;
import moara.util.lexicon.GreekLetter;
import moara.util.text.StringUtil;

public class CreateCaseBase {
	
	private DBKnownCase dbKnown;
	private DBUnknownCase dbUnknown;
	// other variables
	private String[] knownFeatures = MentionConstant.KNOWN_FEATURES;
	private String[] unknownFeatures = MentionConstant.UNKNOWN_FEATURES;
	private TypeCase tc;
	private String organism;
	private int dataSet;
	private String trainId;
	private String typeCategory;
	
	public CreateCaseBase(String organism, boolean cleanCases) {
		this.organism = organism;
		this.dataSet = Constant.ALL_SET;
		this.typeCategory = MentionConstant.CATEGORY_TRUE_FALSE;
		MentionUtil mu = new MentionUtil();
		this.tc = mu.decideTypeCase(this.typeCategory);
		this.trainId = "";
		init(cleanCases);
	}
	
	public void setDataSet(int dataSet) {
		this.dataSet = dataSet;
	}
	
	public void setKnownFeatures(String[] features) {
		this.knownFeatures = features;
	}
	
	public void setUnknownFeatures(String[] features) {
		this.unknownFeatures = features;
	}
	
	public void init(boolean cleanCases) {
		try {
			this.dbKnown = new DBKnownCase(this.organism);
			this.dbUnknown = new DBUnknownCase(this.organism);
			if (cleanCases) {
				this.dbKnown.truncateKnownCase();
				this.dbUnknown.truncateUnknownCase();
			}
		}
		catch(SQLException e) { 
        	System.err.println(e);
        	this.dbKnown.executeRollback();
        	this.dbUnknown.executeRollback();
        }
	}
	
	public void end() {
		if (this.dataSet!=Constant.ALL_SET)
			createTrainIdFile();
	}
	
	public void train(String id, String text, ArrayList<GeneMention> indexes) {
		text = text.replace("\"","");
		if (this.dataSet!=Constant.ALL_SET)
			this.trainId += id + "\n";
		MentionTagger mt = new MentionTagger();
		Vector<Token> words = mt.tagText(text,indexes);
		// update categories
		if (typeCategory.equals(MentionConstant.CATEGORY_BIO) || 
			typeCategory.equals(MentionConstant.CATEGORY_BIEWO))
			tc.updateCategory(words);
		//System.err.print(this.count + "..." + id + "...");
		// create the cases - forwards
		createCases(words,MentionConstant.READING_FORWARD);
		//System.err.print("forward...");
		// create the cases - backwards
		createCases(words,MentionConstant.READING_BACKWARD);
		//System.err.print("backward...");
	}
	
	private void createCases(Vector<Token> words, String typeReading) {
    	try {
    		GreekLetter greek = new GreekLetter();
    		StringUtil su = new StringUtil();
    		// for each element in the vector
    		int firstIndex;
    		int lastIndex;
    		int i;
    		boolean keepLoop;
    		// forward reading
    		if (typeReading.equals(MentionConstant.READING_FORWARD)) {
    			firstIndex = 0;
    			lastIndex = words.size();
    			i = firstIndex;
    			keepLoop = (i<lastIndex);
    		}
    		// backward reading
    		else {
    			firstIndex = words.size()-1;
    			lastIndex = 0;
    			i = firstIndex;
    			keepLoop = (i>=lastIndex);
    		}
    		String categoryBeforeNotMention = tc.getCategoryNotGeneMention();
    		// initiate variables
    		VariablesProcessing vp = new VariablesProcessing(categoryBeforeNotMention);
    		// start loop
    		int count = 0;
    		while (keepLoop || count<=2) {
    			if (!keepLoop) {
					count++;
					vp.word = null;
					vp.tag = null;
					vp.category = tc.getCategoryNotGeneMention();
				}
				else {
					Token t = (Token)words.elementAt(i);
					vp.word = t.TokenText();
					vp.tag = t.Tag();
					vp.category = t.Category();
					//System.err.println(vp.word + " " + vp.category + " " + vp.tag);
				}
				// create cases
    			if (vp.wordBefore2!=null && !vp.wordBefore2.equals("") && !vp.wordBefore2.equals("\"")
    					&& !vp.wordBefore2.equals("(") && !vp.wordBefore2.equals(")")) {
    				//System.err.println(vp.wordBefore2 + " " + vp.categoryBefore2 + " " + vp.tagBefore2);
    				// known case
    				Feature kf = vp.createCaseFeature(knownFeatures);
    				KnownCase kc = new KnownCase(kf,vp.categoryBefore2);
    				// unknown case
    				Feature uf = vp.createCaseFeature(unknownFeatures);
    				String format = uf.getStrValue(MentionConstant.FEATURE_FORMAT);
    				// save known case, except numbers
    				if (!su.isNumeral(format) && !greek.hasGreekLetter(vp.wordBefore2) && 
    						!format.equals("A.")) {
    					dbKnown.insertKnownCase(kc,typeReading);
    					String word = kc.Feature().getStrValue(MentionConstant.FEATURE_WORD);
    					if (!tc.isGeneMentionCategory(kc.Category())) {
        					// lower case
        					kc.Feature().setStrValue(MentionConstant.FEATURE_WORD,word.toLowerCase());
        					dbKnown.insertKnownCase(kc,typeReading);
        				}
    					// family name, if any
    					String family = kc.getFamilyName(word);
    					//System.err.println(word + " " + family);
    					if (family!=null) {
    						kc.Feature().setStrValue(MentionConstant.FEATURE_WORD,family);
    						kc.setTypeKnownCase(MentionConstant.KNOWN_FAMILY);
        					dbKnown.insertKnownCase(kc,typeReading);
    					}
    					// insert parts of words
    					Vector<KnownCase> casesPart = kc.getPartsKnownWords(kc,typeReading);
    					for (int j=0; j<casesPart.size(); j++) {
				 			KnownCase kcPart = (KnownCase)casesPart.elementAt(j);
				 			String wordPart = kcPart.Feature().getStrValue(MentionConstant.FEATURE_WORD); 
				 			kcPart.setCategory(kc.Category());
				 			vp.wordBefore2 = wordPart;
				 			this.dbKnown.insertKnownCase(kcPart,typeReading);
				 			vp.updateVariablesBefore();
						}
    				}
    				// save unknown case
    				StringTokenizer st = new StringTokenizer(format,MentionConstant.SYMBOL_FORMAT_SEPARATION);
    				while (st.hasMoreTokens()) {
    					uf.setFormat(st.nextToken());
    					if (format.indexOf("-")==-1 && format.indexOf("/")==-1 &&
    							format.indexOf("+")==-1 && format.indexOf("*")==-1 &&
    							format.indexOf("=")==-1 && (format.indexOf(".")==-1 || 
    							format.indexOf(".")==(format.length()-1))) {
    						UnknownCase uc = new UnknownCase(uf,vp.categoryBefore2);
    						this.dbUnknown.insertUnknownCase(uc,typeReading);
    					}
    				}
    			}
    			// update variables
				vp.updateVariables();
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
    		this.dbKnown.executeCommit();
        	this.dbUnknown.executeCommit();
    	}
    	catch(SQLException e) { 
        	System.err.println(e);
        	this.dbKnown.executeRollback();
        	this.dbUnknown.executeRollback();
        }
    }
	
	private void createTrainIdFile() {
		try {
			String fileName = "ids_train" + dataSet + ".txt";
			File err = new File(fileName);
            if (err.exists())
                err.delete();
            err.createNewFile();
            if (!err.exists())
            	System.err.println("�Error in file!");
           	RandomAccessFile rw = new RandomAccessFile(err,"rw");
			rw.writeBytes(this.trainId);
			rw.close();
		}
		catch(IOException e) { 
        	System.err.println(e);
        }
	}
	
	/*public static void main(String[] args) {
		DBMySQL db = new DBMySQL(MentionConstant.MYSQL_MENTION);
		DBMySQL dbMoara = new DBMySQL(Constant.MYSQL_MOARA);
		CreateCaseBase app = new CreateCaseBase(Constant.ORGANISM_YEAST,true,db,dbMoara);
		app.train(true,Constant.CORPUS_TRAINING);
	}*/
}


