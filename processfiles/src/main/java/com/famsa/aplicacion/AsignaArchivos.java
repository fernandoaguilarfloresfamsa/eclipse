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
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.CallableStatement;
import java.sql.Connection;
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
import com.famsa.enums.BDEnum;
import com.famsa.exceptions.AsignaArchivosExc;
import com.famsa.exceptions.ProcessFileCtrlExc;
import com.famsa.fabricas.BaseDatosFactory;
import com.famsa.fabricas.ProcessFileFactory;
import com.famsa.interfaces.IBaseDatosConexion;
import com.famsa.interfaces.IProcessFile;

public class AsignaArchivos {

	static final Logger logAsignaArchivos = Logger.getLogger(AsignaArchivos.class.getName());
	static FileHandler fhAsignaArchivos;
	static Configuracion configuracion = null;
	static String archivoXML;
	static String msg = null;
	static Path origenPath = null; 
	static Path destinoPath = null;

	public static void main(String[] args) throws AsignaArchivosExc {
		try {
			AsignaArchivos.inicio();
		} catch (AsignaArchivosExc e1) {
			throw new AsignaArchivosExc(e1.toString(), e1);
		}
    	
    	String parametros = null;
    	for (String s: args) {
        	parametros = s;
        }

    	if (parametros==null) {
    		logAsignaArchivos.log(Level.SEVERE,"FALTA INFORMACION PARA CONTINUAR CON EL PROCESO.");
        	throw new AsignaArchivosExc("FALTA INFORMACION PARA CONTINUAR CON EL PROCESO.");
    	}

    	Archivos archivosParaProcesar = null;
    	archivoXML=args[0];
    	File f = new File(configuracion.getFolder().getEncontrados()+archivoXML);

    	if(f.exists() && !f.isDirectory()) {

			try {
				archivosParaProcesar = AsignaArchivos.unmarshalXMLToList();
			} catch (AsignaArchivosExc e) {
				logAsignaArchivos.log(Level.SEVERE, e.toString(), e);
				throw new AsignaArchivosExc(e.toString(), e);
			}
		}
        	
		if(archivosParaProcesar!=null && !archivosParaProcesar.getListArchivo().isEmpty()) {

			msg = String.format("Existen %d Archivo(s) para procesar: %s.", 
					archivosParaProcesar.getListArchivo().size(), archivoXML);
			logAsignaArchivos.log(Level.INFO,msg);

			for (int num=0;num<archivosParaProcesar.getListArchivo().size();num++) {
				
				ProcessFileBean resultado = null;
				archivosParaProcesar.getListArchivo().get(num).setXmlArchivo(archivoXML);
				try {
					msg = String.format("Procesando el archivo %s", 
							archivosParaProcesar.getListArchivo().get(num).getFilePath());
					logAsignaArchivos.log(Level.INFO, msg);

					IProcessFile loadWS = ProcessFileFactory.loadWebServices();
					resultado = loadWS.consumeWebService(archivosParaProcesar.getListArchivo().get(num));
					
					AsignaArchivos.mueveFile(resultado);
						
					 AsignaArchivos.guardaDatos(resultado);
							
				} catch (ProcessFileCtrlExc e) {
					logAsignaArchivos.log(Level.SEVERE, e.toString(), e);
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
		creaCarpeta(configuracion.getFolder().getTemporal());

		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS-");
		String nomArc = String.format("%s%s%s.log", 
				configuracion.getHilo().getPathLogErr(), dateFormat.format(date), 
				AsignaArchivos.class.getName());
		try {
			fhAsignaArchivos = new FileHandler(nomArc);
		} catch (SecurityException | IOException e) {
			throw new AsignaArchivosExc(e.toString(), e);
		}
		logAsignaArchivos.addHandler(fhAsignaArchivos);
		SimpleFormatter formatter = new SimpleFormatter();
		fhAsignaArchivos.setFormatter(formatter);
		fhAsignaArchivos.setLevel(Level.ALL);
		logAsignaArchivos.setLevel(Level.ALL);		
	}
	
	private static void obtenerConfiguracion() throws AsignaArchivosExc {
		IProcessFile config = ProcessFileFactory.buscaConfiguracion();
		try {
			configuracion = config.findConfiguration();
		} catch (ProcessFileCtrlExc e) {
			logAsignaArchivos.log(Level.SEVERE, e.toString(), e);
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
    			logAsignaArchivos.log(Level.SEVERE, e.toString(), e);
    			throw new AsignaArchivosExc(e.toString(), e);
    		}
            Unmarshaller jaxbUnmarshaller;
    		try {
    			jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    		} catch (JAXBException e) {
    			logAsignaArchivos.log(Level.SEVERE, e.toString(), e);
    			throw new AsignaArchivosExc(e.toString(), e);
    		}
            try {
    			archivosPendientes = (Archivos) jaxbUnmarshaller.unmarshal( new File(configuracion.getFolder().getEncontrados()+archivoXML));
    		} catch (JAXBException e) {
    			logAsignaArchivos.log(Level.SEVERE, e.toString(), e);
    			throw new AsignaArchivosExc(e.toString(), e);
    		}
    	}
    	return archivosPendientes;
    }

    public static void mueveFile(ProcessFileBean myResultado) throws AsignaArchivosExc {
    	
		String dirDestino = 
				configuracion.getFolder().getTemporal()+
				archivoXML.substring(0, archivoXML.lastIndexOf('.'))+'\\'+
				myResultado.getUuid()+'\\'+
				myResultado.getFile();
		
		origenPath = FileSystems.getDefault().getPath(myResultado.getFilePath());
		destinoPath = FileSystems.getDefault().getPath(dirDestino);
		
		File directorio = new File(dirDestino);
		directorio.mkdirs();
		
		try {
			Files.move(origenPath, destinoPath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			logAsignaArchivos.log(Level.SEVERE, e.toString(), e);
			throw new AsignaArchivosExc(e.toString(), e);
		}
    	
    }
    
    public static void guardaDatos(ProcessFileBean proFilBean) throws ProcessFileCtrlExc {
        
		int errorInt = 0;
		String errorMsg = null;
    	
    	IBaseDatosConexion baseDatosConexion = BaseDatosFactory.getBaseDatosConexion(BDEnum.BD_SQL_SERVER);
    	try (Connection conn = baseDatosConexion.getConnection()) {
			String sql = "{ call [PROMADM].[dbo].[SP_PROCESS_FILE] ( ? , ? , ? , ? , ? , ? , ? , ? , ? , ? ) }";
			
			try (CallableStatement cstmt = conn.prepareCall(sql)) {
				cstmt.setString(1, proFilBean.getXmlFileName());
				cstmt.setString(2, proFilBean.getCreationTime());
				cstmt.setString(3, proFilBean.getFilePath());
				cstmt.setString(4, proFilBean.getUuid());
				cstmt.setString(5, proFilBean.getPath());
				cstmt.setString(6, proFilBean.getFile());
				cstmt.setString(7, proFilBean.getExtension());
				cstmt.setString(8, proFilBean.getHash());
				
				cstmt.registerOutParameter(9, java.sql.Types.INTEGER);
				cstmt.registerOutParameter(10, java.sql.Types.VARCHAR);
			
				cstmt.executeUpdate();
				
				errorInt = cstmt.getInt(9);
				errorMsg = cstmt.getString(10);
				
				if (errorInt!=0) {
					proFilBean.setErrorInt(errorInt);
					proFilBean.setErrorMsg(errorMsg);
					
					proFilBean.setCreationTime(null);
					proFilBean.setExtension(null);
					proFilBean.setFile(null);
					proFilBean.setFilePath(null);
					proFilBean.setHash(null);
					proFilBean.setPath(null);
					proFilBean.setUuid(null);
					proFilBean.setXmlFileName(null);
					
					logAsignaArchivos.log(Level.SEVERE, errorMsg, new Object());
				}
			}
		} catch (Exception e) {
			logAsignaArchivos.log(Level.SEVERE, e.toString(), e);
			throw new ProcessFileCtrlExc("#"+e.toString(), e);
		}
    	
    }
    
}
