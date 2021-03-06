package com.famsa.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.famsa.enums.EstadoEnum;

@XmlRootElement(name="archivo")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "archivo", propOrder = {
    "creationTime",
    "filePath",
    "uuid",
    
	"estado"
})
public class Archivo {

	@XmlElement(required = true)
	protected String creationTime;
	@XmlElement(required = true)
	protected String filePath;
	@XmlElement(name = "uuid")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")	
	protected String uuid;
	
	@XmlElement(required = false)
	protected EstadoEnum estado;
	
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
	public EstadoEnum getEstado() {
		return estado;
	}
	public void setEstado(EstadoEnum estado) {
		this.estado = estado;
	}
	@Override
	public String toString() {
		return "Archivo [creationTime=" + creationTime + ", filePath=" + filePath + ", uuid=" + uuid + ", estado="
				+ estado + "]";
	}
	
}
