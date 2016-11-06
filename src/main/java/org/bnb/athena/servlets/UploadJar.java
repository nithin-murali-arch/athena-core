package org.bnb.athena.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.logging.Level;
import java.util.logging.Logger;

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

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.bnb.athena.jdbc.JDBCHandler;
import org.bnb.athena.queries.SQLQueries;
import org.h2.jdbcx.JdbcDataSource;
import org.json.JSONArray;
import org.json.JSONObject;

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
		if(session.getAttribute("userName") == null){
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
        factory.setSizeThreshold(1024 * 1024 * 300); //300MB
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setFileSizeMax(1024 * 1024 * 300); //300MB
        upload.setSizeMax(1024 * 1024 * 300); //300MB
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
                        request.setAttribute("message",
                            "Upload has been done successfully!");
                    }
                }
            }
        } catch (Exception ex) {
            request.setAttribute("message",
                    "There was an error: " + ex.getMessage());
        }
		//*****************************UPLOAD COMPLETE***********************************//
		File f = new File(USER_HOME);
		if (!f.exists()) {
			f.mkdir();
		}
		try {
			File jar = new File(USER_HOME + pathSep + fileName);
			String moduleName = fileName.substring(0, fileName.lastIndexOf("."));
			File folder = new File(USER_HOME + pathSep + moduleName);
			if(JDBCHandler.getInstance().executeQuery(SQLQueries.findJarQuery.replace("?", moduleName)).getJSONObject(0).length() != 0){
				json.put("error", "Module Already exists! If a new version, remove existing version.");
				writer.println(json);
				return;
			}
			folder.mkdir();
			jar.renameTo(new File(USER_HOME + pathSep + fileName.substring(0, fileName.lastIndexOf(".")) + pathSep + fileName));
			addJarContents(USER_HOME + pathSep + fileName.substring(0, fileName.lastIndexOf(".")), fileName);
			JDBCHandler.getInstance().executeQuery(SQLQueries.insertQuery.replace("?", moduleName));
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
		//getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);

	}

	private void writeToFile(InputStream uploadedInputStream, String uploadedFileLocation) {
		try {
			OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];
			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addJarContents(String pathToJar, String jar) throws Exception {
		System.out.println("jar xf " + pathToJar);
		Runtime.getRuntime().exec("jar xf " + jar, null, new File(pathToJar));
		String destDir = USER_HOME + pathToJar.substring(pathToJar.lastIndexOf(pathSep), pathToJar.length());
		String destFolder = UploadJar.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		copyFolder(new File(destDir), new File(destFolder));
	}

	public void copyFolder(File src, File dest) throws IOException {
		if (src.isDirectory()) {
			if (!dest.exists()) {
				dest.mkdir();
				System.out.println("Directory copied from " + src + "  to " + dest);
			}
			String files[] = src.list();
			for (String file : files) {
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				copyFolder(srcFile, destFile);
			}
		} else {
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
			in.close();
			out.close();
			System.out.println("File copied from " + src + " to " + dest);
		}
	}
}
