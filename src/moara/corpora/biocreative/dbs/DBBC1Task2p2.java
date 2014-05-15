package moara.corpora.biocreative.dbs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import moara.dbs.DBBioCreative;

public class DBBC1Task2p2 extends DBBioCreative {

	public DBBC1Task2p2() {
		super();
	}
	
	public void truncate() throws SQLException {
	 	String truncate = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	truncate = "truncate bc1_task2_2";
	    	stmt.executeUpdate(truncate);
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.out.println(truncate);
	     	throw ex; 
	   	}
	 }
	
	public void insert(String pmid, String protName, String dbId, String dbSource, 
			String goCode, String goName, String evalProt, String evalGo, 
			String evidence, String comment) throws SQLException {
		String insert = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	insert = "insert into bc1_task2_2(pmid,protein_name,database_id,database_source," +
	    		"go_code,go_name,eval_prot,eval_go,evidence,comment) " +
	    		"values('" + pmid + "',\"" + protName + "\",'" + dbId + "','" + dbSource + "','" + 
	    		goCode + "',\"" + goName + "\",'" + evalProt + "','" + evalGo + "',\"" + 
	    		evidence + "\",\"" + comment + "\")";
	    	stmt.executeUpdate(insert);
			stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.out.println(insert);
	     	throw ex;
	     }
	 }
	
	public HashMap<Integer,String> getEvidences(String evalGo, String evalProt) throws SQLException {
		HashMap<Integer,String> evidences = new HashMap<Integer,String>();
		String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select sequential, evidence from bc1_task2_2";
	    	if (evalGo!=null || evalProt!=null) {
	    		select += " where ";
	    		if (evalGo!=null)
	    			select += "eval_go='" + evalGo + "'";
	    		if (evalProt!=null) {
	    			if (evalGo!=null)
	    				select += " and ";
	    			select += "eval_prot='" + evalProt + "'";
	    		}
	    	}
	    	ResultSet res  = stmt.executeQuery(select);
	    	while (res.next()) {
	    		evidences.put(res.getInt("sequential"),res.getString("evidence"));
	    	}
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.out.println(select);
	     	throw ex; 
	   	}
		return evidences;
	 }
	
	public String getGoCodeBySequential(int sequential) throws SQLException {
		String code = "";
		String select = "";
	 	try {
	    	Statement stmt = conn.createStatement();
	    	select = "select go_code from bc1_task2_2 where sequential=" + sequential;
	    	ResultSet res  = stmt.executeQuery(select);
	    	if (res.next())
	    		code = res.getString("go_code");
	    	stmt.close();
	    }
		catch(SQLException ex) { 
	     	System.out.println(select);
	     	throw ex; 
	   	}
		return code;
	}
	
}
