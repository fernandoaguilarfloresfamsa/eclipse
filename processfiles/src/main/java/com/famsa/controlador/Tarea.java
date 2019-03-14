package com.famsa.controlador;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.Formatter;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.famsa.aplicacion.CreateThread;
import com.famsa.bean.Archivo;
import com.famsa.bean.Configuracion;
import com.famsa.enums.BDEnum;
import com.famsa.exceptions.ProcessFileCtrlExc;
import com.famsa.exceptions.TareaExc;
import com.famsa.fabricas.BaseDatosFactory;
import com.famsa.interfaces.IBaseDatosConexion;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;
import com.sun.media.jai.codec.TIFFEncodeParam;

public class Tarea implements Callable<String> {

	static final Logger loggerTarea = Logger.getLogger(CreateThread.class.getName());
	static Configuracion configuracion = null;
	static String msg = null;
	static String codeBar = null;
	
	private Archivo archivoTarea;

	public Tarea(Archivo archivoTarea) {
		super();
		this.archivoTarea = archivoTarea;
	}
	public Archivo getArchivoTarea() {
		return archivoTarea;
	}
	public void setArchivoTarea(Archivo archivoTarea) {
		this.archivoTarea = archivoTarea;
	}

	@Override
	public String call() throws Exception {

		String nombreTarea = Thread.currentThread().getName();
		
		getArchivoTarea().setThreadName(nombreTarea);
		Tarea.proceso(getArchivoTarea());
		
		String indice = String.valueOf(getArchivoTarea().getId());
		return nombreTarea+";"+indice;
	}
	
	private static void proceso(Archivo archivoProceso) throws TareaExc {
		try {
			obtenerConfiguracion();
		} catch (TareaExc e1) {
			loggerTarea.log(Level.INFO, e1.toString(), e1);
			throw new TareaExc(e1.toString(), e1);
		}
		
		int numPaginas;
		try {
			numPaginas=numeroDePaginas(archivoProceso);
		} catch (TareaExc e) {
			loggerTarea.log(Level.INFO, e.toString(), e);
			throw new TareaExc(e.toString(), e);
		}
		
		codeBar=null;
		boolean encontre = false;
		
		for (int i=0;i<numPaginas;i++) {
			RenderedImage renderedImage;
			try {
				renderedImage = readOnePageOfTiff(archivoProceso, i);
			} catch (TareaExc e) {
				loggerTarea.log(Level.INFO, e.toString(), e);
				throw new TareaExc(e.toString(), e);
			}
			
			String nomArc;
			try {
				nomArc = Tarea.guardaComoTIFF(renderedImage, archivoProceso, i);
			} catch (TareaExc e) {
				loggerTarea.log(Level.INFO, e.toString(), e);
				throw new TareaExc(e.toString(), e);
			}

			if (!encontre) {
				try {
					String resultado = buscaCodeBar(nomArc);
					if (resultado!=null) {
						encontre=true;
						codeBar = resultado;
					}
					
				} catch (TareaExc e) {
					loggerTarea.log(Level.INFO, e.toString(), e);
					throw new TareaExc(e.toString(), e);
				}
			}
		}
	}
	
	private static void obtenerConfiguracion() throws TareaExc {
		ProcessFileCtrl cfg = new ProcessFileCtrl();
		try {
			configuracion = cfg.findConfiguration();
		} catch (ProcessFileCtrlExc e) {
			loggerTarea.log(Level.INFO, e.toString(), e);
			throw new TareaExc(e.toString(), e);
		}
	}
	
	private static int numeroDePaginas(Archivo archivoNumeroDePaginas) throws TareaExc {
		/*
		 * construye el file path de la nueva ubicacion del archivo "original"
		 */
		String nuevaUbicacion = 
			configuracion.getFolder().getTemporal()+
			archivoNumeroDePaginas.getXmlArchivo().substring(0, archivoNumeroDePaginas.getXmlArchivo().lastIndexOf('.'))+"\\"+
			archivoNumeroDePaginas.getUuid()+"\\"+
			archivoNumeroDePaginas.getImageFileName()+"."+
			archivoNumeroDePaginas.getExtension();		
		
		int numPages = -1;
		File file = new File(nuevaUbicacion);
		SeekableStream ss = null;
		try {
			ss = new FileSeekableStream(file);
		} catch (IOException e) {
			loggerTarea.log(Level.INFO, e.toString(), e);
			throw new TareaExc(e.toString(), e);
		}
		TIFFDecodeParam decodeParam = new TIFFDecodeParam();
		decodeParam.setDecodePaletteAsShorts(true);
		ImageDecoder decoder = ImageCodec.createImageDecoder("tiff", ss, null);
		try {
			numPages = decoder.getNumPages();
		} catch (IOException e) {
			loggerTarea.log(Level.INFO, e.toString(), e);
			throw new TareaExc(e.toString(), e);
		}
		try {
			ss.close();
		} catch (IOException e) {
			loggerTarea.log(Level.INFO, e.toString(), e);
			throw new TareaExc(e.toString(), e);
		}

		/*
		 * actualiza informacion en la bade de datos
		 */
		archivoNumeroDePaginas.setNumeroPaginas(numPages);
		
		try {
			Tarea.estatusEnProceso(archivoNumeroDePaginas);
		} catch (TareaExc e) {
			loggerTarea.log(Level.INFO, e.toString(), e);
			throw new TareaExc(e.toString(), e);
		}
		
		return numPages;
	}
	
	private static void estatusEnProceso(Archivo archivo) throws TareaExc {
    	IBaseDatosConexion baseDatosConexion = BaseDatosFactory.getBaseDatosConexion(BDEnum.BD_SQL_SERVER);
    	try (Connection conn = baseDatosConexion.getConnection()) {
			String sql = "{ call [PROMADM].[dbo].[SP_PROCESS_FILE_EN_PROCESO] ( ? , ? , ? , ? , ? ) }";
			
			try (CallableStatement cstmt = conn.prepareCall(sql)) {
				cstmt.setInt(1, archivo.getId());
				cstmt.setString(2, archivo.getThreadName());
				cstmt.setInt(3, archivo.getNumeroPaginas());
				
				cstmt.registerOutParameter(4, java.sql.Types.INTEGER);
				cstmt.registerOutParameter(5, java.sql.Types.VARCHAR);
			
				cstmt.executeUpdate();
			}
		} catch (Exception e) {
			loggerTarea.log(Level.INFO, e.toString(), e);
			throw new TareaExc(e.toString(), e);
		}

	}
	
	private static RenderedImage readOnePageOfTiff (Archivo archivoReadOne, int paginaIndividual) throws TareaExc {
		/*
		 * construye el file path de la nueva ubicacion del archivo "original"
		 */
		String nuevaUbicacion = 
			configuracion.getFolder().getTemporal()+
			archivoReadOne.getXmlArchivo().substring(0, archivoReadOne.getXmlArchivo().lastIndexOf('.'))+"\\"+
			archivoReadOne.getUuid()+"\\"+
			archivoReadOne.getImageFileName()+"."+
			archivoReadOne.getExtension();		
		
		File file = new File(nuevaUbicacion);
		SeekableStream ss = null;
		ImageDecoder decoder = null;
		RenderedImage rImage = null;
		try {
			ss = new FileSeekableStream(file);
			decoder = ImageCodec.createImageDecoder("tiff", ss, null);
			rImage = decoder.decodeAsRenderedImage(paginaIndividual);
		} catch (IOException e) {
			loggerTarea.log(Level.INFO, e.toString(), e);
			throw new TareaExc(e.toString(), e);
		}
		return rImage;	
	}

	private static String guardaComoTIFF (RenderedImage image, Archivo archivoGuarda, int indice) throws TareaExc {
		
		Formatter fmt = new Formatter();
		fmt.format("%03d",(indice+1));
		
		String file = 
				configuracion.getFolder().getTemporal()+
				archivoGuarda.getXmlArchivo().substring(0, archivoGuarda.getXmlArchivo().lastIndexOf('.'))+"\\"+
				archivoGuarda.getUuid()+"\\"+
				archivoGuarda.getImageFileName()+"-"+fmt+".tif";	

		fmt.close();
		String filename = file;
		OutputStream out = null;
		try {
			out = new FileOutputStream(filename);
		} catch (FileNotFoundException e) {
			loggerTarea.log(Level.INFO, e.toString(), e);
			throw new TareaExc(e.toString(), e);
		}
		TIFFEncodeParam param = new TIFFEncodeParam();
		ImageEncoder encoder = ImageCodec.createImageEncoder("tiff", out, param);
		try {
			encoder.encode(image);
			out.close();
		} catch (IOException e) {
			loggerTarea.log(Level.INFO, e.toString(), e);
			throw new TareaExc(e.toString(), e);
		}
		
		return file;
	}
	
	private static String buscaCodeBar(String archivo) throws TareaExc {
		String resultado = null;
		InputStream barCodeInputStream;
		try {
			barCodeInputStream = new FileInputStream(archivo);
		} catch (FileNotFoundException e) {
			loggerTarea.log(Level.INFO, e.toString(), e);
			throw new TareaExc(e.toString(), e);
		}
		BufferedImage barCodeBufferedImage;
		BufferedImage cropedImage;
		try {
			barCodeBufferedImage = ImageIO.read(barCodeInputStream);
			cropedImage = barCodeBufferedImage.getSubimage(
					0, 0, barCodeBufferedImage.getWidth(), barCodeBufferedImage.getHeight()/2);
		} catch (IOException e) {
			loggerTarea.log(Level.INFO, e.toString(), e);
			throw new TareaExc(e.toString(), e);
		}
		LuminanceSource source = new BufferedImageLuminanceSource(cropedImage);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		Reader reader = new MultiFormatReader();
		try {
			resultado = reader.decode(bitmap).getText();
		} catch (NotFoundException | ChecksumException | FormatException e) {
			loggerTarea.log(Level.INFO, e.toString(), e);
			resultado = null;
		}
		try {
			barCodeInputStream.close();
		} catch (IOException e) {
			loggerTarea.log(Level.INFO, e.toString(), e);
			throw new TareaExc(e.toString(), e);
		}
		return resultado;
	}
	

}
