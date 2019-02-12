package com.famsa.config;

import com.famsa.interfaces.IConfig;
import com.famsa.xmlmarshall.Config;

public class Configuracion implements IConfig {

	@Override
	public Config getIConfig() {
		Cfg cfg = new Cfg();
		return cfg.getCFG();
	}

}
