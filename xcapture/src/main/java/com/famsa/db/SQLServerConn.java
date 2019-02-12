package com.famsa.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.famsa.aplicacion.XCapture;
import com.famsa.exceptions.XCaptureExcep;
import com.famsa.fabricas.ConfigFactory;
import com.famsa.interfaces.IConfig;
import com.famsa.interfaces.IConnectBD;
import com.famsa.xmlmarshall.Config;

public class SQLServerConn implements IConnectBD {

	final Logger logger = Logger.getLogger(XCapture.class.getName());
	private Connection connection;
	Config config = null;
	
	private void obtenerConfiguracion() {
		IConfig iConfig = ConfigFactory.getConfig();
		config = iConfig.getIConfig();
	}
	
	public Connection getConnection() throws XCaptureExcep {

    	if (connection == null) {
			obtenerConfiguracion();
	        try {
	            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	        } catch (ClassNotFoundException e) {
	        	logger.log(Level.SEVERE, e.toString(), e);
	        	throw new XCaptureExcep(e.toString(), e);
	        }			
	        String urlConeccion = String.format(
	        		"jdbc:sqlserver://%s:%s;instanceName=%s;databaseName=%s;%s=%s;%s=%s",
	        		config.getConexion().getHost(),
	        		config.getConexion().getPuerto(),
	        		config.getConexion().getInstancia(),
	        		config.getConexion().getEsquema(),
	        		"user",config.getConexion().getUsuario(),
	        		"password",config.getConexion().getContrasena());
			try {
				connection = DriverManager.getConnection(urlConeccion);
			} catch (SQLException e) {
				logger.log(Level.SEVERE, e.toString(), e);
				throw new XCaptureExcep(e.toString(), e);
			}
		}
    	return connection;
	}
}