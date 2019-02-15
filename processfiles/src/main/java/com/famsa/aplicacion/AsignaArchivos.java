package com.famsa.aplicacion;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.famsa.bean.Archivos;
import com.famsa.bean.Configuracion;
import com.famsa.exceptions.AsignaArchivosExc;
import com.famsa.exceptions.ProcessFileCtrlExc;
import com.famsa.fabricas.ProcessFileFactory;
import com.famsa.interfaces.IProcessFile;

public class AsignaArchivos {

	static final Logger logger = Logger.getLogger(BuscaArchivos.class.getName());
	static FileHandler fileHandler;
	static Configuracion configuracion = null;
	static String archivoXML;
	static String msg = null;

	public static void main(String[] args) throws AsignaArchivosExc {

		try {
			AsignaArchivos.inicio();
		} catch (AsignaArchivosExc e1) {
			logger.log(Level.SEVERE, e1.toString(), e1);
			throw new AsignaArchivosExc(e1.toString(), e1);
		}
    	
    	String parametros = null;
    	for (String s: args) {
        	parametros = s;
        }

    	if (parametros!=null) {
        	archivoXML=args[0];
        	Archivos archivosParaProcesar = null;
			try {
				archivosParaProcesar = AsignaArchivos.unmarshalXMLToList();
			} catch (AsignaArchivosExc e) {
				logger.log(Level.SEVERE, e.toString(), e);
				throw new AsignaArchivosExc(e.toString(), e);
			}
			
			File f = new File(archivoXML);
			if(
				f.exists() && 
				!f.isDirectory() && 
				archivosParaProcesar!=null && 
				!archivosParaProcesar.getListArchivo().isEmpty()) {

				msg = String.format("Existen %d Archivo(s) para procesar: %s.", 
						archivosParaProcesar.getListArchivo().size(),archivoXML);
				logger.log(Level.INFO,msg);

			}
        } else {
        	logger.log(Level.SEVERE,"FALTA INFORMACION PARA CONTINUAR CON EL PROCESO.");
        	throw new AsignaArchivosExc("FALTA INFORMACION PARA CONTINUAR CON EL PROCESO.");
        }

	}
	
	private static void inicio() throws AsignaArchivosExc {

		try {
			obtenerConfiguracion();
		} catch (AsignaArchivosExc e1) {
			throw new AsignaArchivosExc(e1.toString(), e1);
		}
		
		creaCarpeta(configuracion.getHilo().getPathLogErr());
		creaCarpeta(configuracion.getFolder().getEncontrados());

		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS-");
		String nomArc = String.format("%s%s%s.log", 
				configuracion.getHilo().getPathLogErr(), dateFormat.format(date), 
				BuscaArchivos.class.getName());
		try {
			fileHandler = new FileHandler(nomArc);
		} catch (SecurityException | IOException e) {
			throw new AsignaArchivosExc(e.toString(), e);
		}
		logger.addHandler(fileHandler);
		SimpleFormatter formatter = new SimpleFormatter();
		fileHandler.setFormatter(formatter);
		fileHandler.setLevel(Level.ALL);
		logger.setLevel(Level.ALL);		
	}
	
	private static void obtenerConfiguracion() throws AsignaArchivosExc {
		IProcessFile config = ProcessFileFactory.buscaConfiguracion();
		try {
			configuracion = config.findConfiguration();
		} catch (ProcessFileCtrlExc e) {
			logger.log(Level.SEVERE, e.toString(), e);
			throw new AsignaArchivosExc(e.toString(), e);
		}
	}
	
	private static void creaCarpeta(String carpeta) {
    	File directory = new File(carpeta);
		if (! directory.exists()) {
			directory.mkdirs();
		}		
	}

    public static Archivos unmarshalXMLToList() throws AsignaArchivosExc {
    	
		Archivos archivosPendientes = null;
    	File f = new File(archivoXML);
    	if(f.exists() && !f.isDirectory()) { 
            JAXBContext jaxbContext;
    		try {
    			jaxbContext = JAXBContext.newInstance(Archivos.class);
    		} catch (JAXBException e) {
    			logger.log(Level.SEVERE, e.toString(), e);
    			throw new AsignaArchivosExc(e.toString(), e);
    		}
            Unmarshaller jaxbUnmarshaller;
    		try {
    			jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    		} catch (JAXBException e) {
    			logger.log(Level.SEVERE, e.toString(), e);
    			throw new AsignaArchivosExc(e.toString(), e);
    		}
            try {
    			archivosPendientes = (Archivos) jaxbUnmarshaller.unmarshal( new File(archivoXML));
    		} catch (JAXBException e) {
    			logger.log(Level.SEVERE, e.toString(), e);
    			throw new AsignaArchivosExc(e.toString(), e);
    		}
    	}
    	return archivosPendientes;
    }

}
