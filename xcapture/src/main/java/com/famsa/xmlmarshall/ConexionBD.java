package com.famsa.xmlmarshall;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="conexionbd")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "conexionbd", propOrder = {
    "host",
    "puerto",
    "instancia",
    "esquema",
    "usuario",
    "contrasena"
})
public class ConexionBD {

	@XmlElement(required = true)
	protected String host;
	@XmlElement(required = true)
	protected String puerto;
	@XmlElement(required = true)
	protected String instancia;
	@XmlElement(required = true)
	protected String esquema;
	@XmlElement(required = true)
	protected String usuario;
	@XmlElement(name = "contrasena")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")	
	protected String contrasena;
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPuerto() {
		return puerto;
	}
	public void setPuerto(String puerto) {
		this.puerto = puerto;
	}
	public String getInstancia() {
		return instancia;
	}
	public void setInstancia(String instancia) {
		this.instancia = instancia;
	}
	public String getEsquema() {
		return esquema;
	}
	public void setEsquema(String esquema) {
		this.esquema = esquema;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public String getContrasena() {
		return contrasena;
	}
	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}
	@Override
	public String toString() {
		return "ConexionBD [host=" + host + ", puerto=" + puerto + ", instancia=" + instancia + ", esquema=" + esquema
				+ ", usuario=" + usuario + ", contrasena=" + contrasena + "]";
	}

}
