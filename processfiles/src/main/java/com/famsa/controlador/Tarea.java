package com.famsa.controlador;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.famsa.aplicacion.CreateThread;
import com.famsa.bean.Configuracion;
import com.famsa.bean.PbProcessFilesHalf;
import com.famsa.exceptions.ProcessFileCtrlExc;
import com.famsa.exceptions.TareaExc;
import com.famsa.fabricas.ProcessFileFactory;
import com.famsa.interfaces.IProcessFile;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;

public class Tarea implements Callable<String> {

	private static final Logger logTarea = Logger.getLogger(CreateThread.class.getName());
	static Configuracion configuracion = null;
	static String msg = null;
	
	private PbProcessFilesHalf pfHalf;

	public Tarea(PbProcessFilesHalf pfHalf) {
		super();
		this.pfHalf = pfHalf;
	}
	public PbProcessFilesHalf getPfHalf() {
		return pfHalf;
	}
	public void setPfHalf(PbProcessFilesHalf pfHalf) {
		this.pfHalf = pfHalf;
	}

	@Override
	public String call() throws Exception {
		
		Tarea.proceso(getPfHalf());
		
		String indice = String.valueOf(getPfHalf().getId());
		return Thread.currentThread().getName()+";"+indice;
	}
	
	private static void proceso(PbProcessFilesHalf pfH) throws TareaExc {
		try {
			obtenerConfiguracion();
		} catch (TareaExc e1) {
			logTarea.log(Level.SEVERE, e1.toString(), e1);
			throw new TareaExc(e1.toString(), e1);
		}
		
		int numPag = 0;
		try {
			numPag = obtenerNumeroDePaginas(
					configuracion.getFolder().getTemporal()+
					pfH.getXmlFileName().substring(0, pfH.getXmlFileName().lastIndexOf('.'))+"\\"+
					pfH.getUuid()+"\\"+
					pfH.getImageFileName());
		} catch (TareaExc e) {
			logTarea.log(Level.SEVERE, e.toString(), e);
			throw new TareaExc(e.toString(), e);
		}
		msg = String.format("Numero de paginas:%d", numPag);
		logTarea.log(Level.INFO, msg);
	}
	
	private static void obtenerConfiguracion() throws TareaExc {
		IProcessFile config = ProcessFileFactory.buscaConfiguracion();
		try {
			configuracion = config.findConfiguration();
		} catch (ProcessFileCtrlExc e) {
			logTarea.log(Level.SEVERE, e.toString(), e);
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
			logTarea.log(Level.SEVERE, e.toString(), e);
			throw new TareaExc(e.toString(), e);
		}
		TIFFDecodeParam decodeParam = new TIFFDecodeParam();
		decodeParam.setDecodePaletteAsShorts(true);
		ImageDecoder decoder = ImageCodec.createImageDecoder("tiff", ss, null);
		try {
			numPages = decoder.getNumPages();
		} catch (IOException e) {
			logTarea.log(Level.SEVERE, e.toString(), e);
			throw new TareaExc(e.toString(), e);
		}
		try {
			ss.close();
		} catch (IOException e) {
			logTarea.log(Level.SEVERE, e.toString(), e);
			throw new TareaExc(e.toString(), e);
		}
		return numPages;
	}
}
