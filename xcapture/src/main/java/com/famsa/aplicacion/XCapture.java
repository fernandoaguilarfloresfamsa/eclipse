package com.famsa.aplicacion;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import javax.xml.bind.Unmarshaller;

import com.famsa.controlador.CreaHilo;
import com.famsa.enums.EstadoEnum;
import com.famsa.exceptions.XCaptureExcep;
import com.famsa.fabricas.ConfigFactory;
import com.famsa.interfaces.IConfig;
import com.famsa.xmlmarshall.Archivo;
import com.famsa.xmlmarshall.Archivos;
import com.famsa.xmlmarshall.Config;

public class XCapture {
	
	static final Logger logger = Logger.getLogger(XCapture.class.getName());
	static FileHandler fileHandler;
	static Config config = null;
	static String archivoXML;
	static String msg = null;
	
    public static void main( String[] args ) {
    	
    	boolean continua = false;
    	
    	try {
			continua = XCapture.inicio(args);
		} catch (XCaptureExcep e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}

        if (continua) {
        	XCapture.procesoBuscaImagenes();
        } else {
        	logger.log(Level.INFO, "SIN PROCESAR INFORMACION.");
        }
        
    	fileHandler.close();
    }
    
    private static boolean inicio(String[] args) throws XCaptureExcep {
    	
		obtenerConfiguracion();
		creaCarpeta(config.getHilo().getPathLogErr());

		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS-");
		String nomArc = String.format("%s%s%s.log", 
				config.getHilo().getPathLogErr(), dateFormat.format(date), 
				XCapture.class.getName());
		
		try {
			fileHandler = new FileHandler(nomArc);
		} catch (SecurityException | IOException e1) {
			logger.log(Level.SEVERE, e1.toString(), e1);
			throw new XCaptureExcep(e1.toString(), e1);
		}

		logger.addHandler(fileHandler);
		SimpleFormatter formatter = new SimpleFormatter();
		fileHandler.setFormatter(formatter);
		fileHandler.setLevel(Level.ALL);
		logger.setLevel(Level.ALL);		

		boolean regreso = false;
		
    	String parametros = null;
        for (String s: args) {
        	parametros = s;
        }
        if (parametros==null) {
        	logger.log(Level.INFO, "FALTA INFORMACION PARA CONTINUAR CON EL PROCESO.");
        } else if (parametros.equals("-i") || parametros.equals("-I")) {
        	logger.log(Level.INFO, "COMANDO EJECUTADO CORRECTAMENTE.");
        } else if (parametros.equals("-p") || parametros.equals("-P")) {
        	regreso=true;
        }

		return regreso;
    }
    
	private static void obtenerConfiguracion() {
		IConfig iConfig = ConfigFactory.getConfig();
		config = iConfig.getIConfig();
	}
    
	private static void creaCarpeta(String carpeta) {
    	File directory = new File(carpeta);
		if (! directory.exists()) {
			directory.mkdirs();
		}		
	}
	
    private static void procesoBuscaImagenes() {
    	boolean encuentraFiles = false;
    	
		try {
			logger.log(Level.INFO,"Busca Imagenes para Procesar.");
			encuentraFiles = XCapture.buscaImagenes();
		} catch (XCaptureExcep e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
		
		if (encuentraFiles) {
			XCapture.procesoCreaHilo();
		} else {
			logger.log(Level.INFO, "No existen archivos en la carpeta de entrada.");
		}
    }
    
	public static boolean buscaImagenes() throws XCaptureExcep {
		
		boolean resultado = false;
		
		HashMap<FileTime, String> hmap = new HashMap<>();
		File[] fileList = getFileList(config.getFolder().getEntrada());
        for(File file : fileList) {
        	if (!file.isDirectory()) {
            	Path filePath = file.toPath();
	            BasicFileAttributes attributes = null;
	            FileTime fileTime = null;
	            try {
					attributes = Files.readAttributes(filePath, BasicFileAttributes.class);
	            	fileTime = attributes.creationTime();
	                hmap.put(fileTime, filePath.toString());
				} catch (IOException e) {
					logger.log(Level.SEVERE, e.toString(), e);
					throw new XCaptureExcep(e.toString(), e);
				}
            }
        }
        
        if (hmap.size()!=0) {
        	        
			DateFormat df = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
			Date today = Calendar.getInstance().getTime();
			String reportDate = df.format(today);
	
			creaCarpeta(config.getFolder().getParaProcesar());
	
			archivoXML = String.format("%s%s.xml",config.getFolder().getParaProcesar(),reportDate);
	        
	        Map<FileTime, String> treeMap = new TreeMap<>(hmap);
	        
        	marshalListToXMLFile(treeMap);
	        
	        resultado=true;
        }
        
        return resultado;
	}

    private static File[] getFileList(String dirPath) {
        File dir = new File(dirPath);
        return dir.listFiles();
    }
    
    public static void marshalListToXMLFile(Map<FileTime, String> map) throws XCaptureExcep {
    	
    	Archivos archivosEnCarpeta = new Archivos();
    	archivosEnCarpeta.setListArchivo(new ArrayList<Archivo>());
    	
    	Set<Entry<FileTime, String>> s = map.entrySet();
		Iterator<Entry<FileTime, String>> it = s.iterator();
		
		int maximo = config.getHilo().getMaximo();
		int contador = 0;

		while ( it.hasNext() ) {
			Entry<FileTime, String> entry = it.next();
			FileTime key = entry.getKey();
			String value = entry.getValue();
			String keyStr = String.valueOf(key);
			
			Archivo unArchivo = new Archivo();
			unArchivo.setLastAccessTime(keyStr);
			unArchivo.setFilePath(value);
			unArchivo.setFilePathXML(archivoXML);
			unArchivo.setUuid(UUID.randomUUID().toString().toUpperCase());
			unArchivo.setFilePathCopy(null);
			unArchivo.setPathCopy(null);
			unArchivo.setThreadName(null);
			unArchivo.setExtension(null);
			unArchivo.setNumeroPaginas(0);
			unArchivo.setExisteDesTif(false);
			unArchivo.setPathTif(null);
			unArchivo.setExisteDesOtr(false);
			unArchivo.setPathOtro(null);
			unArchivo.setNomArch(null);
			unArchivo.setEstado(EstadoEnum.AC);
			
			archivosEnCarpeta.getListArchivo().add(unArchivo);

			contador++;
			if (contador >= maximo) {
				break;
			}
        }

		File file = new File(archivoXML);
		JAXBContext jaxbContext = null;
		try {
			jaxbContext = JAXBContext.newInstance(Archivos.class);
		} catch (JAXBException e) {
			logger.log(Level.SEVERE, e.toString(), e);
			throw new XCaptureExcep(e.toString(), e);
		}
		Marshaller jaxbMarshaller = null;
		try {
			jaxbMarshaller = jaxbContext.createMarshaller();
		} catch (JAXBException e) {
			logger.log(Level.SEVERE, e.toString(), e);
			throw new XCaptureExcep(e.toString(), e);
		}
		try {
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		} catch (PropertyException e) {
			logger.log(Level.SEVERE, e.toString(), e);
			throw new XCaptureExcep(e.toString(), e);
		}
		try {
			jaxbMarshaller.marshal(archivosEnCarpeta, file);
		} catch (JAXBException e) {
			logger.log(Level.SEVERE, e.toString(), e);
			throw new XCaptureExcep(e.toString(), e);
		}
    }
    
    public static Archivos unmarshalXMLToList() throws XCaptureExcep {
    	
		Archivos archivosPendientes = null;
    	File f = new File(archivoXML);
    	if(f.exists() && !f.isDirectory()) { 
            JAXBContext jaxbContext;
    		try {
    			jaxbContext = JAXBContext.newInstance(Archivos.class);
    		} catch (JAXBException e) {
    			logger.log(Level.SEVERE, e.toString(), e);
    			throw new XCaptureExcep(e.toString(), e);
    		}
            Unmarshaller jaxbUnmarshaller;
    		try {
    			jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    		} catch (JAXBException e) {
    			logger.log(Level.SEVERE, e.toString(), e);
    			throw new XCaptureExcep(e.toString(), e);
    		}
            try {
    			archivosPendientes = (Archivos) jaxbUnmarshaller.unmarshal( new File(archivoXML));
    		} catch (JAXBException e) {
    			logger.log(Level.SEVERE, e.toString(), e);
    			throw new XCaptureExcep(e.toString(), e);
    		}
    	}
    	return archivosPendientes;
    }
    
    private static void procesoCreaHilo() {
    	
		Archivos archivosParaProcesar = null;
		try {
			logger.log(Level.INFO,"Recupera información desde archivo XML");
			archivosParaProcesar = XCapture.unmarshalXMLToList();
		} catch (XCaptureExcep e) {
			logger.log(Level.SEVERE, e.toString(), e);
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
			
			CreaHilo creaHilo = new CreaHilo();
			try {
				creaHilo.proceso(config.getHilo().getMaximo(), archivosParaProcesar);
			} catch (XCaptureExcep e) {
				logger.log(Level.SEVERE, e.toString(), e);
			}
		}
    }
    
}
