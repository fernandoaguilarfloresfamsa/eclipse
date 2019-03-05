package com.famsa.aplicacion;

import java.io.File;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.famsa.bean.Archivo;
import com.famsa.bean.Archivos;
import com.famsa.bean.Configuracion;
import com.famsa.controlador.ProcessFileCtrl;
import com.famsa.controlador.Tarea;
import com.famsa.enums.BDEnum;
import com.famsa.exceptions.CreateThreadExc;
import com.famsa.exceptions.ProcessFileCtrlExc;
import com.famsa.fabricas.BaseDatosFactory;
import com.famsa.interfaces.IBaseDatosConexion;

public class CreateThread {

	static final Logger logCreateThread = Logger.getLogger(CreateThread.class.getName());
	static FileHandler fhCreateThread;
	static Configuracion configuracion = null;
	static String archivoXML;
	static String msg = null;

	public static void main(String[] args) throws CreateThreadExc {
		try {
			CreateThread.inicio();
		} catch (CreateThreadExc e1) {
			throw new CreateThreadExc(e1.toString(), e1);
		}
    	
		Archivos listArchivos = new Archivos();
    	try {
    		listArchivos=CreateThread.datosDetalle();
		} catch (CreateThreadExc e1) {
			throw new CreateThreadExc(e1.toString(),e1);
		}
    	
    	if(listArchivos.getListArchivo().isEmpty()) {
    		logCreateThread.log(Level.SEVERE,"NO EXISTEN ARCHIVOS PARA PROCESAR.");
        	throw new CreateThreadExc("NO EXISTEN ARCHIVOS PARA PROCESAR.");
    	}
    	
    	try {
			CreateThread.proceso(listArchivos);
		} catch (CreateThreadExc e) {
			throw new CreateThreadExc(e.toString(),e);
		}
	}

	private static void inicio() throws CreateThreadExc {

		try {
			obtenerConfiguracion();
		} catch (CreateThreadExc e1) {
			throw new CreateThreadExc(e1.toString(), e1);
		}
		
		creaCarpeta(configuracion.getHilo().getPathLogErr());
		creaCarpeta(configuracion.getFolder().getEncontrados());
		creaCarpeta(configuracion.getFolder().getTemporal());

		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS-");
		String nomArc = String.format("%s%s%s.log", 
				configuracion.getHilo().getPathLogErr(), dateFormat.format(date), 
				CreateThread.class.getName());
		try {
			fhCreateThread = new FileHandler(nomArc);
		} catch (SecurityException | IOException e) {
			throw new CreateThreadExc(e.toString(), e);
		}
		logCreateThread.addHandler(fhCreateThread);
		SimpleFormatter formatter = new SimpleFormatter();
		fhCreateThread.setFormatter(formatter);
		fhCreateThread.setLevel(Level.ALL);
		logCreateThread.setLevel(Level.ALL);		
	}
	
	private static void obtenerConfiguracion() throws CreateThreadExc {
		ProcessFileCtrl cfg = new ProcessFileCtrl();
		try {
			configuracion = cfg.findConfiguration();
		} catch (ProcessFileCtrlExc e) {
			throw new CreateThreadExc(e.toString(), e);
		}
	}
	
	private static void creaCarpeta(String carpeta) {
    	File directory = new File(carpeta);
		if (! directory.exists()) {
			directory.mkdirs();
		}		
	}
	
	private static Archivos datosDetalle() throws CreateThreadExc {
		Archivos lista = new Archivos();
		lista.setListArchivo(new ArrayList<Archivo>());
		
		IBaseDatosConexion baseDatosConexion = BaseDatosFactory.getBaseDatosConexion(BDEnum.BD_SQL_SERVER);
		try (Connection conn = baseDatosConexion.getConnection()) {
			String sql = "{ call [PROMADM].[dbo].[SP_PROCESS_FILE_DATOS_DETALLE] ( ? , ? ) }";
			
			try (CallableStatement cstmt = conn.prepareCall(sql)) {

				cstmt.registerOutParameter(1, java.sql.Types.INTEGER);
				cstmt.registerOutParameter(2, java.sql.Types.VARCHAR);
				
				try (ResultSet resultSet = cstmt.executeQuery()) {
					while(resultSet.next()) {
						Archivo unArchivo = new Archivo();
						unArchivo.setId(			resultSet.getInt(	"ID"));
						unArchivo.setXmlArchivo(	resultSet.getString("XML_FILE_NAME"));
						unArchivo.setImageFileName(	resultSet.getString("IMAGE_FILE_NAME"));
						unArchivo.setPath(			resultSet.getString("FILE_PATH"));
						unArchivo.setExtension(		resultSet.getString("EXTENSION"));
						unArchivo.setHash(			resultSet.getString("HASH"));
						unArchivo.setCreationTime(	resultSet.getString("CREATION_TIME"));
						unArchivo.setFilePath(		resultSet.getString("FILE_PATH")+"\\"+resultSet.getString("IMAGE_FILE_NAME"));
						unArchivo.setUuid(			resultSet.getString("UUID"));
						lista.getListArchivo().add(unArchivo);
					}
				}
			}
		} catch (Exception e) {
			throw new CreateThreadExc(e.toString(), e);
		}
		return lista;
	}
	
	private static void proceso(Archivos listaArchivos) throws CreateThreadExc {
		
		List<Future<String>> listFuture = new ArrayList<>();
		
		ExecutorService executor = Executors.newFixedThreadPool(listaArchivos.getListArchivo().size());
		for(int i=0;i<listaArchivos.getListArchivo().size();i++) {
			Future<String> future = executor.submit(new Tarea(listaArchivos.getListArchivo().get(i)));
			listFuture.add(future);
		}
		
		for(Future<String> fut : listFuture){
			try {
				String msg = String.format("%s",fut.get());
				logCreateThread.info(msg);
			} catch (InterruptedException e) {
				logCreateThread.log(Level.WARNING, e.toString(), e);
				Thread.currentThread().interrupt();
			} catch (ExecutionException e) {
				throw new CreateThreadExc(e.toString(), e);
			}
        }
		
		executor.shutdown();
		logCreateThread.info("executor.shutdown()");
	}
}
