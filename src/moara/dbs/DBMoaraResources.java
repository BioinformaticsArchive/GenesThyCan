package moara.dbs;

import java.sql.Connection;
import java.sql.SQLException;

public class DBMoaraResources extends DBMySQL {

	static protected Connection conn;
	static private int numChildren = 0;
	
	public DBMoaraResources() {
		super();
		if (numChildren==0) {
			conn = this.init("moara_resources");
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
	
	public String[] getIdText(boolean start, String[] organisms) throws SQLException {
		return null;
	}
	
}
