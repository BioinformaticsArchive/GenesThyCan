package moara.corpora.biocreative.dbs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import java.util.ArrayList;
import moara.bio.entities.Organism;
import moara.dbs.DBNormalization;
import moara.normalization.NormalizationConstant;
import moara.normalization.entities.GenePrediction;
import moara.util.Constant;
import moara.gene.dbs.DBGene;

public class DBArticleGenePrediction extends DBNormalization {
	
	public DBArticleGenePrediction() {
		super();
	}
	
	public void insertArticleGenePrediction(Organism organism, String corpus, int pmdi, GenePrediction gp) throws SQLException {
	 	String insert = "";
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	/*DBGene dbGene = new DBGene(organism);
	    	String databaseId = dbGene.getDatabase1Gene(gp.GeneId());
	    	if (organism.Code().equals(Constant.ORGANISM_YEAST))
	    		databaseId = databaseId.substring(databaseId.indexOf("SGD:")+4);
	    	else if (organism.Code().equals(Constant.ORGANISM_FLY))
	    		databaseId = databaseId.substring(databaseId.indexOf("FLYBASE:")+8);
	    	else if (organism.Code().equals(Constant.ORGANISM_HUMAN))
	    		databaseId = gp.GeneId();*/
	    	select = "select * from article_gene_prediction where " +
    			"pmdi=" + pmdi + " and type_corpus='" + corpus + "' and gene_id='" +
    			gp.GeneId() + "' and type_data='" + NormalizationConstant.DATA_PREDICTION + 
    			"' and organism='" + organism.Code() + "'";
	    	ResultSet res  = stmt.executeQuery(select);
	    	if (!res.next()) {
	    		Statement stmt2 = conn.createStatement();
	    		insert = "insert into article_gene_prediction(pmdi, type_corpus, organism, gene_id, " +
					"similarity, synonym_words, mention_words, type_data, type_prediction," +
					"disambiguation_words, type_matching, number_case) values(" + pmdi + ",'" + 
					corpus + "','" + organism.Code() + "','" + gp.GeneId() + "'," + gp.MaxScore() + ",\"" + 
					gp.Synonym() + "\",\"" + gp.Mention() + "\",'" + NormalizationConstant.DATA_PREDICTION + "','" + 
					gp.Type() + "',\"" + gp.WordsDisambiguation() + "\",'" + gp.TypeMatching() + "'," +
					gp.NumberCase() + ")";
	    		stmt2.executeUpdate(insert);
		    	stmt2.close();
	    	}
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.out.println(insert);
	     	throw ex;
	    }
	 }
	
	public void insertGoldStandard(int pmdi, String corpus, Organism organism, String id, 
			String goldList, String goldAbstract, String mention) throws SQLException {
	 	String insert = "";
	 	try {
	 		Statement stmt = conn.createStatement();
	    	ResultSet res  = stmt.executeQuery("select * from article_gene_prediction where " +
	    		"pmdi=" + pmdi + " and type_corpus='" + corpus + "' and gene_id='" +
	    		id + "' and type_data='" + NormalizationConstant.DATA_GOLD_STANDARD + "' and organism='" +
	    		organism.Code() + "'");
	    	if (!res.next()) {
	    		Statement stmt2 = conn.createStatement();
		    	insert = "insert into article_gene_prediction(pmdi, type_corpus, organism, gene_id, " +
					"type_data, gold_list, gold_abstract,mention_words) values(" + pmdi + ",'" + 
					corpus + "','" + organism.Code() + "','" + id + "','" + NormalizationConstant.DATA_GOLD_STANDARD + 
					"','" + goldList + "','" + goldAbstract + "',\"" + mention + "\")";
		    	stmt2.executeUpdate(insert);
		    	stmt2.close();
	    	}
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.out.println(insert);
	     	throw ex;
	    }
	 }
	
	public void insertExternalPrediction(int pmdi, String corpus, Organism organism, String id, 
			String mention, String system) throws SQLException {
	 	String insert = "";
	 	try {
	 		Statement stmt = conn.createStatement();
	    	ResultSet res  = stmt.executeQuery("select * from article_gene_prediction where " +
	    		"pmdi=" + pmdi + " and type_corpus='" + corpus + "' and gene_id='" +
	    		id + "' and type_data='" + NormalizationConstant.DATA_PREDICTION + "' and organism='" +
	    		organism.Code() + "'");
	    	if (!res.next()) {
	    		Statement stmt2 = conn.createStatement();
	    		insert = "insert into article_gene_prediction(pmdi, type_corpus, organism, " +
	    			"gene_id, mention_words, type_data, system) values(" + pmdi + ",'" + corpus + "','" + 
	    			organism.Code() + "','" + id + "',\"" + mention + "\",'" + 
	    			NormalizationConstant.DATA_PREDICTION + "','miguel')";
	    		stmt2.executeUpdate(insert);
		    	stmt2.close();
	    	}
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.out.println(insert);
	     	throw ex;
	    }
	 }
 
	 public void deleteArticleGenePredictionOrganism(Organism organism, String corpus) throws SQLException {
	 	String delete = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	delete = "delete from article_gene_prediction where organism='" + organism.Code() + 
	    		"' and type_data='" + NormalizationConstant.DATA_PREDICTION + "' and type_corpus='" +
	    		corpus + "'";
	    	//System.out.println(delete);
	    	stmt.executeUpdate(delete);
	    	stmt.close();
	    }
		catch(SQLException ex) { 
			System.out.println(delete);
			throw ex; 
	   	}
	 }
	 
	 public void deleteGenePredictionArticle(Organism organism, String corpus, int pmdi) throws SQLException {
	 	String delete = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	delete = "delete from article_gene_prediction where organism='" + organism.Code() + 
	    		"' and type_data='" + NormalizationConstant.DATA_PREDICTION + "' and type_corpus='" +
	    		corpus + "' and pmdi=" + pmdi;
	    	stmt.executeUpdate(delete);
	    	stmt.close();
	    }
		catch(SQLException ex) { 
			System.out.println(delete);
			throw ex; 
	   	}
	 }
	 
	 public void deleteGoldStandardOrganism(Organism organism, String corpus) throws SQLException {
	 	String delete = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	delete = "delete from article_gene_prediction where organism='" + organism.Code() + 
    			"' and type_corpus='" + corpus + "' and type_data='" + 
    			NormalizationConstant.DATA_GOLD_STANDARD + "'";
	    	stmt.executeUpdate(delete);
	    	stmt.close();
	    }
		catch(SQLException ex) { 
			System.out.println(delete);
			throw ex; 
	   	}
	 }
	 
	 public void clearStatusPrediction(Organism organism, String corpus, int idDebug) throws SQLException {
	 	String delete = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	delete = "update article_gene_prediction set status='' where " +
	    		"organism='" + organism.Code() + "' and type_corpus='" + corpus + "'";
	    	if (idDebug!=0)
	    		delete += " and pmdi=" + idDebug;
	    	stmt.executeUpdate(delete);
	    	stmt.close();
	    }
		catch(SQLException ex) { 
			System.out.println(delete);
			throw ex; 
	   	}
	 }
	 
	 public int updateTruePositives(Organism organism, String corpus, int idDebug) throws SQLException {
	 	int total = 0;
		String select = "";
		String update = "";
	 	try {
	    	Statement stmt = conn.createStatement();
    		select = "select a1.pmdi, a1.gene_id from article_gene_prediction a1 " +
	    		"where a1.organism='" + organism.Code() + "' and a1.type_data='" +
	    		NormalizationConstant.DATA_PREDICTION + "' and a1.type_corpus='" + corpus + "'";
    			if (idDebug!=0)
    				select += " and a1.pmdi=" + idDebug;
    			select += " and (a1.pmdi, a1.gene_id) in (select a2.pmdi, a2.gene_id " +
	    		"from article_gene_prediction a2 where a2.organism='" + 
	    		organism.Code() + "' and a2.type_data='" + NormalizationConstant.DATA_GOLD_STANDARD + 
	    		"' and a2.type_corpus='" + corpus + "'";
    		if (idDebug!=0)
    			select += " and a2.pmdi=" + idDebug;
    		if (!organism.Code().equals(Constant.ORGANISM_HUMAN))
    			select += " and a2.gold_abstract='Y'";
    		select += ")";
    		//System.out.println(select);
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		int pmdi = res.getInt("a1.pmdi");
	    		String id = res.getString("a1.gene_id");	    		
	    		Statement stmt2 = conn.createStatement();
		    	update = "update article_gene_prediction set status='" + Constant.STATUS_TRUE_POSITIVE +
		    		"' where organism='" + organism.Code() + "' and type_data='" + Constant.DATA_PREDICTION +
		    		"' and type_corpus='" + corpus + "' and pmdi=" + pmdi + " and gene_id='" +
		    		id + "'";
		    	stmt2.executeUpdate(update);
		    	stmt2.close();
		    	total++;
	    	}
	    	stmt.close();
	    	return total;
		}
		catch(SQLException ex) { 
	     	System.out.println(select);
	     	throw ex;
	    }
	 }
	 
	 public int updateFalseNegative(Organism organism, String corpus, int idDebug) throws SQLException {
	 	int total = 0;
		String select = "";
		String update = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select a1.pmdi, a1.gene_id from article_gene_prediction a1 " +
				"where a1.organism='" + organism.Code() + "' and a1.type_data='" +
				Constant.DATA_GOLD_STANDARD + "' and a1.type_corpus='" + corpus + "'";
	    	if (idDebug!=0)
				select += " and a1.pmdi=" + idDebug;
	    	if (!organism.Code().equals(Constant.ORGANISM_HUMAN))
    			select += " and a1.gold_abstract='Y'";
	    	select += " and (a1.pmdi, a1.gene_id) not in (select a2.pmdi, a2.gene_id " +
	    		"from article_gene_prediction a2 where a2.organism='" + organism.Code() + 
	    		"' and a2.type_data='" + Constant.DATA_PREDICTION + 
	    		"' and a2.type_corpus='" + corpus + "'";
	    	if (idDebug!=0)
    			select += " and a2.pmdi=" + idDebug;
	    	select += ")";
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		int pmdi = res.getInt("a1.pmdi");
	    		String id = res.getString("a1.gene_id");	    		
	    		Statement stmt2 = conn.createStatement();
		    	update = "update article_gene_prediction set status='" + Constant.STATUS_FALSE_NEGATIVE +
		    		"' where organism='" + organism.Code() + "' and type_data='" + Constant.DATA_GOLD_STANDARD +
		    		"' and type_corpus='" + corpus + "' and pmdi=" + pmdi + " and gene_id='" +
		    		id + "'";
		    	stmt2.executeUpdate(update);
		    	stmt2.close();
		    	total++;
	    	}
	    	stmt.close();
	    	return total;
		}
		catch(SQLException ex) { 
	     	System.out.println(select);
	     	throw ex;
	    }
	 }
	 
	 public int updateFalsePositive(Organism organism, String corpus, int idDebug) throws SQLException {
	 	int total = 0;
		String select = "";
		String update = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select a1.pmdi, a1.gene_id from article_gene_prediction a1 " +
	    		"where a1.organism='" + organism.Code() + "' and a1.type_data='" +
	    		Constant.DATA_PREDICTION + "' and a1.type_corpus='" + corpus + "'";
	    		if (idDebug!=0)
	    			select += " and a1.pmdi=" + idDebug;
	    		select += "	and (a1.pmdi, a1.gene_id) not in (select a2.pmdi, a2.gene_id " +
	    		"from article_gene_prediction a2 where a2.organism='" + organism.Code() + 
	    		"' and a2.type_data='" + Constant.DATA_GOLD_STANDARD + 
	    		"' and a2.type_corpus='" + corpus + "'";
	    	if (idDebug!=0)
    			select += " and a2.pmdi=" + idDebug;
	    	if (!organism.Code().equals(Constant.ORGANISM_HUMAN))
    			select += " and a2.gold_abstract='Y'";
    		select += ")";
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		int pmdi = res.getInt("a1.pmdi");
	    		String id = res.getString("a1.gene_id");	    		
	    		Statement stmt2 = conn.createStatement();
		    	update = "update article_gene_prediction set status='" + Constant.STATUS_FALSE_POSITIVE +
		    		"' where organism='" + organism.Code() + "' and type_data='" + Constant.DATA_PREDICTION +
		    		"' and type_corpus='" + corpus + "' and pmdi=" + pmdi + " and gene_id='" +
		    		id + "'";
		    	stmt2.executeUpdate(update);
		    	stmt2.close();
		    	total++;
	    	}
	    	stmt.close();
	    	return total;
		}
		catch(SQLException ex) { 
	     	System.out.println(select);
	     	throw ex;
	    }
	 }
	 
	 public Vector<GenePrediction> getArticlesPrediction(Organism organism, String corpus) throws SQLException {
	 	Vector<GenePrediction> predictions = new Vector<GenePrediction>();
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select pmdi, status, gene_id, type_prediction, type_matching, " +
	    		"synonym_words, mention_words, disambiguation_words " +
	    		"from article_gene_prediction where organism='" + 
	    		organism.Code() + "' and type_data='" + Constant.DATA_PREDICTION + "' and " +
	    		"type_corpus='" + corpus + "'" + 
	    		" union " +
	    		"select pmdi, status, gene_id, type_prediction, type_matching, " +
	    		"synonym_words, mention_words, disambiguation_words " +
	    		"from article_gene_prediction where organism='" + 
	    		organism.Code() + "' and type_data='" + Constant.DATA_GOLD_STANDARD + "' and " +
	    		"type_corpus='" + corpus + "' and status='" + Constant.STATUS_FALSE_NEGATIVE + "'" + 
	    		" order by pmdi";
	    	ResultSet res  = stmt.executeQuery(select);
	    	//System.out.println(select);
	    	while (res.next()) {
	    		GenePrediction gp = new GenePrediction(res.getInt("pmdi"),
		    		res.getString("gene_id"),res.getString("synonym_words"),
		    		res.getString("mention_words"));
	    		gp.setStatus(res.getString("status"));
	    		gp.setType(res.getString("type_prediction"));
	    		gp.setTypeMatching(res.getString("type_matching"));
	    		gp.setWordsDisambiguation(res.getString("disambiguation_words"));
	    		predictions.add(gp);
	    	}
	    	stmt.close();
	    	predictions.trimToSize();
	    	return predictions;
		}
		catch(SQLException ex) { 
	     	System.out.println(select);
	     	throw ex;
	    }
	 }
	 
	 public Vector<String> getGoldStandardArticle(int pmdi, String corpus, Organism organism, boolean goldAbstract) throws SQLException {
	 	Vector<String> genes = new Vector<String>();
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select gene_id from article_gene_prediction where pmdi=" + pmdi + " and type_corpus='" +
	    		corpus + "' and organism='" + organism.Code() + "' and type_data='" + Constant.DATA_GOLD_STANDARD + "'";
	    	if (goldAbstract)
	    		select = select + " and gold_abstract='Y'";
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		genes.addElement(res.getString("gene_id"));
	    	}
	    	stmt.close();
	    	genes.trimToSize();
	    	return genes;
		}
		catch(SQLException ex) { 
	     	System.out.println(select);
	     	throw ex;
	    }
	 }
	 
	 public Vector<String> getGenesArticlePrediction(int pmdi, String corpus) throws SQLException {
		 Vector<String> genes = new Vector<String>();
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "SELECT SGDID FROM ARTICLE_GENE_PREDICTION WHERE PMDI=" + pmdi + 
	    		" AND TYPE_CORPUS='" + corpus + "' ORDER BY SIMILARITY DESC";
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		genes.addElement(res.getString("sgdid"));
	    	}
	    	stmt.close();
	    	genes.trimToSize();
	    	return genes;
		}
		catch(SQLException ex) { 
	     	System.out.println(select);
	     	throw ex;
	    }
	 }
	 
	 public Vector<String> getArticleGoldStandardGene(Organism organism, String corpus, String geneId) throws SQLException {
		 Vector<String> articles = new Vector<String>();
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select a.abstract from article_gene_prediction agp, article a " +
	    		"where agp.organism='" + organism.Code() + "' and agp.type_corpus='" + corpus + 
	    		"' and agp.type_data='" + Constant.DATA_GOLD_STANDARD + "' and agp.gene_id='" +
	    		geneId + "' and agp.organism=a.organism and agp.pmdi=a.pmdi";
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		articles.addElement(res.getString(1));
	    	}
	    	stmt.close();
	    	articles.trimToSize();
	    	return articles;
		}
		catch(SQLException ex) { 
	     	System.out.println(select);
	     	throw ex;
	    }
	 }
	 
	 public ArrayList<Integer> getPubMedGoldStandard(Organism organism, String corpus) throws SQLException {
		 ArrayList<Integer> articles = new ArrayList<Integer>();
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select distinct pmdi from article_gene_prediction " +
	    		"where organism='" + organism.Code() + "' and type_corpus='" + corpus + 
	    		"' and type_data='" + Constant.DATA_GOLD_STANDARD + "'";
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		articles.add(new Integer(res.getString(1)));
	    	}
	    	stmt.close();
	    	articles.trimToSize();
	    	return articles;
		}
		catch(SQLException ex) { 
	     	System.out.println(select);
	     	throw ex;
	    }
	 }
	 
	 public int getNumTrainingArticleGene(Organism organism, String id) throws SQLException {
	 	int total = 0;
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select count(*) from article_gene_prediction where organism='" + 
	    		organism.Code() + "' and type_data='" + Constant.DATA_GOLD_STANDARD + "' and " +
	    		"type_corpus='" + Constant.CORPUS_TRAINING + "' and gene_id='" + id + "'";
	    	ResultSet res  = stmt.executeQuery(select);
	    	if (res.next()) {
	    		total = res.getInt(1);
	    	}
	    	stmt.close();
	    	return total;
		}
		catch(SQLException ex) { 
	     	System.out.println(select);
	     	throw ex;
	    }
	 }
	 
	 public float getSimilarityArticleGene(int pmdi, String corpus, String sgdid) throws SQLException {
	 	float similarity = 0;
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "SELECT SIMILARITY FROM ARTICLE_GENE_PREDICTION WHERE PMDI=" + pmdi + 
	    		" AND TYPE_CORPUS='" + corpus + "' AND SGDID='" + sgdid + "'";
	    	ResultSet res  = stmt.executeQuery(select);
	    	if (res.next()) {
	    		similarity = res.getFloat("similarity");
	    	}
	    	stmt.close();
	    	return similarity;
		}
		catch(SQLException ex) { 
	     	System.out.println(select);
	     	throw ex;
	    }
	 }
	 
	 public int getTotalStatus(Organism organism, String corpus, String status) throws SQLException {
	 	int total = 0;
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select count(*) from article_gene_prediction where organism='" + 
	    		organism.Code() + "' and type_corpus='" + corpus + "' and status='" + status + "'";
	    	ResultSet res  = stmt.executeQuery(select);
	    	if (res.next()) {
	    		total = res.getInt(1);
	    	}
	    	stmt.close();
	    	return total;
		}
		catch(SQLException ex) { 
	     	System.out.println(select);
	     	throw ex;
	    }
	 }
	 
	 public int getTotalPredictionMatching(Organism organism, String corpus, 
			 String typePrediction, String typeMatching) throws SQLException {
		 	int total = 0;
		 	String select = "";
		 	try {
		    	Statement stmt = conn.createStatement();
		    	select = "select count(*) from article_gene_prediction where organism='" + 
		    		organism.Code() + "' and type_corpus='" + corpus + "' and type_data='" + 
		    		Constant.DATA_PREDICTION + "' and type_prediction='" + 
		    		typePrediction + "' and type_matching='" + typeMatching + "'";
		    	ResultSet res  = stmt.executeQuery(select);
		    	if (res.next()) {
		    		total = res.getInt(1);
		    	}
		    	stmt.close();
		    	return total;
			}
			catch(SQLException ex) { 
		     	System.out.println(select);
		     	throw ex;
		    }
		 }
	 
	 public int getTotalByTypeAndSynonymType(Organism organism, String corpus, 
			 String typePrediction, String typeSynonym) throws SQLException {
	 	int total = 0;
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select count(*) from article_gene_prediction agp, gene_synonym gs " +
	    		"where agp.organism='" + organism.Code() + "' and agp.type_corpus='" + corpus + 
	    		"' and agp.type_data='" + Constant.DATA_PREDICTION + 
	    		"' and agp.type_prediction='" + typePrediction + "' and agp.organism=gs.organism " +
	    		"and agp.gene_id=gs.gene_id and gs.type='" + typeSynonym + 
	    		"' and agp.synonym_words=gs.synonym";
	    	ResultSet res  = stmt.executeQuery(select);
	    	if (res.next()) {
	    		total = res.getInt(1);
	    	}
	    	stmt.close();
	    	return total;
		}
		catch(SQLException ex) { 
	     	System.out.println(select);
	     	throw ex;
	    }
	 }
	 
	 public Vector<GenePrediction> getTrainingGoldStandard(Organism organism) throws SQLException {
	 	Vector<GenePrediction> articles = new Vector<GenePrediction>();
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select pmdi, gene_id from article_gene_prediction " +
	    		"where organism='" + organism.Code() + "' and type_corpus='" + 
	    		Constant.CORPUS_TRAINING + "' and type_data='" + 
	    		Constant.DATA_GOLD_STANDARD + "' and gold_abstract='Y'";
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		articles.addElement(new GenePrediction(res.getInt("pmdi"),
	    			res.getString("gene_id")));
	    	}
	    	stmt.close();
	    	articles.trimToSize();
	    	return articles;
		}
		catch(SQLException ex) { 
	     	System.out.println(select);
	     	throw ex;
	    }
	 }
 
}