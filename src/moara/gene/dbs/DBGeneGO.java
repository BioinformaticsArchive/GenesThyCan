package moara.gene.dbs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import moara.bio.entities.GeneOntology;
import moara.bio.entities.Organism;
import moara.dbs.DBMoaraGene;

public class DBGeneGO extends DBMoaraGene {
	
	private String name;
	
	public DBGeneGO(Organism organism) {
		super();
		this.name = organism.ShortName();
		//System.out.println("DBGeneGO");
	}
	
	public void insertGeneGO(String id, String go) throws SQLException {
	 	String insert = "";
	 	String select1 = "";
	 	String select2 = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select1 = "select id from " + this.name + "_gene where id='" + id + "'";
	    	ResultSet res  = stmt.executeQuery(select1);
	    	if (res.next()) {
	    		Statement stmt2 = conn.createStatement();
	    		select2 = "select gene_id, go_id from " + this.name + "_gene_go " +
	    			"where gene_id='" + id + "' and go_id='" + go + "'";
		    	ResultSet res2  = stmt2.executeQuery(select2);
		    	if (!res2.next()) {
		    		Statement stmt3 = conn.createStatement();
		    		insert = "insert into " + this.name + "_gene_go " +
		    			"(gene_id, go_id) values('" + id + "','" + go + "')";
		    		stmt3.executeUpdate(insert);
					stmt3.close();
		    	}
		    	stmt2.close();
	    	}
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.err.println(insert);
	     	System.err.println(select1);
	     	System.err.println(select2);
	     	throw ex;
	    }
	 }
	 
	 public void deleteGeneGO() throws SQLException {
	 	String delete = "";
	 	try {
		    	Statement stmt = conn.createStatement();
		    	delete = "truncate " + this.name + "_gene_go";
		    	stmt.executeUpdate(delete);
		    	stmt.close();
		    }
			catch(SQLException ex) { 
	     	System.err.println(delete);
	     	throw ex; 
	   	}
	 }
	 
	 public void createTableGeneGO() throws SQLException {
	 	String create = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	create = "create table " + this.name + "_gene_go select * from " +
	    		"yeast_gene_go";
	    	stmt.executeUpdate(create);
	    	create = "truncate " + this.name + "_gene_go";
	    	stmt.executeUpdate(create);
	    	create = "alter table " + this.name + "_gene_go " +
	    		"add primary key (gene_id,go_id)";
	    	stmt.executeUpdate(create);
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.err.println(create);
	     	throw ex; 
	   	}
	 }
	 
	 public void dropTableGeneGO() throws SQLException {
	 	String delete = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	delete = "drop table " + this.name + "_gene_go";
	    	stmt.executeUpdate(delete);
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.err.println(delete);
	     	throw ex; 
	   	}
	 }
	 
	 public ArrayList<GeneOntology> getGOsGene(String id) throws SQLException {
		 ArrayList<GeneOntology> gos = new ArrayList<GeneOntology>();
	 	String select = "";
	 	try {
		    	Statement stmt = conn.createStatement();
		    	select = "select go.id, go.name, go.namespace, go.definition, go.synonym " +
		    		"from " + this.name + "_gene_go gg, gene_ontology go " +
		    		"where gg.gene_id='" + id + "' and gg.go_id=go.id";
		    	ResultSet res  = stmt.executeQuery(select);
		    	while (res.next()) {
		    		GeneOntology go = new GeneOntology();
		    		go.setId(res.getString("id"));
		    		go.setGoName(res.getString("name"));
		    		go.setNamespace(res.getString("namespace"));
		    		go.setDefinition(res.getString("definition"));
		    		go.setSynonym(res.getString("synonym"));
		    		gos.add(go);
		    	}
		    	stmt.close();
		    	gos.trimToSize();
		    	return gos;
			}
			catch(SQLException ex) { 
	     	System.err.println(select);
	     	throw ex;
	     }
	 }
 
}