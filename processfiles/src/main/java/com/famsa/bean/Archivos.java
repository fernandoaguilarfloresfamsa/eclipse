package com.famsa.bean;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="archivos")
@XmlAccessorType(XmlAccessType.FIELD)
public class Archivos {

    @XmlElement(name = "archivo")
    private List<Archivo> listArchivo = null;

	public List<Archivo> getListArchivo() {
		return listArchivo;
	}
	public void setListArchivo(List<Archivo> listArchivo) {
		this.listArchivo = listArchivo;
	}
	@Override
	public String toString() {
		return "Archivos [listArchivo=" + listArchivo + "]";
	}

}
