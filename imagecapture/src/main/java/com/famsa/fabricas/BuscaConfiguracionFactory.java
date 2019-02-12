package com.famsa.fabricas;

import com.famsa.interfaces.IBuscaConfiguracion;

public class BuscaConfiguracionFactory {
	
	private BuscaConfiguracionFactory() {
		throw new IllegalStateException("BuscaConfiguracionFactory class");
	}

	public static IBuscaConfiguracion instance() {
		return null;
	}
}
