package moara.gene.dbs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import moara.dbs.DBMoaraGene;
import moara.util.text.TermSpaceModel;
import moara.bio.entities.Organism;

public class DBGeneTerm extends DBMoaraGene {
	
	private String name;
	
	public DBGeneTerm(Organism organism) {
		super();
		this.name = organism.ShortName();
		//System.out.println("DBGeneTerm");
	}
	
	public void insertTerms(HashMap<String,TermSpaceModel> terms) throws SQLException {
	 	String insert_update = "";
	 	try {
	    	Iterator<String> iter = terms.keySet().iterator();
	 		while (iter.hasNext()) {
	    		String key = iter.next();
	    		TermSpaceModel tvm = terms.get(key);
	    		String word = tvm.Term();
	    		if (word.endsWith("\\"))
	    			word = word.substring(0,word.length()-1);
	    		int frequency = tvm.Frequency();
	    		Statement stmt = conn.createStatement();
	    		ResultSet res  = stmt.executeQuery("select num_genes, frequency " +
	    			"from " + this.name + "_gene_terms where term=\"" + word + "\"");
	    		if (!res.next()) {
	    			Statement stmt2 = conn.createStatement();
		    		insert_update = "insert into " + this.name + "_gene_terms " + 
		    			"(term, frequency, num_genes) values(\"" + word + 
		    			"\"," + frequency + ",1)";
		    		stmt2.executeUpdate(insert_update);
					stmt2.close();	
	    		}
	    		else {
	    			int totalFrequency = res.getInt("frequency");
	    			int totalGenes = res.getInt("num_genes");
	    			totalFrequency += frequency;
	    			totalGenes++;
	    			Statement stmt3 = conn.createStatement();
	    			insert_update = "update " + this.name + "_gene_terms set frequency=" + 
	    				totalFrequency + ", num_genes=" + totalGenes + 
	    				" where term=\"" + word + "\"";
		    		stmt3.executeUpdate(insert_update);
					stmt3.close();
	    		}
	    		stmt.close();
			}
		}
		catch(SQLException ex) { 
	     	System.err.println(insert_update);
	     	throw ex;
	   	}
	 }
	 
	 public void truncateTerm() throws SQLException {
	 	String delete = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	delete = "truncate " + this.name + "_gene_terms";
	    	stmt.executeUpdate(delete);
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.err.println(delete);
	     	throw ex; 
	   	}
	 }
	 
	 public void createTableGeneTerm() throws SQLException {
	 	String create = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	create = "create table " + this.name + "_gene_terms select * from " +
	    		"yeast_gene_terms";
	    	stmt.executeUpdate(create);
	    	create = "truncate " + this.name + "_gene_terms";
	    	stmt.executeUpdate(create);
	    	create = "alter table " + this.name + "_gene_terms " + 
	    		"add primary key (term)";
	    	stmt.executeUpdate(create);
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.err.println(create);
	     	throw ex; 
	   	}
	 }
	 
	 public void dropTableGeneTerm() throws SQLException {
	 	String delete = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	delete = "drop table " + this.name + "_gene_terms";
	    	stmt.executeUpdate(delete);
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.err.println(delete);
	     	throw ex; 
	   	}
	 }
	 
	 public ArrayList<String> getAllTerms() throws SQLException {
		 ArrayList<String> terms = new ArrayList<String>();
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select term from " + this.name + "_gene_terms";
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
	 
	 public ArrayList<String> getAllWordsTerms() throws SQLException {
		 ArrayList<String> terms = new ArrayList<String>();
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select term from " + this.name + "_gene_terms";
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		String word = res.getString("term");
	    		terms.add(word);
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
	 
	 public void removeUnfrequentTerms(int minFrequencyTerm) throws SQLException {
	 	String delete1 = "";
	 	String delete2 = "";
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select term from " + this.name + "_gene_term" + 
	    		" where frequency<" + minFrequencyTerm;
	    	ResultSet res  = stmt.executeQuery(select);
    		if (res.next()) {
    			String word = res.getString("term");
    			Statement stmt2 = conn.createStatement();
    			delete1 = "delete from " + this.name + "_gene_text_terms " + 
    				"where term=\"" + word + "\"";
	    		stmt2.executeUpdate(delete1);
				stmt2.close();	
    		}
    		delete2 = "delete from " + this.name + "_gene_terms " + 
    			"where frequency<" + minFrequencyTerm;
	    	stmt.executeUpdate(delete2);
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.err.println(delete1);
	     	System.err.println(delete2);
	     	System.err.println(select);
	     	throw ex; 
	   	}
	 }
	 
	 public void removeTermsFewGenes(int minNumGeneTerm) throws SQLException {
		String delete1 = "";
	 	String delete2 = "";
	 	String select = "";
	 	try {
	 		Statement stmt = conn.createStatement();
	    	select = "select term from " + this.name + "_gene_terms " + 
	    		"where num_genes<" + minNumGeneTerm;
	    	ResultSet res  = stmt.executeQuery(select);
    		if (res.next()) {
    			String word = res.getString("term");
    			Statement stmt2 = conn.createStatement();
    			delete1 = "delete from " + this.name + "_gene_text_terms " +
    				"where term=\"" + word + "\"";
	    		stmt2.executeUpdate(delete1);
				stmt2.close();	
    		}
    		delete2 = "delete from " + this.name + "_gene_terms " + 
    			"where num_genes<" + minNumGeneTerm;
	    	stmt.executeUpdate(delete2);
	    	stmt.close();
	 	}
		catch(SQLException ex) { 
			System.err.println(delete1);
	     	System.err.println(delete2);
	     	System.err.println(select);
	     	throw ex; 
	   	}
	 }
	 
	 public int getNumGenesTerm(String word) throws SQLException {
	 	int numGenes = 0;
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select num_genes from " + this.name + "_gene_terms " + 
	    		"where term=\"" + word + "\"";
	    	ResultSet res  = stmt.executeQuery(select);
	    	if (res.next()) {
	    		numGenes = res.getInt("num_genes");
	    	}
	    	stmt.close();
	    	return numGenes;
		}
		catch(SQLException ex) { 
	     	System.err.println(select);
	     	throw ex;
	    }
	 }

}
