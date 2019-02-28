package com.famsa.controlador;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.famsa.aplicacion.CreateThread;
import com.famsa.bean.Configuracion;
import com.famsa.bean.PbProcessFilesHalf;
import com.famsa.enums.BDEnum;
import com.famsa.exceptions.CreateThreadCtrlExc;
import com.famsa.fabricas.BaseDatosFactory;
import com.famsa.interfaces.IBaseDatosConexion;
import com.famsa.interfaces.ICreateThread;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

public class CreateThreadCtrl implements ICreateThread {

	static final Logger logger = Logger.getLogger(CreateThread.class.getName());
	private static final String RUTATXT = "ruta.txt";
	private static final String CADENASPLIT = "\\\\(?=[^\\\\]+$)";
	private static final String CARPETACONFIG = "\\Expedientes\\Config\\";
	private static final String XMLFILE = "config.xml";
	String nomArcConf = null;
	String nomCarpeta = null;
	String[] tokens;
	List<PbProcessFilesHalf> resIds = new ArrayList<>();
	
	private static final String APP_JSON = "application/json";  
	
	@Override
	public Configuracion findConfiguration() throws CreateThreadCtrlExc {
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
		
		//	---------------------------------------------------------------------------------------
		File fileConf = new File(nomArcConf);
    	if(fileConf.exists() && !fileConf.isDirectory()) {
            JAXBContext jaxbContext = null;
			try {
				jaxbContext = JAXBContext.newInstance(Configuracion.class);
			} catch (JAXBException e) {
				throw new CreateThreadCtrlExc(e.toString(), e);
			}
            Unmarshaller jaxbUnmarshaller = null;
			try {
				jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			} catch (JAXBException e) {
				throw new CreateThreadCtrlExc(e.toString(), e);
			}
			try {
				configXML = (Configuracion) jaxbUnmarshaller.unmarshal( new File(nomArcConf));
			} catch (JAXBException e) {
				throw new CreateThreadCtrlExc(e.toString(), e);
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
	
	@Override
	public String generaJsonIds() throws CreateThreadCtrlExc {
		try {
			obtenerDatosIds();
		} catch (CreateThreadCtrlExc e) {
			logger.log(Level.SEVERE, e.toString(), e);
			throw new CreateThreadCtrlExc("#"+e.toString(), e);
		}
		Gson gson = new Gson();
		return gson.toJson(resIds);
	}

	public void obtenerDatosIds() throws CreateThreadCtrlExc {
		
		IBaseDatosConexion baseDatosConexion = BaseDatosFactory.getBaseDatosConexion(BDEnum.BD_SQL_SERVER);
		try (Connection conn = baseDatosConexion.getConnection()) {
			String sql = "{ call [PROMADM].[dbo].[SP_PROCESS_FILE_DATOS_IDS] }";
			try (CallableStatement cstmt = conn.prepareCall(sql)) {
				try (ResultSet resultSet = cstmt.executeQuery()) {
					while(resultSet.next()) {
						PbProcessFilesHalf fh = new PbProcessFilesHalf();
						fh.setId(resultSet.getInt("ID"));
						
						resIds.add(fh);
					}
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.toString(), e);
			throw new CreateThreadCtrlExc("#"+e.toString(), e);
		}
	}

	@Override
	public String generaJsonDetalle(int paramId) throws CreateThreadCtrlExc {
		PbProcessFilesHalf detalle = new PbProcessFilesHalf();
		try {
			detalle = obtenerDatosDetalle(paramId);
		} catch (CreateThreadCtrlExc e) {
			logger.log(Level.SEVERE, e.toString(), e);
			throw new CreateThreadCtrlExc("#"+e.toString(), e);
		}
		Gson gson = new Gson();
		return gson.toJson(detalle);
	}
	
	public PbProcessFilesHalf obtenerDatosDetalle(int paramId) throws CreateThreadCtrlExc {
		
		PbProcessFilesHalf resDetalle = new PbProcessFilesHalf();
		IBaseDatosConexion baseDatosConexion = BaseDatosFactory.getBaseDatosConexion(BDEnum.BD_SQL_SERVER);
		try (Connection conn = baseDatosConexion.getConnection()) {
			String sql = "{ call [PROMADM].[dbo].[SP_PROCESS_FILE_DATOS_DETALLE] ( ? ) }";
			try (CallableStatement cstmt = conn.prepareCall(sql)) {
				cstmt.setInt(1, paramId);
				try (ResultSet resultSet = cstmt.executeQuery()) {
					while(resultSet.next()) {
						resDetalle.setId(resultSet.getInt("ID"));
						resDetalle.setXmlFileName(resultSet.getString("XML_FILE_NAME"));
						resDetalle.setUuid(resultSet.getString("UUID"));
						resDetalle.setImageFileName(resultSet.getString("IMAGE_FILE_NAME"));
					}
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.toString(), e);
			throw new CreateThreadCtrlExc("#"+e.toString(), e);
		}
		return resDetalle;
	}

	@Override
	public PbProcessFilesHalf consumeWebServiceDetalle(int id) throws CreateThreadCtrlExc {
		String miUrl = String.format(
				"http://localhost:8080/processfiles/rest/createthread/buscaDetalle/%d", id);
    	try {
	    	ClientConfig clientConfig = new DefaultClientConfig();
	    	clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
	    	Client client = Client.create(clientConfig);
	
	    	WebResource webResource = client.resource(miUrl);
	
	    	ClientResponse response = webResource.accept(APP_JSON)
	    	        .type(APP_JSON).get(ClientResponse.class);    	
	    	
	    	if (response.hasEntity()) {
	    		String output = response.getEntity(String.class);
	    		Gson gson = new Gson();
	    		return gson.fromJson(output, PbProcessFilesHalf.class);
	    	}
    	} catch(Exception e) {
    		logger.log(Level.SEVERE, e.toString(), e);
			throw new CreateThreadCtrlExc(e.toString(), e);
    	}
		return null;
	}

	@Override
	public String generaJsonEnProceso(int paramIdEnProceso) throws CreateThreadCtrlExc {
		PbProcessFilesHalf enProceso = new PbProcessFilesHalf();
		try {
			enProceso = obtenerDatosEnProceso(paramIdEnProceso);
		} catch (CreateThreadCtrlExc e) {
			logger.log(Level.SEVERE, e.toString(), e);
			throw new CreateThreadCtrlExc("#"+e.toString(), e);
		}
		Gson gson = new Gson();
		return gson.toJson(enProceso);
	}

	public PbProcessFilesHalf obtenerDatosEnProceso(int paramIdEnProceso) throws CreateThreadCtrlExc {
		PbProcessFilesHalf resEnProceso = new PbProcessFilesHalf();
		IBaseDatosConexion baseDatosConexion = BaseDatosFactory.getBaseDatosConexion(BDEnum.BD_SQL_SERVER);
		try (Connection conn = baseDatosConexion.getConnection()) {
			String sql = "{ call [PROMADM].[dbo].[SP_PROCESS_FILE_DATOS_IDS_EP] ( ? ) }";
			try (CallableStatement cstmt = conn.prepareCall(sql)) {
				cstmt.setInt(1, paramIdEnProceso);
				try (ResultSet resultSet = cstmt.executeQuery()) {
					while(resultSet.next()) {
						resEnProceso.setId(resultSet.getInt("ID"));
					}
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.toString(), e);
			throw new CreateThreadCtrlExc("#"+e.toString(), e);
		}
		return resEnProceso;
	}
	
	@Override
	public PbProcessFilesHalf consumeWebServiceEnProceso(int idEnProceso) throws CreateThreadCtrlExc {
		String miUrl = String.format(
				"http://localhost:8080/processfiles/rest/createthread/buscaDetalle/%d", idEnProceso);
    	try {
	    	ClientConfig clientConfig = new DefaultClientConfig();
	    	clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
	    	Client client = Client.create(clientConfig);
	
	    	WebResource webResource = client.resource(miUrl);
	
	    	ClientResponse response = webResource.accept(APP_JSON)
	    	        .type(APP_JSON).get(ClientResponse.class);    	
	    	
	    	if (response.hasEntity()) {
	    		String output = response.getEntity(String.class);
	    		Gson gson = new Gson();
	    		return gson.fromJson(output, PbProcessFilesHalf.class);
	    	}
    	} catch(Exception e) {
    		logger.log(Level.SEVERE, e.toString(), e);
			throw new CreateThreadCtrlExc(e.toString(), e);
    	}
		return null;
	}

}
