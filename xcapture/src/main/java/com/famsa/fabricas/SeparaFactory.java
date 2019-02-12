package com.famsa.fabricas;

import com.famsa.controlador.Separa;
import com.famsa.interfaces.ISepara;

public class SeparaFactory {

	private SeparaFactory() {
		throw new IllegalStateException("SeparaImagenFactory class");
	}
	
	public static ISepara buildInstance() {
		return new Separa();
	}

}
