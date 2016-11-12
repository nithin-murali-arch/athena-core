package org.bnb.athena.restapis;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.bnb.pluginhub.dao.PluginHubCoreDAO;
import org.bnb.pluginhub.pojos.SearchData;
import org.bnb.pluginhub.pojos.User;
import org.json.JSONObject;

@Path("/pluginhub")
public class PluginHubCore {
	PluginHubCoreDAO dao;

	public PluginHubCore() {
		dao = PluginHubCoreDAO.getInstance();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/searchAll/{search}")
	public List<SearchData> searchPluginHub(@PathParam("search") String search) throws Exception {
		search = "%" + search + "%";
		return dao.search(search, search, search, search);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/fetch/{id}")
	public SearchData fetch(@PathParam("id") int id) throws Exception {
		return dao.fetch(id);
	}

	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/add")
	public String insert(SearchData data, @Context HttpServletRequest request) throws Exception {
		HttpSession session = request.getSession();
		String createdBy = (String) session.getAttribute("username");
		if (createdBy != null && !createdBy.equals(data.getCreatedBy())) {
			return "Don't try to mess with us. We have a smart architect ;)";
		}
		dao.insert(0, data.getPluginName(), data.getPluginDesc(), data.getFileName(), createdBy);
		return "Success!";
	}

	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/update")
	public String update(SearchData data, @Context HttpServletRequest request) throws Exception {
		HttpSession session = request.getSession();
		String createdBy = (String) session.getAttribute("username");
		if (createdBy != null && !createdBy.equals(data.getCreatedBy())) {
			return "Don't try to mess with us. We have a smart architect ;)";
		}
		dao.update(data.getId(), data.getPluginDesc(), data.getFileName(), data.getVersion());
		dao.insert(0, data.getPluginName(), data.getPluginDesc(), data.getFileName(), createdBy);
		return "Success!";
	}

	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("/download/{id}")
	public void getFile(@PathParam("id") int id, @Context HttpServletResponse response) throws Exception {
		SearchData data = dao.fetch(id);
		String fileLocation = System.getProperty("user.home") + "/" + data.getCreatedBy() + "/" + data.getFileName();
		InputStream is = new FileInputStream(fileLocation);
		IOUtils.copy(is, response.getOutputStream());
		response.setContentType("application/octet-stream");
		response.flushBuffer();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/login")
	public User login(User user, @Context HttpServletRequest request) throws SQLException {
		HttpSession session = request.getSession();
		String username = org.bnb.pluginhub.utils.StringUtils.escape(user.getUsername(), true);
		String password = org.bnb.pluginhub.utils.StringUtils.escape(user.getPassword(), true);
		Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]*$");
		user = new User();
		if (username != null && password != null && pattern.matcher(username).matches()
				&& pattern.matcher(password).matches()) {
			
			user.setLoggedin(dao.login(username, password));
			if(user.isLoggedin()){
				session.setAttribute("username", username);
			}
			user.setMessage(user.isLoggedin() ? "Logged in." : "Username/Password combination does not exist");
		} else {
			user.setLoggedin(false);
			user.setMessage("Username/Password combination does not exist");
		}
		return user;
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/register")
	public User register(User user) {
		// TODO
		return null;
	}

}
