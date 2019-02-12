package com.famsa.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="tabla")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tabla", propOrder = {
    "nombreTabla",
    "tipo",
    "folderOut"
})
public class Tabla {

	@XmlElement(required = true)
	private String nombreTabla;
	@XmlElement(required = true)
	private String tipo;
	@XmlElement(name = "folderOut")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")	
	protected String folderOut;
	
	public String getNombreTabla() {
		return nombreTabla;
	}
	public void setNombreTabla(String nombreTabla) {
		this.nombreTabla = nombreTabla;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getFolderOut() {
		return folderOut;
	}
	public void setFolderOut(String folderOut) {
		this.folderOut = folderOut;
	}
	@Override
	public String toString() {
		return "Tabla [nombreTabla=" + nombreTabla + ", tipo=" + tipo + ", folderOut=" + folderOut + "]";
	}
	
}
