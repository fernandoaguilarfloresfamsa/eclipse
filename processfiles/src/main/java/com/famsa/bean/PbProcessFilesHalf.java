package com.famsa.bean;

public class PbProcessFilesHalf {

	private int id;
	private String xmlFileName;
	private String uuid;
	private String imageFileName;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getXmlFileName() {
		return xmlFileName;
	}
	public void setXmlFileName(String xmlFileName) {
		this.xmlFileName = xmlFileName;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getImageFileName() {
		return imageFileName;
	}
	public void setImageFileName(String imageFileName) {
		this.imageFileName = imageFileName;
	}
	@Override
	public String toString() {
		return "PbProcessFilesHalf [id=" + id + ", xmlFileName=" + xmlFileName + ", uuid=" + uuid + ", imageFileName="
				+ imageFileName + "]";
	}
	
}
