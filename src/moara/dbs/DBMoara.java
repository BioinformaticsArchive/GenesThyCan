package moara.dbs;

import java.sql.Connection;

public class DBMoara extends DBMySQL {

	static protected Connection conn;
	static private int numChildren = 0;
	
	public DBMoara() {
		super();
		if (numChildren==0) {
			conn = this.init("moara");
		}
		numChildren++;
	}
	
	public Connection getConnection() {
		return conn;
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
