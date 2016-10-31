package org.bnb.athena.restapis;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
		// save it
		File f = new File(USER_HOME);
		if (!f.exists()) {
			f.mkdir();
		}
		writeToFile(uploadedInputStream, USER_HOME + "/" + fileDetail.getFileName());
		addJarContents(USER_HOME + "/" + fileDetail.getFileName());

		// URL url = new URL("file:\\" + USER_HOME + fileDetail.getFileName());
		// ClassLoader loader = URLClassLoader.newInstance(
		// new URL[] { url },
		// getClass().getClassLoader()
		// );
		// Class<?> clazz = Class.forName(className);
		// if(context.getAttribute("JarUrlMap") == null){
		// Map<String, String> jarUrlMap = new HashMap<String,String>();
		// jarUrlMap.put(urlPattern, fileDetail.getFileName() + ";;"
		// +className);
		// context.setAttribute("JarUrlMap", jarUrlMap);
		// }
		return Response.status(200).entity("Pass").build();

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

	private void addJarContents(String pathToJar) throws Exception {
		String destDir = UploadJar.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		java.util.jar.JarFile jar = new java.util.jar.JarFile(pathToJar);
		Enumeration<JarEntry> enumEntries = jar.entries();
		while (enumEntries.hasMoreElements()) {
			java.util.jar.JarEntry file = (java.util.jar.JarEntry) enumEntries.nextElement();
			java.io.File f = new java.io.File(destDir + java.io.File.separator + file.getName());
			if (file.isDirectory()) { // if its a directory, create it
				f.mkdir();
				continue;
			}
			java.io.InputStream is = jar.getInputStream(file); // get the input
																// stream
			java.io.FileOutputStream fos = new java.io.FileOutputStream(f);
			while (is.available() > 0) { // write contents of 'is' to 'fos'
				fos.write(is.read());
			}
			fos.close();
			is.close();
			jar.close();
		}
	}
}
