package org.bnb.athena.restapis;

import java.io.File;
import java.io.FileInputStream;
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
		Runtime.getRuntime().exec("jar xf " + pathToJar);
		String destDir = USER_HOME + File.pathSeparator + "classes";
//		File dest = new File(destDir);
//		if(!dest.exists()){
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
	public void copyFolder(File src, File dest)
	    	throws IOException{
	    	if(src.isDirectory()){

	    		//if directory not exists, create it
	    		if(!dest.exists()){
	    		   dest.mkdir();
	    		   System.out.println("Directory copied from "
	                              + src + "  to " + dest);
	    		}

	    		//list all the directory contents
	    		String files[] = src.list();

	    		for (String file : files) {
	    		   //construct the src and dest file structure
	    		   File srcFile = new File(src, file);
	    		   File destFile = new File(dest, file);
	    		   //recursive copy
	    		   copyFolder(srcFile,destFile);
	    		}

	    	}else{
	    		//if file, then copy it
	    		//Use bytes stream to support all file types
	    		InputStream in = new FileInputStream(src);
	    	        OutputStream out = new FileOutputStream(dest);

	    	        byte[] buffer = new byte[1024];

	    	        int length;
	    	        //copy the file content in bytes
	    	        while ((length = in.read(buffer)) > 0){
	    	    	   out.write(buffer, 0, length);
	    	        }

	    	        in.close();
	    	        out.close();
	    	        System.out.println("File copied from " + src + " to " + dest);
	    	}
	    }
}
