package com.famsa.controlador;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.famsa.aplicacion.ProcesaImagen;
import com.famsa.bean.Configuracion;
import com.famsa.exceptions.BuscaArchivosExc;
import com.famsa.interfaces.IBuscaArchivos;

public class BuscaArchivos implements IBuscaArchivos {

	static final Logger logger = Logger.getLogger(ProcesaImagen.class.getName());
	static FileHandler fileHandler;
	static Configuracion configuracion = null;
	static String archivoXML;
	static String msg = null;

	@Override
	public void find() throws BuscaArchivosExc {
		
		HashMap<FileTime, String> hmap = new HashMap<>();
		File[] fileList = getFileList(configuracion.getFolder().getEntrada());
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
					throw new BuscaArchivosExc(e.toString(), e);
				}
            }
        }
	}
	
    private static File[] getFileList(String dirPath) {
        File dir = new File(dirPath);
        return dir.listFiles();
    }
}
