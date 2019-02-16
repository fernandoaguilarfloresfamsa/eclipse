package com.famsa.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.famsa.exceptions.ConfiguracionExc;

public class Configuracion {

	public String getPropValues(String key) throws ConfiguracionExc {
		 
		InputStream inputStream;
		String valor = "";
		
		Properties prop = new Properties();
		String propFileName = "config.properties";
		inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
		try {
			prop.load(inputStream);
		} catch (IOException e) {
			throw new ConfiguracionExc(e.toString(), e);
		}
		valor = prop.getProperty(key);
		try {
			inputStream.close();
		} catch (IOException e) {
			throw new ConfiguracionExc(e.toString(), e);
		}
		return valor;
	}

}
