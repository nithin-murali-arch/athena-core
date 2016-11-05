package org.bnb.athena.restapis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bnb.athena.jdbc.JDBCHandler;
import org.bnb.athena.queries.SQLQueries;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("/upload")
public class UploadJar {
	final static String USER_HOME = System.getProperty("user.home") + "\\libs\\".replaceAll("/", "\\");

	@Path("/jar")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) throws Exception {
		File f = new File(USER_HOME);
		if (!f.exists()) {
			f.mkdir();
		}
		writeToFile(uploadedInputStream, USER_HOME + "/" + fileDetail.getFileName(), fileDetail.getFileName());
		addJarContents(USER_HOME + "/" + fileDetail.getFileName());
		return Response.status(200).entity("Pass").build();
	}

	private void writeToFile(InputStream uploadedInputStream, String uploadedFileLocation, String fileName) throws SQLException {
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
			JDBCHandler.getInstance().execute(SQLQueries.insertQuery.replace("?", fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addJarContents(String pathToJar) throws Exception {
		Runtime.getRuntime().exec("jar xf " + pathToJar);
		String destDir = USER_HOME + File.pathSeparator + "classes";
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
