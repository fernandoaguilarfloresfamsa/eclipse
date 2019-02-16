package com.famsa.fabricas;

import com.famsa.db.SQLServerConnection;
import com.famsa.enums.BDEnum;
import com.famsa.interfaces.IBaseDatosConexion;

public class BaseDatosFactory {

	private BaseDatosFactory() {
		throw new IllegalStateException("BaseDatosFactory class");
	}
	  
	public static IBaseDatosConexion getBaseDatosConexion(BDEnum tipo) {
		
		if (tipo==BDEnum.BD_MYSQL) {
			return null;
		} else if (tipo==BDEnum.BD_ORACLE) {
			return null;
		} else if (tipo==BDEnum.BD_SQL_SERVER) {
			return new SQLServerConnection();
		}
		return null;
	}

}
