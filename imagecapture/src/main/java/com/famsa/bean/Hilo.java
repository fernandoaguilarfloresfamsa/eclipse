package com.famsa.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="hilo")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "hilo", propOrder = {
    "maximo",
    "poolSegundos",
    "porcentaje",
    "pathLogErr"
})
public class Hilo {

	@XmlElement(required = true)
	protected int maximo;
	@XmlElement(required = true)
	protected int poolSegundos;
	@XmlElement(required = true)
	protected Double porcentaje;
	@XmlElement(name = "pathLogErr")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")	
	protected String pathLogErr;
	
	public int getMaximo() {
		return maximo;
	}
	public void setMaximo(int maximo) {
		this.maximo = maximo;
	}
	public int getPoolSegundos() {
		return poolSegundos;
	}
	public void setPoolSegundos(int poolSegundos) {
		this.poolSegundos = poolSegundos;
	}
	public Double getPorcentaje() {
		return porcentaje;
	}
	public void setPorcentaje(Double porcentaje) {
		this.porcentaje = porcentaje;
	}
	public String getPathLogErr() {
		return pathLogErr;
	}
	public void setPathLogErr(String pathLogErr) {
		this.pathLogErr = pathLogErr;
	}
	@Override
	public String toString() {
		return "Hilo [maximo=" + maximo + ", poolSegundos=" + poolSegundos + ", porcentaje=" + porcentaje
				+ ", pathLogErr=" + pathLogErr + "]";
	}
	
}
