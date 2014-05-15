package moara.mention.functions;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.SQLException;
import java.util.StringTokenizer;
import moara.mention.MentionConstant;
import moara.corpora.biocreative.dbs.DBDocumentGene;
import moara.corpora.biocreative.dbs.DBDocumentSet;
import moara.util.Constant;
import java.util.HashMap;
import java.util.ArrayList;

public class ReadBioCreativeFiles {

	private DBDocumentGene dbDocGene;
	private DBDocumentSet dbDocSet;
	
	public ReadBioCreativeFiles() {
		this.dbDocGene = new DBDocumentGene("mention");
		this.dbDocSet = new DBDocumentSet("mention");
	}
	
	public void readDocumentFile(String file, String type) {
		try {
            if (type.equals(MentionConstant.CORPUS_USER))
            	this.dbDocSet.deleteDocumentTypeDataset(type,0);
            else  {
            	for (int i=1; i<=10; i++)
                	this.dbDocSet.deleteDocumentTypeDataset(type,i);
            }
            System.err.println("Processing data file..." + file);
            File in = new File(file);
            RandomAccessFile r = new RandomAccessFile(in,"r");
            int count = 0;
            String line = r.readLine();
            while (line!=null) {
                StringTokenizer tokens = new StringTokenizer(line);
                String id = "";
                if (tokens.hasMoreTokens())
                	id = tokens.nextToken();
                else
                	System.err.println("Error in id!");
                String text = line.substring(id.length()+1,line.length());
                if (type.equals(MentionConstant.CORPUS_USER))
                	id = MentionConstant.CORPUS_USER + "_" + id;
                text = text.replace("\"","'");
                this.dbDocSet.insertDocument(id,type,text,Constant.ALL_SET);
                count++;
				if ((count%1000)==0) {
					System.err.print(count + "...");
				}
                line = r.readLine();
            }
            r.close();
            this.dbDocSet.executeCommit();
        }
        catch (IOException e) {
            System.err.println(e);
            this.dbDocSet.executeRollback();
        }
        catch(SQLException e) { 
        	System.err.println(e);
        	this.dbDocSet.executeRollback();
        }
	}
	
	public void readAnnotationFile(String file, String type, String mentionType) {
        try {
            this.dbDocGene.deleteMentionsType(type);
        	System.err.println("Processing gene file..." + file);
            RandomAccessFile r = new RandomAccessFile(file,"r");
            int count = 0;
            int longGeneMention = 0;
            String line = r.readLine();
            while (line!=null) {
                StringTokenizer tokens = new StringTokenizer(line,"|");
                if (tokens.hasMoreTokens()) {
                    String id = tokens.nextToken();
                    if (type.equals(MentionConstant.CORPUS_USER))
                    	id = MentionConstant.CORPUS_USER + "_" + id;
                    String offset = tokens.nextToken();
                    String geneText = tokens.nextToken().replace("\"","'");
                    int startOffset = 0;
                    int endOffset = 0;
                    StringTokenizer offs = new StringTokenizer(offset);
                    if (offs.hasMoreTokens()) {
                    	startOffset = (new Integer(offs.nextToken())).intValue();
                    	endOffset = (new Integer(offs.nextToken())).intValue();
                    }
                    if (geneText.length()<=100)
                    	this.dbDocGene.insertDocumentGene(id,type,geneText,mentionType,startOffset,endOffset);
                    else
                    	longGeneMention++;
                }
                count++;
				if ( (count%1000)==0 ) {
					System.err.print(count + "...");
				}
                line = r.readLine();
            }
            //System.err.println("\nlong gene mention " + longGeneMention + "\n");
            r.close();
            this.dbDocGene.executeCommit();
        }
        catch (IOException e) {
            System.err.println(e);
            this.dbDocGene.executeRollback();
        }
        catch(SQLException e) { 
        	System.err.println(e);
        	this.dbDocGene.executeRollback();
        }
    }
	
	public void readOrganismMentionFile(String file, String organism, String type, int dataset) {
        try {
            this.dbDocGene.deleteMentionsType(type,dataset);
        	System.err.println("Processing gene file..." + file);
            RandomAccessFile r = new RandomAccessFile(file,"r");
            int count = 0;
            String line = r.readLine();
            while (line!=null) {
                StringTokenizer tokens = new StringTokenizer(line,"|");
                if (tokens.hasMoreTokens()) {
                    String id = tokens.nextToken();
                    id = organism + fillZeros(id.substring(organism.length(),id.length()));
                    String geneText = tokens.nextToken().replace("\"","'");
                    int start = (new Integer(tokens.nextToken())).intValue();
                    int end = (new Integer(tokens.nextToken())).intValue();
                    this.dbDocGene.insertDocumentGene(id,type,geneText,
                    	MentionConstant.MENTION_COMPLETE,start,end);
                }
                count++;
				if ( (count%1000)==0 ) {
					System.err.print(count + "...");
				}
                line = r.readLine();
            }
            //System.err.println("\nlong gene mention " + longGeneMention + "\n");
            r.close();
            this.dbDocGene.executeCommit();
        }
        catch (IOException e) {
            System.err.println(e);
            this.dbDocGene.executeRollback();
        }
        catch(SQLException e) { 
        	System.err.println(e);
        	this.dbDocGene.executeRollback();
        }
    }
	
	private String fillZeros(String number) {
		String results = "";
		for (int i=1; i<=(5-number.length()); i++) {
			results += "0";
		}
		return results + number;
	}
	
	private void calculateMacroResults(String file) {
		try {
			ArrayList<String> docs = new ArrayList<String>();
			HashMap<String,Integer> tps = new HashMap<String,Integer>();
			HashMap<String,Integer> fps = new HashMap<String,Integer>();
			HashMap<String,Integer> fns = new HashMap<String,Integer>();
			System.err.println("Macro file..." + file);
            File in = new File(file);
            RandomAccessFile r = new RandomAccessFile(in,"r");
            String line = r.readLine();
            while (line!=null && !line.equals("") && line.indexOf("|")>0) {
                //System.err.println(line);
            	StringTokenizer tokens = new StringTokenizer(line,"|");
                String status = tokens.nextToken();
                String id = tokens.nextToken();
                if (!docs.contains(id))
                	docs.add(id);
                if (status.equals("TP")) {
                	int frequency = 0;
                	if (tps.containsKey(id))
                		frequency = tps.get(id);
                	frequency++;
                	tps.put(id,frequency);	
                }
                else if (status.equals("FP")) {
                	int frequency = 0;
                	if (fps.containsKey(id))
                		frequency = fps.get(id);
                	frequency++;
                	fps.put(id,frequency);	
                }
                else if (status.equals("FN")) {
                	int frequency = 0;
                	if (fns.containsKey(id))
                		frequency = fns.get(id);
                	frequency++;
                	fns.put(id,frequency);	
                }
                line = r.readLine();
            }
            double totalP = 0;
            double totalR = 0;
            double totalFM = 0;
            System.err.println(tps.size());
            System.err.println(fps.size());
            System.err.println(fns.size());
            for (int i=0; i<docs.size(); i++) {
            	String id = docs.get(i);
            	int tp = 0;
            	if (tps.containsKey(id))
            		tp = tps.get(id);
            	int fp = 0;
            	if (fps.containsKey(id))
            		fp = fps.get(id);
            	int fn = 0;
            	if (fns.containsKey(id))
            		fn = fns.get(id);
            	double precision = 0;
            	if (tp!=0 || fp!=0)
            		precision = (double)tp/(double)(tp+fp); 
            	double recall = 0;
            	if (tp!=0 || fn!=0)
            		recall = (double)tp/(double)(tp+fn);
            	if (precision!=0 || recall!=0)
            		totalFM += 2*precision*recall/(precision+recall);
            	totalP += precision;
            	totalR += recall;
            }
            System.err.println("Precision: " + totalP/(double)docs.size());
            System.err.println("Recall: " + totalR/(double)docs.size());
            System.err.println("F-Measure: " + totalFM/(double)docs.size());
        }
        catch (IOException e) {
            System.err.println(e);
        }
	}
	
	public static void main(String[] args) {
		ReadBioCreativeFiles app = new ReadBioCreativeFiles();
		//app.readDocumentFile("dataset/mention/test/test.in","S");
		//app.readOrganismMentionFile("dataset/fly_mention_T.txt","fly","T",13);
		app.calculateMacroResults("mention/new_test/compare_training/result_S_10090_bc2.txt");
	}
	
}
