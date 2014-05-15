package moara.bio.dbs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import moara.bio.entities.GeneOntology;
import moara.dbs.DBMoaraResources;

public class DBGeneOntology extends DBMoaraResources {
	
	private Statement stmtQuery;
	private ResultSet resQuery;
	
	public DBGeneOntology() {
		super();
		this.stmtQuery = null;
		this.resQuery = null;
	}
	
	public void truncateGeneOntology() throws SQLException {
	 	String truncate = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	truncate = "truncate gene_ontology_alternate";
	    	stmt.executeUpdate(truncate);
	    	truncate = "truncate gene_ontology";
	    	stmt.executeUpdate(truncate);
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.err.println(truncate);
	     	throw ex; 
	   	}
	 }
	
	public void insertGeneOntology(GeneOntology go) throws SQLException {
	 	String insert = "";
	 	try {
	    	ArrayList<String> altIds = go.AlternativeIds();
	    	Statement stmt = conn.createStatement();
	    	insert = "insert into gene_ontology (id, name, namespace, definition, synonym) " +
				"values('" + go.Id() + "',\"" + go.GoName() + "\",'" + go.Namespace() + "',\"" + 
				go.Definition() + "\",\"" + go.Synonym() + "\")";
	    	stmt.executeUpdate(insert);
	    	// save the alternate ids
	    	for (int i=0; i<altIds.size(); i++) {
    			String alternate = altIds.get(i);
    			insert = "insert into gene_ontology_alternate (go_id, alt_id) " +
    				"values('" + go.Id() + "','" + alternate + "')";
    			stmt.executeUpdate(insert);
    		}
	    	stmt.close();
		}
		catch(SQLException ex) { 
	     	System.err.println(insert);
	     	throw ex;
	    }
	 }
	
	public GeneOntology getGeneOntology(String id) throws SQLException {
		GeneOntology go = null;
		String select = "";
		try {
 			Statement stmt = conn.createStatement();
 			select = "select name, namespace, definition, synonym from gene_ontology " +
 				"where id='" + id + "'";
 			ResultSet res  = stmt.executeQuery(select);
 			if (res.next()) {
 				go = new GeneOntology(id,res.getString("name"),res.getString("namespace"));
 				go.setDefinition(res.getString("definition"));
 				go.setSynonym(res.getString("synonym"));
 			}
 			stmt.close();
 			return go;
		}
		catch(SQLException ex) { 
	     	System.err.println(select);
	     	throw ex;
	   	}
	}
	
	public String getNewCodeAlternative(String alternate) throws SQLException {
		String code = null;
		String select = "";
		try {
 			Statement stmt = conn.createStatement();
 			select = "select go_id from gene_ontology_alternate where alt_id='" + alternate + "'";
 			ResultSet res  = stmt.executeQuery(select);
 			if (res.next()) {
 				code = res.getString("go_id");
 			}
 			stmt.close();
 			return code;
		}
		catch(SQLException ex) { 
	     	System.err.println(select);
	     	throw ex;
	   	}
	}
	
	public String[] getIdText(boolean start) throws SQLException {
		String idText[] = new String[2];
		String select = "";
		try {
 			if (start) {
 				this.stmtQuery = conn.createStatement();
 	 			select = "select id, definition from gene_ontology";
 	 			this.resQuery  = this.stmtQuery.executeQuery(select);
 			}
			if (!this.resQuery.isClosed() && this.resQuery.next()) {
				idText[0] = this.resQuery.getString("id"); 
				idText[1] = this.resQuery.getString("definition");
 				if (this.resQuery.isLast())
 					this.stmtQuery.close();
 				return idText;
			}
		}
		catch(SQLException ex) { 
	     	System.err.println(select);
	     	throw ex;
	   	}
		return null;
	}
	 
}