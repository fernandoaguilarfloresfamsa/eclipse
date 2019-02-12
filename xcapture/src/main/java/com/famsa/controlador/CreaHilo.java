package com.famsa.controlador;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.famsa.aplicacion.XCapture;
import com.famsa.enums.EstadoEnum;
import com.famsa.exceptions.XCaptureExcep;
import com.famsa.xmlmarshall.Archivo;
import com.famsa.xmlmarshall.Archivos;

public class CreaHilo {

	private static final Logger logger = Logger.getLogger(XCapture.class.getName());
	Archivos archivos = null;
	int numHilos = 0;
	boolean continuar = false;
	List<Future<String>> list = new ArrayList<>();

	public void proceso(int numeroDeHilos, Archivos arch) throws XCaptureExcep {
		
		numHilos = numeroDeHilos;
		archivos=arch;
		continuar=true;
		
		ExecutorService executor = Executors.newFixedThreadPool(numHilos);
		
		while(continuar) {

			Archivo archivo = getPropertiesFromList();
			if (archivo.getLastAccessTime()==null) {
				continuar=false;
			} else {
				Future<String> future = executor.submit(new Tarea(archivo));
				list.add(future);
			}
		}
		
		for(Future<String> fut : list){
			try {
				String msg = String.format("Se libero %s", new Date()+ "::"+fut.get());
				logger.info(msg);
			} catch (InterruptedException e) {
				logger.log(Level.WARNING, e.toString(), e);
				Thread.currentThread().interrupt();
			} catch (ExecutionException e) {
				logger.log(Level.SEVERE, e.toString(), e);
				throw new XCaptureExcep(e.toString(), e);
			}
        }
		
		executor.shutdown();
		logger.info("executor.shutdown()");
	}

	public Archivo getPropertiesFromList() {
		
		Archivo resultado = new Archivo();
		
		resultado.setLastAccessTime(null);
		resultado.setFilePath(null);
		resultado.setFilePathXML(null);
		resultado.setUuid(null);
		resultado.setFilePathCopy(null);
		resultado.setPathCopy(null);
		resultado.setThreadName(null);
		resultado.setExtension(null);
		resultado.setNumeroPaginas(0);
		resultado.setExisteDesTif(false);
		resultado.setPathTif(null);
		resultado.setExisteDesOtr(false);
		resultado.setPathOtro(null);
		resultado.setNomArch(null);
		resultado.setEstado(null);
		
		for (int i=0;i<archivos.getListArchivo().size();i++) {
			if (archivos.getListArchivo().get(i).getEstado()==EstadoEnum.AC) {
				
				resultado.setLastAccessTime(archivos.getListArchivo().get(i).getLastAccessTime());
				resultado.setFilePath(archivos.getListArchivo().get(i).getFilePath());
				resultado.setFilePathXML(archivos.getListArchivo().get(i).getFilePathXML());
				resultado.setUuid(archivos.getListArchivo().get(i).getUuid());
				resultado.setFilePathCopy(null);
				resultado.setPathCopy(null);
				resultado.setThreadName(null);
				resultado.setExtension(null);
				resultado.setNumeroPaginas(0);
				resultado.setExisteDesTif(false);
				resultado.setPathTif(null);
				resultado.setExisteDesOtr(false);
				resultado.setPathOtro(null);
				resultado.setNomArch(null);
				resultado.setEstado(null);
				
				archivos.getListArchivo().get(i).setEstado(EstadoEnum.PR);
				
				break;
			}
		}
		return resultado;
	}
}
