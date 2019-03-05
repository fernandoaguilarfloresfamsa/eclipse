package com.famsa.controlador;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.famsa.aplicacion.CreateThread;
import com.famsa.bean.Archivo;
import com.famsa.bean.Configuracion;
import com.famsa.exceptions.ProcessFileCtrlExc;
import com.famsa.exceptions.TareaExc;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;

public class Tarea implements Callable<String> {

	private static final Logger logTarea = Logger.getLogger(CreateThread.class.getName());
	static Configuracion configuracion = null;
	static String msg = null;
	
	private Archivo pfHalf;

	public Tarea(Archivo pfHalf) {
		super();
		this.pfHalf = pfHalf;
	}
	public Archivo getPfHalf() {
		return pfHalf;
	}
	public void setPfHalf(Archivo pfHalf) {
		this.pfHalf = pfHalf;
	}

	@Override
	public String call() throws Exception {

		String nombreTarea = Thread.currentThread().getName();
		
		getPfHalf().setThreadName(nombreTarea);
		Tarea.proceso(getPfHalf());
		
		String indice = String.valueOf(getPfHalf().getId());
		return nombreTarea+";"+indice;
	}
	
	private static void proceso(Archivo pfH) throws TareaExc {
		try {
			obtenerConfiguracion();
		} catch (TareaExc e1) {
			throw new TareaExc(e1.toString(), e1);
		}
		
		int numPag = 0;
		try {
			numPag = obtenerNumeroDePaginas(
					configuracion.getFolder().getTemporal()+
					pfH.getXmlArchivo().substring(0, pfH.getXmlArchivo().lastIndexOf('.'))+"\\"+
					pfH.getUuid()+"\\"+
					pfH.getImageFileName());
		} catch (TareaExc e) {
			throw new TareaExc(e.toString(), e);
		}
		msg = String.format("Numero de paginas:%d", numPag);
		logTarea.log(Level.INFO, msg);
		
		pfH.setNumeroPaginas(numPag);
		
	}
	
	private static void obtenerConfiguracion() throws TareaExc {
		ProcessFileCtrl cfg = new ProcessFileCtrl();
		try {
			configuracion = cfg.findConfiguration();
		} catch (ProcessFileCtrlExc e) {
			throw new TareaExc(e.toString(), e);
		}
	}
	
	private static int obtenerNumeroDePaginas(String filename) throws TareaExc {
		int numPages = -1;
		File file = new File(filename);
		SeekableStream ss = null;
		try {
			ss = new FileSeekableStream(file);
		} catch (IOException e) {
			throw new TareaExc(e.toString(), e);
		}
		TIFFDecodeParam decodeParam = new TIFFDecodeParam();
		decodeParam.setDecodePaletteAsShorts(true);
		ImageDecoder decoder = ImageCodec.createImageDecoder("tiff", ss, null);
		try {
			numPages = decoder.getNumPages();
		} catch (IOException e) {
			throw new TareaExc(e.toString(), e);
		}
		try {
			ss.close();
		} catch (IOException e) {
			throw new TareaExc(e.toString(), e);
		}
		return numPages;
	}
	
	
}
