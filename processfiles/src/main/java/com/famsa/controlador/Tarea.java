package com.famsa.controlador;

import java.io.File;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.concurrent.Callable;

import com.famsa.bean.Archivo;
import com.famsa.bean.Configuracion;
import com.famsa.enums.BDEnum;
import com.famsa.exceptions.ProcessFileCtrlExc;
import com.famsa.exceptions.TareaExc;
import com.famsa.fabricas.BaseDatosFactory;
import com.famsa.interfaces.IBaseDatosConexion;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;

public class Tarea implements Callable<String> {

	static Configuracion configuracion = null;
	static String msg = null;
	
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
			throw new TareaExc(e1.toString(), e1);
		}
		
		try {
			numeroDePaginas(archivoProceso);
		} catch (TareaExc e) {
			throw new TareaExc(e.toString(), e);
		}
		
	}
	
	private static void obtenerConfiguracion() throws TareaExc {
		ProcessFileCtrl cfg = new ProcessFileCtrl();
		try {
			configuracion = cfg.findConfiguration();
		} catch (ProcessFileCtrlExc e) {
			throw new TareaExc(e.toString(), e);
		}
	}
	
	private static void numeroDePaginas(Archivo archivoNumeroDePaginas) throws TareaExc {
		/*
		 * construye el file path de la nueva ubicacion del archivo "original"
		 */
		String nuevaUbicacion = 
			configuracion.getFolder().getTemporal()+
			archivoNumeroDePaginas.getXmlArchivo().substring(0, archivoNumeroDePaginas.getXmlArchivo().lastIndexOf('.'))+"\\"+
			archivoNumeroDePaginas.getUuid()+"\\"+
			archivoNumeroDePaginas.getImageFileName();		
		
		int numPages = -1;
		File file = new File(nuevaUbicacion);
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

		/*
		 * actualiza informacion en la bade de datos
		 */
		archivoNumeroDePaginas.setNumeroPaginas(numPages);
		
		try {
			Tarea.estatusEnProceso(archivoNumeroDePaginas);
		} catch (TareaExc e) {
			throw new TareaExc(e.toString(), e);
		}
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
			throw new TareaExc("#"+e.toString(), e);
		}

	}
}
