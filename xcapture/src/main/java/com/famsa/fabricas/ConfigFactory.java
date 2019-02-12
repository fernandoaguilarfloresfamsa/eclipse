package com.famsa.fabricas;

import com.famsa.config.Configuracion;
import com.famsa.interfaces.IConfig;

public class ConfigFactory {

	private ConfigFactory() {
		throw new IllegalStateException("ConfigFactory class");
	}

	public static IConfig getConfig() {
		return new Configuracion();
	}

}
