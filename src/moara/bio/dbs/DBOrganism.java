package moara.bio.dbs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.StringTokenizer;
import moara.dbs.DBMoara;
import moara.bio.entities.Organism;

public class DBOrganism extends DBMoara {

	public DBOrganism() {
		super();
		//System.out.println("DBOrganism");
	}
	
	public void truncateOrganism() throws SQLException {
		String truncate = "";
		try {
	    	Statement stmt = conn.createStatement();
	    	truncate = "truncate organism";
	    	stmt.executeUpdate(truncate);
    		stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(truncate);
			throw ex;
		}
	}
	
	public void insertOrganism(String code, String name) throws SQLException {
	 	String insert = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	insert = "insert into organism (code,name) values ('" + 
	    		code + "','" + name + "')";
	    	stmt.executeUpdate(insert);
	    	stmt.close();
		}
		catch(SQLException ex) { 
	     	System.err.println(insert);
	     	throw ex;
	    }
	 }
	
	public void updateShortName(Organism organism, String shortName) throws SQLException {
	 	String update = "";
	 	try {
	 		Statement stmt3 = conn.createStatement();
	 		update = "update organism set short_name='" + shortName + 
	 			"' where code='" + organism.Code() + "'";
    		stmt3.executeUpdate(update);
			stmt3.close();
		}
		catch(SQLException ex) { 
	     	System.err.println(update);
	     	throw ex;
	   	}
	 }
	
	public ArrayList<Organism> getAllOrganisms() throws SQLException {
		ArrayList<Organism> organisms = new ArrayList<Organism>();
		String select = "";
		try {
 			Statement stmt = conn.createStatement();
 			select = "select code, name from organism";
 			ResultSet res  = stmt.executeQuery(select);
 			while (res.next()) {
 				Organism org = new Organism(res.getString("code"),res.getString("name"));
 				organisms.add(org);
 			}
 			stmt.close();
 			organisms.trimToSize();
			return organisms;
		}
		catch(SQLException ex) { 
	     	System.err.println(select);
	     	throw ex;
	   	}
	}
	
	public ArrayList<Organism> getScientificOrganisms() throws SQLException {
		ArrayList<Organism> organisms = new ArrayList<Organism>();
		String select = "";
		try {
 			Statement stmt = conn.createStatement();
 			select = "select code, name from organism";
 			ResultSet res  = stmt.executeQuery(select);
 			while (res.next()) {
 				StringTokenizer tokens = new StringTokenizer(res.getString("name"));
				if (tokens.countTokens()==2) {
					Organism org = new Organism(res.getString("code"),res.getString("name"));
	 				organisms.add(org);
				}
 			}
 			stmt.close();
 			organisms.trimToSize();
			return organisms;
		}
		catch(SQLException ex) { 
	     	System.err.println(select);
	     	throw ex;
	   	}
	}
	
	public Organism loadData(String code) throws SQLException {
		Organism org = null;
		String select = "";
		try {
 			Statement stmt = conn.createStatement();
 			select = "select name, short_name from organism " +
 				"where code='" + code + "'";
 			ResultSet res  = stmt.executeQuery(select);
 			if (res.next()) {
 				org = new Organism(code,res.getString("name"));
 				org.setShortName(res.getString("short_name"));
 			}
 			stmt.close();
 			return org;
		}
		catch(SQLException ex) { 
	     	System.err.println(select);
	     	throw ex;
	   	}
	}
	
}
