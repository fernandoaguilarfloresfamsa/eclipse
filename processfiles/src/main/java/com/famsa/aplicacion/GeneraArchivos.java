package com.famsa.aplicacion;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.famsa.bean.Configuracion;
import com.famsa.controlador.ProcessFileCtrl;
import com.famsa.exceptions.GeneraArchivosExc;
import com.famsa.exceptions.ProcessFileCtrlExc;

public class GeneraArchivos {

	static final Logger logGeneraArchivos = Logger.getLogger(GeneraArchivos.class.getName());
	static FileHandler fhBuscaArchivos;
	static Configuracion configuracion = null;

	public static void main(String[] args) throws GeneraArchivosExc {
		try {
			GeneraArchivos.inicio();
		} catch (GeneraArchivosExc e) {
			throw new GeneraArchivosExc(e.toString(), e);
		}
	}

	private static void inicio() throws GeneraArchivosExc {

		try {
			obtenerConfiguracion();
		} catch (GeneraArchivosExc e1) {
			throw new GeneraArchivosExc(e1.toString(), e1);
		}
		
		creaCarpeta(configuracion.getHilo().getPathLogErr());
		creaCarpeta(configuracion.getFolder().getEncontrados());
		creaCarpeta(configuracion.getFolder().getTemporal());

		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS-");
		String nomArc = String.format("%s%s%s.log", 
				configuracion.getHilo().getPathLogErr(), dateFormat.format(date), 
				GeneraArchivos.class.getName());
		try {
			fhBuscaArchivos = new FileHandler(nomArc);
		} catch (SecurityException | IOException e) {
			throw new GeneraArchivosExc(e.toString(), e);
		}
		logGeneraArchivos.addHandler(fhBuscaArchivos);
		SimpleFormatter formatter = new SimpleFormatter();
		fhBuscaArchivos.setFormatter(formatter);
		fhBuscaArchivos.setLevel(Level.ALL);
		logGeneraArchivos.setLevel(Level.ALL);		
	}
	
	private static void obtenerConfiguracion() throws GeneraArchivosExc {
		ProcessFileCtrl cfg = new ProcessFileCtrl();
		try {
			configuracion = cfg.findConfiguration();
		} catch (ProcessFileCtrlExc e) {
			logGeneraArchivos.log(Level.INFO, e.toString(), e);
			throw new GeneraArchivosExc(e.toString(), e);
		}
	}
	
	private static void creaCarpeta(String carpeta) {
    	File directory = new File(carpeta);
		if (! directory.exists()) {
			directory.mkdirs();
		}		
	}
	
}
