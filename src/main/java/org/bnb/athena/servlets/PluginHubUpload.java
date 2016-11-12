package org.bnb.athena.servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.bnb.pluginhub.dao.PluginHubCoreDAO;
import org.bnb.pluginhub.pojos.SearchData;
import org.json.JSONObject;

@MultipartConfig
@WebServlet("/pluginHubUpload")
public class PluginHubUpload extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter writer = response.getWriter();
		HttpSession session = request.getSession();
		String createdBy = (String) session.getAttribute("username");

		JSONObject json = new JSONObject();
		if (session.getAttribute("username") == null) {
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
		String uploadPath = System.getProperty("user.home") + "/" + createdBy + "/";
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
			PluginHubCoreDAO dao = PluginHubCoreDAO.getInstance();
			SearchData data = dao.search(fileName, fileName, fileName, fileName).get(0);
			writer.print(new JSONObject().put("id", data.getId()));
		} catch (Exception ex) {
			request.setAttribute("message", "There was an error: " + ex.getMessage());
		}
		// *****************************UPLOAD COMPLETE***********************************//

		
	}
}