package com.famsa.interfaces;

import java.sql.Connection;

import com.famsa.exceptions.SQLServerConnectionExc;

public interface IBaseDatosConexion {

	public Connection getConnection() throws SQLServerConnectionExc;
	
}
