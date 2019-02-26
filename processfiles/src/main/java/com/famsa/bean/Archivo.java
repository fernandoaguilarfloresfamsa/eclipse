package com.famsa.bean;

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
    "creationTime",
    "filePath",
    "xmlArchivo",
    "uuid"
})
public class Archivo {

	@XmlElement(required = true)
	protected String creationTime;
	@XmlElement(required = true)
	protected String filePath;
	protected String xmlArchivo;
	@XmlElement(name = "uuid")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")	
	protected String uuid;
	
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
	public String getXmlArchivo() {
		return xmlArchivo;
	}
	public void setXmlArchivo(String xmlArchivo) {
		this.xmlArchivo = xmlArchivo;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	@Override
	public String toString() {
		return "Archivo [creationTime=" + creationTime + ", filePath=" + filePath + ", xmlArchivo=" + xmlArchivo
				+ ", uuid=" + uuid + "]";
	}
	
}
