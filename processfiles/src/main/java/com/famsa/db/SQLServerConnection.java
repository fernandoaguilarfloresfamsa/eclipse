package com.famsa.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.famsa.config.Configuracion;
import com.famsa.exceptions.ConfiguracionExc;
import com.famsa.exceptions.SQLServerConnectionExc;
import com.famsa.interfaces.IBaseDatosConexion;

public class SQLServerConnection implements IBaseDatosConexion {

	private String servidorSQLServer;
	private String puertoSQLServer;
	private String instanciaSQLServer;
	private String schemaSQLServer;
	private String usuarioSQLServer;
	private String contrasenaSQLServer;
	private Connection connection;
	private Properties properties;

	private void obtenerConfiguracion() throws SQLServerConnectionExc {
		Configuracion conf = new Configuracion();
		try {
			servidorSQLServer=conf.getPropValues("servidorSQLServer");
		} catch (ConfiguracionExc e) {
			throw new SQLServerConnectionExc(e.toString(), e);
		}
		try {
			puertoSQLServer=conf.getPropValues("puertoSQLServer");
		} catch (ConfiguracionExc e) {
			throw new SQLServerConnectionExc(e.toString(), e);
		}
		try {
			instanciaSQLServer=conf.getPropValues("instanciaSQLServer");
		} catch (ConfiguracionExc e) {
			throw new SQLServerConnectionExc(e.toString(), e);
		}
		try {
			schemaSQLServer=conf.getPropValues("schemaSQLServer");
		} catch (ConfiguracionExc e) {
			throw new SQLServerConnectionExc(e.toString(), e);
		}
		try {
			usuarioSQLServer=conf.getPropValues("usuarioSQLServer");
		} catch (ConfiguracionExc e) {
			throw new SQLServerConnectionExc(e.toString(), e);
		}
		try {
			contrasenaSQLServer=conf.getPropValues("contrasenaSQLServer");
		} catch (ConfiguracionExc e) {
			throw new SQLServerConnectionExc(e.toString(), e);
		}
	}

	private Properties getProperties() {
		if (properties == null) {
		    properties = new Properties();
		    properties.setProperty("user", usuarioSQLServer);
		    properties.setProperty("password", contrasenaSQLServer);
		    properties.setProperty("MaxPooledStatements", "250");
		}
		return properties;
	}             
	
	@Override
	public Connection getConnection() throws SQLServerConnectionExc {
    	if (connection == null) {

			//obtiene datos de conexion desde archivo de configuracion
			try {
				obtenerConfiguracion();
			} catch (SQLServerConnectionExc e1) {
				throw new SQLServerConnectionExc(e1.toString(), e1);
			}
	        try {
	            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	        } catch (ClassNotFoundException e) {
	        	throw new SQLServerConnectionExc(e.toString(), e);
	        }			
			String urlConeccion = String.format("jdbc:sqlserver://%s:%s;instanceName=%s;databaseName=%s", 
					servidorSQLServer, puertoSQLServer, instanciaSQLServer, schemaSQLServer);
			try {
				connection = DriverManager.getConnection(urlConeccion, getProperties());
			} catch (SQLException e) {
				throw new SQLServerConnectionExc(e.toString(), e);
			}
		}
    	return connection;
	}
    
}
