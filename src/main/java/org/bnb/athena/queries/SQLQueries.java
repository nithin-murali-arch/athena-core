package org.bnb.athena.queries;

public interface SQLQueries {
	String loginQuery = "SELECT USERNAME FROM ADMINS WHERE USERNAME = '?' AND PASSWORD = '#'";
	String deleteQuery = "DELETE FROM JARS WHERE JARNAME = '?'";
	String insertQuery = "INSERT INTO JARS VALUES('?')";
	String listQuery = "SELECT * FROM JARS";
	String findJarQuery = "SELECT * FROM JARS WHERE JARNAME = '?'";
	String addAppParam = "INSERT INTO APPPARAMS VALUES ('?','#')";
	String listAppParam = "SELECT * FROM APPPARAMS";
	String removeAppParam = "DELETE FROM APPPARAMS WHERE KEYTEXT='?'";
	String findParam = "SELECT * FROM APPPARAMS WHERE KEYTEXT='?'";
}
