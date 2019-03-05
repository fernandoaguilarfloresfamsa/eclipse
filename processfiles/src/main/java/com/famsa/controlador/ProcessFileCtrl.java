package com.famsa.controlador;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;

import com.famsa.aplicacion.BuscaArchivos;
import com.famsa.bean.Campo;
import com.famsa.bean.Campos;
import com.famsa.bean.ConexionBD;
import com.famsa.bean.Configuracion;
import com.famsa.bean.Folder;
import com.famsa.bean.Hilo;
import com.famsa.bean.Monitor;
import com.famsa.bean.Tabla;
import com.famsa.exceptions.ProcessFileCtrlExc;

public class ProcessFileCtrl {

	static final Logger logger = Logger.getLogger(BuscaArchivos.class.getName());
	private static final String FECHAARCHIVO = "FECHA_ARCHIVO";  
	private static final String CADENASPLIT = "\\\\(?=[^\\\\]+$)";
	private static final String CARPETACONFIG = "\\Expedientes\\Config\\";
	private static final String RUTATXT = "ruta.txt";
	private static final String XMLFILE = "config.xml";
	String nomArcConf = null;
	String nomCarpeta = null;
	String[] tokens;

	public Configuracion findConfiguration() throws ProcessFileCtrlExc {
		Configuracion configXML = new Configuracion();
		
		//	---------------------------------------------------------------------------------------
		File f = new File(RUTATXT);
		String cadena = f.getAbsolutePath();

		tokens = cadena.split(CADENASPLIT);
		nomCarpeta = tokens[0]+CARPETACONFIG;
		nomArcConf = nomCarpeta + XMLFILE;
		
		File carpeta = new File(nomCarpeta);
		if(!carpeta.exists()) { 
			creaCarpeta(nomCarpeta);
		}
		
		File archivo = new File(nomArcConf);
		if(!archivo.exists() && !archivo.isDirectory()) {
			try {
				creaArchivoXML();
			} catch (ProcessFileCtrlExc e) {
				throw new ProcessFileCtrlExc(e.toString(), e);
			}
		}
		
		//	---------------------------------------------------------------------------------------
		File fileConf = new File(nomArcConf);
    	if(fileConf.exists() && !fileConf.isDirectory()) {
            JAXBContext jaxbContext = null;
			try {
				jaxbContext = JAXBContext.newInstance(Configuracion.class);
			} catch (JAXBException e) {
				throw new ProcessFileCtrlExc(e.toString(), e);
			}
            Unmarshaller jaxbUnmarshaller = null;
			try {
				jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			} catch (JAXBException e) {
				throw new ProcessFileCtrlExc(e.toString(), e);
			}
			try {
				configXML = (Configuracion) jaxbUnmarshaller.unmarshal( new File(nomArcConf));
			} catch (JAXBException e) {
				throw new ProcessFileCtrlExc(e.toString(), e);
			}
    	}
    	
    	creaCarpeta(configXML.getFolder().getEntrada());
    	creaCarpeta(configXML.getFolder().getEncontrados());
    	creaCarpeta(configXML.getFolder().getTemporal());

		return configXML;
	}
	
	private void creaCarpeta(String carpeta) {
    	File directory = new File(carpeta);
		if (! directory.exists()) {
			directory.mkdirs();
		}		
		
	}

	private void creaArchivoXML() throws ProcessFileCtrlExc {
		File fileXML = new File(nomArcConf);
		if(!fileXML.exists()) { 
		    
			Configuracion configuracion = new Configuracion();
			configuracion.setHilo(creaHilo());
			configuracion.setConexion(creaConexion());
			configuracion.setMonitor(creaMonitor());
			configuracion.setFolder(creaFolder());
			configuracion.setAutorizacion(creaAutorizacion());
			configuracion.setPreautorizacion(creaPreAutorizacion());

			File file = new File(nomArcConf);
			JAXBContext jaxbContext = null;
			try {
				jaxbContext = JAXBContext.newInstance(Configuracion.class);
			} catch (JAXBException e) {
				throw new ProcessFileCtrlExc(e.toString(), e);
			}
			Marshaller jaxbMarshaller = null;
			try {
				jaxbMarshaller = jaxbContext.createMarshaller();
			} catch (JAXBException e) {
				throw new ProcessFileCtrlExc(e.toString(), e);
			}
			try {
				jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			} catch (PropertyException e) {
				throw new ProcessFileCtrlExc(e.toString(), e);
			}
			try {
				jaxbMarshaller.marshal(configuracion, file);
			} catch (JAXBException e) {
				throw new ProcessFileCtrlExc(e.toString(), e);
			}
		}
	}

	private Hilo creaHilo() {
		Hilo hilo = new Hilo();
		hilo.setMaximo(5);
		hilo.setPoolSegundos(15);
		hilo.setPorcentaje(0.45);
		hilo.setPathLogErr(getPathLogErr());
		
		return hilo;
	}

	private String getPathLogErr() {
		File f = new File(RUTATXT);
		String cadena = f.getAbsolutePath();

		tokens = cadena.split(CADENASPLIT);
		String directoryLogs = tokens[0]+"\\Expedientes\\LogsErr\\";
		File directoryLog = new File(directoryLogs);
		if (! directoryLog.exists()) {
			directoryLog.mkdirs();
		}
		
		return directoryLogs;
	}

	private ConexionBD creaConexion() {
		String[] octeto = {"192","168","56","101"};
		
		ConexionBD conexion = new ConexionBD();
		conexion.setHost(octeto[0]+"."+octeto[1]+"."+octeto[2]+"."+octeto[3]);
		conexion.setPuerto("1433");
		conexion.setInstancia("sqlserver");
		conexion.setEsquema("PROMADM");
		conexion.setUsuario("sa");
		conexion.setContrasena("maSt3rk3yY?");

		return conexion;
	}

	private Monitor creaMonitor() {
		Monitor monitor = new Monitor();
		monitor.setPoolSegMonitor("15");
		monitor.setNombreServicio("monitorXCTienda");
		monitor.setCadenaRunning("RUNNING");
		monitor.setCadenaStop("STOPPED");
		monitor.setExtensionImagen("tif");
	
		return monitor;
	}

	private Folder creaFolder() {
		Folder folder = new Folder();
		folder.setEntrada(tokens[0]+"\\Expedientes\\Entrada\\");
		folder.setEncontrados(tokens[0]+"\\Expedientes\\Proceso\\001-Encontrados\\");
		folder.setTemporal(tokens[0]+"\\Expedientes\\Proceso\\002-Temporal\\");
		folder.setImagenTif(tokens[0]+"\\Expedientes\\Temporal\\Imagen\\Tif\\%BC(1)%\\");
		folder.setImagenDifTif(tokens[0]+"\\Expedientes\\Error\\Imagen\\DifTif\\%BC(1)%\\");
		folder.setImagenFallo(tokens[0]+"\\Expedientes\\Error\\Imagen\\Tif\\%BC(1)%\\");
		folder.setAutorizacion(tokens[0]+"\\Expedientes\\Procesados\\Autorizacion\\%BC(1)%\\%BC(2)%\\%BC(3)%\\");
		folder.setPreautorizacion(tokens[0]+"\\Expedientes\\Procesados\\Cap_Pago\\%BC(1)%\\%BC(2)%\\");
		
		return folder;
	}

	private Campos creaAutorizacion() {

		String[] tipoAut = {"S","I","I","I","I","S","S","S","S","S","M","I","S","I","D","I","I","I","M",
				"S",FECHAARCHIVO,"IMG","DIRIMG"};
		int[] posicionAut = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,22,0,0,0};
		String[] columnaAut = {"ORDEN_VENTA","CLA_SUCURSAL","CREDITO_INICIAL","SISTEMA_VENTA",
				"TIPO_ORDEN","CLIENTE_STORIS","RFC_CLIENTE","NOM_CLIENTE","APP_CLIENTE","APM_CLIENTE",
				"IMPORTE_ORDEN","CLA_EMPRESA","NUM_NOMINA","PLAZO","FECHA_ORDEN","CLIENTE_RECOGE",
				"CLIENTE_PISO","FRECUENCIA","ABONO","FOLIO_CAP",FECHAARCHIVO,"NOM_ARCHIVO",
				"RUTA_ARCHIVO"};
		String[] traducirAut = {"","","","1=PROMOBIEN,2=FAMSA","","","","","","","","","","","","","","",
				"","","","",""};
		
		List<Campo> listAut = new ArrayList<>();
		for(int i=0;i<tipoAut.length;i++) {
			Campo campo = new Campo();
			campo.setTipo(tipoAut[i]);
			campo.setPosicion(posicionAut[i]);
			campo.setColumna(columnaAut[i]);
			campo.setTraducir(traducirAut[i]);
			
			listAut.add(campo);
		}
		
		Tabla tablaAut = new Tabla();
		tablaAut.setNombreTabla("TEMP_AUT_PROM");
		tablaAut.setTipo("autorizacion");
		tablaAut.setFolderOut(
				tokens[0]+"\\Expedientes\\Temporal\\Procesados\\%BC(1)%\\%BC(2)%\\%BC(3)%\\");
		Campos autorizacion = new Campos();
		autorizacion.setTabla(tablaAut);
		autorizacion.setListCampo(listAut);

		return autorizacion;
	}
	
	private Campos creaPreAutorizacion() {

		String[] tipoPre = {"S","I","I","I","I","S","S","S","S","S","M","I","S","I","D","I","I","I","M",
				"S","D","S",FECHAARCHIVO,"IMG","DIRIMG"};
		int[] posicionPre = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,0,0,0};
		String[] columnaPre = {"ORDEN_VENTA","CLA_SUCURSAL","CREDITO_INICIAL","SISTEMA_VENTA",
				"TIPO_ORDEN","CLIENTE_STORIS","RFC_CLIENTE","NOM_CLIENTE","APP_CLIENTE","APM_CLIENTE",
				"IMPORTE_ORDEN","CLA_EMPRESA","NUM_NOMINA","PLAZO","FECHA_ORDEN","CLIENTE_RECOGE",
				"CLIENTE_PISO","FRECUENCIA","ABONO","NUM_CELULAR","FECHA_INGRESO","FOLIO_CAP",
				FECHAARCHIVO,"NOM_ARCHIVO","RUTA_ARCHIVO"};
		String[] traducirPre = {"","","","1=PROMOBIEN,2=FAMSA","","","","","","","","","","","","","","",
				"","","","","","",""};
		
		List<Campo> listPreAut = new ArrayList<>();
		for(int i=0;i<tipoPre.length;i++) {
			Campo campo = new Campo();
			campo.setTipo(tipoPre[i]);
			campo.setPosicion(posicionPre[i]);
			campo.setColumna(columnaPre[i]);
			campo.setTraducir(traducirPre[i]);
			
			listPreAut.add(campo);
		}
		Tabla tablaPreAut = new Tabla();
		tablaPreAut.setNombreTabla("TEMP_AUT_PROM");
		tablaPreAut.setTipo("preautorizacion");
		tablaPreAut.setFolderOut(
				tokens[0]+"\\Expedientes\\Temporal\\Procesados\\Cap_Pago\\%BC(1)%\\%BC(2)%\\");
		Campos preAutorizacion = new Campos();
		preAutorizacion.setTabla(tablaPreAut);
		preAutorizacion.setListCampo(listPreAut);

		return preAutorizacion;
	}
	
}
