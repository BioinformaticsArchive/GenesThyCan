package moara.mention.dbs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;

import moara.dbs.DBMoaraMention;
import moara.mention.MentionConstant;
import moara.mention.entities.Feature;
import moara.mention.entities.TypeCase;
import moara.mention.entities.UnknownCase;

public class DBUnknownCase extends DBMoaraMention {
	
	// type of category
	private TypeCase tc;
	// table name
	private String table;
	// levels of features
	private boolean useCategoryBefore1;
	
	public DBUnknownCase(String model) {
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
			return "unknown_case";
		else {
			return "unknown_case_" + model;
		}
	}
	
	public void copyUnknownCase(String origin) throws SQLException {
		String copy = "";
		try {
		    	Statement stmt = conn.createStatement();
		    	copy = "insert into " + this.table + " (format_text, category, " +
		    		"category_before_1, frequency, example, suffix4, reading) " +
		    		"select format_text, category, category_before_1, frequency, " +
		    		"example, suffix4, reading from " + decideTableName(origin);
		    	stmt.executeUpdate(copy);
		    	stmt.close();
		    }
			catch(SQLException ex) { 
	     	System.err.println(copy);
	     	throw ex; 
	   	}
	}
	
	public void insertUnknownCase(UnknownCase uc, String typeReading) throws SQLException {
	 	String insert_update = "";
	 	try {
	 		//System.err.println("(" + uc.Example() + ")---> " + uc.Feature().getStrValue(MentionConstant.FEATURE_FORMAT));
	 		StringTokenizer st = new StringTokenizer(uc.Feature().getStrValue(MentionConstant.FEATURE_FORMAT),
	 			MentionConstant.SYMBOL_FORMAT_SEPARATION);
			while (st.hasMoreTokens()) {
				String format = st.nextToken();
				uc.Feature().setFormatBefore1(format);
				Statement stmt = conn.createStatement();
		 		Feature f = uc.Feature();
		 		String query = "select * from " + this.table + " where reading='" + typeReading + 
		 			"' and category='" + uc.Category() + "'";
		 		String where = getWhereFeatures(f);
		 		query += where;
		 		ResultSet res  = stmt.executeQuery(query);
		 		if (!res.next()) {
		 			Statement stmt2 = conn.createStatement();
		 			insert_update = "insert into " + this.table + " (reading,category,frequency,example";
		 			insert_update += getFromFeatures(f);
		 			insert_update += ") values('" + typeReading + "','" + uc.Category() + 
		 				"',1,\"" + uc.Example() + "\"";
		 			insert_update += getValuesFeatures(f);
		 			insert_update += ")";
		 			stmt2.executeUpdate(insert_update);
		 			stmt2.close();	
		 		}
		 		else {
		 			int totalFrequency = res.getInt("frequency");
		 			totalFrequency++;
		 			Statement stmt3 = conn.createStatement();
		 			insert_update = "update " + this.table + " set frequency=" + totalFrequency + 
		 				" where reading='" + typeReading + "' and category='" + 
		 				uc.Category() + "'";
		 			insert_update += where;
		 			stmt3.executeUpdate(insert_update);
					stmt3.close();
		 		}
		 		stmt.close();
			}
	 		//System.err.println(insert_update);
		}
		catch(SQLException ex) { 
	     	System.err.println(insert_update);
	     	throw ex;
	   	}
	 }
	
	public void truncateUnknownCase() throws SQLException {
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
 
	 public UnknownCase getMaxFreqUnknownCase(UnknownCase uc, String typeReading) throws SQLException {
		String select = "";
		try {
			Statement stmt = conn.createStatement();
			Feature f = uc.Feature();
			select = "select frequency, sequential, category from " + this.table + " where " +
				"reading='" + typeReading + "'";
			select += getWhereFeatures(f);
			select += " group by category order by frequency desc";
			//System.err.println(select);
			int lastFrequency = 0;
			ResultSet res  = stmt.executeQuery(select);
			while (res.next()) {
	    		if (res.getInt("frequency")<lastFrequency)
	    			break;
	    		uc.setSequential(res.getInt("sequential"));
	    		uc.setCategory(res.getString("category"));
	        	uc.setFrequency(res.getInt("frequency"));
	        	if (tc.isGeneMentionCategory(uc.Category()))
	    			break;
	        	lastFrequency = uc.Frequency();
	    	}
			/*if (res.next()) {
	    		uc.setSequential(res.getInt("sequential"));
	    		uc.setCategory(res.getString("category"));
	    		uc.setFrequency(res.getInt("frequency"));
	        	// next result set, if draw, nothing
	    		if (res.next() && res.getInt("frequency")==uc.Frequency()) {
	        		// if backwards, forwards will decide
	        		if (typeReading.equals(MentionConstant.READING_BACKWARD)) {
	        			uc.setSequential(0);
	        			uc.setCategory(null);
	        			uc.setFrequency(0);
	        		}
	        		// if forwards, decide for gene mention
	        		else if (tc.isGeneMentionCategory(res.getString("category"))) {
	        			uc.setSequential(res.getInt("sequential"));
	        			uc.setCategory(res.getString("category"));
	        			uc.setFrequency(res.getInt("frequency"));
	        		}
	        	}
	    	}*/
			stmt.close();
			return uc;
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
 			if (feature.equals(MentionConstant.FEATURE_FORMAT))
 				where += " and format_text=\"" + f.getStrValue(feature) + "\"";
 				//where += " and format_text COLLATE latin1_general_cs =\"" + f.getStrValue(feature) + "\"";
 			else if (this.useCategoryBefore1 && feature.equals(MentionConstant.FEATURE_CATEGORY_BEFORE_1))
 				where += " and category_before_1='" + f.getStrValue(feature) + "'";
 			/*else if (feature.equals(Constant.FEATURE_CATEGORY_BEFORE_2))
 				where += " and category_before_2='" + f.getStrValue(feature) + "'";
 			else if (this.useFormatBefore1 && feature.equals(MentionConstant.FEATURE_FORMAT_BEFORE_1))
 				where += " and format_before_1=\"" + f.getStrValue(feature) + "\"";
 			else if (feature.equals(Constant.FEATURE_FORMAT_BEFORE_2))
 				where += " and format_before_2=\"" + f.getStrValue(feature) + "\"";
 			else if (feature.equals(Constant.FEATURE_FORMAT_AFTER_1))
 				where += " and format_after_1=\"" + f.getStrValue(feature) + "\"";
 			else if (feature.equals(Constant.FEATURE_FORMAT_AFTER_2))
 				where += " and format_after_2=\"" + f.getStrValue(feature) + "\"";
 			else if (feature.equals(Constant.FEATURE_POSTAG))
 				where += " and postag='" + f.getStrValue(feature) + "'";
 			else if (feature.equals(Constant.FEATURE_POSTAG_BEFORE_1))
 				where += " and postag_before_1='" + f.getStrValue(feature) + "'";
 			else if (feature.equals(Constant.FEATURE_POSTAG_BEFORE_2))
 				where += " and postag_before_2='" + f.getStrValue(feature) + "'";
 			else if (feature.equals(Constant.FEATURE_POSTAG_AFTER_1))
 				where += " and postag_after_1='" + f.getStrValue(feature) + "'";
 			else if (feature.equals(Constant.FEATURE_POSTAG_AFTER_2))
 				where += " and postag_after_2='" + f.getStrValue(feature) + "'";
 			else if (feature.equals(Constant.FEATURE_SUFFIX3))
 				where += " and suffix3=\"" + f.getStrValue(feature) + "\"";
 			else if (feature.equals(Constant.FEATURE_SUFFIX4))
 				where += " and suffix4=\"" + f.getStrValue(feature) + "\"";
 			else if (feature.equals(Constant.FEATURE_PREFIX3))
 				where += " and prefix3=\"" + f.getStrValue(feature) + "\"";
 			else if (feature.equals(Constant.FEATURE_PREFIX4))
 				where += " and prefix4=\"" + f.getStrValue(feature) + "\"";*/
 		}
		 return where;
	 }
	 
	 private String getFromFeatures(Feature f) {
		 String from = "";
		 for (int i=0; i<f.FeatureSet().length; i++) {
 			String feature = f.FeatureSet()[i];
 			if (feature.equals(MentionConstant.FEATURE_FORMAT))
 				from += ",format_text";
 			else if (this.useCategoryBefore1 && feature.equals(MentionConstant.FEATURE_CATEGORY_BEFORE_1))
 				from += ",category_before_1";
 			/*else if (feature.equals(Constant.FEATURE_CATEGORY_BEFORE_2))
 				from += ",category_before_2";
 			else if (this.useFormatBefore1 && feature.equals(MentionConstant.FEATURE_FORMAT_BEFORE_1))
 				from += ",format_before_1";
 			else if (feature.equals(Constant.FEATURE_FORMAT_BEFORE_2))
 				from += ",format_before_2";
 			else if (feature.equals(Constant.FEATURE_FORMAT_AFTER_1))
 				from += ",format_after_1";
 			else if (feature.equals(Constant.FEATURE_FORMAT_AFTER_2))
 				from += ",format_after_2";
 			else if (feature.equals(Constant.FEATURE_POSTAG))
 				from += ",postag";
 			else if (feature.equals(Constant.FEATURE_POSTAG_BEFORE_1))
 				from += ",postag_before_1";
 			else if (feature.equals(Constant.FEATURE_POSTAG_BEFORE_2))
 				from += ",postag_before_2";
 			else if (feature.equals(Constant.FEATURE_POSTAG_AFTER_1))
 				from += ",postag_after_1";
 			else if (feature.equals(Constant.FEATURE_POSTAG_AFTER_2))
 				from += ",postag_after_2";
 			else if (feature.equals(Constant.FEATURE_SUFFIX3))
 				from += ",suffix3";
 			else if (feature.equals(Constant.FEATURE_SUFFIX4))
 				from += ",suffix4";
 			else if (feature.equals(Constant.FEATURE_PREFIX3))
 				from += ",prefix3";
 			else if (feature.equals(Constant.FEATURE_PREFIX4))
 				from += ",prefix4";*/
 		}
		 return from;
	 }
	 
	 private String getValuesFeatures(Feature f) {
		 String values = "";
		 for (int i=0; i<f.FeatureSet().length; i++) {
 			String feature = f.FeatureSet()[i];
 			if (feature.equals(MentionConstant.FEATURE_FORMAT))
 				values += ",\"" + f.getStrValue(feature) + "\"";
 			else if (this.useCategoryBefore1 && feature.equals(MentionConstant.FEATURE_CATEGORY_BEFORE_1))
 				values += ",'" + f.getStrValue(feature) + "'";
 			/*else if (feature.equals(Constant.FEATURE_CATEGORY_BEFORE_2))
 				values += ",'" + f.getStrValue(feature) + "'";
 			else if (this.useFormatBefore1 && feature.equals(MentionConstant.FEATURE_FORMAT_BEFORE_1))
 				values += ",\"" + f.getStrValue(feature) + "\"";
 			else if (feature.equals(Constant.FEATURE_FORMAT_BEFORE_2))
 				values += ",\"" + f.getStrValue(feature) + "\"";
 			else if (feature.equals(Constant.FEATURE_FORMAT_AFTER_1))
 				values += ",\"" + f.getStrValue(feature) + "\"";
 			else if (feature.equals(Constant.FEATURE_FORMAT_AFTER_2))
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
 				values += ",'" + f.getStrValue(feature) + "'";
 			else if (feature.equals(Constant.FEATURE_SUFFIX3))
 				values += ",\"" + f.getStrValue(feature) + "\"";
 			else if (feature.equals(Constant.FEATURE_SUFFIX4))
 				values += ",\"" + f.getStrValue(feature) + "\"";
 			else if (feature.equals(Constant.FEATURE_PREFIX3))
 				values += ",\"" + f.getStrValue(feature) + "\"";
 			else if (feature.equals(Constant.FEATURE_PREFIX4))
 				values += ",\"" + f.getStrValue(feature) + "\"";*/
 		}
		 return values;
	 }
	 
	 public void createUnknownCase() throws SQLException {
	 	String create = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	create = "create table " + this.table + " select * from unknown_case_user";
	    	stmt.executeUpdate(create);
	    	create = "truncate " + this.table;
	    	stmt.executeUpdate(create);
	    	create = "alter table " + this.table + " add primary key (sequential)";
	    	stmt.executeUpdate(create);
	    	create = "alter table " + this.table + " add index " +
	    		"Index_2(category,format_text,category_before_1,reading)";
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
 
}

