package com.famsa.aplicacion;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.famsa.bean.Configuracion;
import com.famsa.bean.PbProcessFilesHalf;
import com.famsa.exceptions.CreateThreadCtrlExc;
import com.famsa.exceptions.CreateThreadExc;
import com.famsa.exceptions.ProcessFileCtrlExc;
import com.famsa.fabricas.CreateThreadFactory;
import com.famsa.fabricas.ProcessFileFactory;
import com.famsa.interfaces.ICreateThread;
import com.famsa.interfaces.IProcessFile;

public class CreateThread {

	static final Logger logCreateThread = Logger.getLogger(CreateThread.class.getName());
	static FileHandler fhCreateThread;
	static Configuracion configuracion = null;
	static String archivoXML;
	static String msg = null;
	static List<PbProcessFilesHalf> listPF = new ArrayList<>();

	public static void main(String[] args) throws CreateThreadExc {
		try {
			CreateThread.inicio();
		} catch (CreateThreadExc e1) {
			throw new CreateThreadExc(e1.toString(), e1);
		}
    	
    	if (args.length!=2) {
    		logCreateThread.log(Level.SEVERE,"FALTA INFORMACION PARA CONTINUAR CON EL PROCESO.");
        	throw new CreateThreadExc("FALTA INFORMACION PARA CONTINUAR CON EL PROCESO.");
    	}
    	
    	for(int i=0;i<args.length;i++) {
    		
    		if ( !args[i].equals("-1")) {
	    		int id = Integer.parseInt(args[i]);
	
	        	ICreateThread loadWS = CreateThreadFactory.loadWebServicesDetalle();
	        	try {
	        		PbProcessFilesHalf resultado = loadWS.consumeWebServiceDetalle(id);
	        		listPF.add(resultado);
				} catch (CreateThreadCtrlExc e) {
					logCreateThread.log(Level.SEVERE, e.toString(), e);
					throw new CreateThreadExc(e.toString(), e);
				}
    		}
    		
    	}
    	
    	for(int i=0;i<listPF.size();i++) {
    		System.out.println("listPf:"+listPF.get(i));
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
		IProcessFile config = ProcessFileFactory.buscaConfiguracion();
		try {
			configuracion = config.findConfiguration();
		} catch (ProcessFileCtrlExc e) {
			logCreateThread.log(Level.SEVERE, e.toString(), e);
			throw new CreateThreadExc(e.toString(), e);
		}
	}
	
	private static void creaCarpeta(String carpeta) {
    	File directory = new File(carpeta);
		if (! directory.exists()) {
			directory.mkdirs();
		}		
	}
	
}