package org.bnb.athena.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import javax.swing.plaf.synth.SynthSeparatorUI;
import javax.ws.rs.core.Response;

import org.apache.catalina.Server;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardEngine;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.bnb.athena.jdbc.JDBCHandler;
import org.bnb.athena.queries.SQLQueries;
import org.bnb.athena.utils.ClassFinder;
import org.h2.jdbcx.JdbcDataSource;
import org.json.JSONArray;
import org.json.JSONObject;
import org.reflections.Reflections;

import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.api.model.AbstractSubResourceMethod;
import com.sun.jersey.server.impl.modelapi.annotation.IntrospectionModeller;

/**
 * Servlet implementation class UploadJar
 */
@MultipartConfig
@WebServlet("/UploadJar")
public class UploadJar extends HttpServlet {
	Logger logger = Logger.getLogger("UploadJar");
	final static String pathSep = "/";
	final static String USER_HOME = System.getProperty("user.home") + "\\libs\\".replaceAll("/", "\\");
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UploadJar() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = (HttpSession) request.getSession();
		PrintWriter writer = response.getWriter();
		JSONObject json = new JSONObject();
		if (session.getAttribute("userName") == null) {
			json.put("error", "Error: You must log in first!");
			writer.println(json.toString());
			writer.flush();
			return;
		}
		if (!ServletFileUpload.isMultipartContent(request)) {
			json.put("error", "Error: Request must be in multipart/form-data encoding.");
			writer.println(json.toString());
			writer.flush();
			return;
		}
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(1024 * 1024 * 300); // 300MB
		factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setFileSizeMax(1024 * 1024 * 300); // 300MB
		upload.setSizeMax(1024 * 1024 * 300); // 300MB
		String fileName = "";
		String uploadPath = USER_HOME;
		File uploadDir = new File(uploadPath);
		if (!uploadDir.exists()) {
			uploadDir.mkdir();
		}
		try {
			List<FileItem> formItems = upload.parseRequest(request);
			if (formItems != null && formItems.size() > 0) {
				for (FileItem item : formItems) {
					if (!item.isFormField()) {
						fileName = new File(item.getName()).getName();
						String filePath = uploadPath + File.separator + fileName;
						File storeFile = new File(filePath);
						item.write(storeFile);
						request.setAttribute("message", "Upload has been done successfully!");
					}
				}
			}
		} catch (Exception ex) {
			request.setAttribute("message", "There was an error: " + ex.getMessage());
		}
		// *****************************UPLOAD
		// COMPLETE***********************************//
		File f = new File(USER_HOME);
		if (!f.exists()) {
			f.mkdir();
		}
		try {
			File jar = new File(USER_HOME + pathSep + fileName);
			String moduleName = fileName.substring(0, fileName.lastIndexOf("."));
			File folder = new File(USER_HOME + pathSep + moduleName);
			if (JDBCHandler.getInstance().executeQuery(SQLQueries.findJarQuery.replace("?", moduleName))
					.length() != 0) {
				json.put("error", "Module Already exists! If a new version, remove existing version.");
				writer.println(json);
				return;
			}
			if(!folder.exists()){
				folder.mkdir();
			}
			jar.renameTo(new File(
					USER_HOME + pathSep + fileName.substring(0, fileName.lastIndexOf(".")) + pathSep + fileName));
			addJarContents(USER_HOME + pathSep + fileName.substring(0, fileName.lastIndexOf(".")), fileName);
			JDBCHandler.getInstance().execute(SQLQueries.insertQuery.replace("?", moduleName));
		} catch (Exception e) {
			json.put("error", "JARUPLOAD ERROR");
			System.out.println("JARUPLOAD ERROR");
			e.printStackTrace();
		}
		try {
			JSONArray appParams = JDBCHandler.getInstance().executeQuery(SQLQueries.listAppParam);
			JSONArray jars = JDBCHandler.getInstance().executeQuery(SQLQueries.listQuery);
			json.put("appParams", appParams.toString());
			json.put("jars", jars.toString());
			System.out.println(appParams);
			System.out.println(jars);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
		writer.println(json.toString());
		try {
			sendToRegistry();
			MBeanServer mBeanServer = MBeanServerFactory.findMBeanServer(null).get(0);
		    ObjectName name = new ObjectName("Catalina", "type", "Server");
		    Server server = (Server) mBeanServer.getAttribute(name, "managedResource");
		    StandardEngine engine = (StandardEngine) server.findService("Catalina").getContainer();
		    StandardContext context = (StandardContext) engine.findChild(engine.getDefaultHost()).findChild(getServletContext().getContextPath());
		    context.reload();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO org.apache.catalina.core.StandardContext
		// getServletContext().getRequestDispatcher("/index.jsp").forward(request,
		// response);

	}

	private void sendToRegistry() throws Exception {
		JSONArray registryArray = JDBCHandler.getInstance()
				.executeQuery(SQLQueries.findParam.replace("?", "apiGatewayURL"));
		if (registryArray.length() == 0) {
			return;
		}
		JSONObject registryUrl = registryArray.getJSONObject(0);
		String url = registryUrl.getString("KEYTEXT");
		List<Class<?>> classes = ClassFinder.find("org.bnb.athena.restapis");
		JSONArray array = new JSONArray();
		for (Class tempClass : classes) {
			AbstractResource resource = IntrospectionModeller.createResource(tempClass);
			System.out.println("Path is " + resource.getPath().getValue());
			String uriPrefix = resource.getPath().getValue();
			for (AbstractSubResourceMethod srm : resource.getSubResourceMethods()) {
				JSONObject object = new JSONObject();
				String uri = uriPrefix + srm.getPath().getValue();
				object.put("httpMethod", srm.getHttpMethod());
				object.put("uri", uri);
				array.put(object);
			}
		}
		URL gatewayUrl = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) gatewayUrl.openConnection();
		conn.setRequestProperty("discoveryContent", array.toString());
		conn.connect();
	}

	private void addJarContents(String pathToJar, String jar) throws Exception {
		System.out.println("jar xf " + pathToJar);
		ThreadRunner runner = new ThreadRunner(pathToJar, jar);
		runner.start();
		runner.join();
		String destDir = USER_HOME + pathToJar.substring(pathToJar.lastIndexOf(pathSep), pathToJar.length());
		String destFolder = UploadJar.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		copyFolder(new File(destDir), new File(destFolder));
	}

	public void copyFolder(File src, File dest) throws IOException {
		if (src.isDirectory()) {
			if (!dest.exists()) {
				dest.mkdir();
				// System.out.println("Directory copied from " + src + " to " +
				// dest);
			}
			String files[] = src.list();
			for (String file : files) {
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				copyFolder(srcFile, destFile);
				
			}
		} else {
			System.out.println(src + " copied to " + dest);
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
			in.close();
			out.close();
			// System.out.println("File copied from " + src + " to " + dest);
		}
	}
}

class ThreadRunner extends Thread{
	String initPath;
	String jarName;
	
	ThreadRunner(String initPath, String jarName){
		this.initPath = initPath;
		this.jarName = jarName;
	}
	
	public void run(){
		try {
			Runtime.getRuntime().exec("jar xf " + jarName, null, new File(initPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
