package com.famsa.controlador;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.famsa.aplicacion.XCapture;
import com.famsa.exceptions.XCaptureExcep;
import com.famsa.interfaces.ISepara;
import com.famsa.xmlmarshall.Archivo;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;
import com.sun.media.jai.codec.TIFFEncodeParam;

public class Separa implements ISepara {

	private static final Logger logger = Logger.getLogger(XCapture.class.getName());
	Archivo archivo = null;
	String msg = null;
	
	@Override
	public Archivo buildI(Archivo arch) throws XCaptureExcep {	

		archivo=arch;
		
		archivo.setNumeroPaginas(obtenerNumeroDePaginas(archivo.getFilePathCopy()));
		msg = String.format("%s   Numero de paginas: %d", 
				archivo.getThreadName(),archivo.getNumeroPaginas());
		logger.log(Level.INFO, msg);
		
		for (int i=0;i<archivo.getNumeroPaginas();i++) {

			Date fecIni = new Date();
			
			RenderedImage renderedImage=readOnePageOfTiff(archivo.getFilePathCopy(), i);

			String[] tokens = archivo.getNomArch().split("\\.(?=[^\\.]+$)");
			String nuevoArchivo = archivo.getPathCopy()+tokens[0]+"-"+(i+1)+".ant";

			guardaComoTIFF(renderedImage, nuevoArchivo);
			
			/* esto es una prueba para medir el tiempo que transcurre*/
			Date fecFin = new Date();
			
			Format formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String s1 = formatter1.format(fecIni);
			Format formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String s2 = formatter2.format(fecFin);
			msg = String.format("Fecha Inicial:%s   Fecha Final:%s", s1, s2);
		}
		return archivo;
	}
	
	private int obtenerNumeroDePaginas(String filename) throws XCaptureExcep {
		int numPages = -1;
		File file = new File(filename);
		SeekableStream ss = null;
		try {
			ss = new FileSeekableStream(file);
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.toString(), e);
			throw new XCaptureExcep(e.toString(), e);
		}
		TIFFDecodeParam decodeParam = new TIFFDecodeParam();
		decodeParam.setDecodePaletteAsShorts(true);
		ImageDecoder decoder = ImageCodec.createImageDecoder("tiff", ss, null);
		try {
			numPages = decoder.getNumPages();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.toString(), e);
			throw new XCaptureExcep(e.toString(), e);
		}
		try {
			ss.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.toString(), e);
			throw new XCaptureExcep(e.toString(), e);
		}
		return numPages;
	}

	private RenderedImage readOnePageOfTiff (String filename, int paginaIndividual) throws XCaptureExcep {
		File file = new File(filename);
		SeekableStream ss = null;
		ImageDecoder decoder = null;
		RenderedImage rImage = null;
		try {
			ss = new FileSeekableStream(file);
			decoder = ImageCodec.createImageDecoder("tiff", ss, null);
			rImage = decoder.decodeAsRenderedImage(paginaIndividual);
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.toString(), e);
			throw new XCaptureExcep(e.toString(), e);
		}
		return rImage;	
	}
	
	private void guardaComoTIFF (RenderedImage image, String file) throws XCaptureExcep {
		String filename = file;
		OutputStream out = null;
		try {
			out = new FileOutputStream(filename);
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, e.toString(), e);
			throw new XCaptureExcep(e.toString(), e);
		}
		TIFFEncodeParam param = new TIFFEncodeParam();
		ImageEncoder encoder = ImageCodec.createImageEncoder("tiff", out, param);
		try {
			encoder.encode(image);
			out.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.toString(), e);
			throw new XCaptureExcep(e.toString(), e);
		}
	}

}
