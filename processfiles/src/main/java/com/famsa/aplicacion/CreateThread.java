package com.famsa.aplicacion;

import java.io.File;
import java.io.IOException;
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
import com.famsa.bean.Configuracion;
import com.famsa.controlador.ProcessFileCtrl;
import com.famsa.controlador.Tarea;
import com.famsa.exceptions.CreateThreadExc;
import com.famsa.exceptions.ProcessFileCtrlExc;

public class CreateThread {

	static final Logger logCreateThread = Logger.getLogger(CreateThread.class.getName());
	static FileHandler fhCreateThread;
	static Configuracion configuracion = null;
	static String archivoXML;
	static String msg = null;
	static List<Archivo> listPF = new ArrayList<>();

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
    	

    	CreateThread.proceso();
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
	
	private static void proceso() throws CreateThreadExc {
		
		List<Future<String>> list = new ArrayList<>();
		
		ExecutorService executor = Executors.newFixedThreadPool(listPF.size());
		for(int i=0;i<listPF.size();i++) {
			Future<String> future = executor.submit(new Tarea(listPF.get(i)));
			list.add(future);
		}
		
		for(Future<String> fut : list){
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
