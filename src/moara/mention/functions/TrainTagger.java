package moara.mention.functions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import moara.corpora.biocreative.dbs.DBDocumentGene;
import moara.corpora.biocreative.dbs.DBDocumentSet;
import moara.mention.MentionConstant;
import moara.mention.dbs.DBKnownCase;
import moara.mention.dbs.DBUnknownCase;
import moara.mention.entities.GeneMention;
import moara.util.Constant;

public class TrainTagger {

	private String dataModel;
	private DBKnownCase dbKnownCase;
	private DBUnknownCase dbUnknownCase;
	private String taggerId;
	
	public TrainTagger() {
		this.taggerId = MentionConstant.TABLE_USER;
	}
	
	public TrainTagger(String id) {
		this.taggerId = MentionConstant.TABLE_USER + "_" + id;
	}
	
	public void useDataModel(String model) {
		this.dataModel = model;
	}
	
	public void readDocuments(String file) {
		System.err.println("Reading training documents...");
		ReadBioCreativeFiles r = new ReadBioCreativeFiles();
		r.readDocumentFile(file,MentionConstant.CORPUS_USER);
	}
	
	public void readAnnotations(String file) {
		System.err.println("Reading training annotations...");
		ReadBioCreativeFiles r = new ReadBioCreativeFiles();
		r.readAnnotationFile(file,MentionConstant.CORPUS_USER,MentionConstant.MENTION_COMPLETE);
	}
	
	private void createTables() {
		try {
			this.dbKnownCase.createKnownCase();
			this.dbUnknownCase.createUnknownCase();
			this.dbKnownCase.executeCommit();
			this.dbUnknownCase.executeCommit();
		}
		catch(SQLException e) { 
        	System.err.println(e);
        	this.dbKnownCase.executeRollback();
        	this.dbUnknownCase.executeRollback();
        }
	}
	
	public void train() {
		try {
			this.dbKnownCase = new DBKnownCase(this.taggerId);
			this.dbUnknownCase = new DBUnknownCase(this.taggerId);
			DBDocumentSet dbDocumentSet = new DBDocumentSet("moara_mention");
			DBDocumentGene dbDocumentGene = new DBDocumentGene("moara_mention");
			//create tables
			if (!this.taggerId.equals(MentionConstant.TABLE_USER)) {
				System.err.println("Creating tables...");
				createTables();
			}
			// copy data
			System.err.println("Copying data...");
			copyData();
			// train tagger
			CreateCaseBase ccb = new CreateCaseBase(this.taggerId,false);
			Vector<String> ids = dbDocumentSet.getAllDocIdTrain(MentionConstant.CORPUS_USER,
				0,false,false,false);
			System.err.println("Training the tagger..." + ids.size() + " documents");
			int count = 0;
			for (int i=0; i<ids.size(); i++) {
    			String id = ids.get(i);
    			String text = dbDocumentSet.getTextDocument(MentionConstant.CORPUS_USER,id);
    			ArrayList<GeneMention> indexes = dbDocumentGene.getOffsetDocument(MentionConstant.CORPUS_USER,
        			id,MentionConstant.MENTION_COMPLETE,Constant.DATA_GOLD_STANDARD);
    			ccb.train(id,text,indexes);
    			count++;
    			System.err.print(id + "..." + count + "...");
			}
			ccb.end();
			this.dbKnownCase.executeCommit();
			this.dbUnknownCase.executeCommit();
		}
		catch(SQLException e) { 
        	System.err.println(e);
        	this.dbKnownCase.executeRollback();
			this.dbUnknownCase.executeRollback();
        }
	}
	
	private void copyData() {
		try {
			// clean old data
			this.dbKnownCase.truncateKnownCase();
			this.dbUnknownCase.truncateUnknownCase();
			if (this.dataModel!=null) {
				this.dbKnownCase.copyKnownCase(this.dataModel);
				this.dbUnknownCase.copyUnknownCase(this.dataModel);
			}
			this.dbKnownCase.executeCommit();
			this.dbUnknownCase.executeCommit();
		}
		catch(SQLException e) { 
        	System.err.println(e);
        	this.dbKnownCase.executeRollback();
			this.dbUnknownCase.executeRollback();
        }
	}
	
	public static void main(String[] args) {
		TrainTagger app = new TrainTagger("id");
		app.useDataModel("");
		app.readDocuments("mention/train_tagger/train.in");
		app.readAnnotations("mention/train_tagger/annotations.txt");
		app.train();
	}
	
}
