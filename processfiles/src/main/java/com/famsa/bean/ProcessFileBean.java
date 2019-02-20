package com.famsa.bean;

public class ProcessFileBean {

	private String xmlFileName;
	private String creationTime;
	private String filePath;
	private String uuid;
	private String path;
	private String file;
	private String extension;
	private String hash;
	private int errorInt;
	private String errorMsg;
	
	public ProcessFileBean() {
		super();
	}

	public String getXmlFileName() {
		return xmlFileName;
	}
	public void setXmlFileName(String xmlFileName) {
		this.xmlFileName = xmlFileName;
	}
	public String getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	public int getErrorInt() {
		return errorInt;
	}
	public void setErrorInt(int errorInt) {
		this.errorInt = errorInt;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	@Override
	public String toString() {
		return "ProcessFileBean [xmlFileName=" + xmlFileName + ", creationTime=" + creationTime + ", filePath="
				+ filePath + ", uuid=" + uuid + ", path=" + path + ", file=" + file + ", extension=" + extension
				+ ", hash=" + hash + ", errorInt=" + errorInt + ", errorMsg=" + errorMsg + "]";
	}
	
}
