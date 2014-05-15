package moara.normalization.dbs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import moara.bio.entities.Organism;
import moara.dbs.DBMoaraNormalization;
import moara.normalization.entities.FeatureSynonym;

public class DBGeneSynonymFeature extends DBMoaraNormalization {

	private String name;
	
	public DBGeneSynonymFeature(Organism organism) {
		super();
		this.name = organism.ShortName();
	}
	
	public void createTableGeneSynonymFeature() throws SQLException {
	 	String create = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	// synonym_feature
	    	create = "create table " + this.name + "_synonym_feature " +
	    		"select * from yeast_synonym_feature";
	    	stmt.executeUpdate(create);
	    	create = "truncate " + this.name + "_synonym_feature";
	    	stmt.executeUpdate(create);
	    	create = "alter table " + this.name + "_synonym_feature " +
	    		"add primary key (sequential)";
	    	stmt.executeUpdate(create);
	    	create = "alter table " + this.name + "_synonym_feature " +
				"modify sequential int not null auto_increment";
	    	stmt.executeUpdate(create);
	    	create = "alter table " + this.name + "_synonym_feature " +
    			"add index Index_2 (num_bigram)";
	    	stmt.executeUpdate(create);
	    	create = "alter table " + this.name + "_synonym_feature " +
				"add index Index_3 (num_trigram)";
	    	stmt.executeUpdate(create);
	    	// synonym_bigram
	    	create = "create table " + this.name + "_synonym_bigram " +
	    		"select * from yeast_synonym_bigram";
	    	stmt.executeUpdate(create);
	    	create = "truncate " + this.name + "_synonym_bigram";
	    	stmt.executeUpdate(create);
	    	create = "alter table " + this.name + "_synonym_bigram " +
	    		"add primary key (sequential,ngram)";
	    	stmt.executeUpdate(create);
	    	// synonym_bigram
	    	create = "create table " + this.name + "_synonym_trigram " +
	    		"select * from yeast_synonym_trigram";
	    	stmt.executeUpdate(create);
	    	create = "truncate " + this.name + "_synonym_trigram";
	    	stmt.executeUpdate(create);
	    	create = "alter table " + this.name + "_synonym_trigram " +
	    		"add primary key (sequential,ngram)";
	    	stmt.executeUpdate(create);
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.err.println(create);
	     	throw ex; 
	   	}
	 }
	 
	 public void dropTableGeneSynonymFeature() throws SQLException {
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
	
	public void trucateSynonymFeature() throws SQLException {
		String delete = "";
		try {
	    	Statement stmt = conn.createStatement();
	    	delete = "truncate " + this.name + "_synonym_feature";
    		stmt.executeUpdate(delete);
    		delete = "truncate " + this.name + "_synonym_bigram";
    		stmt.executeUpdate(delete);
    		delete = "truncate " + this.name + "_synonym_trigram";
    		stmt.executeUpdate(delete);
    		stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(delete);
			throw ex;
		}
	}
	
	public void insertFeatureSynonym(FeatureSynonym fs) throws SQLException {
		int key = -1;
		String insert = "";
		try {
	    	Statement stmt = conn.createStatement();
	    	insert = "insert into " + this.name + "_synonym_feature (gene_id,prefix," +
	    		"suffix,synonym,number,greek,length_gram,shape,tokens,num_bigram," +
	    		"num_trigram) values('" + fs.GeneId() + "',\"" + fs.Prefix() + 
	    		"\",\"" + fs.Suffix() + "\",\"" + fs.Synonym() + "\",'" + 
	    		fs.Number() + "','" + fs.GreekLetter() + "','" + fs.LengthGram() + 
	    		"',\"" + fs.Shape() + "\",\"" + fs.Tokens() + "\"," + 
	    		fs.Bigram().size() + "," + fs.Trigram().size() + ")";
	    	stmt.executeUpdate(insert,Statement.RETURN_GENERATED_KEYS);
    		ResultSet res = stmt.getGeneratedKeys();
	    	if (res.next()) {
	    		key = res.getInt(1);
	    		insertFeatureNgram(key,fs.Bigram(),2);
	    		insertFeatureNgram(key,fs.Trigram(),3);
	    	}
	    	stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(insert);
			throw ex;
	    }
	}
	
	public void insertFeatureNgram(int sequential, ArrayList<String> grams, int ngram) throws SQLException {
		String insert = "";
		try {
	    	Statement stmt = conn.createStatement();
	    	for (int i=0; i<grams.size(); i++) {
	    		String select = "select sequential from " + this.name + "_synonym_";
		    	if (ngram==2)
		    		select += "bigram ";
	    		else
	    			select += "trigram ";
		    	select += "where sequential=" + sequential + " and ngram=\"" + grams.get(i) + "\"";
		    	ResultSet res  = stmt.executeQuery(select);
		    	if (!res.next()) {
		    		Statement stmt2 = conn.createStatement();
		    		insert = "insert into " + this.name + "_synonym_";
		    		if (ngram==2)
		    			insert += "bigram ";
		    		else
		    			insert += "trigram ";
	    			insert += "(sequential,ngram) values(" + sequential + ",\"" + grams.get(i) + "\")";
	    			stmt2.executeUpdate(insert);
	    			stmt2.close();
		    	}
		    }
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
	
	public ArrayList<Integer> getAllSequentialOrganism() throws SQLException {
		ArrayList<Integer> features = new ArrayList<Integer>();
		String select = "";
		try {
	    	Statement stmt = conn.createStatement();
	    	select = "select sequential from " + this.name + "_synonym_feature " +
	    		" order by rand()";
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		features.add(new Integer(res.getInt("sequential")));
	    	}
	    	features.trimToSize();
	    	stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	    }
		return features;
	}
	
	public ArrayList<String> getAllSynonymsOrganism() throws SQLException {
		ArrayList<String> features = new ArrayList<String>();
		String select = "";
		try {
	    	Statement stmt = conn.createStatement();
	    	select = "select synonym from " + this.name + "_synonym_feature";
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		features.add(res.getString("synonym"));
	    	}
	    	features.trimToSize();
	    	stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	    }
		return features;
	}
	
	public ArrayList<FeatureSynonym> getFeaturesByOrganism() throws SQLException {
		ArrayList<FeatureSynonym> features = new ArrayList<FeatureSynonym>();
		String select = "";
		try {
	    	Statement stmt = conn.createStatement();
	    	select = "select sequential, gene_id, synonym, prefix, suffix, number, " +
	    		"greek, length_gram, shape, tokens from " + this.name + "_synonym_feature";
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		FeatureSynonym fs = new FeatureSynonym(res.getString("gene_id"),res.getString("synonym"));
	    		fs.setPrefix(res.getString("prefix"));
	    		fs.setSuffix(res.getString("suffix"));
	    		fs.setNumber(res.getString("number"));
	    		fs.setGreekLetter(res.getString("greek"));
	    		fs.setLengthGram(res.getInt("length_gram"));
	    		fs.setShape(res.getString("shape"));
	    		fs.setTokens(res.getString("tokens"));
	    		ArrayList<String> bigrams = getNgramsFeature(res.getInt("sequential"),2);
	    			fs.setBigram(bigrams);
	    		ArrayList<String> trigrams = getNgramsFeature(res.getInt("sequential"),3);
	    		fs.setTrigram(trigrams);
	    		features.add(fs);
	    	}
	    	features.trimToSize();
	    	stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	    }
		return features;
	}
	
	public FeatureSynonym getFeaturesBySequential(int sequential) throws SQLException {
		FeatureSynonym fs = null;
		String select = "";
		try {
	    	Statement stmt = conn.createStatement();
	    	select = "select gene_id, synonym, prefix, suffix, number, " +
	    		"greek, length_gram, shape, tokens from " + this.name + "_synonym_feature " +
	    		"where sequential=" + sequential;
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		fs = new FeatureSynonym(res.getString("gene_id"),res.getString("synonym"));
	    		fs.setPrefix(res.getString("prefix"));
	    		fs.setSuffix(res.getString("suffix"));
	    		fs.setNumber(res.getString("number"));
	    		fs.setGreekLetter(res.getString("greek"));
	    		fs.setLengthGram(res.getInt("length_gram"));
	    		fs.setShape(res.getString("shape"));
	    		fs.setTokens(res.getString("tokens"));
	    		ArrayList<String> bigrams = getNgramsFeature(sequential,2);
	    		fs.setBigram(bigrams);
	    		ArrayList<String> trigrams = getNgramsFeature(sequential,3);
	    		fs.setTrigram(trigrams);
	    	}
	    	stmt.close();
	    }
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	    }
		return fs;
	}
	
	public ArrayList<int[]> getFeaturesByNGramsGene(ArrayList<String> ngrams, 
			String gene, String synonym, int numGram, double pct) throws SQLException {
		ArrayList<int[]> features = new ArrayList<int[]>();
		String select = "";
		try {
			//double maxGram = (double)ngrams.size()*((double)2-pct)/pct;
			double minGram = (double)ngrams.size()*pct;
			//System.err.println(synonym + " " + minGram);
			Statement stmt = conn.createStatement();
			/*select = "select sequential, quocient from (select g.sequential, " +
				"(2*count(*)/(";*/
			//select = "select f.sequential, (2*count(*)/(";
			select = "select sequential, count(*) as common from ";
			/*if (numGram==2)
				select += "f.num_bigram";
			else
				select += "f.num_trigram";
			select += "+" + ngrams.size()+ ")) as quocient from " + this.name + 
				"_synonym_feature f,";*/
			if (numGram==2)
				select += this.name + "_synonym_bigram g";
			else
				select += this.name + "_synonym_trigram g";
			select += " where ";
			/*if (numGram==2)
				select += "f.num_bigram>=" + minGram + " and f.num_bigram<=" + maxGram;
			else
				select += "f.num_trigram>=" + minGram + " and f.num_trigram<=" + maxGram;*/
			/*if (numGram==2)
				select += "f.num_bigram";
			else
				select += "f.num_trigram";
			select += " between " + minGram + " and " + maxGram;*/
			//select += " and g.sequential=f.sequential and g.ngram in (";
			select += " g.ngram in (";
			//select += " and g.sequential=f.sequential and (";
			for (int i=0; i<ngrams.size(); i++) {
				select += "\"" + ngrams.get(i) + "\",";
			}
			/*select = select.substring(0,select.length()-1) + 
				") group by f.sequential ";*/
			select = select.substring(0,select.length()-1) + 
				") group by sequential order by 2 desc limit 100";
			/*if (numGram==2)
				select += "f.num_bigram";
			else
				select += "f.num_trigram";*/
			//select += " order by 2 desc) grams where quocient>=" + pct;
			//System.err.println(select);
	    	ResultSet res  = stmt.executeQuery(select);
	    	//System.err.println("OK...");
	    	int totalPos = 0;
	    	int totalNeg = 0;
	    	//int total = 0;
	    	//while (res.next() && res.getDouble("quocient")>=pct) {
	    	while (res.next() && res.getDouble("common")>=minGram) {
	    		//total++;
	    		int sequential = res.getInt("sequential");
	    		int common = res.getInt("common");
	    		select = "select gene_id, synonym, ";
	    		if (numGram==2)
	    			select += "num_bigram";
	    		else
	    			select += "num_trigram";
	    		select += " from " + this.name + "_synonym_feature where sequential=" + 
	    			sequential;
    			Statement stmt2 = conn.createStatement();
	    		ResultSet res2  = stmt2.executeQuery(select);
	    		if (res2.next() && ((double)2*common/(double)(res2.getInt(3)+ngrams.size()))>=pct 
	    				&& !res2.getString("synonym").equals(synonym)) {
	    			//System.err.println("-->" + res2.getString("synonym"));
	    			if (totalPos<10 && res2.getString("gene_id").equals(gene)) {
	    				int positive[] = {sequential,1};
			    		features.add(positive);
			    		totalPos++;
	    			}
	    			else if (totalNeg<5 && !res2.getString("gene_id").equals(gene)) {
		    			int negative[] = {sequential,0};
			    		features.add(negative);
			    		totalNeg++;
		    		}
		    		if (totalPos==10 && totalNeg==5)
		    			break;
		    	}
	    		stmt2.close();
	    	}
	    	//System.err.println(gene + " " + totalPos + " " + totalNeg + " " + features.size());
	    	features.trimToSize();
	    	stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	    }
		return features;
	}
	
	/*public ArrayList<int[]> getFeaturesByNGramsGene(ArrayList<String> ngrams, 
			String gene, String synonym, int numGram, double pct) throws SQLException {
		ArrayList<int[]> features = new ArrayList<int[]>();
		String select = "";
		try {
			double minGram = ngrams.size()*pct;
			Statement stmt = conn.createStatement();
			select = "select sequential from (select sequential, " +
				"count(*) as total from ";
			if (numGram==2)
				select += this.name + "_synonym_bigram ";
			else
				select += this.name + "_synonym_trigram ";
			select += "where ngram in (";
			for (int i=0; i<ngrams.size(); i++) {
				select += "\"" + ngrams.get(i) + "\",";
			}
			select = select.substring(0,select.length()-1) + 
				") group by sequential order by 2 desc) grams where " +
				"total>=" + minGram; 
			//System.err.println(select);
	    	ResultSet res  = stmt.executeQuery(select);
	    	int totalPos = 0;
	    	int totalNeg = 0;
	    	int total = 0;
	    	while (res.next()) {
	    		total++;
	    		int sequential = res.getInt(1);
	    		select = "select gene_id, synonym from " + this.name + "_synonym_feature " +
    				"where sequential=" + sequential;
    			Statement stmt2 = conn.createStatement();
	    		ResultSet res2  = stmt2.executeQuery(select);
	    		if (res2.next()) {
	    			//if (!posFound && res2.getString("gene_id").equals(gene) && 
	    			//		!res2.getString("synonym").equals(synonym)) {
	    			//	posFound = true;
	    			//	int positive[] = {sequential,1};
		    		//	features.add(positive);
	    			//}
	    			//else if (!res2.getString("gene_id").equals(gene))
	    			//	negativeSeq = sequential;
		    		//if (posFound && negativeSeq>0) {
		    		//	int negative[] = {negativeSeq,0};
		    		//	features.add(negative);
		    		//	break;
		    		//}
		    		
		    		if (totalPos<2 && res2.getString("gene_id").equals(gene)) {
	    				int positive[] = {sequential,1};
			    		features.add(positive);
			    		totalPos++;
	    			}
		    		else if (totalNeg<2 && !res2.getString("gene_id").equals(gene)) {
		    			int negative[] = {sequential,0};
			    		features.add(negative);
			    		totalNeg++;
		    		}
		    		if (totalPos==2 && totalNeg==2)
		    			break;
		    		
		    	}
	    		stmt2.close();
	    	}
	    	//System.err.println(gene + " " + total + " " + features.size());
	    	features.trimToSize();
	    	stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	    }
		return features;
	}*/
	
	public ArrayList<Integer> getFeaturesByNGrams(ArrayList<String> ngrams, 
			int nGram, double pct) throws SQLException {
		ArrayList<Integer> features = new ArrayList<Integer>();
		String select = "";
		try {
			double maxGram = ngrams.size()*pct;
			Statement stmt = conn.createStatement();
	    	select = "select sequential from (select sequential, count(*) as total from ";
	    	if (nGram==2)
	    		select += this.name + "_synonym_bigram ";
	    	else if (nGram==3)
	    		select += this.name + "_synonym_trigram ";
	    	select += "where ngram in (";
	    	for (int i=0; i<ngrams.size(); i++) {
	    		select += "\"" + ngrams.get(i) + "\",";
	    	}
	    	select = select.substring(0,select.length()-1) + ") group by sequential) " +
	    		"grams where total>=" + maxGram; 
	    	//else
	    	//	select += " order by rand()";
	    	//System.err.println(select);
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		features.add(new Integer(res.getInt("sequential")));
	    	}
	    	features.trimToSize();
	    	stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	    }
		return features;
	}
	
	public ArrayList<FeatureSynonym> getFeaturesByGene(String gene) throws SQLException {
		ArrayList<FeatureSynonym> features = new ArrayList<FeatureSynonym>();
		String select = "";
		try {
			Statement stmt = conn.createStatement();
			select = "select sequential, synonym, prefix, suffix, number, " +
	    		"greek, length_gram, shape, tokens from " + this.name + "_synonym_feature " +
	    		"where gene_id='" + gene + "'";
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		FeatureSynonym fs = new FeatureSynonym(gene,res.getString("synonym"));
	    		fs.setPrefix(res.getString("prefix"));
	    		fs.setSuffix(res.getString("suffix"));
	    		fs.setNumber(res.getString("number"));
	    		fs.setGreekLetter(res.getString("greek"));
	    		fs.setLengthGram(res.getInt("length_gram"));
	    		fs.setShape(res.getString("shape"));
	    		fs.setTokens(res.getString("tokens"));
	    		ArrayList<String> bigrams = getNgramsFeature(res.getInt("sequential"),2);
	    		fs.setBigram(bigrams);
	    		ArrayList<String> trigrams = getNgramsFeature(res.getInt("sequential"),3);
	    		fs.setTrigram(trigrams);
	    		features.add(fs);
	    	}
	    	features.trimToSize();
	    	stmt.close();
	    }
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	    }
		return features;
	}
	
	public ArrayList<FeatureSynonym> getFeaturesOfOtherGene(String gene) throws SQLException {
		ArrayList<FeatureSynonym> features = new ArrayList<FeatureSynonym>();
		String select = "";
		try {
			int maxGene = 100;
			Statement stmt = conn.createStatement();
			select = "select sequential, synonym, prefix, suffix, number, " +
	    		"greek, length_gram, shape, tokens from " + this.name + "_synonym_feature " +
	    		"where gene_id<>'" + gene + "'";
	    	ResultSet res  = stmt.executeQuery(select);
	    	int total = 0;
	    	while (res.next() && total<maxGene) {
	    		FeatureSynonym fs = new FeatureSynonym(gene,res.getString("synonym"));
	    		fs.setPrefix(res.getString("prefix"));
	    		fs.setSuffix(res.getString("suffix"));
	    		fs.setNumber(res.getString("number"));
	    		fs.setGreekLetter(res.getString("greek"));
	    		fs.setLengthGram(res.getInt("length_gram"));
	    		fs.setShape(res.getString("shape"));
	    		fs.setTokens(res.getString("tokens"));
	    		ArrayList<String> bigrams = getNgramsFeature(res.getInt("sequential"),2);
	    		fs.setBigram(bigrams);
	    		ArrayList<String> trigrams = getNgramsFeature(res.getInt("sequential"),3);
	    		fs.setTrigram(trigrams);
	    		features.add(fs);
	    		total++;
	    	}
	    	features.trimToSize();
	    	stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	    }
		return features;
	}
	
	public ArrayList<String> getNgramsFeature(int sequential, int ngram) throws SQLException {
		ArrayList<String> gramsArr = new ArrayList<String>(10);
		String select = "";
		try {
	    	Statement stmt = conn.createStatement();
	    	select = "select ngram from " + this.name + "_synonym_";
	    	if (ngram==2)
	    		select += "bigram ";
	    	else
	    		select += "trigram ";
	    	select += "where sequential=" + sequential + " order by ngram";
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		gramsArr.add(res.getString("ngram"));
	    	}
	    	stmt.close();
		}
		catch(SQLException ex) { 
			System.err.println(select);
			throw ex;
	    }
		gramsArr.trimToSize();
		return gramsArr;
	}
	
}

