package moara.bio.dbs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import moara.dbs.DBMoara;

public class DBBioThesaurus extends DBMoara {

	public DBBioThesaurus() {
		super();
		//System.out.println("DBBioThesaurus");
	}
	
	public void deleteBioThesaurus(String type) throws SQLException {
		String delete = "";
		try {
	    	Statement stmt = conn.createStatement();
	    	delete = "delete from biothesaurus where type='" + type + "'";
	    	stmt.executeUpdate(delete);
    		stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(delete);
			throw ex;
		}
	}
	
	public void insertBioThesaurus(String term, int frequency, String type, String info) throws SQLException {
	 	String insert = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	insert = "insert into biothesaurus (term,frequency,type,additional_info) " +
	    		"values ('" + term + "'," + frequency + ",'" + type + "','" + info + "')";
	    	stmt.executeUpdate(insert);
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
	
	public ArrayList<String> getTermsFrequency(int frequency) throws SQLException {
		ArrayList<String> terms = new ArrayList<String>();
		String select = "";
		try {
 			Statement stmt = conn.createStatement();
 			select = "select term from biothesaurus where frequency>=" + frequency + 
 				" and additional_info not in ('gn','pr')";
 			ResultSet res  = stmt.executeQuery(select);
 			while (res.next()) {
 				terms.add(res.getString("term"));
 			}
 			stmt.close();
 			terms.trimToSize();
			return terms;
		}
		catch(SQLException ex) { 
	     	System.err.println(select);
	     	throw ex;
	   	}
	}
	
}
