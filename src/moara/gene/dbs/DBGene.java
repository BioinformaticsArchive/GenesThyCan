package moara.gene.dbs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; 
import java.util.*;

import moara.bio.entities.Organism;
import moara.dbs.DBMoaraGene;
import moara.gene.entities.Gene;

public class DBGene extends DBMoaraGene {
	
	private String name;
	
	public DBGene(Organism organism) {
		super();
		this.name = organism.ShortName();
	}

	public void insertGene(Gene g) throws SQLException {
	 	String insert = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	ResultSet res  = stmt.executeQuery("select id from " + this.name + 
	    		"_gene where id='" + g.GeneId() + "'");
	    	if (!res.next()) {
	    		Statement stmt2 = conn.createStatement();
	    		String columns = "id,symbol,name,description,phenotype";
	    		String values = "'" + g.GeneId() + "',\"" + g.Symbol() + 
    				"\",\"" + g.Name() + "\",\"" + g.Description() + "\",\"" + 
    				g.Phenotype() + "\"";
	    		if (g.Ids().size()>0) {
	    			for (int i=0; i<g.Ids().size(); i++) {
	    				if (i==5)
	    					break;
	    				String id = g.Ids().get(i);
	    				columns += ",database" + (i+1);
	    				values += ",'" + id + "'";
	    			}
	    		}
	    		insert = "insert into " + this.name + "_gene (" + columns + 
	    			") values(" + values + ")";
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
	 
	 public void deleteGene() throws SQLException {
	 	String delete = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	delete = "truncate " + this.name + "_gene";
	    	stmt.executeUpdate(delete);
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.err.println(delete);
	     	throw ex; 
	   	}
	 }
	 
	 public void createTableGene() throws SQLException {
	 	String create = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	create = "create table " + this.name + "_gene select * from " +
	    		"yeast_gene";
	    	stmt.executeUpdate(create);
	    	create = "truncate " + this.name + "_gene";
	    	stmt.executeUpdate(create);
	    	create = "alter table " + this.name + "_gene add primary key (id)";
	    	stmt.executeUpdate(create);
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.err.println(create);
	     	throw ex; 
	   	}
	 }
	 
	 public void dropTableGene() throws SQLException {
	 	String delete = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	delete = "drop table " + this.name + "_gene";
	    	stmt.executeUpdate(delete);
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.err.println(delete);
	     	throw ex; 
	   	}
	 }
	 
	 public Vector<String> getAllIdGene() throws SQLException {
		 Vector<String> genes = new Vector<String>();
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select id from " + this.name + "_gene";
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		String id = res.getString("id");
	    		genes.addElement(id);
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
	 
	 public Vector<Gene> getAllIdGeneObject() throws SQLException {
	 	Vector<Gene> genes = new Vector<Gene>();
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select id, description from " + this.name + "_gene";
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		String id = res.getString("id");
	    		String description = res.getString("description");
	    		Gene g = new Gene(id);
	    		g.setDescription(description);
	    		genes.addElement(g);
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
	 
	 public Gene getGeneObject(String id) throws SQLException {
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select symbol, name, description, phenotype " +
	    		"from " + this.name + "_gene where " +
	    		"id='" + id + "'";
	    	ResultSet res  = stmt.executeQuery(select);
	    	Gene g;
	    	if (res.next()) {
	    		g = new Gene(id);
	    		g.setSymbol(res.getString("symbol"));
	    		g.setName(res.getString("name"));
	    		g.setDescription(res.getString("description"));
	    		g.setPhenotype(res.getString("phenotype"));
	    	}
	    	else {
	    		g = null;
	    	}
	    	stmt.close();
	    	return g;
		}
		catch(SQLException ex) { 
	     	System.err.println(select);
	     	throw ex;
	    }
	 }
	 
	 public String getTokenizedTextGene(String id) throws SQLException {
		String text = new String();
		 String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select tokenized_text from " + this.name + "_gene " + 
	    		"where id='" + id + "'";
	    	ResultSet res  = stmt.executeQuery(select);
	    	if (res.next()) {
	    		text = res.getString("tokenized_text");
	    	}
	    	stmt.close();
	    	return text;
	    }
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	     }
	 }
	 
	 public Vector<Gene> getTokenizedTextGenes() throws SQLException {
		 Vector<Gene> geneTexts = new Vector<Gene>();
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select id, tokenized_text from " + this.name + "_gene ";
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		Gene g = new Gene(res.getString("id"));
	    		g.setGeneText(res.getString("tokenized_text"));
	    		geneTexts.addElement(g);
	    	}
	    	stmt.close();
	    	return geneTexts;
		}
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	     }
	 }
	 
	 public int getTotalNumGene() throws SQLException {
	 	int total = 0;
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select count(*) from (select distinct gene_id from " +
	    		this.name + "_gene_text_terms) genes";
	    	ResultSet res  = stmt.executeQuery(select);
	    	if (res.next()) {
	    		total = res.getInt(1);
	    	}
	    	stmt.close();
	    	return total;
		}
		catch(SQLException ex) { 
	     	System.err.println(select);
	     	throw ex;
	    }
	 }
	 
	 public String getDatabase1Gene(String id) throws SQLException {
		String database = null;
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select database1 from " + this.name + "_gene where id='" + id + "'";
	    	ResultSet res  = stmt.executeQuery(select);
	    	if (res.next())
	    		database = res.getString("database1");
	    	stmt.close();
	    	return database;
		}
		catch(SQLException ex) { 
	     	System.err.println(select);
	     	throw ex;
	    }
	 }
	 
	 public void updateTokenizedTextGene(String id, String text) throws SQLException {
	 	String update = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	update = "update " + this.name + "_gene set tokenized_text=\"" + 
	    		text + "\" where id='" + id + "'";
	    	stmt.executeUpdate(update);
			stmt.close();
		}
		catch(SQLException ex) { 
	     	System.err.println(update);
	     	throw ex;
	    }
	 }
	 
}

