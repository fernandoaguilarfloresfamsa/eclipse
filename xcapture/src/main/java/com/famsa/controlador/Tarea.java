package com.famsa.controlador;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.famsa.aplicacion.XCapture;
import com.famsa.enums.ActualizaEnum;
import com.famsa.exceptions.XCaptureExcep;
import com.famsa.fabricas.ActualizaBDFactory;
import com.famsa.fabricas.ConfigFactory;
import com.famsa.fabricas.CopiaFactory;
import com.famsa.fabricas.EliminaFactory;
import com.famsa.fabricas.ProcesaFactory;
import com.famsa.interfaces.IActualizaBD;
import com.famsa.interfaces.IConfig;
import com.famsa.interfaces.ICopia;
import com.famsa.interfaces.IElimina;
import com.famsa.interfaces.IProcesa;
import com.famsa.xmlmarshall.Archivo;
import com.famsa.xmlmarshall.Config;

public class Tarea implements Callable<String> {
	
	private static final Logger logger = Logger.getLogger(XCapture.class.getName());
	Config config = null;
	String msg = null;
	
	private Archivo archivo;
	
	public Tarea(Archivo archivo) {
		super();
		this.archivo = archivo;
	}

	public Archivo getArchivo() {
		return archivo;
	}

	public void setArchivo(Archivo archivo) {
		this.archivo = archivo;
	}

	@Override
	public String call() throws Exception {
		
		getArchivo().setThreadName(Thread.currentThread().getName());
		
		String[] tokens = archivo.getFilePath().split("\\\\(?=[^\\\\]+$)");
		msg = String.format("%s - archivo: %s", archivo.getThreadName(), tokens[1]);
		logger.log(Level.INFO,msg);
		
		try {
			obtenerConfiguracion();
			
			guardaInicioCopia();
			Thread.sleep(10);
			
			copiarArchivo();
			Thread.sleep(10);
			eliminarArchivo();
			Thread.sleep(10);

			procesarImagen();
			Thread.sleep(10);
			
			guardaContenidoImagen();
			Thread.sleep(10);
			guardaTerminaCopia();
			Thread.sleep(10);
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, e.toString(), e);
			Thread.currentThread().interrupt();
		} catch (XCaptureExcep e) {
			Thread.currentThread().interrupt();
			logger.log(Level.SEVERE, e.toString(), e);
		}
		
		return Thread.currentThread().getName();
	}
	
	private synchronized void obtenerConfiguracion() {
		IConfig iConfig = ConfigFactory.getConfig();
		config = iConfig.getIConfig();
	}

	private synchronized void guardaInicioCopia() {
		IActualizaBD iActualiza = ActualizaBDFactory.guarda(ActualizaEnum.ACT_INICIO);
		iActualiza.guardaBD(getArchivo());
	}
	
	private synchronized void guardaContenidoImagen() {
		IActualizaBD iActualiza = ActualizaBDFactory.guarda(ActualizaEnum.ACT_CONTENIDO_IMG);
		iActualiza.guardaBD(getArchivo());
	}

	private synchronized void guardaTerminaCopia() {
		IActualizaBD iActualiza = ActualizaBDFactory.guarda(ActualizaEnum.ACT_TERMINA_COPIA);
		iActualiza.guardaBD(getArchivo());
	}

	private synchronized void copiarArchivo() throws XCaptureExcep {
		ICopia iCopiaArchivo = CopiaFactory.buildInstance();
		try {
			Archivo arch = iCopiaArchivo.buildI(getArchivo());
			
			getArchivo().setExtension(arch.getExtension());
			getArchivo().setNomArch(arch.getNomArch());
			getArchivo().setFilePathCopy(arch.getFilePathCopy());
			getArchivo().setPathCopy(arch.getPathCopy());
			getArchivo().setPathTif(arch.getPathTif());
			getArchivo().setPathOtro(arch.getPathOtro());
		} catch (XCaptureExcep e) {
			logger.log(Level.SEVERE, e.toString(), e);
			throw new XCaptureExcep(e.toString(), e);
		}
	}

	private synchronized void eliminarArchivo() throws XCaptureExcep {
		IElimina iEliminaArchivo = EliminaFactory.delFileEntradaInstance();
		try {
			iEliminaArchivo.delFileEntrada(getArchivo());
		} catch (XCaptureExcep e) {
			logger.log(Level.SEVERE, e.toString(), e);
			throw new XCaptureExcep(e.toString(), e);
		}
	}
	
	private synchronized void procesarImagen() throws XCaptureExcep {
		IProcesa iProcesaImagen = ProcesaFactory.buildInstance();
			Archivo arch;
			arch = iProcesaImagen.buildI(getArchivo());
			getArchivo().setNumeroPaginas(arch.getNumeroPaginas());
			
	}
}
