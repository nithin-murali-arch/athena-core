package org.bnb.pluginhub.pojos;

public class SearchData {
	private int id;
	private String pluginName;
	private String pluginDesc;
	private String fileName;
	private int version;
	private int downloadCount;
	private String createdBy;
	private int isLatest;
	private String createdDate;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPluginName() {
		return pluginName;
	}
	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public int getDownloadCount() {
		return downloadCount;
	}
	public void setDownloadCount(int downloadCount) {
		this.downloadCount = downloadCount;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public int getIsLatest() {
		return isLatest;
	}
	public void setIsLatest(int isLatest) {
		this.isLatest = isLatest;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getPluginDesc() {
		return pluginDesc;
	}
	public void setPluginDesc(String pluginDesc) {
		this.pluginDesc = pluginDesc;
	}
	
}
