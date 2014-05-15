package moara.corpora.biocreative.dbs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;
import moara.dbs.DBNormalization;
import moara.mention.MentionConstant;
import moara.mention.entities.Mention;
import moara.util.Constant;
import moara.bio.entities.Organism;

public class DBArticleMention extends DBNormalization {
	
	public DBArticleMention() {
		super();
	}
	
	public void insertArticleMention(Organism organism, String corpus, int pmdi, 
			String mention, int start, int end, String system) throws SQLException {
		String insert = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	mention = mention.replace("\"","'");
	    	insert = "insert into article_mention(pmdi, organism, type_corpus, " +
	    		"mention, start_mention, end_mention, system) values(" + pmdi + ",'" + 
	    		organism.Code() + "','" + corpus + "',\"" + mention + "\"," + start + "," +
	    		end + ",'" + system + "')";
	    	stmt.executeUpdate(insert);
			stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.out.println(insert);
	     	throw ex;
	     }
	 }
	
	public void deleteArticleMention(String system) throws SQLException {
	 	String delete = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	delete = "delete from article_mention where system='" + system + "'";
	    	stmt.executeUpdate(delete);
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.out.println(delete);
	     	throw ex; 
	   	}
	 }
	 
	 public void deleteArticleMentionOrganism(Organism organism, String corpus, 
			 String system) throws SQLException {
	 	String delete = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	delete = "delete from article_mention where organism='" + organism.Code() + 
	    		"' and type_corpus='" + corpus + "' and system='" + system + "'";
	    	stmt.executeUpdate(delete);
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.out.println(delete);
	     	throw ex; 
	   	}
	 }
	 
	 public ArrayList<Mention> getMentionsArticle(Organism organism, String corpus, 
			 int pmdi, String system) throws SQLException {
		ArrayList<Mention> mentions = new ArrayList<Mention>();
		String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select mention, start_mention, end_mention from article_mention " +
	    		"where organism='" + organism.Code() + "' and type_corpus='" + corpus + 
	    		"' and pmdi=" + pmdi;
	    	if (system!=null)
	    		select += " and system='" + system + "'";
	    	else
	    		select += " and system in ('" + MentionConstant.TAGGER_BANNER + "','" +
	    			MentionConstant.TAGGER_ABNER + "','" + MentionConstant.TAGGER_CBR + "')";
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		String mention = res.getString("mention");
	    		int start;
	    		if (res.getInt("start_mention")!=0)
	    			start = res.getInt("start_mention");
	    		else
	    			start = 0;
	    		int end;
	    		if (res.getInt("end_mention")!=0)
	    			end = res.getInt("end_mention");
	    		else
	    			end = mention.length()-1;
	    		Mention gm = new Mention(mention,start,end);
	    		mentions.add(gm);
	    	}
	    	mentions.trimToSize();
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.out.println(select);
	     	throw ex; 
	   	}
		mentions.trimToSize();
		return mentions;
	 }
	 
	 public void insertArticleMentionNormalization(Organism organism, int pmdi, String mention, 
			int start, int end) throws SQLException {
		String insert = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	insert = "insert into article_mention(pmdi, organism, type_corpus, " +
	    		"mention, start_mention, end_mention, system) values(" + pmdi + 
	    		",'" + organism.Code() + "','" + Constant.CORPUS_TRAINING + "',\"" + 
	    		mention + "\"," + start + "," + end + ",'normaliz')";
	    	stmt.executeUpdate(insert);
			stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.out.println(insert);
	     	throw ex;
	     }
	 }
	 
	 public void deleteArticleMentionNormalization(Organism organism) throws SQLException {
		String delete = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	delete = "delete from article_mention where organism='" + organism.Code() +
	    		"' and system='normaliz'";
	    	stmt.executeUpdate(delete);
			stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.out.println(delete);
	     	throw ex;
	     }
	 }
	 
	 public Vector<Mention> getMentionsNormalization(Organism organism) throws SQLException {
	 	Vector<Mention> mentions = new Vector<Mention>();
		String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select pmdi, mention, start_mention, end_mention " +
	    		"from article_mention where organism='" + organism.Code() + 
	    		"' and type_corpus='" + Constant.CORPUS_TRAINING + "'";
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		Mention gm = new Mention(res.getString("mention"),
	    			res.getInt("start_mention"),res.getInt("end_mention"),
	    			organism.Code()+res.getInt("pmdi"));
	    		mentions.add(gm);
	    	}
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.out.println(select);
	     	throw ex; 
	   	}
		mentions.trimToSize();
		return mentions;
	 }
	 
	 public boolean getOverlappingMention(Organism organism, int pmdi,
			 int start, int end) throws SQLException {
	 	boolean found = false;
		String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select * from article_mention where organism='" +
	    		organism.Code() + "' and pmdi=" + pmdi + " and type_corpus='" + 
	    		Constant.CORPUS_TRAINING + "' and ((start_mention<=" + start + 
	    		" and end_mention>=" + start + ") or (start_mention<=" + end + 
	    		" and end_mention>=" + end + ") or (start_mention>=" + start + 
	    		" and end_mention<=" + end + "))";
	    	ResultSet res  = stmt.executeQuery(select);
	    	if (!res.next()) {
	    		found = true;
	    	}
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.out.println(select);
	     	throw ex; 
	   	}
		return found;
	 }
	 
}
