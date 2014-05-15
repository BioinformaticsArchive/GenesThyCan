package moara.dbs;

import java.sql.Connection;

public class DBMoaraGene extends DBMySQL {

	static protected Connection conn;
	static private int numChildren = 0;
	
	public DBMoaraGene() {
		super();
		if (numChildren==0) {
			conn = this.init("moara_gene");
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
