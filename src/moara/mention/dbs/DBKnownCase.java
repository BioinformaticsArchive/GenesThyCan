package moara.mention.dbs;

import moara.dbs.DBMoaraMention;
import moara.mention.MentionConstant;
import moara.mention.entities.Feature;
import moara.mention.entities.KnownCase;
import moara.mention.entities.TypeCase;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DBKnownCase extends DBMoaraMention {
	
	// type of category
	private TypeCase tc;
	// table name
	private String table;
	// levels of features
	private boolean useCategoryBefore1;
	
	public DBKnownCase(String model) {
		super();
		this.table = decideTableName(model);
		// default level
		this.useCategoryBefore1 = true;
	}
	
	public void setUseCategoryBefore1(boolean set) {
		this.useCategoryBefore1 = set;
	}
	
	public void setTypeCase(TypeCase tc) {
		this.tc = tc;
	}
	
	private String decideTableName(String model) {
		if (model.equals(""))
			return "known_case";
		else {
			return "known_case_" + model;
		}
	}
	
	public void copyKnownCase(String origin) throws SQLException {
		String copy = "";
		try {
	    	Statement stmt = conn.createStatement();
	    	copy = "insert into " + this.table + " (word, category, " +
	    		"category_before_1, frequency, reading, type_known_case) " +
	    		"select word, category, category_before_1, frequency, reading, " +
	    		"type_known_case from " + decideTableName(origin);
	    	stmt.executeUpdate(copy);
	    	stmt.close();
		}
		catch(SQLException ex) { 
	     	System.err.println(copy);
	     	throw ex; 
	   	}
	}
	
	public void insertKnownCase(KnownCase kc, String typeReading) throws SQLException {
	 	String insert_update = "";
	 	try {
		    Statement stmt = conn.createStatement();
	 		Feature f = kc.Feature();
		    String query = "select * from " + this.table + " where reading='" + typeReading +
		    	"' and category='" + kc.Category() + "' and type_known_case='" +
		    	kc.TypeKnownCase() + "'";
		    String where = getWhereFeatures(f);
		    query += where;
		    //System.err.println(query);
		    ResultSet res  = stmt.executeQuery(query);
	 		if (!res.next()) {
	 			Statement stmt2 = conn.createStatement();
	 			insert_update = "insert into " + this.table + " (reading,category,frequency,type_known_case";
	 			insert_update += getFromFeatures(f);
	 			insert_update += ") values('" + typeReading + "','" + kc.Category() + "',1,'" + 
	 				kc.TypeKnownCase() + "'";
	 			insert_update += getValuesFeatures(f);
	 			insert_update += ")";
	 			//System.err.println(insert_update);
	 			stmt2.executeUpdate(insert_update);
				stmt2.close();	
	 		}
	 		else {
	 			int totalFrequency = res.getInt("frequency");
	 			totalFrequency++;
	 			Statement stmt3 = conn.createStatement();
	 			insert_update = "update " + this.table + " set frequency=" + totalFrequency + 
	 				" where reading='" + typeReading + "' and category='" + kc.Category() + 
	 				"' and type_known_case='" + kc.TypeKnownCase() + "'";
	 			insert_update += where;
	 			//System.err.println(insert_update);
	 			stmt3.executeUpdate(insert_update);
				stmt3.close();
	 		}
	 		stmt.close();
		}
		catch(SQLException ex) { 
	     	System.err.println(insert_update);
	     	throw ex;
	   	}
	 }
 
	 public void truncateKnownCase() throws SQLException {
	 	String truncate = "";
	 	try {
		    	Statement stmt = conn.createStatement();
		    	truncate = "truncate " + this.table;
		    	stmt.executeUpdate(truncate);
		    	stmt.close();
		    }
			catch(SQLException ex) { 
	     	System.err.println(truncate);
	     	throw ex; 
	   	}
	 }
 
	 public KnownCase getMaxFreqKnownCase(KnownCase kc, String typeReading) throws SQLException {
	 	String select = "";
	 	try {
	 		ArrayList<String> cases = kc.getKnownWords();
	 		for (int i=0; i<cases.size(); i++) {
	 			String word = cases.get(i);
	 			kc.Feature().setStrValue(MentionConstant.FEATURE_WORD,word);
	 			Statement stmt = conn.createStatement();
	 			select = "select frequency, sequential, category from " + this.table + " where " +
		    		"reading='" + typeReading + "' and type_known_case='" + 
		    		kc.TypeKnownCase() + "'";
		    	select += getWhereFeatures(kc.Feature());
		    	select += " group by category order by frequency desc";
		    	//System.err.println(select);
		    	int lastFrequency = 0;
		    	ResultSet res  = stmt.executeQuery(select);
		    	while (res.next()) {
		    		if (res.getInt("frequency")<lastFrequency)
		    			break;
		    		kc.setSequential(res.getInt("sequential"));
		    		kc.setCategory(res.getString("category"));
		        	kc.setFrequency(res.getInt("frequency"));
		        	if (tc.isGeneMentionCategory(kc.Category()))
		    			break;
		        	lastFrequency = kc.Frequency();
		    	}
		    	/*if (kc.Feature().getStrValue(MentionConstant.FEATURE_WORD).equals("Cdc42"))
		    		System.out.println(kc.Feature().getStrValue(MentionConstant.FEATURE_WORD) + kc.Sequential());*/
		    	/*if (res.next()) {
		    		kc.setSequential(res.getInt("sequential"));
		    		kc.setCategory(res.getString("category"));
		        	kc.setFrequency(res.getInt("frequency"));
		        	// next result set, if draw, nothing
		        	if (res.next() && res.getInt("frequency")==kc.Frequency()) {
		        		// if backwards, forwards will decide
		        		if (typeReading.equals(MentionConstant.READING_BACKWARD)) {
		        			kc.setSequential(0);
		        			kc.setCategory(null);
		        			kc.setFrequency(0);
		        		}
		        		// if forwards, decide for gene mention
		        		else if (tc.isGeneMentionCategory(res.getString("category"))) {
		        			kc.setSequential(res.getInt("sequential"));
		        			kc.setCategory(res.getString("category"));
		        			kc.setFrequency(res.getInt("frequency"));
		        		}
		        	}
		        	stmt.close();
		        	break;
		        }*/
		    	stmt.close();
	 		}
	 		return kc;
		}
		catch(SQLException ex) { 
	     	System.err.println(select);
	     	throw ex;
	    }
	 }
	 
	 private String getWhereFeatures(Feature f) {
		 String where = "";
		 for (int i=0; i<f.FeatureSet().length; i++) {
 			String feature = f.FeatureSet()[i];
 			if (feature.equals(MentionConstant.FEATURE_WORD))
 				where += " and word=\"" + f.getStrValue(feature) + "\"";
 				//where += " and word COLLATE latin1_general_cs =\"" + f.getStrValue(feature) + "\"";
 			else if (this.useCategoryBefore1 && feature.equals(MentionConstant.FEATURE_CATEGORY_BEFORE_1))
 				where += " and category_before_1='" + f.getStrValue(feature) + "'";
 			/*else if (feature.equals(Constant.FEATURE_CATEGORY_BEFORE_2))
 				where += " and category_before_2='" + f.getStrValue(feature) + "'";
 			else if (this.useWordBefore1 && feature.equals(MentionConstant.FEATURE_WORD_BEFORE_1))
 				where += " and word_before_1=\"" + f.getStrValue(feature) + "\"";
 			else if (feature.equals(Constant.FEATURE_WORD_BEFORE_2))
 				where += " and word_before_2=\"" + f.getStrValue(feature) + "\"";
 			else if (feature.equals(Constant.FEATURE_WORD_AFTER_1))
 				where += " and word_after_1=\"" + f.getStrValue(feature) + "\"";
 			else if (feature.equals(Constant.FEATURE_WORD_AFTER_2))
 				where += " and word_after_2=\"" + f.getStrValue(feature) + "\"";
 			else if (feature.equals(Constant.FEATURE_POSTAG))
 				where += " and postag='" + f.getStrValue(feature) + "'";
 			else if (feature.equals(Constant.FEATURE_POSTAG_BEFORE_1))
 				where += " and postag_before_1='" + f.getStrValue(feature) + "'";
 			else if (feature.equals(Constant.FEATURE_POSTAG_BEFORE_2))
 				where += " and postag_before_2='" + f.getStrValue(feature) + "'";
 			else if (feature.equals(Constant.FEATURE_POSTAG_AFTER_1))
 				where += " and postag_after_1='" + f.getStrValue(feature) + "'";
 			else if (feature.equals(Constant.FEATURE_POSTAG_AFTER_2))
 				where += " and postag_after_2='" + f.getStrValue(feature) + "'";*/
 		}
		 return where;
	 }
	 
	 private String getFromFeatures(Feature f) {
		 String from = "";
		 for (int i=0; i<f.FeatureSet().length; i++) {
 			String feature = f.FeatureSet()[i];
 			if (feature.equals(MentionConstant.FEATURE_WORD))
 				from += ",word";
 			else if (this.useCategoryBefore1 && feature.equals(MentionConstant.FEATURE_CATEGORY_BEFORE_1))
 				from += ",category_before_1";
 			/*else if (feature.equals(Constant.FEATURE_CATEGORY_BEFORE_2))
 				from += ",category_before_2";
 			else if (this.useWordBefore1 && feature.equals(MentionConstant.FEATURE_WORD_BEFORE_1))
 				from += ",word_before_1";
 			else if (feature.equals(Constant.FEATURE_WORD_BEFORE_2))
 				from += ",word_before_2";
 			else if (feature.equals(Constant.FEATURE_WORD_AFTER_1))
 				from += ",word_after_1";
 			else if (feature.equals(Constant.FEATURE_WORD_AFTER_2))
 				from += ",word_after_2";
 			else if (feature.equals(Constant.FEATURE_POSTAG))
 				from += ",postag";
 			else if (feature.equals(Constant.FEATURE_POSTAG_BEFORE_1))
 				from += ",postag_before_1";
 			else if (feature.equals(Constant.FEATURE_POSTAG_BEFORE_2))
 				from += ",postag_before_2";
 			else if (feature.equals(Constant.FEATURE_POSTAG_AFTER_1))
 				from += ",postag_after_1";
 			else if (feature.equals(Constant.FEATURE_POSTAG_AFTER_2))
 				from += ",postag_after_2";*/
 		}
		 return from;
	 }
	 
	 private String getValuesFeatures(Feature f) {
		 String values = "";
		 for (int i=0; i<f.FeatureSet().length; i++) {
 			String feature = f.FeatureSet()[i];
 			if (feature.equals(MentionConstant.FEATURE_WORD))
 				values += ",\"" + f.getStrValue(feature) + "\"";
 			else if (this.useCategoryBefore1 && feature.equals(MentionConstant.FEATURE_CATEGORY_BEFORE_1))
 				values += ",'" + f.getStrValue(feature) + "'";
 			/*else if (feature.equals(MentionConstant.FEATURE_CATEGORY_BEFORE_2))
 				values += ",'" + f.getStrValue(feature) + "'";
 			else if (this.useWordBefore1 && feature.equals(MentionConstant.FEATURE_WORD_BEFORE_1))
 				values += ",\"" + f.getStrValue(feature) + "\"";
 			else if (feature.equals(Constant.FEATURE_WORD_BEFORE_2))
 				values += ",\"" + f.getStrValue(feature) + "\"";
 			else if (feature.equals(Constant.FEATURE_WORD_AFTER_1))
 				values += ",\"" + f.getStrValue(feature) + "\"";
 			else if (feature.equals(Constant.FEATURE_WORD_AFTER_2))
 				values += ",\"" + f.getStrValue(feature) + "\"";
 			else if (feature.equals(Constant.FEATURE_POSTAG))
 				values += ",'" + f.getStrValue(feature) + "'";
 			else if (feature.equals(Constant.FEATURE_POSTAG_BEFORE_1))
 				values += ",'" + f.getStrValue(feature) + "'";
 			else if (feature.equals(Constant.FEATURE_POSTAG_BEFORE_2))
 				values += ",'" + f.getStrValue(feature) + "'";
 			else if (feature.equals(Constant.FEATURE_POSTAG_AFTER_1))
 				values += ",'" + f.getStrValue(feature) + "'";
 			else if (feature.equals(Constant.FEATURE_POSTAG_AFTER_2))
 				values += ",'" + f.getStrValue(feature) + "'";*/
 		}
		 return values;
	 }
	 
	 public void getListWords() throws SQLException {
	 	String select = "";
	 	try {
	 		String fileName = "mention/words_bc2_" + this.table + ".txt";
    		File f = new File(fileName);
            if (f.exists())
            	f.delete();
            f.createNewFile();
            if (!f.exists())
            	System.err.println("¡Error in file!");
            RandomAccessFile rw = new RandomAccessFile(f,"rw");
	 		Statement stmt = conn.createStatement();
		    select = "select word from " + this.table + " where word>='a' and " +
		    	"word<='zz' and category='T'";
		    ResultSet res  = stmt.executeQuery(select);
		    int count = 0;
		    String words = "";
	 		while (res.next()) {
	 			words += res.getString("word") + " ";
	 			count++;
	 			if (count==20) {
	 				rw.writeBytes(words + "\n");
	 				words = "";
	 				count = 0;
	 			}
	 		}
	 		rw.close();
	 		stmt.close();
		}
		catch(SQLException ex) { 
	     	System.err.println(select);
	     	throw ex;
	   	}
		catch(IOException e) { 
        	System.err.println(e);
        }
	 }
	 
	 public void createKnownCase() throws SQLException {
	 	String create = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	create = "create table " + this.table + " select * from known_case_user";
	    	stmt.executeUpdate(create);
	    	create = "truncate " + this.table;
	    	stmt.executeUpdate(create);
	    	create = "alter table " + this.table + " add primary key (sequential)";
	    	stmt.executeUpdate(create);
	    	create = "alter table " + this.table + " add index " +
	    		"Index_2(word,category,category_before_1,reading,type_known_case)";
	    	stmt.executeUpdate(create);
	    	create = "alter table " + this.table + " modify sequential integer " +
	    		"unsigned not null default null auto_increment";
	    	stmt.executeUpdate(create);
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.err.println(create);
	     	throw ex; 
	   	}
	 }
	 
	 public void dropKnownCase() throws SQLException {
	 	String delete = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	delete = "drop table " + this.table;
	    	stmt.executeUpdate(delete);
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.err.println(delete);
	     	throw ex; 
	   	}
	 }
	 
	 public static void main (String[] args) {
		try {
			DBKnownCase app = new DBKnownCase(null);
			app.getListWords();
		}
		catch(SQLException ex) { 
	     	System.err.println(ex);
	    }
	 }
	 
}

