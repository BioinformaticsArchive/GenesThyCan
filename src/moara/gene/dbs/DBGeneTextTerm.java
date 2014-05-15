package moara.gene.dbs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import moara.bio.entities.Organism;
import moara.dbs.DBMoaraGene;
import moara.gene.entities.Gene;
import moara.util.text.TermSpaceModel;

public class DBGeneTextTerm extends DBMoaraGene {
	
	private String name;
	
	public DBGeneTextTerm(Organism organism) {
		super();
		this.name = organism.ShortName();
		//System.out.println("DBGeneTextTerm");
	}
	
	public void insertTermGene(String id, HashMap<String,TermSpaceModel> terms) throws SQLException {
	 	String insert = "";
	 	try {
	 		Iterator<String> iter = terms.keySet().iterator();
	 		while (iter.hasNext()) {
	    		String key = iter.next();
	    		TermSpaceModel tvm = terms.get(key);
	    		String word = tvm.Term();
	    		double weight = tvm.MeasureValue();
	    		int frequency = tvm.Frequency();
	    		if (weight!=-1) {
	    			Statement stmt = conn.createStatement();
			    	ResultSet res = stmt.executeQuery("select gene_id, term " +
			    		"from " + this.name + "_gene_text_terms " + 
			    		"where gene_id='" + id + "' and term=\"" + word + "\"");
			    	if (!res.next()) {
			    		Statement stmt2 = conn.createStatement();
			    		insert = "insert into " + this.name + "_gene_text_terms " + 
			    			"(gene_id,term, weight, frequency) values('"+ id + 
			    			"',\"" + word + "\"," + weight + "," + frequency + ")";
			    		stmt2.executeUpdate(insert);
						stmt2.close();
			    	}
			    	stmt.close();
	    		}
	    	}
	    }
		catch(SQLException ex) { 
	     	System.err.println(insert);
	     	throw ex;
	     }
	 }
	
	public void updateWeightTerm(String id, TermSpaceModel tvm) throws SQLException {
	 	String update = "";
	 	try {
	 		Statement stmt = conn.createStatement();
	 		update = "update " + this.name + "_gene_text_terms set weight=" + 
		 		tvm.MeasureValue() + " where gene_id='" + id + "' and term=\"" + 
		 		tvm.Term() + "\"";
	 		stmt.executeUpdate(update);
	 		stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.err.println(update);
	     	throw ex;
	     }
	 }
		 
	 public void truncateTermGene() throws SQLException {
	 	String delete = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	delete = "truncate " + this.name + "_gene_text_terms";
	    	stmt.executeUpdate(delete);
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.err.println(delete);
	     	throw ex; 
	   	}
	 }
	 
	 public void createTableGeneTextTerm() throws SQLException {
	 	String create = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	create = "create table " + this.name + "_gene_text_terms " + 
	    		"select * from yeast_gene_text_terms";
	    	stmt.executeUpdate(create);
	    	create = "truncate " + this.name + "_gene_text_terms";
	    	stmt.executeUpdate(create);
	    	create = "alter table " + this.name + "_gene_text_terms " + 
    			"add primary key (gene_id,term)";
    	stmt.executeUpdate(create);
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.err.println(create);
	     	throw ex; 
	   	}
	 }
	 
	 public void dropTableGeneTextTerm() throws SQLException {
	 	String delete = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	delete = "drop table " + this.name + "_gene_text_terms";
	    	stmt.executeUpdate(delete);
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.err.println(delete);
	     	throw ex; 
	   	}
	 }
		 
	 public HashMap<String,TermSpaceModel> getTermsGene(String id) throws SQLException {
		HashMap<String,TermSpaceModel> terms = new HashMap<String,TermSpaceModel>();
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select term, frequency, weight from " + 
	    		this.name + "_gene_text_terms where gene_id='" + id + "'";
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		TermSpaceModel tvm = new TermSpaceModel(res.getString("term"));
	    		tvm.setFrequency(res.getInt("frequency"));
	    		tvm.setMeasureValue(res.getDouble("weight"));
	    		terms.put(tvm.Term(),tvm);
	    	}
	    	stmt.close();
	    	return terms;
		}
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	    }
	 }
 
	 public ArrayList<Gene> getAllGeneTerm() throws SQLException {
		ArrayList<Gene> genes = new ArrayList<Gene>();
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select distinct gene_id from " + this.name + "_gene_text_terms";
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		String id = res.getString("gene_id");
	    		Gene g = new Gene(id);
	    		genes.add(g);
	    	}
	    	stmt.close();
	    	genes.trimToSize();
	    	return genes;
		}
		catch(SQLException ex) { 
	     	System.err.println(select);
	     	throw ex;
	     }
	 }
 
	 public ArrayList<String> getAllGeneIdTerm() throws SQLException {
		ArrayList<String> genes = new ArrayList<String>();
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select distinct gene_id from " + this.name + "_gene_text_terms";
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		String id = res.getString("gene_id");
	    		genes.add(id);
	    	}
	    	stmt.close();
	    	genes.trimToSize();
	    	return genes;
		}
		catch(SQLException ex) { 
	     	System.err.println(select);
	     	throw ex;
	     }
	 }
 
	 public Hashtable<String,Double> getGenesTerm(String word) throws SQLException {
		Hashtable<String,Double> genes = new Hashtable<String,Double>();
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select gene_id, weigth from " + this.name + "_gene_text_terms " +
	    		"where term='" + word + "' order by gene_id";
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		String sgdid = res.getString("gene_id");
	    		Double weight = new Double(res.getDouble("weight"));
	    		genes.put(sgdid,weight);
	    	}
	    	stmt.close();
	    	return genes;
		}
		catch(SQLException ex) { 
	     	System.err.println(select);
	     	throw ex;
	     }
	 }
 
}