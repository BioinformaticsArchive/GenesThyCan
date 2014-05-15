package moara.normalization.dbs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;
import moara.bio.entities.Organism;
import moara.dbs.DBMoaraNormalization;
import moara.normalization.NormalizationConstant;
import moara.normalization.entities.FeatureSynonymComparison;

public class DBCaseFeature extends DBMoaraNormalization {

	public DBCaseFeature() {
		super();
	}
	
	public void deleteCaseFeature(Organism organism) throws SQLException {
		String delete = "";
		try {
	    	Statement stmt = conn.createStatement();
	    	delete = "delete from case_feature where organism='" + organism.Code() + "'";
    		stmt.executeUpdate(delete);
    		delete = "delete from case_feature_tokens where organism='" + organism.Code() + "'";
    		stmt.executeUpdate(delete);
    		stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(delete);
			throw ex;
		}
	}
	
	public void insertCaseFeature(Organism organism, FeatureSynonymComparison fc) throws SQLException {
		int key = -1;
		String insert = "";
		try {
	    	Statement stmt = conn.createStatement();
	    	insert = "insert into case_feature (organism,word1,word2,num_bigrams," +
				"same_prefix,same_suffix,same_number,same_greek,category,frequency," +
				"bigram_similarity,total_number,shape_difference,string_similarity, " +
				"num_trigrams, trigram_similarity) " +
				"values('" + organism.Code() + "',\"" + fc.Word1().Synonym() + 
				"\",\"" + fc.Word2().Synonym() + "\"," + fc.NumberOfBigrams() + "," + 
				(fc.SamePrefix()==true?1:0) + "," + (fc.SameSuffix()==true?1:0) + "," + 
				(fc.SameNumber()==true?1:0) + "," + (fc.SameGreekLetter()==true?1:0) + 
				",'" + fc.Category() + "',1," + fc.BigramSimilarity() + "," + 
				fc.TotalNumber() + "," + fc.ShapeDifference() + "," + 
				fc.StringSimilarity() + "," + fc.NumberOfTrigrams() + "," + 
				fc.TrigramSimilarity() + ")";
	    	stmt.executeUpdate(insert,Statement.RETURN_GENERATED_KEYS);
	    	//System.err.println(insert);
	    	ResultSet res = stmt.getGeneratedKeys();
	    	if (res.next()) {
	    		key = res.getInt(1);
	    		if (fc.DifferentTokens().size()>0)
	    			insertCaseFeatureTokens(key,organism,fc.DifferentTokens(),
	    				NormalizationConstant.TOKENS_DIFFERENT);
	    	}
	    	stmt.close();	
	    }
		catch(SQLException ex) { 
			System.err.println(insert);
			throw ex;
	    }
	}
	
	public void insertCaseFeatureTokens(int sequential, Organism organism, 
			ArrayList<String> tokens, String type) throws SQLException {
		String insert = "";
		try {
	    	Statement stmt = conn.createStatement();
	    	for (int i=0; i<tokens.size(); i++) {
	    		String token = tokens.get(i);
	    		String select = "select * from case_feature_tokens where sequential=" + 
	    			sequential + " and token=\"" + token + "\" and type='" + type + 
	    			"' and organism='" + organism.Code() + "'";
	    		ResultSet res  = stmt.executeQuery(select);
	    		if (!res.next()) {
	    			insert = "insert into case_feature_tokens(sequential,token,type,organism) " +
						"values(" + sequential + ",\"" + token + "\",'" + type + "','" +
						organism.Code() + "')";
	    			stmt.executeUpdate(insert);
	    		}
	    		//System.err.println(insert);
	    	}
	    	stmt.close();	
	    }
		catch(SQLException ex) { 
			System.err.println(insert);
			throw ex;
	    }
	}
	
	public FeatureSynonymComparison getSimilarCaseFeature(Organism organism, FeatureSynonymComparison fc) throws SQLException {
		String select = "";
		fc.setCategory(NormalizationConstant.CATEGORY_FALSE);
		try {
	    	Statement stmt = conn.createStatement();
	    	select = "select sequential, category from case_feature where organism='" + 
	    		organism.Code() + 
	    		"' and same_prefix=" + (fc.SamePrefix()==true?1:0) + 
	    		" and same_suffix=" + (fc.SameSuffix()==true?1:0) + 
	    		" and same_number=" + (fc.SameNumber()==true?1:0) + 
	    		" and same_greek=" + (fc.SameGreekLetter()==true?1:0) +
	    		" and bigram_similarity<=" + fc.BigramSimilarity() +
	    		" and trigram_similarity<=" + fc.TrigramSimilarity() + 
	    		" and total_number=" + fc.TotalNumber() +
	    		" and shape_difference='" + fc.ShapeDifference() +
	    		"' order by gram_similarity desc, frequency desc";
	    	ResultSet res  = stmt.executeQuery(select);
	    	if (res.next()) {
	    		fc.setCategory(res.getString("category"));
	    		fc.setNumberCase(res.getInt("sequential"));
	    	}
	    	stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	    }
		return fc;
	}
	
	public Vector<Integer> getAllCaseFeature(Organism organism) throws SQLException {
		Vector<Integer> pairs = new Vector<Integer>();
		String select = "";
		try {
	    	Statement stmt = conn.createStatement();
	    	select = "select sequential from case_feature where " +
	    		"organism='" + organism.Code() + "' limit 50000";
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		pairs.add(new Integer(res.getInt("sequential")));
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
	
	public FeatureSynonymComparison getCaseFeature(int sequential) throws SQLException {
		String select = "";
		FeatureSynonymComparison fc = new FeatureSynonymComparison();
		try {
	    	Statement stmt = conn.createStatement();
	    	select = "select same_prefix, same_suffix, same_number, same_greek, " +
	    		"bigram_similarity, total_number, category, shape_difference, " +
	    		"string_similarity, trigram_similarity " +
	    		"from case_feature where sequential=" + sequential;
	    	ResultSet res  = stmt.executeQuery(select);
	    	if (res.next()) {
	    		fc.setSamePrefix(res.getString("same_prefix").equals("1")?true:false);
	    		fc.setSameSuffix(res.getString("same_suffix").equals("1")?true:false);
	    		fc.setSameNumber(res.getString("same_number").equals("1")?true:false);
	    		fc.setSameGreekLetter(res.getString("same_greek").equals("1")?true:false);
	    		fc.setBigramSimilarity(res.getFloat("bigram_similarity"));
	    		fc.setTrigramSimilarity(res.getFloat("trigram_similarity"));
	    		fc.setTotalNumber(res.getInt("total_number"));
	    		fc.setCategory(res.getString("category"));
	    		fc.setShapeDifference(res.getFloat("shape_difference"));
	    		fc.setStringSimilarity(res.getFloat("string_similarity"));
	    		fc.setDifferentTokens(getCaseFeatureTokens(sequential,NormalizationConstant.TOKENS_DIFFERENT));
	    	}
	    	stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	    }
		return fc;
	}
	
	public ArrayList<String> getCaseFeatureTokens(int sequential, String type) throws SQLException {
		String select = "";
		ArrayList<String> tokens = new ArrayList<String>();
		try {
	    	Statement stmt = conn.createStatement();
	    	select = "select token from case_feature_tokens where sequential=" + 
	    		sequential + " and type='" + type + "'";
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		tokens.add(res.getString("token"));
	    	}
	    	stmt.close();
	    	tokens.trimToSize();
		}
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	    }
		return tokens;
	}
	
	public ArrayList<String> getDistinctCaseFeatureTokens(Organism organism, String type) throws SQLException {
		String select = "";
		ArrayList<String> tokens = new ArrayList<String>();
		try {
	    	Statement stmt = conn.createStatement();
	    	select = "select count(*), token from case_feature_tokens " +
	    		"where type='" + type + "' and organism='" + organism.Code() + "' " + 
	    		"group by token order by 1 desc";
	    	ResultSet res  = stmt.executeQuery(select);
	    	int total = 0;
	    	while (total<10) {
	    		res.next();
	    		tokens.add(res.getString("token"));
	    		total++;
	    	}		
	    	stmt.close();
	    	tokens.trimToSize();
		}
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	    }
		return tokens;
	}
	
}

