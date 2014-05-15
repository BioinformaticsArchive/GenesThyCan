
//Class that makes the interaction and queries with the MySQL database

package moara.dbs;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.*; 

import moara.util.EnvironmentVariable;

import com.mysql.jdbc.CommunicationsException;

public abstract class DBMySQL {
	
	private String server;
	private String login;
	private String password;
	
	public DBMySQL() {
		
	}
	
	public Connection init(String database) {
		readConfiguration();
		return initConnection(database);
	}
	
	private Connection initConnection(String database) {
		String url = "";
		try {
			url = "jdbc:mysql://" + this.server + "/" + database;
			Class.forName("com.mysql.jdbc.Driver");
			return connect(url);
		} 
		catch(ClassNotFoundException ex) { 
			System.err.println(ex); 
		}
		catch(CommunicationsException ex) { 
			System.err.println(ex); 
		}
		catch(Exception ex) { 
			System.err.println(ex); 
		}
		return null;
	}
	
	private void readConfiguration() {
		try {
			String file = EnvironmentVariable.getMoaraHome()+"moara.ini";
			//String file = "/home/mariana/ucompare/moara.ini";
			RandomAccessFile r = new RandomAccessFile(file,"r");
			String line = r.readLine();
			while (line!=null) {
				int index = line.indexOf("=");
				String value = line.substring(index+1,line.length()).trim();
				// server
				if (line.startsWith("server"))
					this.server = value;
				// login
				else if (line.startsWith("login"))
					this.login = value;
				// password
				else if (line.startsWith("password"))
					this.password = value;
				line = r.readLine();
			}
			r.close();
		}
		catch(IOException ex) { 
			System.err.println(ex); 
		}
	}
	
	private Connection connect(String url) throws CommunicationsException {
		try {
			Connection conn = DriverManager.getConnection(url,this.login,this.password);
			/*if (conn != null) { 
				System.err.println("Connection to database " + url + "... Ok"); 
			}*/
			conn.setAutoCommit(false);
			return conn;
	   } 
	   catch(CommunicationsException ex) { 
		   throw ex;
	   }
	   catch(SQLException ex) { 
		   System.err.println(ex); 
	   }
	   return null;
	}
	
	public void closeConnection(Connection conn) {
		try {
			conn.close();
			//System.err.println("Closing connection to database...");
		}
		catch(SQLException ex) { 
	     	System.err.println(ex); 
	   	} 
	 }
	 
	 protected void executeCommit(Connection conn) {
	 	try {
		    	//System.err.println("Commiting...");
		    	Statement stmt = conn.createStatement();
				stmt.executeUpdate("COMMIT");
				stmt.close();
			}
			catch(SQLException ex) { 
	     	System.err.println(ex); 
	   	}
	 }
	 
	 protected void executeRollback(Connection conn) {
	 	try {
		    	System.err.println("Rolling back...");
		    	Statement stmt = conn.createStatement();
				stmt.executeUpdate("ROLLBACK");
				stmt.close();
			}
			catch(SQLException ex) { 
	     	System.err.println(ex); 
	   	}
	 }
	 
}
 
