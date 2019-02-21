package com.famsa.aplicacion;

/*
 * Autor:	Fernando Aguilar Flores.
 * 
 * Parametros de Entrada
 * 			Nombre del archivo donde se encuentra la lista de files para procesar.
 * 
 * Descripción
 * 			Lee el contenido del archivo de tipo xml. Un registro a la vez.
 * 
 */
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
import com.famsa.bean.ProcessFileBean;
import com.famsa.exceptions.AsignaArchivosExc;
import com.famsa.exceptions.ProcessFileCtrlExc;
import com.famsa.fabricas.ProcessFileFactory;
import com.famsa.interfaces.IProcessFile;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

public class AsignaArchivos {

	static final Logger logger = Logger.getLogger(AsignaArchivos.class.getName());
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

    	if (parametros==null) {
        	logger.log(Level.SEVERE,"FALTA INFORMACION PARA CONTINUAR CON EL PROCESO.");
        	throw new AsignaArchivosExc("FALTA INFORMACION PARA CONTINUAR CON EL PROCESO.");
    	}

    	Archivos archivosParaProcesar = null;
    	archivoXML=args[0];
    	File f = new File(configuracion.getFolder().getEncontrados()+archivoXML);

    	if(f.exists() && !f.isDirectory()) {

			try {
				archivosParaProcesar = AsignaArchivos.unmarshalXMLToList();
			} catch (AsignaArchivosExc e) {
				logger.log(Level.SEVERE, e.toString(), e);
				throw new AsignaArchivosExc(e.toString(), e);
			}
		}
        	
		if(archivosParaProcesar!=null && !archivosParaProcesar.getListArchivo().isEmpty()) {

			msg = String.format("Existen %d Archivo(s) para procesar: %s.", 
					archivosParaProcesar.getListArchivo().size(),archivoXML);
			logger.log(Level.INFO,msg);

			for (int num=0;num<archivosParaProcesar.getListArchivo().size();num++) {
				
				ProcessFileBean resultado = null;
				try {
					resultado = consumeWebService(
							archivosParaProcesar.getListArchivo().get(num).getCreationTime(), 
							archivosParaProcesar.getListArchivo().get(num).getFilePath(), 
							archivosParaProcesar.getListArchivo().get(num).getUuid());
					
					logger.log(Level.FINE, resultado.toString(), new Object());
					
				} catch (ProcessFileCtrlExc e) {
					throw new AsignaArchivosExc(e.toString(), e);
				}
			}
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
    	File f = new File(configuracion.getFolder().getEncontrados()+archivoXML);
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
    			archivosPendientes = (Archivos) jaxbUnmarshaller.unmarshal( new File(configuracion.getFolder().getEncontrados()+archivoXML));
    		} catch (JAXBException e) {
    			logger.log(Level.SEVERE, e.toString(), e);
    			throw new AsignaArchivosExc(e.toString(), e);
    		}
    	}
    	return archivosPendientes;
    }

    public static ProcessFileBean consumeWebService(
    		String creationTime, String filePath, String uuid) throws ProcessFileCtrlExc {
    	
    	String miUrl = String.format(
    			"http://localhost:8080/processfiles/rest/processfiles/buscaArchivos/%s/%s/%s/%s", 
    			filePath.replace('\\','/').replaceAll(" ", "%20"),
    			uuid, 
    			creationTime, 
    			archivoXML);
    	
    	ClientConfig clientConfig = new DefaultClientConfig();
    	clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
    	Client client = Client.create(clientConfig);

    	WebResource webResource = client.resource(miUrl);

    	//put switch, name,priority....
    	ClientResponse response = webResource.accept("application/json")
    	        .type("application/json").get(ClientResponse.class);    	
    	
    	if (response.hasEntity()) {
    		String output = response.getEntity(String.class);
    		Gson gson = new Gson();
    		return gson.fromJson(output, ProcessFileBean.class);
    	}
		return null;
    }
    
}
