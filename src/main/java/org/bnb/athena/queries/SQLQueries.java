package org.bnb.athena.queries;

public interface SQLQueries {
	String loginQuery = "SELECT USERNAME FROM ADMINS WHERE USERNAME = '?' AND PASSWORD = '#'";
}
