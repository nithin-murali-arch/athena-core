package org.bnb.athena.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import javax.ws.rs.core.Response;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;

/**
 * Servlet implementation class UploadJar
 */
@MultipartConfig
@WebServlet("/UploadJar")
public class UploadJar extends HttpServlet {
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
		if(session.getAttribute("userName") == null){
			writer.println("Error: You must log in first!");
		}
		if (!ServletFileUpload.isMultipartContent(request)) {
            writer.println("Error: Form must have enctype=multipart/form-data.");
            writer.flush();
            return;
        }
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(1024 * 1024 * 300); //300MB
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
        ServletFileUpload upload = new ServletFileUpload(factory);
        // sets maximum size of upload file
        upload.setFileSizeMax(1024 * 1024 * 300); //300MB
        upload.setSizeMax(1024 * 1024 * 300); //300MB
		InputStream uploadedInputStream = null;
		String fileName = "";
		String uploadPath = USER_HOME;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
        try {
            // parses the request's content to extract file data
            @SuppressWarnings("unchecked")
            List<FileItem> formItems = upload.parseRequest(request);
 
            if (formItems != null && formItems.size() > 0) {
                // iterates over form's fields
                for (FileItem item : formItems) {
                    // processes only fields that are not form fields
                    if (!item.isFormField()) {
                        fileName = new File(item.getName()).getName();
                        String filePath = uploadPath + File.separator + fileName;
                        File storeFile = new File(filePath);
 
                        // saves the file on disk
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
		//****************************************************************//
		File f = new File(USER_HOME);
		if (!f.exists()) {
			f.mkdir();
		}
//		writeToFile(uploadedInputStream, USER_HOME + "/" + fileName);
		try {
			File jar = new File(USER_HOME + pathSep + fileName);
			File folder = new File(USER_HOME + pathSep + fileName.substring(0, fileName.lastIndexOf(".")));
			folder.mkdir();
			jar.renameTo(new File(USER_HOME + pathSep + fileName.substring(0, fileName.lastIndexOf(".")) + pathSep + fileName));
			addJarContents(USER_HOME + pathSep + fileName.substring(0, fileName.lastIndexOf(".")), fileName);
		} catch (Exception e) {
			System.out.println("JARUPLOAD ERROR");
			e.printStackTrace();
		}
		getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);

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
//		File dest = new File(destDir);
//		if (!dest.exists()) {
//			dest.mkdir();
//		}
//		java.util.jar.JarFile jar = new java.util.jar.JarFile(pathToJar);
//		Enumeration<JarEntry> enumEntries = jar.entries();
//		while (enumEntries.hasMoreElements()) {
//			java.util.jar.JarEntry file = (java.util.jar.JarEntry) enumEntries.nextElement();
//			java.io.File f = new java.io.File(destDir + java.io.File.separator + file.getName());
//			if (file.isDirectory()) { // if its a directory, create it
//				f.mkdir();
//				continue;
//			}
//			java.io.InputStream is = jar.getInputStream(file); // get the input
//																// stream
//			java.io.FileOutputStream fos = new java.io.FileOutputStream(f);
//			while (is.available() > 0) { // write contents of 'is' to 'fos'
//				fos.write(is.read());
//			}
//			fos.close();
//			is.close();
//			jar.close();
//		}
		String destFolder = UploadJar.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		copyFolder(new File(destDir), new File(destFolder));
		//dest.delete();
	}

	public void copyFolder(File src, File dest) throws IOException {
		if (src.isDirectory()) {

			// if directory not exists, create it
			if (!dest.exists()) {
				dest.mkdir();
				System.out.println("Directory copied from " + src + "  to " + dest);
			}

			// list all the directory contents
			String files[] = src.list();

			for (String file : files) {
				// construct the src and dest file structure
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				// recursive copy
				copyFolder(srcFile, destFile);
			}

		} else {
			// if file, then copy it
			// Use bytes stream to support all file types
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);

			byte[] buffer = new byte[1024];

			int length;
			// copy the file content in bytes
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}

			in.close();
			out.close();
			System.out.println("File copied from " + src + " to " + dest);
		}
	}
}
