package moara.gene.dbs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import moara.dbs.DBMoaraGene;
import moara.bio.entities.Organism;

public class DBGeneProduct extends DBMoaraGene {
	
	public DBGeneProduct() {
		super();
		//System.out.println("DBGeneProduct");
	}
	
	public void insertGeneProduct(Organism organism, String gene, String product) throws SQLException {
	 	String insert = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	ResultSet res  = stmt.executeQuery("select gene_id from gene_product " +
	    		"where organism='" + organism.Code() + "' and gene_id='" + gene + 
	    		"' and product=\"" + product + "\"");
	    	if (!res.next()) {
	    		Statement stmt2 = conn.createStatement();
	    		insert = "insert into gene_product (organism, gene_id, product) values('" + 
	    			organism.Code() + "','" + gene + "',\"" + product + "\")";
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
	 
	 public void deleteGeneProduct(Organism organism) throws SQLException {
	 	String truncate = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	truncate = "delete from gene_product where organism='" + 
	    		organism.Code() + "'";
	    	stmt.executeUpdate(truncate);
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.err.println(truncate);
	     	throw ex; 
	   	}
	 }
	 
	 public ArrayList<String> getProductsGene(Organism organism, String id) throws SQLException {
		ArrayList<String> products = new ArrayList<String>();
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select product from gene_product where organism='" + 
	    		organism.Code() + "' and gene_id='" + id + "'";
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		String product = res.getString("product");
	    		products.add(product);
	    	}
	    	stmt.close();
	    	products.trimToSize();
	    	return products;
		}
		catch(SQLException ex) { 
	     	System.err.println(select);
	     	throw ex;
	    }
	 }

}