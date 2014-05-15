package moara.corpora.biocreative.dbs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import moara.dbs.DBNormalization;
import moara.bio.entities.Organism;

public class DBArticle extends DBNormalization {
	
	public DBArticle() {
		super();
	}
	
	/*public void insertArticle(Article a) throws SQLException {
	 	String insert = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	ResultSet res = stmt.executeQuery("select * from article where pmdi=" + a.Pmdi() + 
	    			" and type_corpus='" + a.CorpusType() + "' and organism='" + a.Organism() + "'");
	    	if (!res.next()) {
	    		Statement stmt2 = conn.createStatement();
	    		insert = "insert into article(pmdi, title, abstract, type_corpus, organism) " +
	    			"values(" + a.Pmdi() + ",\"" + a.Title() + "\",\"" + a.AbstractText() + 
	    			"\",'" + a.CorpusType() + "','" + a.Organism() + "')";
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
 
	 public void truncateArticle() throws SQLException {
	 	String truncate = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	truncate = "truncate article";
	    	stmt.executeUpdate(truncate);
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.out.println(truncate);
	     	throw ex; 
	   	}
	 }
 
	 public void deleteArticleTypeOrganism(String corpus, String organism) throws SQLException {
	 	String delete = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	delete = "delete from article where type_corpus='" + corpus + "' and " +
	    		"organism='" + organism + "'";
	    	stmt.executeUpdate(delete);
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.out.println(delete);
	     	throw ex; 
	   	}
	 }*/
 
	public int updateAbstract(Organism organism, int pmdi, String corpus, String text) throws SQLException {
	 	int total = 0;
		String update = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	update = "update article set abstract=\"" + text + "\" where organism='" + 
	    		organism.Code() + "' and type_corpus='" + corpus + "' and pmdi=" + pmdi;
	    	stmt.executeUpdate(update);
	    	stmt.close();
	    	return total;
		}
		catch(SQLException ex) { 
	     	System.out.println(update);
	     	throw ex;
	    }
	 } 
	
	public Vector<Integer> getAllArticles(String corpus, Organism organism) throws SQLException {
	 	Vector<Integer> articles = new Vector<Integer>();
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select pmdi from article where type_corpus='" + corpus + 
	    		"' and organism='" + organism.Code() + "'";
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		int pmdi = res.getInt("pmdi");
	    		articles.addElement(new Integer(pmdi));
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
 
	 /*public Vector<Article> getAllArticlesObject(String corpus, String organism) throws SQLException {
	 	Vector<Article> articles = new Vector<Article>();
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select pmdi article where type_corpus='" + corpus + "' and " +
	    		"organism='" + organism + "'";
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		int pmdi = res.getInt("pmdi");
	    		articles.addElement(new Article((pmdi)));
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
 
	 public Vector<Integer> getArticlesNotPredicted(String corpus, String organism) throws SQLException {
	 	Vector<Integer> articles = new Vector<Integer>();
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select pmdi from article where type_corpus='" + corpus + 
	    		"' and organism='" + organism + "' and precision_gene is null";
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		int pmdi = res.getInt("pmdi");
	    		articles.addElement(new Integer(pmdi));
	    	}
	    	stmt.close();
	    	articles.trimToSize();
	    	return articles;
		}
		catch(SQLException ex) { 
	     	System.out.println(select);
	     	throw ex;
	    }
	 }*/
 
	 public String getAbstractTextArticle(int pmdi, String corpus, Organism organism) throws SQLException {
	 	String articleText = "";
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select abstract from article where pmdi=" + pmdi + " and type_corpus='" 
	    		+ corpus + "' and organism='" + organism.Code() + "'";
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		articleText = res.getString("abstract");
	    	}
	    	stmt.close();
	    	return articleText;
		}
		catch(SQLException ex) { 
	     	System.out.println(select);
	     	throw ex;
	    }
	 }
	 
	 public String getTextArticle(int pmdi, String corpus, Organism organism) throws SQLException {
	 	String text = "";
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select title, abstract from article where pmdi=" + pmdi + " and type_corpus='" 
	    		+ corpus + "' and organism='" + organism.Code() + "'";
	    	ResultSet res  = stmt.executeQuery(select);
	    	if (res.next()) {
	    		String titleText = res.getString("abstract");
	    		String abstractText = res.getString("abstract");
	    		if (!titleText.equals("null"))
	    			text = titleText + " " + abstractText;
	    		else
	    			text = abstractText;
	    	}
	    	stmt.close();
	    	return text;
		}
		catch(SQLException ex) { 
	     	System.out.println(select);
	     	throw ex;
	    }
	 }
	 
	 /*public boolean getTrainingArticleGeneMention(String organism, String gene, String mention) throws SQLException {
	 	String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select count(*) from article a, article_gene_prediction agp " +
	    		"where a.organism='" + organism + "' and a.type_corpus='" + Constant.DATA_TRAINING + 
	    		"' and a.abstract like \"%" + mention + "%\" and a.organism=agp.organism and " +
	    		"a.pmdi=agp.pmdi and agp.type_data='" + Constant.DATA_GOLD_STANDARD + 
	    		"' and agp.gene_id='" + gene + "'";
	    	ResultSet res  = stmt.executeQuery(select);
	    	if (res.next() && (res.getInt(1)>0)) {
	    		//System.out.println(select);
	    		stmt.close();
	    		return true;
	    	}
	    	stmt.close();
	    	return false;
		}
		catch(SQLException ex) { 
	     	System.out.println(select);
	     	throw ex;
	    }
	 }
	 
	 public void updateArticlePrecision(int pmdi, String corpus, String organism, float precision) throws SQLException {
	 	String update = "";
	 	try {
		    Statement stmt = conn.createStatement();
	 		update = "update article set precision_gene=" + precision + " where pmdi=" + pmdi +
	 			" and type_corpus='" + corpus + "' and organism='" + organism + "'";
	 		stmt.executeUpdate(update);
	 		stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.out.println(update);
	     	throw ex;
	    }
	 }
	 
	 public void updateRankingAnalysis(Article a) throws SQLException {
	 	String update = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	 		update = "update article set num_genes=" + a.NumGenes() + ", num_genes_complete=" + 
	 			a.NumGenesComplete() + ", last_correct_similarity=" + a.LastCorrectSimilarity() + 
	 			", next_correct_similarity=" + a.NextIncorrectSimilarity() + " where pmdi=" + 
	 			a.Pmdi() + " and type_corpus='" + a.CorpusType() + "' and organism='" +
	 			a.Organism();
	 		stmt.executeUpdate(update);
	 		stmt.close();
	 	}
		catch(SQLException ex) { 
	     	System.out.println(update);
	     	throw ex;
	    }
	 }*/
 
}

