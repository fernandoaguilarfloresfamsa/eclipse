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

public class GuardaTerminaCopia implements IActualizaBD {
	
	final Logger logger = Logger.getLogger(XCapture.class.getName());

	@Override
	public int guardaBD(Archivo archivo) {

		String archDest = null;
		if (archivo.isExisteDesTif()) {
			archDest=archivo.getPathTif()+archivo.getNomArch();
		}
		if (archivo.isExisteDesOtr()) {
			archDest=archivo.getPathOtro()+archivo.getNomArch();
		}
		
	    IConnectBD baseDatosConexion = BaseDatosFactory.getBaseDatosConexion(BDEnum.BD_SQL_SERVER);
		try (Connection conn = baseDatosConexion.getConnection()) {
			String sql = 
					"UPDATE dbo.LOG_PROCESO SET "+
							"FECHA_PROC_IMAGEN_FIN=GETDATE(),"+
							"ARCHIVO_DESTINO=? "+
					"WHERE IDPROCESAMIENTO=?";

			try (CallableStatement cstmt = conn.prepareCall(sql)) {
				cstmt.setString(1, archDest);
				cstmt.setString(2, archivo.getUuid());
				cstmt.executeUpdate();
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
		
		return 0;
	}

}
