package moara.bio.dbs;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import moara.bio.entities.Organism;
import moara.dbs.DBMoara;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

public class DBOrganismSynonym extends DBMoara {

	public DBOrganismSynonym() {
		super();
		//System.out.println("DBOrganismSynonym");
	}
	
	public void truncateOrganismSynonym() throws SQLException {
		String truncate = "";
		try {
	    	Statement stmt = conn.createStatement();
	    	truncate = "truncate organism_synonym";
	    	stmt.executeUpdate(truncate);
    		stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(truncate);
			throw ex;
		}
	}
	
	public void deleteOrganismSynonym(Organism organism) throws SQLException {
		String delete = "";
		try {
	    	Statement stmt = conn.createStatement();
	    	delete = "delete from organism_synonym where organism='" + 
	    		organism.Code() + "'";
	    	stmt.executeUpdate(delete);
    		stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(delete);
			throw ex;
		}
	}
	
	public void deleteSynonymCategory(String category) throws SQLException {
		String delete = "";
		try {
	    	Statement stmt = conn.createStatement();
	    	delete = "delete from organism_synonym where category='" + category + "'";
	    	stmt.executeUpdate(delete);
    		stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(delete);
			throw ex;
		}
	}
	
	public void insertOrganismSynonym(Organism organism, String synonym, String category) throws SQLException {
	 	String insert = "";
	 	try {
	    	//Statement stmt = conn.createStatement();
	    	//insert = "insert into organism_synonym (organism,synonym,category) values ('" + 
	    	//	organism.Code() + "','" + synonym + "','" + category + "')";

			insert = "insert into organism_synonym (organism,synonym,category) values (?,?,?)";
			PreparedStatement stmt = conn.prepareStatement(insert);
			stmt.setString(1, organism.Code());
			stmt.setString(2, synonym);
			stmt.setString(3, category);

			stmt.executeUpdate();
			stmt.close();
		}
	 	catch(MySQLIntegrityConstraintViolationException ex) { 
	     	//System.err.println("repeated...");
	    }
		catch(SQLException ex) { 
	     	System.err.println(insert);
	     	throw ex;
	    }
	 }
	
	public boolean isSynonymsOrganism(Organism organism, String synonym) throws SQLException {
		boolean found = false;
		String select = "";
		try {
 			//Statement stmt = conn.createStatement();
 			select = "select synonym from organism_synonym where organism=? and synonym =?";
 			
 			PreparedStatement stmt = conn.prepareStatement(select);
 			stmt.setString(1, organism.Code());
 			stmt.setString(2, synonym);
 			
 			ResultSet res  = stmt.executeQuery();
 			if (res.next()) {
 				found = true;
 			}
 			stmt.close();
 		}
		catch(SQLException ex) { 
	     	System.err.println(select);
	     	throw ex;
	   	}
		return found;
	}
	
	public boolean isOrganism(String synonym) throws SQLException {
		boolean found = false;
		String select = "";
		try {
 			//Statement stmt = conn.createStatement();
 			//select = "select synonym from organism_synonym where synonym=\"" + synonym + "\"";
 			select = "select synonym from organism_synonym where synonym=?";
 			PreparedStatement stmt = conn.prepareStatement(select);
 			stmt.setString(1, synonym);
 			ResultSet res  = stmt.executeQuery();
 			if (res.next()) {
 				found = true;
 			}
 			stmt.close();
 		}
		catch(SQLException ex) { 
	     	System.err.println(select);
	     	throw ex;
	   	}
		return found;
	}
	
	public ArrayList<String> getSynonymsOrganism(Organism organism) throws SQLException {
		ArrayList<String> synonyms = new ArrayList<String>();
		String select = "";
		try {
 			//Statement stmt = conn.createStatement();
 			select = "select synonym from organism_synonym where organism=?";
 			PreparedStatement stmt = conn.prepareStatement(select);
 			stmt.setString(1, organism.Code());
 			ResultSet res  = stmt.executeQuery();
 			while (res.next()) {
 				synonyms.add(res.getString("synonym"));
 			}
 			stmt.close();
 			synonyms.trimToSize();
			return synonyms;
		}
		catch(SQLException ex) { 
	     	System.err.println(select);
	     	throw ex;
	   	}
	}
	
	public static void main(String[] args) {
		DBOrganismSynonym app = new DBOrganismSynonym();
		try {
			System.err.println(app.isSynonymsOrganism(new Organism("123"),"mus"));
		}
		catch(SQLException ex) { 
	     	System.err.println();
	   	}
	}
	
}
