package com.famsa.bean;

import java.util.Date;

public class PbProcessFilesHalf {

	private int id;
	private String xmlFileName;
	private String uuid;
	private String imageFileName;
	private String threadName;
	private int numeroPaginas;
	private Date fhTermino;
	private int errorInt;
	private String errorMsg;
	
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
	public String getThreadName() {
		return threadName;
	}
	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}
	public int getNumeroPaginas() {
		return numeroPaginas;
	}
	public void setNumeroPaginas(int numeroPaginas) {
		this.numeroPaginas = numeroPaginas;
	}
	public Date getFhTermino() {
		return fhTermino;
	}
	public void setFhTermino(Date fhTermino) {
		this.fhTermino = fhTermino;
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
		return "PbProcessFilesHalf [id=" + id + ", xmlFileName=" + xmlFileName + ", uuid=" + uuid + ", imageFileName="
				+ imageFileName + ", threadName=" + threadName + ", numeroPaginas=" + numeroPaginas + ", fhTermino="
				+ fhTermino + ", errorInt=" + errorInt + ", errorMsg=" + errorMsg + "]";
	}
	
}
