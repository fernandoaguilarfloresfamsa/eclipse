package com.famsa.xmlmarshall;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="config")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "config", propOrder = {
	    "hilo",
	    "conexion",
	    "monitor",
	    "folder",
	    "autorizacion",
	    "preautorizacion"
	})

public class Config {

	//	hilos
	@XmlElement(required = true)
	protected Hilo hilo;
	
	//	conexion a base de datos
	@XmlElement(required = true)
	protected ConexionBD conexion;
	
	//	monitor
	@XmlElement(required = true)
	protected Monitor monitor;

	//	folders
	@XmlElement(required = true)
	protected Folder folder;
	
	//	mapeo tabla (base de datos)
	@XmlElement(required = true)
	protected Campos autorizacion;
	
	@XmlElement(required = true)
	protected Campos preautorizacion;

	public Hilo getHilo() {
		return hilo;
	}
	public void setHilo(Hilo hilo) {
		this.hilo = hilo;
	}
	public ConexionBD getConexion() {
		return conexion;
	}
	public void setConexion(ConexionBD conexion) {
		this.conexion = conexion;
	}
	public Monitor getMonitor() {
		return monitor;
	}
	public void setMonitor(Monitor monitor) {
		this.monitor = monitor;
	}
	public Folder getFolder() {
		return folder;
	}
	public void setFolder(Folder folder) {
		this.folder = folder;
	}
	public Campos getAutorizacion() {
		return autorizacion;
	}
	public void setAutorizacion(Campos autorizacion) {
		this.autorizacion = autorizacion;
	}
	public Campos getPreautorizacion() {
		return preautorizacion;
	}
	public void setPreautorizacion(Campos preautorizacion) {
		this.preautorizacion = preautorizacion;
	}
	@Override
	public String toString() {
		return "Config [hilo=" + hilo + ", conexion=" + conexion + ", monitor=" + monitor + ", folder=" + folder
				+ ", autorizacion=" + autorizacion + ", preautorizacion=" + preautorizacion + "]";
	}

}
