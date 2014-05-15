package moara.normalization.dbs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import moara.bio.entities.Organism;
import moara.dbs.DBMoaraNormalization;
import moara.normalization.NormalizationConstant;

public class DBFeaturePair extends DBMoaraNormalization {
	
	private String name;
	
	public DBFeaturePair(Organism organism) {
		this.name = organism.ShortName();
	}
	
	public void createTableFeaturePair() throws SQLException {
	 	String create = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	// feature pair bigram
	    	create = "create table " + this.name + "_feature_pair_bigram " +
	    		"select * from yeast_feature_pair_bigram";
	    	stmt.executeUpdate(create);
	    	create = "truncate " + this.name + "_feature_pair_bigram";
	    	stmt.executeUpdate(create);
	    	create = "alter table " + this.name + "_feature_pair_bigram " +
	    		"add primary key (sequential1,sequential2,pct_similarity)";
	    	stmt.executeUpdate(create);
	    	// feature pair trigram
	    	create = "create table " + this.name + "_feature_pair_trigram " +
	    		"select * from yeast_feature_pair_trigram";
	    	stmt.executeUpdate(create);
	    	create = "truncate " + this.name + "_feature_pair_trigram";
	    	stmt.executeUpdate(create);
	    	create = "alter table " + this.name + "_feature_pair_trigram " +
	    		"add primary key (sequential1,sequential2,pct_similarity)";
	    	stmt.executeUpdate(create);
	    	
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.err.println(create);
	     	throw ex; 
	   	}
	 }
	 
	 public void dropTableFeaturePair() throws SQLException {
	 	String delete = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	delete = "drop table " + this.name + "_synonym_feature";
	    	stmt.executeUpdate(delete);
	    	delete = "drop table " + this.name + "_synonym_bigram";
	    	stmt.executeUpdate(delete);
	    	delete = "drop table " + this.name + "_synonym_trigram";
	    	stmt.executeUpdate(delete);
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.err.println(delete);
	     	throw ex; 
	   	}
	 }
	 
	 public void truncateFeaturePair() throws SQLException {
		String truncate = "";
		try {
	    	Statement stmt = conn.createStatement();
	    	truncate = "truncate " +this.name + "_feature_pair_bigram";
	    	stmt.executeUpdate(truncate);
	    	truncate = "truncate " + this.name + "_feature_pair_trigram";
    		stmt.executeUpdate(truncate);
    		stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(truncate);
			throw ex;
		}
	}
	
	public void deleteFeaturePair(double pct) throws SQLException {
		String delete = "";
		try {
	    	Statement stmt = conn.createStatement();
	    	delete = "delete from " +this.name + "_feature_pair_bigram" +  
	    		" where pct_similarity=" + pct;
	    	stmt.executeUpdate(delete);
    		delete = "delete from " + this.name + "_feature_pair_trigram" + 
    			" where pct_similarity=" + pct;
    		stmt.executeUpdate(delete);
    		stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(delete);
			throw ex;
		}
	}
	
	public void insertFeaturePairBigram(int seq1, int seq2, double pct, int category) throws SQLException {
		String insert = "";
		String select = "";
		try {
			if (seq1>seq2) {
				int temp = seq1;
				seq1 = seq2;
				seq2 = temp;
			}
			Statement stmt = conn.createStatement();
	    	select = "select * from " + this.name + "_feature_pair_bigram" + 
	    		" where sequential1=" + seq1 + " and sequential2=" + seq2 + 
	    		" and pct_similarity=" + pct;
	    	ResultSet res  = stmt.executeQuery(select);
	    	if (!res.next()) {
	    		Statement stmt2 = conn.createStatement();
		    	insert = "insert into " + this.name + "_feature_pair_bigram" + 
		    		"(sequential1,sequential2,category,pct_similarity) " +
		    		"values (" + seq1 + "," + seq2 + "," + category + "," + pct + ")";
		    	//System.err.println(insert);
		    	stmt2.executeUpdate(insert);
		    	stmt2.close();
	    	}
			stmt.close();	
	    }
		catch(SQLException ex) { 
			System.err.println(insert);
			System.err.println(select);
			throw ex;
	    }
	}
	
	public void insertFeaturePairTrigram(int seq1, int seq2, double pct, int category) throws SQLException {
		String insert = "";
		String select = "";
		try {
			if (seq1>seq2) {
				int temp = seq1;
				seq1 = seq2;
				seq2 = temp;
			}
			Statement stmt = conn.createStatement();
	    	select = "select * from " + this.name + "_feature_pair_trigram" + 
	    		" where sequential1=" + seq1 + " and sequential2=" + seq2 + 
	    		" and pct_similarity=" + pct;
	    	ResultSet res  = stmt.executeQuery(select);
	    	if (!res.next()) {
	    		Statement stmt2 = conn.createStatement();
		    	insert = "insert into " + this.name + "_feature_pair_trigram" + 
		    		"(sequential1,sequential2,category,pct_similarity) " +
		    		"values (" + seq1 + "," + seq2 + "," + category + "," + pct + ")";
		    	stmt.executeUpdate(insert);
		    	stmt2.close();
	    	}
	    	stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(insert);
			System.err.println(select);
			throw ex;
	    }
	}
	
	public ArrayList<int[]> getAllFeaturePair(double pct) throws SQLException {
		ArrayList<int[]> pairs = new ArrayList<int[]>();
		String select = "";
		try {
	    	Statement stmt = conn.createStatement();
	    	select = "select sequential1, sequential2, category " +
	    		"from " + this.name + "_feature_pair_bigram where pct_similarity=" + pct +
	    		" union " +
	    		"select sequential1, sequential2, category " +
	    		"from " + this.name + "_feature_pair_trigram where pct_similarity=" + pct;
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		int[] pair = {res.getInt("sequential1"),res.getInt("sequential2"),
	    			res.getInt("category")};
	    		pairs.add(pair);
	    	}
	    	pairs.trimToSize();
	    	stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	    }
		return pairs;
	}
	
	public int getMaxPairs(double pct, String selection) throws SQLException {
		int max;
		String select = "";
		try {
	    	Statement stmt = conn.createStatement();
	    	String select1 = "select sequential1, sequential2, category from " + 
	    		this.name + "_feature_pair_bigram where pct_similarity=" + pct;
	    	String select2 = "select sequential1, sequential2, category from " + 
	    		this.name + "_feature_pair_trigram where pct_similarity=" + pct;
	    	if (selection.equals(NormalizationConstant.FEATURE_SELECTION_BIGRAM))
	    		select = select1;
	    	else if (selection.equals(NormalizationConstant.FEATURE_SELECTION_TRIGRAM))
	    		select = select2;
	    	else
	    		select = select1 + " union " + select2;
	    	//System.err.println(select);
	    	ResultSet res  = stmt.executeQuery(select);
	    	int pos = 0;
	    	int neg = 0;
	    	// first line
	    	while (res.next()) {
	    		if (res.getInt("category")==0)
	    			neg++;
	    		else
	    			pos++;
	    	}
	    	if (pos<neg)
	    		max = pos;
	    	else
	    		max = neg;
	    	//System.err.println(max);
	    	stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	    }
		return max;
	}
	
	public void setRandomPairs(double pct, String selection, int category, int maxPairs) throws SQLException {
		String select = "";
		try {
	    	Statement stmt = conn.createStatement();
	    	String select1 = "select sequential1, sequential2 from " + 
	    		this.name + "_feature_pair_bigram where pct_similarity=" + pct + 
	    		" and category=" + category;
	    	String select2 = "select sequential1, sequential2 from " + 
	    		this.name + "_feature_pair_trigram where pct_similarity=" + pct + 
	    		" and category=" + category;
	    	if (selection.equals(NormalizationConstant.FEATURE_SELECTION_BIGRAM))
	    		select = select1;
	    	else if (selection.equals(NormalizationConstant.FEATURE_SELECTION_TRIGRAM))
	    		select = select2;
	    	else
	    		select = "(" + select1 + ") union (" + select2 + ")";
	    	select += " order by rand() limit " + maxPairs;
	    	//System.err.println(select);
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		if (selection.equals(NormalizationConstant.FEATURE_SELECTION_BOTH)) {
	    			int seq1 = res.getInt("sequential1");
	    			int seq2 = res.getInt("sequential2");
	    			Statement stmt2 = conn.createStatement();
	    			String select3 = "select * from " + this.name + "_feature_pair_bigram" +  
	    				" where sequential1=" + seq1 + " and sequential2=" + seq2 + 
	    				" and pct_similarity=" + pct + " and category=" + category;
	    			ResultSet res2  = stmt2.executeQuery(select3);
	    			String update = "update ";
	    			if (res2.next())
	    				update += this.name + "_feature_pair_bigram";
				    else
				    	update += this.name + "_feature_pair_trigram";
			    	update += " set selected=1 where sequential1=" + seq1 + 
			    		" and sequential2=" + seq2 + " and pct_similarity=" + pct;
			    	stmt2.executeUpdate(update);
			    	stmt2.close();
	    		}
	    		else {
	    			Statement stmt2 = conn.createStatement();
	    			String update = "update ";
		    		if (selection.equals(NormalizationConstant.FEATURE_SELECTION_BIGRAM))
		    			update += this.name + "_feature_pair_bigram";
			    	else
			    		update += this.name + "_feature_pair_trigram";
		    		update += " set selected=1 where sequential1=" + 
		    			res.getInt("sequential1") + " and sequential2=" +
	    				res.getInt("sequential2") + " and pct_similarity=" + pct;
		    		stmt2.executeUpdate(update);
		    		stmt2.close();
	    		}
	    	}
	    	stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	    }
	}
	
	public void cleanSelectedPairs(double pct, String selection) throws SQLException {
		String update1 = "";
		String update2 = "";
		try {
	    	Statement stmt = conn.createStatement();
	    	update1 = "update " + this.name + "_feature_pair_bigram set selected=0 " +
	    		"where pct_similarity=" + pct;
	    	update2 = "update " + this.name + "_feature_pair_trigram set selected=0 " + 
	    		"where pct_similarity=" + pct;
	    	if (selection.equals(NormalizationConstant.FEATURE_SELECTION_BIGRAM))
	    		stmt.executeUpdate(update1);
	    	else if (selection.equals(NormalizationConstant.FEATURE_SELECTION_TRIGRAM))
	    		stmt.executeUpdate(update2);
	    	else {
	    		stmt.executeUpdate(update1);
	    		stmt.executeUpdate(update2);
	    	}
	    	stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(update1);
			System.err.println(update2);
			throw ex;
		}
	}
	
	public ArrayList<int[]> getSelectedPairs(double pct, String selection) throws SQLException {
		ArrayList<int[]> pairs = new ArrayList<int[]>();
		String select = "";
		try {
	    	Statement stmt = conn.createStatement();
	    	String select1 = "select sequential1, sequential2, category from " +
	    		this.name + "_feature_pair_bigram where pct_similarity=" + pct + 
    			" and selected=1";
	    	String select2 = "select sequential1, sequential2, category from " +
	    		this.name + "_feature_pair_trigram where pct_similarity=" + pct + 
    			" and selected=1";
	    	if (selection.equals(NormalizationConstant.FEATURE_SELECTION_BIGRAM))
	    		select = select1;
	    	else if (selection.equals(NormalizationConstant.FEATURE_SELECTION_TRIGRAM))
	    		select = select2;
	    	else
	    		select = select1 + " union " + select2;
	    	ResultSet res  = stmt.executeQuery(select);
	    	//System.err.println(select);
	    	int pos = 0;
	    	int neg = 0;
	    	while (res.next()) {
	    		int[] pair = {res.getInt("sequential1"),res.getInt("sequential2"),
	    			res.getInt("category")};
	    		pairs.add(pair);
	    		if (res.getInt("category")==1)
	    			pos++;
	    		else
	    			neg++;
	    		//System.err.println("P=" + pos + "... N=" + neg);
	    	}
	    	stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	    }
		return pairs;
	}
	
}

