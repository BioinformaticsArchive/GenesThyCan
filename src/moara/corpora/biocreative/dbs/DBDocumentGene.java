package moara.corpora.biocreative.dbs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

import moara.dbs.DBMention;
import moara.mention.MentionConstant;
import moara.mention.entities.GeneMention;
import moara.mention.entities.Mention;
import moara.util.Constant;

public class DBDocumentGene extends DBMention {
	
	public DBDocumentGene(String database) {
		super(database);
	}
	
	public void insertDocumentGene(String id, String type, String mention, String mentionType,
		int mentionStart, int mentionEnd) throws SQLException {
		String insert = "";
	 	try {
 			Statement stmt = conn.createStatement();
 			insert = "insert into document_gene (document_id, type, gene_mention, type_mention, start_mention, " +
 				"end_mention, type_data) values('" + id + "','" + type + "',\"" + mention + 
 				"\",'" + mentionType + "'," + mentionStart + "," + mentionEnd + ",'" + 
 				Constant.DATA_GOLD_STANDARD + "')";
 			stmt.executeUpdate(insert);
			stmt.close();
		}
		catch(SQLException ex) { 
	     	System.err.println(insert);
	     	throw ex;
	   	}
	}
	
	public void insertGeneMention(String id, String type, Mention gm, int sequential, 
			String typeCase, String tryCode, String reading) throws SQLException {
		String insert = "";
		try {
 			Statement stmt = conn.createStatement();
 			String query = "select * from document_gene where document_id='" + id + 
 				"' and type='" + type + "' and type_data='" + Constant.DATA_PREDICTION + 
 				"' and ((start_mention<=" + gm.Start() + " and end_mention>=" + gm.Start() + 
 				") or (start_mention<=" + gm.End() + " and end_mention>=" + gm.End() + ") or" +
 				" (start_mention>=" + gm.Start() + " and end_mention<=" + gm.End() + "))";
 			//System.err.println(query);
 			ResultSet res  = stmt.executeQuery(query);
	 		if (!res.next()) {
	 			Statement stmt2 = conn.createStatement();
	 			insert = "insert into document_gene (document_id, type, gene_mention, type_mention, " +
 					"start_mention, end_mention, sequential_case, type_case, category_mention, " +
 					"try_code, type_data, reading) values('" + id + "','" + type + "',\"" + 
 					gm.Text() + "\",'" + gm.Type() + "'," + gm.Start() + "," + gm.End() + 
 					"," + sequential + ",'" + typeCase + "','" + gm.Category() + "','" + tryCode + 
 					"','" + Constant.DATA_PREDICTION + "','" + reading + "')";
	 			//System.err.println(insert);
	 			stmt2.executeUpdate(insert);
	 			stmt2.close();
	 		}
	 		stmt.close();
		}
		catch(SQLException ex) { 
	     	System.err.println(insert);
	     	throw ex;
	   	}
	}
	
	public void deleteMentionsType(String type) throws SQLException {
		String delete = "";
	 	try {
 			Statement stmt = conn.createStatement();
 			delete = "delete from document_gene where type='" + type + "'";
 			stmt.executeUpdate(delete);
 			stmt.close();
		}
		catch(SQLException ex) { 
	     	System.err.println(delete);
	     	throw ex;
	   	}
	}
	
	public void deleteMentionsType(String type, int dataset) throws SQLException {
		String delete = "";
	 	try {
	 		Statement stmt = conn.createStatement();
 			String query = "select id from document_set where type='" + type + 
 				"' and data_set=" + dataset;
 			//System.err.println(query);
 			ResultSet res  = stmt.executeQuery(query);
	 		if (!res.next()) {
	 			Statement stmt2 = conn.createStatement();
	 			delete = "delete from document_gene where document_id='" + 
	 				res.getString("id") + " and type='" + type + "'";
	 			//System.err.println(insert);
	 			stmt2.executeUpdate(delete);
	 			stmt2.close();
	 		}
	 		stmt.close();
		}
		catch(SQLException ex) { 
	     	System.err.println(delete);
	     	throw ex;
	   	}
	}
	
	public void deleteMentionsDoc(String id) throws SQLException {
		String delete = "";
	 	try {
 			Statement stmt = conn.createStatement();
 			delete = "delete from document_gene where document_id='" + id + "'";
 			//System.err.println(delete);
 			stmt.executeUpdate(delete);
 			stmt.close();
		}
		catch(SQLException ex) { 
	     	System.err.println(delete);
	     	throw ex;
	   	}
	}
	
	public void deleteGeneMention(String id, int start, int end, String origin) throws SQLException {
		String delete = "";
	 	try {
 			Statement stmt = conn.createStatement();
 			delete = "delete from document_gene where type_data='" + 
 				Constant.DATA_PREDICTION + "' and document_id='" + id + "' and " +
 				"start_mention=" + start + " and end_mention=" + end;
 			//System.err.println(delete);
 			//System.err.println(origin);
 			stmt.executeUpdate(delete);
 			stmt.close();
		}
		catch(SQLException ex) { 
	     	System.err.println(delete);
	     	throw ex;
	   	}
	}
	
	public boolean isGeneMention(String id, int start, int end) throws SQLException {
		boolean found = false;
		String select = "";
	 	try {
 			Statement stmt = conn.createStatement();
 			select = "select * from document_gene where type_data='" + 
 				Constant.DATA_PREDICTION + "' and document_id='" + id + "' and " +
 				"start_mention=" + start + " and end_mention=" + end;
 			ResultSet res  = stmt.executeQuery(select);
 			//System.err.println(select);
 			if (res.next())
 				found = true;
			stmt.close();
			return found;
		}
		catch(SQLException ex) { 
	     	System.err.println(select);
	     	throw ex;
	   	}
	}
	
	public Mention isForwardGeneMention(String id, int start) throws SQLException {
		String select = "";
	 	try {
 			Statement stmt = conn.createStatement();
 			select = "select end_mention, gene_mention from document_gene " +
 				"where type_data='" + Constant.DATA_PREDICTION + 
 				"' and document_id='" + id + "' and start_mention=" + start +
 				" and reading='" + MentionConstant.READING_FORWARD + "'";
 			ResultSet res  = stmt.executeQuery(select);
 			//System.err.println(select);
 			if (res.next()) {
 				Mention gm = new Mention(res.getString("gene_mention"),
 					start,res.getInt("end_mention"));
 				return gm;
 			}
 			stmt.close();
			return null;
		}
		catch(SQLException ex) { 
	     	System.err.println(select);
	     	throw ex;
	   	}
	}
	
	public boolean isPartGeneMention(String id, int start, int end) throws SQLException {
		String select = "";
		try {
 			Statement stmt = conn.createStatement();
 			select = "select gene_mention, start_mention, end_mention " +
 				"from document_gene where type_data='" + Constant.DATA_PREDICTION + 
 				"' and document_id='" + id + "' and ((start_mention<=" + start + 
 				" and end_mention>=" + start + ") or (start_mention<=" + end + 
 				" and end_mention>=" + end + ") or (start_mention>=" + start +
 				" and end_mention<=" + end + "))";
 			ResultSet res  = stmt.executeQuery(select);
 			//System.err.println(select);
 			if (res.next()) {
 				return true;
 			}
 			stmt.close();
			return false;
		}
		catch(SQLException ex) { 
	     	System.err.println(select);
	     	throw ex;
	   	}
	}
	
	public Vector<Mention> getPartGeneMention(String id, String type, int start, int end) throws SQLException {
		Vector<Mention> gms = new Vector<Mention>();
		String select = "";
		try {
 			Statement stmt = conn.createStatement();
 			select = "select gene_mention, start_mention, end_mention " +
 				"from document_gene where type='" + type + "' and type_data='" + 
 				Constant.DATA_PREDICTION + "' and reading='" + 
 				MentionConstant.READING_BACKWARD + "' and document_id='" + id + 
 				"' and ((start_mention<=" + start + " and end_mention>=" + start + 
 				") or (start_mention<=" + end + " and end_mention>=" + end + 
 				")) order by start_mention";
 			ResultSet res  = stmt.executeQuery(select);
 			//System.err.println(select);
 			while (res.next()) {
 				Mention gm = new Mention(res.getString("gene_mention"),
 					res.getInt("start_mention"),res.getInt("end_mention"));
 				gms.add(gm);
 				/*System.err.println("getPartGeneMention(" + start + "," + end + ") " + 
 					gm.Text() + " " + gm.Start() + " " + gm.End());*/
 			}
 			stmt.close();
			gms.trimToSize();
 			return gms;
		}
		catch(SQLException ex) { 
	     	System.err.println(select);
	     	throw ex;
	   	}
	}
	
	public void cleanStatusMention() throws SQLException {
		String update = "";
	 	try {
 			Statement stmt = conn.createStatement();
 			update = "update document_gene set status=''";
 			stmt.executeUpdate(update);
			stmt.close();
		}
		catch(SQLException ex) { 
	     	System.err.println(update);
	     	throw ex;
	   	}
	}
	
	public void updateStatusMention(String id, String type, int start, int end) throws SQLException {
		String update = "";
	 	try {
 			Statement stmt = conn.createStatement();
 			update = "update document_gene set status='" + type + "' where document_id='" + id +
 				"' and start_mention>=" + start + " and end_mention<=" + end;
 			if (type.equals(Constant.STATUS_FALSE_NEGATIVE))
 				update += " and type_data='" + Constant.DATA_GOLD_STANDARD + "'";
 			else
 				update += " and type_data='" + Constant.DATA_PREDICTION + "'";
 			stmt.executeUpdate(update);
			stmt.close();
		}
		catch(SQLException ex) { 
	     	System.err.println(update);
	     	throw ex;
	   	}
	}
 
	public void truncateDocumentGene() throws SQLException {
	 	String truncate = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	truncate = "truncate document_gene";
	    	stmt.executeUpdate(truncate);
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.err.println(truncate);
	     	throw ex; 
	   	}
	 }
 
	 public ArrayList<GeneMention> getOffsetDocument(String type, String id, String typeMention, String typeData) throws SQLException {
		 ArrayList<GeneMention> offsets = new ArrayList<GeneMention>();
	 	String select = "";
	 	try {
		    	Statement stmt = conn.createStatement();
		    	select = "select gene_mention, start_mention, end_mention from document_gene " +
		    		"where type='" + type + "' and document_id='" + id + "' and type_mention='" + 
		    		typeMention + "' and type_data='" + typeData + "' order by start_mention";
		    	ResultSet res  = stmt.executeQuery(select);
		    	//System.err.println(select);
		    	while (res.next()) {
		    		GeneMention gm = new GeneMention(res.getString("gene_mention"),typeMention,
		    			res.getInt("start_mention"), res.getInt("end_mention"),null);
		    		offsets.add(gm);
		    	}
		    	stmt.close();
		    	offsets.trimToSize();
		    	return offsets;
		}
		catch(SQLException ex) { 
	     	System.err.println(select);
	     	throw ex;
	    }
	 }
	 
	 public void deleteMentionsTest(String type) throws SQLException {
		String delete = "";
	 	try {
 			Statement stmt = conn.createStatement();
 			delete = "delete from document_gene where type_data='" + 
 				Constant.DATA_PREDICTION + "' and type='" + type + "'";
 			//System.err.println(delete);
 			stmt.executeUpdate(delete);
			stmt.close();
		}
		catch(SQLException ex) { 
	     	System.err.println(delete);
	     	throw ex;
	   	}
	}
	 
	 /*public void deleteMentionsNormalization() throws SQLException {
		String delete = "";
	 	try {
 			Statement stmt = conn.createStatement();
 			delete = "delete from document_gene where type_data='" + 
 				Constant.DATA_PREDICTION + "' and type='" + MentionConstant.DATA_NORMALIZATION + "'";
 			//System.err.println(delete);
 			stmt.executeUpdate(delete);
			stmt.close();
		}
		catch(SQLException ex) { 
	     	System.err.println(delete);
	     	throw ex;
	   	}
	}*/
 
}
