package moara.corpora.biocreative.dbs;

import java.sql.SQLException;
import java.sql.Statement;
import moara.dbs.DBNormalization;
import moara.evaluation.ResultEvaluation;
import moara.bio.entities.Organism;

public class DBResultsTest extends DBNormalization {

	public DBResultsTest() {
		super();
	}
	
	public void insertResultTest(Organism organism, String corpus, ResultEvaluation result,
			String ml, String ss, String features, String pctSym, String selection, 
			String disambSelec, String disambType) throws SQLException {
		String insert = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	insert = "insert into results_test(organism,corpus,machine_learning," +
	    		"string_similarity,features,pct_similarity,gram_selection," +
	    		"disambiguation_selection,disambiguation_type,true_positive," +
	    		"false_positive,false_negative,precision_micro,recall_micro," +
	    		"fmeasure_micro) " +
	    		"values('" + organism.Code() + "','" + corpus + "','" + ml + "','" + ss + 
	    		"','" + features + "','" + pctSym + "','" + selection + "','" + 
	    		disambSelec + "','" + disambType + "'," + result.TruePos() + 
	    		"," + result.FalsePos() + "," + result.FalseNeg() + "," + 
	    		result.Precision()*100 + "," + result.Recall()*100 + "," + 
	    		result.FMeasure()*100 + ")";
	    	//System.out.println(insert);
	    	stmt.executeUpdate(insert);
			stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.out.println(insert);
	     	throw ex;
	     }
	 }
	
	public void deleteResultTest(Organism organism, String corpus, String ml, String ss, 
			String features, String pctSym, String selection, String disambSelec, 
			String disambType) throws SQLException {
		String delete = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	delete = "delete from results_test where organism='" + organism.Code() + 
	    		"' and corpus='" + corpus + "' and machine_learning='" + ml + 
	    		"' and string_similarity='" + ss + "' and features='" + features + 
	    		"' and pct_similarity='" + pctSym + "' and gram_selection='" +
	    		selection + "' and disambiguation_selection='" + disambSelec + 
	    		"' and disambiguation_type='" + disambType + "'";
	    	stmt.executeUpdate(delete);
			stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.out.println(delete);
	     	throw ex;
	     }
	 }
	
}
