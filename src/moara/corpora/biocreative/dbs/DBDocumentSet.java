package moara.corpora.biocreative.dbs;

import java.sql.*; 
import java.util.*;
import moara.dbs.DBMention;
import moara.mention.MentionConstant;

public class DBDocumentSet extends DBMention {
	
	public DBDocumentSet(String database) {
		super(database);
	}
	
	public void insertDocument(String id, String type, String text, int dataset) throws SQLException {
	 	String insert = "";
	 	try {
		   	Statement stmt = conn.createStatement();
	 		insert = "insert into document_set (id, type, text, data_set) " +
	 			"values('" + id + "','" + type + "',\"" + text + "\"," +
	 			dataset + ")";
	 		stmt.executeUpdate(insert);
			stmt.close();
		}
		catch(SQLException ex) { 
	     	System.err.println(insert);
	     	throw ex;
	   	}
	 }
	 
	 public void truncateDocument() throws SQLException {
	 	String truncate = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	truncate = "truncate document_set";
	    	stmt.executeUpdate(truncate);
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.err.println(truncate);
	     	throw ex; 
	   	}
	 }
	 
	 public void deleteDoc(String id) throws SQLException {
		String delete = "";
	 	try {
 			Statement stmt = conn.createStatement();
 			delete = "delete from document_set where id='" + id + "'";
 			stmt.executeUpdate(delete);
 			stmt.close();
		}
		catch(SQLException ex) { 
	     	System.err.println(delete);
	     	throw ex;
	   	}
	}
	 
	 public void deleteDocumentType(String type) throws SQLException {
	 	String delete = "";
	 	try {
		    Statement stmt = conn.createStatement();
		    delete = "delete from document_set where type='" + type + "'";
	 		stmt.executeUpdate(delete);
			stmt.close();
		}
		catch(SQLException ex) { 
	     	System.err.println(delete);
	     	throw ex;
	   	}
	 }
	 
	 public void deleteDocumentTypeDataset(String type, int dataSet) throws SQLException {
	 	String delete = "";
	 	try {
		    Statement stmt = conn.createStatement();
		    delete = "delete from document_set where type='" + type + 
		    	"' and data_set=" + dataSet;
	 		stmt.executeUpdate(delete);
			stmt.close();
		}
		catch(SQLException ex) { 
	     	System.err.println(delete);
	     	throw ex;
	   	}
	 }
	 
	 public Vector<String> getAllDocIdTrain(String corpus, int dataSet, boolean yeast, boolean mouse,
			boolean fly) throws SQLException {
	 	Vector<String> ids = new Vector<String>();
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select id from document_set where type='" + 
	    		corpus + "' and data_set in (";
	    	if (dataSet==0) {
	    		if (corpus.equals(MentionConstant.CORPUS_USER))
	    			select += "0,";
	    		else
	    			select += "1,2,3,4,5,6,7,8,9,10,";
	    	}
	    	else if (dataSet>0)
	    		for (int i=1; i<=10; i++)
	    			if (i!=dataSet)
	    				select += i + ",";
	    	if (yeast)
	    		select += MentionConstant.DATA_SET_YEAST + ",";
	    	if (mouse)
	    		select += MentionConstant.DATA_SET_MOUSE + ",";
	    	if (fly)
	    		select += MentionConstant.DATA_SET_FLY + ",";
	    	select = select.substring(0,select.length()-1) + ") order by id desc";
	    	//System.err.println(select);
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		ids.addElement(res.getString("id"));
	    	}
	    	stmt.close();
	    	ids.trimToSize();
	    	return ids;
		}
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	    }
	 }
	 
	 public ArrayList<String> getAllDocIdTest(String type, int dataSet, 
			boolean yeast, boolean mouse, boolean fly, boolean human) throws SQLException {
		 ArrayList<String> ids = new ArrayList<String>();
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select id from document_set where type='" + 
	    		type + "' and data_set in (";
	    	if (dataSet==0)
	    		select += "1,2,3,4,5,6,7,8,9,10,";
	    	else if (dataSet>0)
	    		select += dataSet + ",";
	    	if (yeast)
	    		select += MentionConstant.DATA_SET_YEAST + ",";
	    	if (mouse)
	    		select += MentionConstant.DATA_SET_MOUSE + ",";
	    	if (fly)
	    		select += MentionConstant.DATA_SET_FLY + ",";
	    	if (human)
	    		select += MentionConstant.DATA_SET_HUMAN + ",";
	    	select = select.substring(0,select.length()-1) + ") order by id";
	    	//System.err.println(select);
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		ids.add(res.getString("id"));
	    	}
	    	stmt.close();
	    	ids.trimToSize();
	    	return ids;
		}
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	    }
	 }
	 
	 /*public Vector getAllDocumentsId(String type, int dataSet, String typeTraining) throws SQLException {
	 	Vector ids = new Vector();
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select id from document_set where ";
	    	if (typeTraining.equals(Constant.DATA_TRAINING)) {
	    		if (dataSet>0 && type.equals(Constant.DATA_TRAINING))
	    			select += " type='" + typeTraining + "' and data_set<>" + dataSet;
	    		else if (dataSet>0 && type.equals(Constant.DATA_TEST))
	    			select += " type='" + typeTraining + "' and data_set=" + dataSet;
	    		else
	    			select += " type='" + type + "'";
	    	}
	    	else {
	    		if (dataSet>0 && type.equals(Constant.DATA_TRAINING))
	    			select += " type='" + typeTraining + "' and data_set<>" + dataSet;
	    		else if (dataSet>0 && type.equals(Constant.DATA_TEST))
	    			select += " type='" + typeTraining + "' and data_set=" + dataSet;
	    	}
	    	select += " order by id";
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		ids.addElement(res.getString("id"));
	    	}
	    	stmt.close();
	    	ids.trimToSize();
	    	return ids;
		}
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	    }
	 }
	 
	 public Vector getIdRandom(String type) throws SQLException {
	 	Vector ids = new Vector();
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select id from document_set where type='" + type + "' order by rand()";
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		ids.addElement(res.getString("id"));
	    	}
	    	stmt.close();
	    	ids.trimToSize();
	    	return ids;
		}
		catch(SQLException ex) { 
	     	System.err.println(select);
	     	throw ex;
	    }
	 }*/
	 
	 public void updateDataSet(String id, int dataSet, String type) throws SQLException {
	 	String update = "";
	 	try {
		    Statement stmt = conn.createStatement();
		    update = "update document_set set data_set=" + dataSet + " where id='" + id +
		    	"' and type='" + type + "'";
	 		stmt.executeUpdate(update);
			stmt.close();
		}
			catch(SQLException ex) { 
	     	System.err.println(update);
	     	throw ex;
	   	}
	 }
	 
	 public String getTextDocument(String type, String id) throws SQLException {
	 	String text = new String();
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select text from document_set where type='" + type + "' and id='" + id + "'";
	    	ResultSet res  = stmt.executeQuery(select);
	    	if (res.next()) {
	    		text = res.getString("text");
	    	}
	    	stmt.close();
	    	return text;
		 }
			catch(SQLException ex) { 
	     	System.err.println(select);
	     	throw ex;
	     }
	 }
 
}

