package com.famsa.aplicacion;

/*
 * Autor:	Fernando Aguilar Flores.
 * 
 * Parametros de Entrada
 * 			Nombre del archivo donde se guardan los files ordenados por fecha y hora de creacion.
 * 
 * Descripción
 * 			crea el sistema de carpetas necesarias para el funcionamiento del sistema.
 * 			busca en el directorio de entrada los archivos para procesar.
 * 			guarda la lista de los archivos en el archivo que recibio como parametro de entrada.
 * 
 */
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import com.famsa.bean.Archivo;
import com.famsa.bean.Archivos;
import com.famsa.bean.Configuracion;
import com.famsa.controlador.ProcessFileCtrl;
import com.famsa.exceptions.BuscaArchivosExc;
import com.famsa.exceptions.ProcessFileCtrlExc;

public class BuscaArchivos {
	
	static final Logger logBuscaArchivos = Logger.getLogger(BuscaArchivos.class.getName());
	static FileHandler fhBuscaArchivos;
	static Configuracion configuracion = null;
	static String archivo;
	static String archivoXML;
	static String msg = null;

	public static void main(String[] args) throws BuscaArchivosExc {

		try {
			BuscaArchivos.inicio();
		} catch (BuscaArchivosExc e1) {
			throw new BuscaArchivosExc(e1.toString(), e1);
		}
    	
    	String parametros = null;
    	for (String s: args) {
        	parametros = s;
        }

    	if (parametros!=null) {
        	archivo=args[0];
			try {
				BuscaArchivos.findFiles();
			} catch (BuscaArchivosExc e) {
				throw new BuscaArchivosExc(e.toString(), e);
			}
        } else {
        	throw new BuscaArchivosExc("FALTA INFORMACION PARA CONTINUAR CON EL PROCESO.");
        }
	}
	
	private static void inicio() throws BuscaArchivosExc {

		try {
			obtenerConfiguracion();
		} catch (BuscaArchivosExc e1) {
			throw new BuscaArchivosExc(e1.toString(), e1);
		}
		
		creaCarpeta(configuracion.getHilo().getPathLogErr());
		creaCarpeta(configuracion.getFolder().getEncontrados());
		creaCarpeta(configuracion.getFolder().getTemporal());

		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS-");
		String nomArc = String.format("%s%s%s.log", 
				configuracion.getHilo().getPathLogErr(), dateFormat.format(date), 
				BuscaArchivos.class.getName());
		try {
			fhBuscaArchivos = new FileHandler(nomArc);
		} catch (SecurityException | IOException e) {
			throw new BuscaArchivosExc(e.toString(), e);
		}
		logBuscaArchivos.addHandler(fhBuscaArchivos);
		SimpleFormatter formatter = new SimpleFormatter();
		fhBuscaArchivos.setFormatter(formatter);
		fhBuscaArchivos.setLevel(Level.ALL);
		logBuscaArchivos.setLevel(Level.ALL);		
	}

	private static void obtenerConfiguracion() throws BuscaArchivosExc {
		ProcessFileCtrl cfg = new ProcessFileCtrl();
		try {
			configuracion = cfg.findConfiguration();
		} catch (ProcessFileCtrlExc e) {
			throw new BuscaArchivosExc(e.toString(), e);
		}
	}
	
	private static void creaCarpeta(String carpeta) {
    	File directory = new File(carpeta);
		if (! directory.exists()) {
			directory.mkdirs();
		}		
	}

	public static void findFiles() throws BuscaArchivosExc {
		
		HashMap<FileTime, String> hmap = new HashMap<>();
		File[] fileList = getFileList(configuracion.getFolder().getEntrada());
        for(File file : fileList) {
        	if (!file.isDirectory()) {
            	Path filePath = file.toPath();
	            BasicFileAttributes attributes = null;
	            FileTime fileTime = null;

				try {
					attributes = Files.readAttributes(filePath, BasicFileAttributes.class);
				} catch (IOException e) {
					throw new BuscaArchivosExc(e.toString(), e);
				}
            	fileTime = attributes.creationTime();
                hmap.put(fileTime, filePath.toString());
            }
        }
        
        if (hmap.size()!=0) {
        	Map<FileTime, String> treeMap = new TreeMap<>(hmap);
	        try {
				marshalListToXMLFile(treeMap);
			} catch (BuscaArchivosExc e) {
				throw new BuscaArchivosExc(e.toString(), e);
			}
        } else {
        	logBuscaArchivos.log(Level.INFO, "NO EXISTEN ARCHIVOS PARA PROCESAR.");
        }

	}

    private static File[] getFileList(String dirPath) {
        File dir = new File(dirPath);
        return dir.listFiles();
    }

    public static void marshalListToXMLFile(Map<FileTime, String> map) throws BuscaArchivosExc {
    	
    	Archivos archivosEnCarpeta = new Archivos();
    	archivosEnCarpeta.setListArchivo(new ArrayList<Archivo>());
    	
    	Set<Entry<FileTime, String>> s = map.entrySet();
		Iterator<Entry<FileTime, String>> it = s.iterator();
		
		while ( it.hasNext() ) {
			Entry<FileTime, String> entry = it.next();
			FileTime key = entry.getKey();
			String value = entry.getValue();
			String keyStr = String.valueOf(key);
			
			Archivo unArchivo = new Archivo();
			unArchivo.setCreationTime(keyStr);
			unArchivo.setFilePath(value);
			unArchivo.setUuid(UUID.randomUUID().toString().toUpperCase());
			
			archivosEnCarpeta.getListArchivo().add(unArchivo);
        }

		archivoXML = configuracion.getFolder().getEncontrados()+archivo;
    	File arch = new File(archivoXML);
    	if (!arch.exists()) {
    		File file = new File(archivoXML);
    		JAXBContext jaxbContext = null;

			try {
				jaxbContext = JAXBContext.newInstance(Archivos.class);
			} catch (JAXBException e3) {
				throw new BuscaArchivosExc(e3.toString(), e3);
			}
			Marshaller jaxbMarshaller = null;
			try {
				jaxbMarshaller = jaxbContext.createMarshaller();
			} catch (JAXBException e2) {
				throw new BuscaArchivosExc(e2.toString(), e2);
			}
			try {
				jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			} catch (PropertyException e1) {
				throw new BuscaArchivosExc(e1.toString(), e1);
			}
			try {
				jaxbMarshaller.marshal(archivosEnCarpeta, file);
			} catch (JAXBException e) {
				throw new BuscaArchivosExc(e.toString(), e);
			}
    	} else {
    		msg = String.format("EL NOMBRE DE ARCHIVO XML YA EXISTE [%s].", archivoXML);
    		logBuscaArchivos.log(Level.INFO, msg);
    	}
    }
    
}
