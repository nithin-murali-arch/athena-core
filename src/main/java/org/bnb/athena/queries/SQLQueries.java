package org.bnb.athena.queries;

public interface SQLQueries {
	String loginQuery = "SELECT USERNAME FROM ADMINS WHERE USERNAME = '?' AND PASSWORD = '#'";
	String deleteQuery = "DELETE FROM JARS WHERE JARNAME = ?";
	String insertQuery = "INSERT INTO JARS VALUES(?)";
	String listQuery = "SELECT * FROM JARS";
}
