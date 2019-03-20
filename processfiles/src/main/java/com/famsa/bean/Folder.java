package com.famsa.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="folder")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "folder", propOrder = {
    "entrada",
    "encontrados",
    "temporal",
    "imagenTif",
    "imagenDifTif",
    "imagenFallo",
    "jasperReports",
    "jasperReportsJPG",
    "sourceTIF",
    "temporalTIF",
    "autorizacion",
    "batch",
    "preautorizacion"
})
public class Folder {

	@XmlElement(required = true)
	protected String entrada;
	@XmlElement(required = true)
	protected String encontrados;
	@XmlElement(required = true)
	protected String temporal;
	@XmlElement(required = true)
	protected String imagenTif;
	@XmlElement(required = true)
	protected String imagenDifTif;
	@XmlElement(required = true)
	protected String imagenFallo;
	@XmlElement(required = true)
	protected String jasperReports;
	@XmlElement(required = true)
	protected String jasperReportsJPG;
	@XmlElement(required = true)
	protected String sourceTIF;
	@XmlElement(required = true)
	protected String temporalTIF;
	@XmlElement(required = true)
	protected String autorizacion;
	@XmlElement(required = true)
	protected String batch;
	@XmlElement(name = "preautorizacion")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")	
	protected String preautorizacion;
	
	public String getEntrada() {
		return entrada;
	}
	public void setEntrada(String entrada) {
		this.entrada = entrada;
	}
	public String getEncontrados() {
		return encontrados;
	}
	public void setEncontrados(String encontrados) {
		this.encontrados = encontrados;
	}
	public String getTemporal() {
		return temporal;
	}
	public void setTemporal(String temporal) {
		this.temporal = temporal;
	}
	public String getImagenTif() {
		return imagenTif;
	}
	public void setImagenTif(String imagenTif) {
		this.imagenTif = imagenTif;
	}
	public String getImagenDifTif() {
		return imagenDifTif;
	}
	public void setImagenDifTif(String imagenDifTif) {
		this.imagenDifTif = imagenDifTif;
	}
	public String getImagenFallo() {
		return imagenFallo;
	}
	public void setImagenFallo(String imagenFallo) {
		this.imagenFallo = imagenFallo;
	}
	public String getJasperReports() {
		return jasperReports;
	}
	public void setJasperReports(String jasperReports) {
		this.jasperReports = jasperReports;
	}
	public String getJasperReportsJPG() {
		return jasperReportsJPG;
	}
	public void setJasperReportsJPG(String jasperReportsJPG) {
		this.jasperReportsJPG = jasperReportsJPG;
	}
	public String getSourceTIF() {
		return sourceTIF;
	}
	public void setSourceTIF(String sourceTIF) {
		this.sourceTIF = sourceTIF;
	}
	public String getTemporalTIF() {
		return temporalTIF;
	}
	public void setTemporalTIF(String temporalTIF) {
		this.temporalTIF = temporalTIF;
	}
	public String getAutorizacion() {
		return autorizacion;
	}
	public void setAutorizacion(String autorizacion) {
		this.autorizacion = autorizacion;
	}
	public String getBatch() {
		return batch;
	}
	public void setBatch(String batch) {
		this.batch = batch;
	}
	public String getPreautorizacion() {
		return preautorizacion;
	}
	public void setPreautorizacion(String preautorizacion) {
		this.preautorizacion = preautorizacion;
	}
	@Override
	public String toString() {
		return "Folder [entrada=" + entrada + ", encontrados=" + encontrados + ", temporal=" + temporal + ", imagenTif="
				+ imagenTif + ", imagenDifTif=" + imagenDifTif + ", imagenFallo=" + imagenFallo + ", jasperReports="
				+ jasperReports + ", jasperReportsJPG=" + jasperReportsJPG + ", sourceTIF=" + sourceTIF
				+ ", temporalTIF=" + temporalTIF + ", autorizacion=" + autorizacion + ", batch=" + batch
				+ ", preautorizacion=" + preautorizacion + "]";
	}
	
}
