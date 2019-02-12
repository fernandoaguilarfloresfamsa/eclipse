package com.famsa.fabricas;

import com.famsa.controlador.Procesa;
import com.famsa.interfaces.IProcesa;

public class ProcesaFactory {

	private ProcesaFactory() {
		throw new IllegalStateException("ProcesarImagenFactory class");
	}
	
	public static IProcesa buildInstance() {
		return new Procesa();
	}
}
