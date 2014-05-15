package moara.mention.functions;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.SQLException;
import java.util.ArrayList;

import moara.corpora.biocreative.dbs.DBDocumentGene;
import moara.corpora.biocreative.dbs.DBDocumentSet;
import moara.mention.MentionConstant;
import moara.mention.entities.GeneMention;
import moara.mention.entities.Mention;
import moara.util.Constant;
import moara.util.lexicon.Symbols;

public class OutputGeneMention {

	private RandomAccessFile output;
	
	public OutputGeneMention() {
		
	}
	
	public ArrayList<GeneMention> extractGeneMention(ArrayList<GeneMention> gms) {
		Symbols sym = new Symbols();
		ArrayList<GeneMention> mentions = new ArrayList<GeneMention>();
		int startMention = 0;
   		int endMention = -1;
   		int countMention = 0;
   		String mention = new String();
   		for (int j=0; j<gms.size(); j++) {
   			GeneMention gm = gms.get(j);
   			countMention++;
   			// first loop
   			if (endMention==-1) {
   				startMention = gm.Start();
   				mention = gm.Text();
   			}
   			// next loops
   			else {
   				// if gene mention continues
   				if (gm.Start()==endMention+1) {
   					if (sym.isSymbol(gm.Text()) || gm.Text().equals(")") ||
   						mention.endsWith("(") || gm.Text().equals("/") ||
   						mention.endsWith("/") || gm.Text().startsWith("-") ||
   						mention.endsWith("-") || gm.Text().startsWith("#") || 
   						mention.endsWith("#") || gm.Text().equals("+") ||
   						gm.Text().equals("."))
   						mention += gm.Text();
   					else
   						mention += " " + gm.Text();
   				}
   				else {
   					//rw.writeBytes(id + "|" + startMention + " " + endMention + "|" + mention + "\n");
   					GeneMention gm1 = new GeneMention(mention,startMention,endMention);
   					mentions.add(gm1);
   					//System.err.println(mention);
   					startMention = gm.Start();
   					mention = gm.Text();
   				}
   			}
   			endMention = gm.End();
   			if ((countMention%100)==0) {
				System.err.println(countMention + "...");
			}
   		}
   		// print last gene mention
   		//rw.writeBytes(id + "|" + startMention + " " + endMention + "|" + mention + "\n");
   		if (startMention!=-1 && endMention!=-1) {
   			GeneMention gm2 = new GeneMention(mention,startMention,endMention);
   			mentions.add(gm2);
   		}
   		mentions.trimToSize();
   		return mentions;
	}
	
	public void writeResultsFile(String number, int dataSet, String type,
			boolean yeast, boolean mouse, boolean fly, boolean human, 
			String directory) {
		System.err.println("Writing results...");
		try {
    		// output file for false negative errors
    		String fileName = directory+"mention_" + type;
    		if (yeast)
    			fileName += "_" + Constant.ORGANISM_YEAST;
    		if (mouse)
    			fileName += "_" + Constant.ORGANISM_MOUSE;
    		if (fly)
    			fileName += "_" + Constant.ORGANISM_FLY;
    		if (human)
    			fileName += "_" + Constant.ORGANISM_HUMAN;
    		if (number!=null)
    			fileName += number;
    		fileName += ".txt";
    		initFile(fileName);
           	// get test documents
            DBDocumentSet dbDocSet = new DBDocumentSet("moara_mention");
            ArrayList<String> docs = dbDocSet.getAllDocIdTest(type,dataSet,yeast,
           		mouse,fly,human);
           	//Vector docs = new Vector();
           	//docs.add("BC2GM001536665");
           	DBDocumentGene dbDocGene = new DBDocumentGene("moara_mention");
           	for (int i=0; i<docs.size(); i++) {
           		String id = docs.get(i);
           		ArrayList<GeneMention> gms = dbDocGene.getOffsetDocument(type,id,
           				MentionConstant.MENTION_COMPLETE,Constant.DATA_PREDICTION);
           		//System.err.println("print..." + id + "..." + gms.size());
           		if (gms.size()>0) {
           			ArrayList<GeneMention> mentions = extractGeneMention(gms);
           			for (int j=0; j<mentions.size(); j++) {
           				GeneMention gm = mentions.get(j);
           				writeMention(id,gm);
           			}
           		}
           	}
           	endFile();
        }
    	catch(SQLException e) { 
        	System.err.println(e);
        }
    }
	
	public void initFile(String fileName) {
		try {
			File out = new File(fileName);
            if (out.exists())
            	out.delete();
            out.createNewFile();
            if (!out.exists())
            	System.err.println("�Error in file!");
        	this.output = new RandomAccessFile(out,"rw");
		}
		catch(IOException e) { 
        	System.err.println(e);
        }
	}
	
	public void writeMention(String id, GeneMention gm) {
		try {
			this.output.writeBytes(id + "|" + gm.Start() + " " + gm.End() + "|" + gm.Text() + "\n");
		}
		catch(IOException e) { 
        	System.err.println(e);
        }
	}
	
	public void endFile() {
		try {
			this.output.close();
		}
		catch(IOException e) { 
        	System.err.println(e);
        }
	}
	
}


