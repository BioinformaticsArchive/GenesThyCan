package moara.gene.dbs;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import moara.bio.BioConstant;
import moara.bio.entities.Organism;
import moara.dbs.DBMoaraGene;
import moara.gene.entities.GeneSynonym;
import moara.util.Constant;

import com.wcohen.ss.SoftTFIDF;

public class DBGeneSynonym extends DBMoaraGene {
	
	private String name;
	public static HashMap<String, GeneSynonym> geneSynonyms = null;
	
	public DBGeneSynonym(Organism organism) {
		super();
		this.name = organism.ShortName();
		geneSynonyms = new HashMap<String, GeneSynonym>();
		//System.out.println("DBGeneSynonym");
	}
	
	public void insertGeneSynonym(String id, String synonym, String type) throws SQLException {
		String insert = "";
		try {
			String select = "select * from " + this.name + "_gene_synonym where gene_id=? and synonym=?";
			
	    		PreparedStatement stmt = conn.prepareStatement(select);
	    		stmt.setString(1, id);
	    		stmt.setString(2, synonym);
	    		
	    	ResultSet res  = stmt.executeQuery();
	    	
	    	if (!res.next()) {
	    		//Statement stmt2 = conn.createStatement();
//	    		insert = "insert into " + this.name + "_gene_synonym " +
//	    			"(gene_id, synonym, type) " +
//	    			"values('" + id + "',\"" + synonym + "\",'" + type + "')";
//	    		
	    		insert = "insert into " + this.name + "_gene_synonym (gene_id, synonym, type) values(?,?,?)";
	    		
	    		PreparedStatement stmt2 = conn.prepareStatement(select);
	    		stmt.setString(1, id);
	    		stmt.setString(2, synonym);
	    		stmt.setString(3, type);
	    		
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
	
	public void insertGeneSynonymType(String id, String type, String synonym, String original) throws SQLException {
		String insert = "";
		try {
	    	//Statement stmt = conn.createStatement();
	   // 	ResultSet res  = stmt.executeQuery("select * from " + this.name + "_gene_synonym " +
	    	//	"where gene_id='" + id + "' and synonym=\"" + synonym + 
	    	//	"\" and type='" + type + "'");
	    	
	    	String select = "select * from " + this.name + "_gene_synonym where gene_id=? and synonym = ? and type = ?";
	    	PreparedStatement stmt = conn.prepareStatement(select);
	    	stmt.setString(1, id);
	    	stmt.setString(2, synonym);
	    	stmt.setString(3, type);
	    	ResultSet res = stmt.executeQuery();
	    	
	    	if (!res.next()) {
		    		insert = "insert into " + this.name + "_gene_synonym (gene_id, synonym, type,original_synonym) values(?,?,?,?)";
		    		
		    		PreparedStatement stmt2 = conn.prepareStatement(select);
		    		stmt.setString(1, id);
		    		stmt.setString(2, synonym);
		    		stmt.setString(3, type);
		    		stmt.setString(4, original);
		    		
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
	
	public void deleteGeneSynonym() throws SQLException {
		String delete = "";
		try {
		    Statement stmt = conn.createStatement();
		    delete = "truncate " + this.name + "_gene_synonym";
		    stmt.executeUpdate(delete);
		    stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(delete);
			throw ex; 
	 	}
	}
	
	public void deleteGeneSynonymType(String type) throws SQLException {
		String delete = "";
		try {
		    Statement stmt = conn.createStatement();
		    delete = "delete from " + this.name + "_gene_synonym where type='" + 
		    	type + "'";
		    stmt.executeUpdate(delete);
		    stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(delete);
			throw ex; 
	 	}
	}
	
	public void createTableGeneSynonym() throws SQLException {
	 	String create = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	create = "create table " + this.name + "_gene_synonym select * from " +
	    		"yeast_gene_synonym";
	    	stmt.executeUpdate(create);
	    	create = "truncate " + this.name + "_gene_synonym";
	    	stmt.executeUpdate(create);
	    	create = "alter table " + this.name + "_gene_synonym " +
	    		"add primary key (gene_id,synonym,type)";
	    	stmt.executeUpdate(create);
	    	create = "alter table " + this.name + "_gene_synonym " +
				"add index Index_2 (synonym,type)";
	    	stmt.executeUpdate(create);
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.err.println(create);
	     	throw ex; 
	   	}
	 }
	 
	 public void dropTableGeneSynonym() throws SQLException {
	 	String delete = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	delete = "drop table " + this.name + "_gene_synonym";
	    	stmt.executeUpdate(delete);
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.err.println(delete);
	     	throw ex; 
	   	}
	 }
	
	public String getOriginalGeneSynonym(String id, String synonym, String type) throws SQLException {
		String original = "";
		String select = "";
		try {
	    	//Statement stmt = conn.createStatement();
//	    	select = "select original_synonym from " + this.name + "_gene_synonym " +  
//	    		" where gene_id='" + id + "' and synonym=\"" + synonym + 
//	    		"\" and type='" + type + "'";
	    	select = "select original_synonym from " + this.name + "_gene_synonym  where gene_id = ? and synonym=? and type=?";

	    	PreparedStatement stmt = conn.prepareStatement(select);
	    	stmt.setString(1, id);
	    	stmt.setString(2, synonym);
	    	stmt.setString(3, type);
	    	ResultSet res = stmt.executeQuery();
	    	
	    	if (res.next()) {
	    		original = res.getString("original_synonym");
	    	}
	    	stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	    }
		return original;
	}
	
	public int getTotalSynonymType(String type) throws SQLException {
		int total = 0;
		String select = "";
		try {
	    	Statement stmt = conn.createStatement();
	    	select = "select count(*) from " + this.name + "_gene_synonym" +  
	    		" where type='" + type + "'";
	    	ResultSet res  = stmt.executeQuery(select);
	    	if (res.next()) {
	    		total = res.getInt(1);
	    	}
	    	stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	    }
		return total;
	}
	
	/*public Vector getSynonymsOrganism(String organism, String status, boolean descriptionSynonym,
			boolean productSynonym, boolean derivedSynonym) throws SQLException {
		Vector synonyms = new Vector();
		String select = "";
		try {
	    	Statement stmt = conn.createStatement();
	    	select = "select gene_id, synonym from gene_synonym where organism='" + organism + "'";
	    	if (status.equals(Constant.SYNONYM_VALID))
	    		select = select + " and reliable='Y'";
	    	if (!descriptionSynonym && !productSynonym)
	    		select = select + " and type='" + Constant.SYNONYM_LIST + "'";
	    	else if (descriptionSynonym && !productSynonym)
	    		select = select + " and type in ('" + Constant.SYNONYM_LIST + "','" +
	    			Constant.SYNONYM_DESCRIPTION + "')";
	    	else if (productSynonym && !descriptionSynonym)
	    		select = select + " and type in ('" + Constant.SYNONYM_LIST + "','" +
	    			Constant.SYNONYM_PRODUCT + "')";
	    	else if (derivedSynonym && !descriptionSynonym && !productSynonym)
	    		select = select + " and type in ('" + Constant.SYNONYM_LIST + "','" +
	    			Constant.SYNONYM_DERIVED + "')";
	    	//System.err.println(select);
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		GeneSynonym gs = new GeneSynonym(res.getString("gene_id"),res.getString("synonym"));
	    		synonyms.add(gs);
	    	}
	    	synonyms.trimToSize();
	    	stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	    }
		return synonyms;
	}*/
	
	public ArrayList<GeneSynonym> getSynonymsOrganismType(String type) throws SQLException {
		ArrayList<GeneSynonym> synonyms = new ArrayList<GeneSynonym>();
		String select = "";
		try {
	    	Statement stmt = conn.createStatement();
	    	select = "select gene_id, synonym from " + this.name + "_gene_synonym " + 
	    		"where type='" + type + "'";
	    	//System.err.println(select);
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		GeneSynonym gs = new GeneSynonym(res.getString("gene_id"),res.getString("synonym"));
	    		synonyms.add(gs);
	    	}
	    	synonyms.trimToSize();
	    	stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	    }
		return synonyms;
	}
	
	/*private String getRegExpSynonymFilter(String first, String last) {
		// prefix: ^(a|-a|[(]|[0-9])
		String prefix = "^(" + first + "|-" + first + "|[(]|[0-9])";
		// suffix: (-|[)]|[0-9]|[0-9][a-z])$
		String suffix = "(" + last + "|-|[)]|[0-9]|[0-9][a-z])$";
		return "'" + prefix + ".*" + suffix + "'";
	}*/
	
	public ArrayList<String> getSynonymsGene(String id) throws SQLException {
		ArrayList<String> synonyms = new ArrayList<String>();
		String select = "";
		try {
	    	Statement stmt = conn.createStatement();
	    	select = "select synonym from " + this.name + "_gene_synonym " + 
	    		"where gene_id='" + id + "' and type='" + BioConstant.SYNONYM_LIST + "'";
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		synonyms.add(res.getString("synonym"));
	    	}
	    	synonyms.trimToSize();
	    	stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	    }
		return synonyms;
	}
	
	public ArrayList<String> getSynonymsGeneList(String gene) throws SQLException {
		ArrayList<String> synonyms = new ArrayList<String>();
		String select = "";
		try {
	    	Statement stmt = conn.createStatement();
	    	select = "select synonym from " + this.name + "_gene_synonym " + 
	    		"where gene_id='" + gene + "' and type='" + BioConstant.SYNONYM_LIST + 
	    		"' order by length(synonym) desc";
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		synonyms.add(res.getString("synonym"));
	    	}
	    	synonyms.trimToSize();
	    	stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	    }
		return synonyms;
	}
	
	public ArrayList<String> getGeneIdSynonym(String synonym) throws SQLException {
		ArrayList<String> synonyms = new ArrayList<String>();
		String select = "";
		try {
//	    	select = "select gene_id from " + this.name + "_gene_synonym " + 
//	    		"where synonym=\"" + synonym + "\"";
	    	
	    	select = "select gene_id from " + this.name + "_gene_synonym where synonym=?";
    		PreparedStatement stmt = conn.prepareStatement(select);
    		stmt.setString(1, synonym);
	    	
	    	ResultSet res  = stmt.executeQuery();
	    	while (res.next()) {
	    		synonyms.add(res.getString("gene_id"));
	    	}
	    	synonyms.trimToSize();
	    	stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	    }
		return synonyms;
	}
	
	public ArrayList<GeneSynonym> getGenesSynonym(String synonym, boolean descriptionSynonym,
			boolean productSynonym, boolean orderedSynonym) throws SQLException {
		ArrayList<GeneSynonym> genes = new ArrayList<GeneSynonym>();
		String select = "";
		try {
	    	//Statement stmt = conn.createStatement();
//	    	select = "select gene_id, synonym, original_synonym from " + this.name + 
//	    		"_gene_synonym where synonym=\"" + synonym + "\"";
//	    	
	    	select = "select gene_id, synonym, original_synonym from " + this.name + "_gene_synonym where synonym=?";
	     PreparedStatement stmt = null;
	     	    
	    	if (!descriptionSynonym && !productSynonym && !orderedSynonym){
	    		select = select + " and type=? ";// + BioConstant.SYNONYM_LIST + "'";
	    		stmt = conn.prepareStatement(select);
	    	 	stmt.setString(1, synonym);
	    	 	stmt.setString(2, BioConstant.SYNONYM_LIST);
	    	}
	    	else if (descriptionSynonym && !productSynonym){
	    		//select = select + " and type in ('" + BioConstant.SYNONYM_LIST + "','" +
	    		//BioConstant.SYNONYM_DESCRIPTION + "')";
	    		select = select + " and type in (?, ?)";
	    		stmt = conn.prepareStatement(select);
	    	 	stmt.setString(1, synonym);
	    	 	stmt.setString(2, BioConstant.SYNONYM_LIST);
	    	 	stmt.setString(3, BioConstant.SYNONYM_DESCRIPTION);
	    	}
	    	else if (productSynonym && !descriptionSynonym){
//	    		select = select + " and type in ('" + BioConstant.SYNONYM_LIST + "','" +
//	    		BioConstant.SYNONYM_PRODUCT + "')";
	    		
	    		select = select + " and type in (?, ?)";
	    		stmt = conn.prepareStatement(select);
	    	 	stmt.setString(1, synonym);
	    	 	stmt.setString(2, BioConstant.SYNONYM_LIST);
	    	 	stmt.setString(3, BioConstant.SYNONYM_PRODUCT);
	    	}
	    	else if (orderedSynonym){
	    		//select = select + " and type='" + BioConstant.SYNONYM_ORDERED + "'";
	    		select = select + " and type=? ";
	    		stmt = conn.prepareStatement(select);
	    	 	stmt.setString(1, synonym);
	    	 	stmt.setString(2, BioConstant.SYNONYM_ORDERED);
	    	}
	    	
	    	//ResultSet res  = stmt.executeQuery(select);
	    	ResultSet res = stmt.executeQuery();
	    	//System.err.println(select);
	    	while (res.next()) {
	    		GeneSynonym gs = new GeneSynonym(res.getString("gene_id"),res.getString("synonym"));
	    		gs.setOriginalSynonym(res.getString("original_synonym"));
	    		genes.add(gs);
	    	}
	    	genes.trimToSize();
	    	stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	    }
		return genes;
	}
	
	public ArrayList<String> getGeneIdOrganism() throws SQLException {
		ArrayList<String> ids = new ArrayList<String>();
		String select = "";
		try {
	    	Statement stmt = conn.createStatement();
	    	select = "select distinct gene_id from " + this.name + "_gene_synonym";
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		ids.add(res.getString("gene_id"));
	    	}
	    	ids.trimToSize();
	    	stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	    }
		return ids;
	}
	
	public GeneSynonym searchSimilarSynonym(String geneId, String synonym, 
			double pctSim, boolean sameId, SoftTFIDF soft) throws SQLException {
		GeneSynonym gs = null;
		String select = "";
		try {
	    	Statement stmt = conn.createStatement();
	    	/*select = "select gene_id, synonym from gene_synonym " +
	    		"where organism='" + organism + "'";
	    	if (sameId)
	    		select += " and gene_id='" + geneId + "'";
	    	else
	    		select += " and gene_id<>'" + geneId + "'";
	    	select += " and synonym<>\"" + synonym + "\" and type='" + 
	    		BioConstant.SYNONYM_ORDERED + "' and reliable=''";*/
	    	select = "select gene_id, synonym from " + this.name + "_gene_synonym " +
	    		"where type='" + BioConstant.SYNONYM_ORDERED + "'";
	    	//System.err.println(select);
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		if (res.getString("synonym").equals(synonym))
	    			continue;
	    		if (sameId && !res.getString("gene_id").equals(geneId))
	    			continue;
	    		if (!sameId && res.getString("gene_id").equals(geneId))
	    			continue;
	    		gs = new GeneSynonym(res.getString("gene_id"),res.getString("synonym"));
	    		if (soft.score(synonym,gs.Synonym())<pctSim)
	    			gs = null;
	    		else
	    			break;
	    	}
	    	stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	    }
		return gs;
	}
	
	public ArrayList<GeneSynonym> searchSimilarSynonymMention(String synonym, 
			double pctSim, SoftTFIDF soft) throws SQLException {
		ArrayList<GeneSynonym> genes = new ArrayList<GeneSynonym>();
		String select = "";
		
		try {
	    	Statement stmt = conn.createStatement();
	    	select = "select gene_id, synonym, original_synonym from " + this.name + "_gene_synonym " + 
	    		"where type='" + BioConstant.SYNONYM_ORDERED + "'";
	    	//System.err.println(select);
	    	ResultSet res  = stmt.executeQuery(select);
	    	
	    	if (geneSynonyms.size() == 0){
	    		
		    	while (res.next()) {
		    		GeneSynonym gs = new GeneSynonym(res.getString("gene_id"),res.getString("synonym"));
		    		gs.setOriginalSynonym(res.getString("original_synonym"));
		    		
		    		String key = res.getString("gene_id") + ":" + res.getString("synonym");
		    		geneSynonyms.put(key, gs);
		    	}
	    	}    	

	    	for (GeneSynonym gs : geneSynonyms.values()){
	    		double simScore = soft.score(synonym,gs.Synonym());
	    		
	    		if (simScore>=pctSim){
	    			gs.setMaxScore(simScore);
	    			genes.add(gs);
	    		}
	    	}
	    	
	    	genes.trimToSize();
	    	stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	    }
		
		
		return genes;
	}
	
	private void printDictionary() {
		String select = "";
		try {
			File f = new File("resources/" + this.name + ".txt");
            if (f.exists())
            	f.delete();
            f.createNewFile();
            if (!f.exists())
            	System.err.println("Error in file!");
            RandomAccessFile rw = new RandomAccessFile(f,"rw");
            rw.writeBytes("ID\tSYNONYM\tORIGINAL SYNONYM\n");
			Statement stmt = conn.createStatement();
	    	select = "select gene_id, synonym, original_synonym " +
	    		"from " + this.name + "_gene_synonym " + 
	    		"where type='" + BioConstant.SYNONYM_ORDERED + 
	    		"' order by gene_id, synonym";
	    	//System.err.println(select);
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		rw.writeBytes(res.getString("gene_id") + "\t" + 
	    			res.getString("synonym") + "\t" + 
	    			res.getString("original_synonym") + "\n");
	    	}
	    	rw.close();
		}
		catch(IOException e) { 
        	System.err.println(e);
        }
		catch(SQLException ex) { 
			System.err.println(select);
		}
	}
	
	public static void main(String[] args) {
		DBGeneSynonym app = new DBGeneSynonym(new Organism(Constant.ORGANISM_HUMAN));
		app.printDictionary();
	}

}

