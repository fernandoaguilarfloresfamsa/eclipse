package com.famsa.xmlmarshall;

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
    "paraProcesar",
    "imagenTif",
    "imagenDifTif",
    "imagenFallo",
    "autorizacion",
    "preautorizacion"
})
public class Folder {

	@XmlElement(required = true)
	protected String entrada;
	@XmlElement(required = true)
	protected String paraProcesar;
	@XmlElement(required = true)
	protected String imagenTif;
	@XmlElement(required = true)
	protected String imagenDifTif;
	@XmlElement(required = true)
	protected String imagenFallo;
	@XmlElement(required = true)
	protected String autorizacion;
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
	public String getParaProcesar() {
		return paraProcesar;
	}
	public void setParaProcesar(String paraProcesar) {
		this.paraProcesar = paraProcesar;
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
	public String getAutorizacion() {
		return autorizacion;
	}
	public void setAutorizacion(String autorizacion) {
		this.autorizacion = autorizacion;
	}
	public String getPreautorizacion() {
		return preautorizacion;
	}
	public void setPreautorizacion(String preautorizacion) {
		this.preautorizacion = preautorizacion;
	}
	@Override
	public String toString() {
		return "Folder [entrada=" + entrada + ", paraProcesar=" + paraProcesar + ", imagenTif=" + imagenTif
				+ ", imagenDifTif=" + imagenDifTif + ", imagenFallo=" + imagenFallo + ", autorizacion=" + autorizacion
				+ ", preautorizacion=" + preautorizacion + "]";
	}
	
}
