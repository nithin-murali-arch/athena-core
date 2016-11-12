package org.bnb.athena.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bnb.athena.utils.ResultsetJsonConverter;
import org.h2.jdbcx.JdbcDataSource;
import org.json.JSONArray;

public class JDBCHandler {
	private static final String sqliteHome = "jdbc:mariadb://localhost:3306/acore";
	//private static final String sqliteHome = "jdbc:sqlite:C:/work/product.db";
	Logger logger = Logger.getLogger("JDBCHandler");
	private static JDBCHandler handler = null;
	private static Connection conn;
	private JDBCHandler() {
		
		System.out.println(sqliteHome);
		try {
			Class.forName("org.mariadb.jdbc.Driver");
			conn = DriverManager.getConnection(sqliteHome, "root", "");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static JDBCHandler getInstance(){
		if(handler == null){
			handler = new JDBCHandler();
		}
		try {
			Class.forName("org.mariadb.jdbc.Driver");
			conn = DriverManager.getConnection(sqliteHome, "root", "");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return handler;
	}
	
	public void execute(String query) throws SQLException{
		Statement stmt = conn.createStatement();
		stmt.execute(query);
		stmt.close();
		conn.close();
	}
	
	public JSONArray executeQuery(String query) throws SQLException{
		Statement stmt = conn.createStatement();
		JSONArray array = ResultsetJsonConverter.convert(stmt.executeQuery(query));
		stmt.close();
		conn.close();
		return array;
	}
}
