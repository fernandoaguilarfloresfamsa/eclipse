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
import com.famsa.exceptions.BuscaArchivosExc;
import com.famsa.exceptions.ProcesaImagenExc;
import com.famsa.fabricas.BuscaArchivosFactory;
import com.famsa.fabricas.BuscaConfiguracionFactory;
import com.famsa.interfaces.IBuscaArchivos;
import com.famsa.interfaces.IBuscaConfiguracion;

public class ProcesaImagen {

	static final Logger logger = Logger.getLogger(ProcesaImagen.class.getName());
	static FileHandler fileHandler;
	static Configuracion configuracion = null;
	static String archivoXML;
	static String msg = null;

	public static void main(String[] args) throws ProcesaImagenExc, BuscaArchivosExc {
		
        ProcesaImagen.creaLogger();
        
        if (args[0].equals("-p") || args[0].equals("-P")) {
			IBuscaArchivos archivos = BuscaArchivosFactory.instance();
			archivos.find();
        }
	}
	
	private static void creaLogger() throws ProcesaImagenExc {
		
		configuracion = obtenerConfiguracion();
		creaCarpeta(configuracion.getHilo().getPathLogErr());

		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS-");
		String nomArc = String.format("%s%s%s.log", 
				configuracion.getHilo().getPathLogErr(), dateFormat.format(date), 
				ProcesaImagen.class.getName());
		
		try {
			fileHandler = new FileHandler(nomArc);
		} catch (SecurityException | IOException e1) {
			logger.log(Level.SEVERE, e1.toString(), e1);
			throw new ProcesaImagenExc(e1.toString(), e1);
		}

		logger.addHandler(fileHandler);
		SimpleFormatter formatter = new SimpleFormatter();
		fileHandler.setFormatter(formatter);
		fileHandler.setLevel(Level.ALL);
		logger.setLevel(Level.ALL);		
	}
	
	private static Configuracion obtenerConfiguracion() {
		IBuscaConfiguracion cfg = BuscaConfiguracionFactory.instance();
		return cfg.find();
	}
	
	private static void creaCarpeta(String carpeta) {
    	File directory = new File(carpeta);
		if (! directory.exists()) {
			directory.mkdirs();
		}
	}
}
