package org.bnb.athena.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bnb.athena.utils.ResultsetJsonConverter;
import org.h2.jdbcx.JdbcDataSource;
import org.json.JSONArray;

public class JDBCHandler {
	private static final String sqliteHome = "jdbc:h2:~/athenaApp";
	//private static final String sqliteHome = "jdbc:sqlite:C:/work/product.db";
	Logger logger = Logger.getLogger("JDBCHandler");
	private static JDBCHandler handler = null;
	private Connection conn;
	private JDBCHandler() {
		try {
			System.out.println(sqliteHome);
			JdbcDataSource ds = new JdbcDataSource();
			ds.setURL(sqliteHome);
			conn = ds.getConnection();
			runInitScripts();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	public static JDBCHandler getInstance(){
		if(handler == null){
			handler = new JDBCHandler();
		}
		return handler;
	}
	
	public void runInitScripts() throws SQLException{
		Statement stmt = conn.createStatement();
		stmt.execute("CREATE TABLE IF NOT EXISTS JARS(JARNAME VARCHAR2(100) PRIMARY KEY)");
		stmt.execute("CREATE TABLE IF NOT EXISTS ADMINS(USERNAME VARCHAR2(100) PRIMARY KEY, PASSWORD TEXT)");
		stmt.execute("INSERT INTO ADMINS (USERNAME, PASSWORD) SELECT 'admin', 'admin' WHERE NOT EXISTS (SELECT * From ADMINS WHERE USERNAME = 'admin')");
		stmt.execute("CREATE TABLE IF NOT EXISTS APPPARAMS(KEY VARCHAR2(50) PRIMARY KEY, VALUE VARCHAR2(50)");
	}
	
	public void execute(String query) throws SQLException{
		Statement stmt = conn.createStatement();
		stmt.execute(query);
		stmt.close();
	}
	
	public JSONArray executeQuery(String query) throws SQLException{
		Statement stmt = conn.createStatement();
		JSONArray array = ResultsetJsonConverter.convert(stmt.executeQuery(query));
		stmt.close();
		return array;
	}
}
