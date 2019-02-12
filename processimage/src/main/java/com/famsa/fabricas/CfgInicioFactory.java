package com.famsa.fabricas;

import com.famsa.controlador.CfgInicio;
import com.famsa.interfaces.ICfgInicio;

public class CfgInicioFactory {

	private CfgInicioFactory() {
		throw new IllegalStateException("CfgInicioFactory class");
	}
	
	public static ICfgInicio newInstance() {
		return new CfgInicio();
	}
	
}
