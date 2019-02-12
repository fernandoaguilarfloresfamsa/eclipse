package com.famsa.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="mapeobd")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mapeobd", propOrder = {
    "tipo",
    "posicion",
    "columna",
    "traducir"
})
public class Campo {
	
	@XmlElement(required = true)
	private String tipo;
	@XmlElement(required = true)
	private int posicion;
	@XmlElement(required = true)
	private String columna;
	@XmlElement(name = "traducir")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")	
	private String traducir;
	
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public int getPosicion() {
		return posicion;
	}
	public void setPosicion(int posicion) {
		this.posicion = posicion;
	}
	public String getColumna() {
		return columna;
	}
	public void setColumna(String columna) {
		this.columna = columna;
	}
	public String getTraducir() {
		return traducir;
	}
	public void setTraducir(String traducir) {
		this.traducir = traducir;
	}
	@Override
	public String toString() {
		return "MapeoBD [tipo=" + tipo + ", posicion=" + posicion + ", columna=" + columna + ", traducir=" + traducir
				+ "]";
	}
	

}
