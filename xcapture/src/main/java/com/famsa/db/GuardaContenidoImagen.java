package com.famsa.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.famsa.aplicacion.XCapture;
import com.famsa.enums.BDEnum;
import com.famsa.fabricas.BaseDatosFactory;
import com.famsa.interfaces.IActualizaBD;
import com.famsa.interfaces.IConnectBD;
import com.famsa.xmlmarshall.Archivo;

public class GuardaContenidoImagen implements IActualizaBD {
	
	final Logger logger = Logger.getLogger(XCapture.class.getName());

	@Override
	public int guardaBD(Archivo archivo) {

	    IConnectBD baseDatosConexion = BaseDatosFactory.getBaseDatosConexion(BDEnum.BD_SQL_SERVER);
		try (Connection conn = baseDatosConexion.getConnection()) {
			String sql = 
					"UPDATE dbo.LOG_PROCESO SET "+
							"ARCHIVO_XML=?,"+
							"NO_PAGINAS=? "+
					"WHERE IDPROCESAMIENTO=?";

			try (CallableStatement cstmt = conn.prepareCall(sql)) {
				cstmt.setString(1, archivo.getFilePathXML());
				cstmt.setInt(2, archivo.getNumeroPaginas());
				cstmt.setString(3, archivo.getUuid());
				cstmt.executeUpdate();
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
		
		return 0;
	}

}
