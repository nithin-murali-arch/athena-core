package org.bnb.athena.restapis;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.bnb.athena.jdbc.JDBCHandler;
import org.bnb.athena.queries.SQLQueries;

@Path("/moduleHandler")
public class RemoveJar {
	final static String USER_HOME = System.getProperty("user.home") + "/libs/";
	final static String CLASSES_FOLDER = RemoveJar.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	int fileCount = 0;
	
	@GET
	@Path("/listAll")
	public String moduleNames() throws SQLException{
		return JDBCHandler.getInstance().executeQuery(SQLQueries.listQuery).toString();
	}
	
	@GET
	@Path("/listApps")
	public String moduleApps() throws SQLException{
		return JDBCHandler.getInstance().executeQuery(SQLQueries.listAppParam).toString();
	}
	
	@GET
	@Path("/remove/{jar}")
	public String remove(@PathParam("jar") String jar) throws IOException, SQLException {
		File f = new File(USER_HOME + jar);
		File[] files = f.listFiles();
		deleteFilesRecursively(files, jar);
		System.out.println(fileCount + "files deleted");
		JDBCHandler.getInstance().execute(SQLQueries.deleteQuery.replace("?", jar));
		return JDBCHandler.getInstance().executeQuery(SQLQueries.listQuery).toString();
	}
	
	@GET
	@Path("/addParam/{key}/{value}")
	public String addParam(@PathParam("key") String key, @PathParam("value") String value) throws SQLException{
		JDBCHandler.getInstance().execute(SQLQueries.addAppParam.replace("?", key).replace("#", value));
		return JDBCHandler.getInstance().executeQuery(SQLQueries.listAppParam).toString();
	}
	
	@GET
	@Path("/removeParam/{key}")
	public String removeParam(@PathParam("key") String key) throws SQLException{
		JDBCHandler.getInstance().execute(SQLQueries.removeAppParam.replace("?", key));
		return JDBCHandler.getInstance().executeQuery(SQLQueries.listAppParam).toString();
	}	

	private void deleteFilesRecursively(File[] files, String jar){
		for(File f: files){
			String path = f.getAbsolutePath();
			if(path.contains("META-INF") || path.contains(".jar") || path.contains("sun") || path.contains("javax") || path.contains("jersey") || path.contains("h2") || path.contains("mysql")){
				continue;
			}
			if(f.isDirectory()){
				deleteFilesRecursively(f.listFiles(), jar);
			}
			else{
				path = path.substring(path.indexOf(jar), path.length());
				path = path.replaceAll("\\\\", "/");
				System.out.println(path);
				path = path.substring(path.indexOf("/"), path.length());
				path = CLASSES_FOLDER + path;
				f.delete();
				fileCount++;
			}
		}
	}
}
