package com.famsa.fabricas;

import com.famsa.controlador.BuscaArchivos;
import com.famsa.interfaces.IBuscaArchivos;

public class BuscaArchivosFactory {

	private BuscaArchivosFactory() {
		throw new IllegalStateException("BuscaArchivosFactory class");
	}
	
	public static IBuscaArchivos instance() {
		return new BuscaArchivos();
	}
}
