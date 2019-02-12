package com.famsa.xmlmarshall;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="monitor")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "monitor", propOrder = {
    "poolSegundos",
    "nombreServicio",
    "cadenaRunning",
    "cadenaStop",
    "extensionImagen"
})
public class Monitor {

	@XmlElement(required = true)
	protected String poolSegundos;
	@XmlElement(required = true)
	protected String nombreServicio;
	@XmlElement(required = true)
	protected String cadenaRunning;
	@XmlElement(required = true)
	protected String cadenaStop;
	@XmlElement(name = "extensionImagen")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")	
	protected String extensionImagen;
	
	public String getPoolSegundos() {
		return poolSegundos;
	}
	public void setPoolSegMonitor(String poolSegundos) {
		this.poolSegundos = poolSegundos;
	}
	public String getNombreServicio() {
		return nombreServicio;
	}
	public void setNombreServicio(String nombreServicio) {
		this.nombreServicio = nombreServicio;
	}
	public String getCadenaRunning() {
		return cadenaRunning;
	}
	public void setCadenaRunning(String cadenaRunning) {
		this.cadenaRunning = cadenaRunning;
	}
	public String getCadenaStop() {
		return cadenaStop;
	}
	public void setCadenaStop(String cadenaStop) {
		this.cadenaStop = cadenaStop;
	}
	public String getExtensionImagen() {
		return extensionImagen;
	}
	public void setExtensionImagen(String extensionImagen) {
		this.extensionImagen = extensionImagen;
	}
	@Override
	public String toString() {
		return "Monitor [poolSegundos=" + poolSegundos + ", nombreServicio=" + nombreServicio + ", cadenaRunning="
				+ cadenaRunning + ", cadenaStop=" + cadenaStop + ", extensionImagen=" + extensionImagen + "]";
	}

}
