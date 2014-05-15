package uk.ac.man.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBMYSQL {
	public static ParaDB para = new ParaDB();
	public static Logger logger = Logger.getLogger("DBMYSQL Log");
		
	public static Connection connectMySQL(String schema){
		
		if(schema == null){
			schema = para.schema;
		}
		
		try {
			logger.setLevel(Level.INFO);
			
			logger.info("%t: Connecting to MySQL database " + schema
						+ " at " + para.user + "@" + para.host );

		//	Class.forName("com.mysql.jdbc.Driver");

			String connStr = "jdbc:mysql://" + para.host + ":" + para.port + "/" + para.schema 
					+ "?user=" + para.user
					+ "&password=" + para.pass + "&netTimeoutForStreamingResults=3600" + "&useUnicode=yes&characterEncoding=UTF-8";
			
			Connection conn = null;
			//DriverManager.getConnection(connStr);
			conn = DriverManager.getConnection(connStr);

			logger.info(" done, connected.\n");

			return conn;
		}

		catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
			System.exit(-1);
		}

		return null;
	}

	public static Connection connectMySQL(String schema, ParaDB paraDB){
		
		if(schema == null){
			schema = paraDB.schema;
		}
		
		try {
			logger.setLevel(Level.INFO);
			
			logger.info("%t: Connecting to MySQL database " + schema
						+ " at " + paraDB.user + "@" + paraDB.host );

		//	Class.forName("com.mysql.jdbc.Driver");

			String connStr = "jdbc:mysql://" + paraDB.host + ":" + paraDB.port + "/" + paraDB.schema 
					+ "?user=" + paraDB.user
					+ "&password=" + paraDB.pass + "&netTimeoutForStreamingResults=3600" + "&useUnicode=yes&characterEncoding=UTF-8";
			
			Connection conn = null;
			//DriverManager.getConnection(connStr);
			conn = DriverManager.getConnection(connStr);

			logger.info(" done, connected.\n");

			return conn;
		}

		catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
			System.exit(-1);
		}

		return null;
	}


	
	public static void executeSQL(Connection conn, String sql){
		if (conn == null){
			  conn = connectMySQL(null);
			  
			  if(conn == null){
				  System.err.println("Cannot establish connection to database");
				  return;
			  }
		  }
		
		
		Statement st = null;

        try {
            st = conn.createStatement();
            st.execute( sql );
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(DBMYSQL.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

        } catch (Exception e){
        	e.printStackTrace();
        }  finally {
            try {
                if (st != null) {
                    st.close();
                }
            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(DBMYSQL.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
	}
	
	public static void cleanTable(Connection conn, String tableName){
		Boolean flag = false;
		
		  if (conn == null){
			  conn = connectMySQL(null);
			  flag = true;
		  }
		  
	      Statement st = null;

	        try {
	            st = conn.createStatement();
	            st.execute("delete from " +tableName );
	        } catch (SQLException ex) {
	            Logger lgr = Logger.getLogger(DBMYSQL.class.getName());
	            lgr.log(Level.SEVERE, ex.getMessage(), ex);

	        } catch (Exception e){
	        	e.printStackTrace();
	        }  finally {
	            try {
	                if (st != null) {
	                    st.close();
	                }
	                
	                System.out.println("Table " + tableName + " cleaned");
	               // conn.close();
	            } catch (SQLException ex) {
	                Logger lgr = Logger.getLogger(DBMYSQL.class.getName());
	                lgr.log(Level.WARNING, ex.getMessage(), ex);
	            }
	        }
	}

	public static int getCntFromTable(Connection conn, String tableName, String whereCond){
		int cnt = 0;
		
		Statement st = null;
        ResultSet rs = null;
        String sql = null;
        
        if (whereCond == null || whereCond.equals(""))
        	sql = String.format("select count(*) from %s ", tableName);
        else
        	sql = String.format("select count(*) from %s where id_ext = '%s'", tableName, whereCond);
        

        try {
            st = conn.createStatement();
            rs = st.executeQuery(sql);

            if (rs.next()) {
                cnt = rs.getInt(1);
            }

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(DBMYSQL.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

        } catch (Exception e){
        	e.printStackTrace();
        }  finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }

            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(DBMYSQL.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
		
		return cnt;
	}
	
	public static void copyTableCond(Connection conn, String tableTo, String tableFrom, String whereCond ){
	      Statement st = null;

	        try {
	            st = conn.createStatement();
	            String sql = String.format("INSERT INTO %s (SELECT * FROM %s where %s);", tableTo, tableFrom, whereCond);
	            
	            st.execute(sql);
	        } catch (SQLException ex) {
	            Logger lgr = Logger.getLogger(DBMYSQL.class.getName());
	            lgr.log(Level.SEVERE, ex.getMessage(), ex);

	        } catch (Exception e){
	        	e.printStackTrace();
	        }  finally {
	            try {
	                if (st != null) {
	                    st.close();
	                }
	            } catch (SQLException ex) {
	                Logger lgr = Logger.getLogger(DBMYSQL.class.getName());
	                lgr.log(Level.WARNING, ex.getMessage(), ex);
	            }
	        }
	}
	
	public static void clearColumn(Connection conn, String tableName, String columnName, String value){
		
	      String updateSql = String.format("UPDATE %s SET %s=\'%s\'", tableName, columnName, value);
	      executeSQL(conn, updateSql);
	}
	
    public static void main(String[] args) {

    	Connection conn = DBMYSQL.connectMySQL(null);
    	
        Statement st = null;
        ResultSet rs = null;


        try {
            st = conn.createStatement();
            rs = st.executeQuery("SELECT count(*) from shared.data_events");

            if (rs.next()) {
                System.out.println(rs.getString(1));
            }

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(DBMYSQL.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

        } catch (Exception e){
        	e.printStackTrace();
        }  finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (conn != null) {
                    conn.close();
                }

            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(DBMYSQL.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }
}