package org.bnb.pluginhub.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bnb.pluginhub.pojos.SearchData;
import org.bnb.pluginhub.utils.ResultsetJsonConverter;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;

public class PluginHubCoreDAO {
	Logger logger = Logger.getLogger("PluginHubCoreDAO");
	private static Connection conn;
	ObjectMapper mapper = new ObjectMapper();
	private static PluginHubCoreDAO dao = null;
	private static final String fullSearchQuery = "SELECT * FROM PLUGINS WHERE pluginName LIKE ? OR pluginDesc LIKE ? OR fileName LIKE ? OR createdBy LIKE ? AND isLatest=1";
	private static final String fetchPlugin = "SELECT * FROM PLUGINS WHERE id = ? AND isLatest=1";
	private static final String insertQuery = "INSERT INTO PLUGINS (pluginName, pluginDesc, fileName, version, downloadCount, isLatest, createdBy, createdDate) VALUES(?,?,?,0,0,1,?,?)";
	private static final String decommission = "UPDATE PLUGINS SET isLatest=0 WHERE id=? AND isLatest=1";
	private static final String checkLogin = "SELECT * FROM USERS WHERE USERNAME = ? AND PASSWORD = ?";
	private PluginHubCoreDAO(){
		try {
			Class.forName("org.mariadb.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/athena", "root", "");
		} catch (SQLException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static PluginHubCoreDAO getInstance(){
		if(dao == null){
			dao = new PluginHubCoreDAO();
		}
		return dao;
	}
	
	public List<SearchData> search(String name, String desc, String fileName, String createdBy) throws SQLException, JsonParseException, JsonMappingException, JSONException, IOException{
		
		PreparedStatement stmt = conn.prepareStatement(fullSearchQuery);
		stmt.setString(1, name);
		stmt.setString(2, desc);
		stmt.setString(3, fileName);
		stmt.setString(4, createdBy);
		JSONArray array = ResultsetJsonConverter.convert(stmt.executeQuery());
		List<SearchData> list = new ArrayList<SearchData>(array.length());
		SearchData searchdata;
		for(int i = 0; i < array.length(); i++){
			searchdata = mapper.readValue(array.getJSONObject(i).toString(), SearchData.class);
			list.add(searchdata);
		}
		stmt.close();
		return list;
	}
	
	public SearchData fetch(int id) throws SQLException, JsonParseException, JsonMappingException, JSONException, IOException{
		PreparedStatement stmt = conn.prepareStatement(fetchPlugin);
		stmt.setInt(1, id);
		JSONArray array = ResultsetJsonConverter.convert(stmt.executeQuery());
		return mapper.readValue(array.getJSONObject(0).toString(), SearchData.class);
	}
	
	public void insert(int id, String pluginName, String pluginDesc, String fileName, String createdBy) throws SQLException{
		PreparedStatement stmt = conn.prepareStatement(insertQuery);
		stmt.setString(1, pluginName);
		stmt.setString(2, pluginDesc);
		stmt.setString(3, fileName);
		stmt.setString(4, createdBy);
		stmt.setDate(5, new Date(new java.util.Date().getTime()));
		stmt.execute();
	}
	
	public void update(int id, String description, String fileName, int version) throws SQLException, JsonParseException, JsonMappingException, JSONException, IOException{
		SearchData data = fetch(id);
		PreparedStatement stmt = conn.prepareStatement(decommission);
		stmt.setInt(1, id);
		stmt.execute();
		stmt.close();
		stmt = conn.prepareStatement(insertQuery);
		stmt.setString(1, data.getPluginName());
		stmt.setString(2, description);
		stmt.setString(3, fileName);
		stmt.setInt(4, data.getVersion() + 1);
		stmt.setString(5, data.getCreatedBy());
		stmt.setDate(6, new Date(new java.util.Date().getTime()));
		stmt.execute();
	}
	
	public boolean login(String userName, String password) throws SQLException{
		PreparedStatement stmt = conn.prepareStatement(checkLogin);
		stmt.setString(1, userName);
		stmt.setString(2, password);
		ResultSet rs = stmt.executeQuery();
		if(rs.next()){
			return true;
		}
		return false;
	}
}
