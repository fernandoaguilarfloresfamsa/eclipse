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
 * 			guarda la misma informacion en la base de datos
 * 
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.CallableStatement;
import java.sql.Connection;
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
import com.famsa.enums.BDEnum;
import com.famsa.exceptions.BuscaArchivosExc;
import com.famsa.exceptions.ProcessFileCtrlExc;
import com.famsa.fabricas.BaseDatosFactory;
import com.famsa.interfaces.IBaseDatosConexion;

public class BuscaArchivos {
	
	static final Logger logBuscaArchivos = Logger.getLogger(BuscaArchivos.class.getName());
	static FileHandler fhBuscaArchivos;
	static Configuracion configuracion = null;
	static String paramArchivoXML;
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
    		paramArchivoXML=args[0];
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
        	Archivos archivos = null;
        	
        	archivos=marshalListToXMLFile(treeMap);
			BuscaArchivos.guardaArchivo(archivos);
			BuscaArchivos.mueveFile(archivos);
        } else {
        	logBuscaArchivos.log(Level.INFO, "NO EXISTEN ARCHIVOS PARA PROCESAR.");
        }
	}

    private static File[] getFileList(String dirPath) {
        File dir = new File(dirPath);
        return dir.listFiles();
    }

    public static Archivos marshalListToXMLFile(Map<FileTime, String> map) throws BuscaArchivosExc {
    	
    	Archivos archivosEnCarpeta = new Archivos();
    	archivosEnCarpeta.setListArchivo(new ArrayList<Archivo>());
    	
    	Set<Entry<FileTime, String>> s = map.entrySet();
		Iterator<Entry<FileTime, String>> it = s.iterator();
		
		while ( it.hasNext() ) {
			//	datos principales
			Entry<FileTime, String> entry = it.next();
			FileTime key = entry.getKey();
			String value = entry.getValue();
			String keyStr = String.valueOf(key);
			
			Archivo unArchivo = new Archivo();
			unArchivo.setCreationTime(keyStr);
			unArchivo.setFilePath(value);
			unArchivo.setUuid(UUID.randomUUID().toString().toUpperCase());
			
			//	datos complementarios
			String path = value.substring(0, value.lastIndexOf('\\'));
			Path p = Paths.get(value);
			String fileName = p.getFileName().toString().substring(0, p.getFileName().toString().lastIndexOf('.'));
			String ext = p.getFileName().toString().substring(
					p.getFileName().toString().lastIndexOf('.') + 1, p.getFileName().toString().length());
		
			unArchivo.setPath(path);
			unArchivo.setImageFileName(fileName);
			unArchivo.setExtension(ext);
			unArchivo.setXmlArchivo(paramArchivoXML);
		
			//	obtengo el hash
	        MessageDigest md;
			try {
				md = MessageDigest.getInstance("SHA-256");	// SHA, MD2, MD5, SHA-256, SHA-384...
			} catch (NoSuchAlgorithmException e) {
				throw new BuscaArchivosExc(e.toString(), e);
			}	
		
			String hex;
			hex = checksum(value, md);
			unArchivo.setHash(hex);
			
			archivosEnCarpeta.getListArchivo().add(unArchivo);
        }

		archivoXML = configuracion.getFolder().getEncontrados()+paramArchivoXML;
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
    	
    	return archivosEnCarpeta;
    }
    
    private static String checksum(String filepath, MessageDigest md) throws BuscaArchivosExc {
    	
        // file hashing with DigestInputStream
        try (DigestInputStream dis = new DigestInputStream(new FileInputStream(filepath), md)) {
            while (dis.read() != -1) ; //empty loop to clear the data
            md = dis.getMessageDigest();
        } catch (IOException e) {
        	throw new BuscaArchivosExc(e.toString(), e);
		}
        // bytes to hex
        StringBuilder result = new StringBuilder();
        for (byte b : md.digest()) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    private static void guardaArchivo(Archivos myArchivos) throws BuscaArchivosExc {
    	
    	IBaseDatosConexion baseDatosConexion = BaseDatosFactory.getBaseDatosConexion(BDEnum.BD_SQL_SERVER);
    	try (Connection conn = baseDatosConexion.getConnection()) {
			String sql = "{ call [PROMADM].[dbo].[SP_PROCESS_FILE] ( ? , ? , ? , ? , ? , ? , ? , ? , ? , ? ) }";
			
			for(int i=0;i<myArchivos.getListArchivo().size();i++) {
				try (CallableStatement cstmt = conn.prepareCall(sql)) {
					cstmt.setString(1, myArchivos.getListArchivo().get(i).getXmlArchivo());
					cstmt.setString(2, myArchivos.getListArchivo().get(i).getCreationTime());
					cstmt.setString(3, myArchivos.getListArchivo().get(i).getFilePath());
					cstmt.setString(4, myArchivos.getListArchivo().get(i).getUuid());
					cstmt.setString(5, myArchivos.getListArchivo().get(i).getPath());
					cstmt.setString(6, myArchivos.getListArchivo().get(i).getImageFileName());
					cstmt.setString(7, myArchivos.getListArchivo().get(i).getExtension());
					cstmt.setString(8, myArchivos.getListArchivo().get(i).getHash());
					
					cstmt.registerOutParameter(9, java.sql.Types.INTEGER);
					cstmt.registerOutParameter(10, java.sql.Types.VARCHAR);
				
					cstmt.executeUpdate();
				}

				msg = String.format("UUID:%s   Archivo:%s",
						myArchivos.getListArchivo().get(i).getUuid(),
						myArchivos.getListArchivo().get(i).getImageFileName()+"."+
						myArchivos.getListArchivo().get(i).getExtension());
				logBuscaArchivos.log(Level.INFO, msg);
			}
		} catch (Exception e) {
			throw new BuscaArchivosExc(e.toString(), e);
		}

	}
	
    private static void mueveFile(Archivos myArchivos) throws BuscaArchivosExc {
    	for(int i=0;i<myArchivos.getListArchivo().size();i++) {
        	Path origenPath = null; 
        	Path destinoPath = null;

    		String dirDestino = 
    				configuracion.getFolder().getTemporal()+
    				paramArchivoXML.substring(0, paramArchivoXML.lastIndexOf('.'))+'\\'+
    				myArchivos.getListArchivo().get(i).getUuid()+'\\'+
    				myArchivos.getListArchivo().get(i).getImageFileName()+"."+
    				myArchivos.getListArchivo().get(i).getExtension();
    		
    		origenPath = FileSystems.getDefault().getPath(myArchivos.getListArchivo().get(i).getFilePath());
    		destinoPath = FileSystems.getDefault().getPath(dirDestino);
    		
    		File directorio = new File(dirDestino);
    		directorio.mkdirs();
    		
    		try {
    			Files.move(origenPath, destinoPath, StandardCopyOption.REPLACE_EXISTING);
    		} catch (IOException e) {
    			throw new BuscaArchivosExc(e.toString(), e);
    		}
    	}
    }

}
