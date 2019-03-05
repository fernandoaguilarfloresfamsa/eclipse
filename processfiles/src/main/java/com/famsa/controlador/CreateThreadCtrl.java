package com.famsa.controlador;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.famsa.aplicacion.CreateThread;
import com.famsa.bean.Archivo;
import com.famsa.bean.Configuracion;
import com.famsa.exceptions.CreateThreadCtrlExc;

public class CreateThreadCtrl {

	static final Logger logger = Logger.getLogger(CreateThread.class.getName());
	private static final String RUTATXT = "ruta.txt";
	private static final String CADENASPLIT = "\\\\(?=[^\\\\]+$)";
	private static final String CARPETACONFIG = "\\Expedientes\\Config\\";
	private static final String XMLFILE = "config.xml";
	String nomArcConf = null;
	String nomCarpeta = null;
	String[] tokens;
	List<Archivo> resIds = new ArrayList<>();
	
	public Configuracion findConfiguration() throws CreateThreadCtrlExc {
		Configuracion configXML = new Configuracion();
		
		//	---------------------------------------------------------------------------------------
		File f = new File(RUTATXT);
		String cadena = f.getAbsolutePath();

		tokens = cadena.split(CADENASPLIT);
		nomCarpeta = tokens[0]+CARPETACONFIG;
		nomArcConf = nomCarpeta + XMLFILE;
		
		File carpeta = new File(nomCarpeta);
		if(!carpeta.exists()) { 
			creaCarpeta(nomCarpeta);
		}
		
		//	---------------------------------------------------------------------------------------
		File fileConf = new File(nomArcConf);
    	if(fileConf.exists() && !fileConf.isDirectory()) {
            JAXBContext jaxbContext = null;
			try {
				jaxbContext = JAXBContext.newInstance(Configuracion.class);
			} catch (JAXBException e) {
				throw new CreateThreadCtrlExc(e.toString(), e);
			}
            Unmarshaller jaxbUnmarshaller = null;
			try {
				jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			} catch (JAXBException e) {
				throw new CreateThreadCtrlExc(e.toString(), e);
			}
			try {
				configXML = (Configuracion) jaxbUnmarshaller.unmarshal( new File(nomArcConf));
			} catch (JAXBException e) {
				throw new CreateThreadCtrlExc(e.toString(), e);
			}
    	}
    	
    	creaCarpeta(configXML.getFolder().getEntrada());
    	creaCarpeta(configXML.getFolder().getEncontrados());
    	creaCarpeta(configXML.getFolder().getTemporal());

		return configXML;
	}

	private void creaCarpeta(String carpeta) {
    	File directory = new File(carpeta);
		if (! directory.exists()) {
			directory.mkdirs();
		}		
		
	}
	
	
	
}
