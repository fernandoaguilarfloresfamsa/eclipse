package com.famsa.fabricas;

import com.famsa.db.SQLServerConn;
import com.famsa.enums.BDEnum;
import com.famsa.interfaces.IConnectBD;

public class BaseDatosFactory {

	private BaseDatosFactory() {
		throw new IllegalStateException("BaseDatosFactory class");
	}
	  
	public static IConnectBD getBaseDatosConexion(BDEnum tipo) {
		
		if (tipo==BDEnum.BD_MYSQL) {
			return null;
		} else if (tipo==BDEnum.BD_ORACLE) {
			return null;
		} else if (tipo==BDEnum.BD_SQL_SERVER) {
			return new SQLServerConn();
		} else if (tipo==BDEnum.BD_POSTGRESQL) {
			return null;
		}
		return null;
	}

}
