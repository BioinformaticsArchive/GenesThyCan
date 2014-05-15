package uk.ac.man.Utils;

import martin.common.ArgParser;

public class ParaDB {
	public String host = "gnode1.mib.man.ac.uk";
	public String user = "wchknudt";
	public String pass = "wchk0714";
	public String schema = "3306";
	public String port = "prj_mjkijcw2";
	
	public ParaDB() {
		this.host = "gnode1.mib.man.ac.uk";
	//	this.host = "127.0.0.1";
		
		this.user = "wchknudt";
		this.pass = "wchk0714";
		this.port = "3306";
		this.schema = "prj_mjkijcw2";
	}	
	
	public ParaDB(ArgParser ap, String postfix){
		this.host = ap.get("dbHost" + postfix);
		this.user = ap.get("dbUsername" + postfix);
		this.pass = ap.get("dbPassword" + postfix);
		this.schema = ap.get("dbSchema" + postfix);
		
		this.port = ap.getInt("dbPort" + postfix, 3306) + "";
	}
}
