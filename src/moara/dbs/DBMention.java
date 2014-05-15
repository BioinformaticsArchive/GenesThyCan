package moara.dbs;

import java.sql.Connection;

public class DBMention extends DBMySQL {

	static protected Connection conn;
	static private int numChildren = 0;
	
	public DBMention(String database) {
		super();
		if (numChildren==0) {
			conn = this.init(database);
		}
		numChildren++;
	}
	
	public void executeCommit() {
	 	this.executeCommit(conn);
	}
	 
	public void executeRollback() {
		this.executeRollback(conn);
	}
	 
	protected void finalize() {
		numChildren--;
		if (numChildren==0)
			closeConnection(conn);
	}
	
}
