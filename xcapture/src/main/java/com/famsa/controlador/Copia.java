package com.famsa.controlador;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.famsa.aplicacion.XCapture;
import com.famsa.exceptions.XCaptureExcep;
import com.famsa.fabricas.ConfigFactory;
import com.famsa.interfaces.IConfig;
import com.famsa.interfaces.ICopia;
import com.famsa.xmlmarshall.Archivo;
import com.famsa.xmlmarshall.Config;

public class Copia implements ICopia {

	private static final Logger logger = Logger.getLogger(XCapture.class.getName());
	static final String DOBLE_DIAGONAL = "\\";
	Config config = null;
	String msg = null;
	
	@Override
	public Archivo buildI(Archivo archivo) throws XCaptureExcep {
		
		obtenerConfiguracion();
		
		File inFile = new File(archivo.getFilePath());
		
		String extension = "";
		int i = archivo.getFilePath().lastIndexOf('.');
		int p = Math.max(
				archivo.getFilePath().lastIndexOf('/'),archivo.getFilePath().lastIndexOf(DOBLE_DIAGONAL));
		if (i > p) {
		    extension = archivo.getFilePath().substring(i+1);
		}

		archivo.setExtension(extension);
		String outFile = null;
		archivo.setNomArch(
				inFile.getAbsolutePath().substring(inFile.getAbsolutePath().lastIndexOf(DOBLE_DIAGONAL)+1));
		
		if (extension.equals(config.getMonitor().getExtensionImagen())) {
			
			String pathTif = String.format("%s", 
					config.getFolder().getImagenTif().replace("%BC(1)%", archivo.getUuid()));
			
			creaCarpeta(pathTif);
			
			outFile = pathTif+archivo.getNomArch();
			archivo.setFilePathCopy(outFile);
			archivo.setPathCopy(pathTif);
			archivo.setPathTif(pathTif);
		} else {
			
			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String pathOtro = String.format("%s", 
					config.getFolder().getImagenDifTif().replace("%BC(1)%", dateFormat.format(date)));
			
			creaCarpeta(pathOtro);
			
			outFile = pathOtro+archivo.getNomArch();
			archivo.setPathOtro(pathOtro);
			
			msg = String.format("%s   ExtensiÃ³n diferente a %s -> %s [%s]",
					archivo.getThreadName(),
					config.getMonitor().getExtensionImagen(),
					extension,
					archivo.getNomArch() );
			logger.log(Level.INFO, msg);
		}

		Path source  = Paths.get(archivo.getFilePath());
		Path target = Paths.get(outFile);

		try {
		    Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
		} catch(FileAlreadyExistsException fae) {
			logger.log(Level.SEVERE, fae.toString(), fae);
		    throw new XCaptureExcep(fae.toString(), fae);
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.toString(), e);
			throw new XCaptureExcep(e.toString(), e);
		}
		
		return archivo;
	}

	private synchronized void obtenerConfiguracion() {
		IConfig iConfig = ConfigFactory.getConfig();
		config = iConfig.getIConfig();
	}

	private void creaCarpeta(String carpeta) {
    	File directory = new File(carpeta);
		if (! directory.exists()) {
			directory.mkdirs();
		}		
	}
	
}
