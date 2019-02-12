package com.famsa.xmlmarshall;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="campos")
@XmlAccessorType(XmlAccessType.FIELD)
public class Campos {

	@XmlElement(name = "tabla")
	private Tabla tabla;
    @XmlElement(name = "campo")
    private List<Campo> listCampo = null;

    public Tabla getTabla() {
		return tabla;
	}
	public void setTabla(Tabla tabla) {
		this.tabla = tabla;
	}
	public List<Campo> getListCampo() {
		return listCampo;
	}
	public void setListCampo(List<Campo> listCampo) {
		this.listCampo = listCampo;
	}
	@Override
	public String toString() {
		return "Campos [tabla=" + tabla + ", listCampo=" + listCampo + "]";
	}
    
}
