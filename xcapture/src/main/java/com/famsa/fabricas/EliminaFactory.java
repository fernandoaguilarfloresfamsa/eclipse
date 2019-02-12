package com.famsa.fabricas;

import com.famsa.controlador.Elimina;
import com.famsa.interfaces.IElimina;

public class EliminaFactory {

	private EliminaFactory() {
		throw new IllegalStateException("EliminarArchivoFactory class");
	}
	
	public static IElimina delFileEntradaInstance() {
		return new Elimina();
	}
	
	public static IElimina delFileAntInstance() {
		return new Elimina();
	}
}
