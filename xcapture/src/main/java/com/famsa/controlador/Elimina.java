package com.famsa.controlador;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.famsa.aplicacion.XCapture;
import com.famsa.exceptions.XCaptureExcep;
import com.famsa.interfaces.IElimina;
import com.famsa.xmlmarshall.Archivo;

public class Elimina implements IElimina {

	private static final Logger logger = Logger.getLogger(XCapture.class.getName());
	String msg = null;
	
	@Override
	public Archivo delFileEntrada(Archivo archivo) throws XCaptureExcep {
		boolean existeOri=false;
		archivo.setExisteDesTif(false);
		archivo.setExisteDesOtr(false);
		
		File fo = new File(archivo.getFilePath());
		if(fo.exists() && !fo.isDirectory()) { 
			existeOri=true;
		}
		
		File fd1 = new File(archivo.getPathTif()+archivo.getNomArch());
		if(fd1.exists() && !fd1.isDirectory()) { 
			archivo.setExisteDesTif(true);
		}
		
		File fd2 = new File(archivo.getPathOtro()+archivo.getNomArch());
		if(fd2.exists() && !fd2.isDirectory()) { 
			archivo.setExisteDesOtr(true);
		}
		
		if (existeOri && (archivo.isExisteDesTif() || archivo.isExisteDesOtr())) {
			Path path = Paths.get(archivo.getFilePath());
			try {
			    Files.delete(path);
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.toString(), e);
				throw new XCaptureExcep(e.toString(), e);
			}
		}
		
		return archivo;
	}

	@Override
	public void delFileAnt(String nombreFile) throws XCaptureExcep {

		boolean existeOri=false;
		
		File fo = new File(nombreFile);
		if(fo.exists() && !fo.isDirectory()) { 
			existeOri=true;
		}

		if (existeOri) {
			Path path = Paths.get(nombreFile);
			try {
			    Files.delete(path);
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.toString(), e);
				throw new XCaptureExcep(e.toString(), e);
			}
		}
	}

}
