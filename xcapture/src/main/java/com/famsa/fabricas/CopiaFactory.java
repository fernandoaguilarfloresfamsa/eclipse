package com.famsa.fabricas;

import com.famsa.controlador.Copia;
import com.famsa.interfaces.ICopia;

public class CopiaFactory {

	private CopiaFactory() {
		throw new IllegalStateException("CopiaArchivoFactory class");
	}

	public static ICopia buildInstance() {
		return new Copia();
	}
}
