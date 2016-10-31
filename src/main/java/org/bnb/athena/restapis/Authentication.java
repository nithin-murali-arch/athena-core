package org.bnb.athena.restapis;

import java.sql.SQLException;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.bnb.athena.jdbc.JDBCHandler;
import org.bnb.athena.pojos.User;
import org.bnb.athena.queries.SQLQueries;
import org.bnb.athena.utils.StringUtils;
import org.json.JSONObject;

@Path("/authenticate")
public class Authentication {
	
	@Path("/login")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public User login(User user, @Context HttpServletRequest request) throws SQLException{
		String username = StringUtils.escape(user.getUsername(), true);
		String password = StringUtils.escape(user.getPassword(), true);
		Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]*$");
		user = new User();
		if(username != null && password != null && pattern.matcher(username).matches() && pattern.matcher(password).matches()){
			user.setLoggedin(true);
			user.setMessage("Logged in.");
			String query = SQLQueries.loginQuery.replace("?", username).replace("#", password);
			JSONObject json = JDBCHandler.getInstance().executeQuery(query).getJSONObject(0);
			String userNameCopy = json.getString("USERNAME");
			if(userNameCopy != null && userNameCopy.equals(username)){
			HttpSession session = request.getSession();
			session.setAttribute("userName", username);
			user.setLoggedin(true);
			user.setMessage("Logged in.");
			}
			else{
				user.setLoggedin(false);
				user.setMessage("Username/Password combination does not exist");
			}
		}
		else{
			user.setLoggedin(false);
			user.setMessage("Username/Password can contain alphanumerics only.");
		}
		return user;
	}
}
