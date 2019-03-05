package com.famsa.bean;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="archivo")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "archivo", propOrder = {
    "id",
    "xmlArchivo",
	"imageFileName",
	"threadName",
	"numeroPaginas",
	"fhTermino",
	"path",
	"extension",
	"hash",
	"errorInt",
	"errorMsg",
    "creationTime",
    "filePath",
    "uuid"
})
public class Archivo {

	@XmlElement(required = false)
	protected int id;
	@XmlElement(required = false)
	protected String xmlArchivo;
	@XmlElement(required = false)
	protected String imageFileName;
	@XmlElement(required = false)
	protected String threadName;
	@XmlElement(required = false)
	protected int numeroPaginas;
	@XmlElement(required = false)
	protected Date fhTermino;
	@XmlElement(required = false)
	protected String path;
	@XmlElement(required = false)
	protected String extension;
	@XmlElement(required = false)
	protected String hash;
	@XmlElement(required = false)
	protected int errorInt;
	@XmlElement(required = false)
	protected String errorMsg;
	
	
	@XmlElement(required = true)
	protected String creationTime;
	@XmlElement(required = true)
	protected String filePath;
	@XmlElement(name = "uuid")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")	
	protected String uuid;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getXmlArchivo() {
		return xmlArchivo;
	}
	public void setXmlArchivo(String xmlArchivo) {
		this.xmlArchivo = xmlArchivo;
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
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
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
	@Override
	public String toString() {
		return "Archivo [id=" + id + ", xmlArchivo=" + xmlArchivo + ", imageFileName=" + imageFileName + ", threadName="
				+ threadName + ", numeroPaginas=" + numeroPaginas + ", fhTermino=" + fhTermino + ", path=" + path
				+ ", extension=" + extension + ", hash=" + hash + ", errorInt=" + errorInt + ", errorMsg=" + errorMsg
				+ ", creationTime=" + creationTime + ", filePath=" + filePath + ", uuid=" + uuid + "]";
	}
	
}
