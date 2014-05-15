package moara.dbs;

import java.sql.Connection;

public class DBNormalization extends DBMySQL {

	static protected Connection conn;
	static private int numChildren = 0;
	
	public DBNormalization() {
		super();
		if (numChildren==0) {
			conn = this.init("normalization");
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
