package com.famsa.xmlmarshall;

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
    "lastAccessTime",
    "filePath",
    "filePathXML",
    "uuid",
    
	"filePathCopy",
	"pathCopy",
	"threadName",
	"extension",
	"numeroPaginas",
	"existeDesTif",
	"pathTif",
	"existeDesOtr",
	"pathOtro",
	"nomArch",
	"estado"
})
public class Archivo {

	@XmlElement(required = true)
	protected String lastAccessTime;
	@XmlElement(required = true)
	protected String filePath;
	@XmlElement(required = true)
	protected String filePathXML;
	@XmlElement(name = "uuid")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")	
	protected String uuid;
	
	@XmlElement(required = false)
	protected String filePathCopy;
	@XmlElement(required = false)
	protected String pathCopy;
	@XmlElement(required = false)
	protected String threadName;
	@XmlElement(required = false)
	protected String extension;
	@XmlElement(required = false)
	protected int numeroPaginas;
	@XmlElement(required = false)
	protected boolean existeDesTif;
	@XmlElement(required = false)
	protected String pathTif;
	@XmlElement(required = false)
	protected boolean existeDesOtr;
	@XmlElement(required = false)
	protected String pathOtro;
	@XmlElement(required = false)
	protected String nomArch;
	@XmlElement(required = false)
	protected EstadoEnum estado;
	
	public String getLastAccessTime() {
		return lastAccessTime;
	}
	public void setLastAccessTime(String lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getFilePathXML() {
		return filePathXML;
	}
	public void setFilePathXML(String filePathXML) {
		this.filePathXML = filePathXML;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getFilePathCopy() {
		return filePathCopy;
	}
	public void setFilePathCopy(String filePathCopy) {
		this.filePathCopy = filePathCopy;
	}
	public String getPathCopy() {
		return pathCopy;
	}
	public void setPathCopy(String pathCopy) {
		this.pathCopy = pathCopy;
	}
	public String getThreadName() {
		return threadName;
	}
	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	public int getNumeroPaginas() {
		return numeroPaginas;
	}
	public void setNumeroPaginas(int numeroPaginas) {
		this.numeroPaginas = numeroPaginas;
	}
	public boolean isExisteDesTif() {
		return existeDesTif;
	}
	public void setExisteDesTif(boolean existeDesTif) {
		this.existeDesTif = existeDesTif;
	}
	public String getPathTif() {
		return pathTif;
	}
	public void setPathTif(String pathTif) {
		this.pathTif = pathTif;
	}
	public boolean isExisteDesOtr() {
		return existeDesOtr;
	}
	public void setExisteDesOtr(boolean existeDesOtr) {
		this.existeDesOtr = existeDesOtr;
	}
	public String getPathOtro() {
		return pathOtro;
	}
	public void setPathOtro(String pathOtro) {
		this.pathOtro = pathOtro;
	}
	public String getNomArch() {
		return nomArch;
	}
	public void setNomArch(String nomArch) {
		this.nomArch = nomArch;
	}
	public EstadoEnum getEstado() {
		return estado;
	}
	public void setEstado(EstadoEnum estado) {
		this.estado = estado;
	}
	@Override
	public String toString() {
		return "Archivo [lastAccessTime=" + lastAccessTime + ", filePath=" + filePath + ", filePathXML=" + filePathXML
				+ ", uuid=" + uuid + ", filePathCopy=" + filePathCopy + ", pathCopy=" + pathCopy + ", threadName="
				+ threadName + ", extension=" + extension + ", numeroPaginas=" + numeroPaginas + ", existeDesTif="
				+ existeDesTif + ", pathTif=" + pathTif + ", existeDesOtr=" + existeDesOtr + ", pathOtro=" + pathOtro
				+ ", nomArch=" + nomArch + ", estado=" + estado + "]";
	}
	
}
