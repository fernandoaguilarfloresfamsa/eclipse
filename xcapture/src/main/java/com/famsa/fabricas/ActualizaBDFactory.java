package com.famsa.fabricas;

import com.famsa.db.GuardaContenidoImagen;
import com.famsa.db.GuardaInicioCopia;
import com.famsa.db.GuardaTerminaCopia;
import com.famsa.enums.ActualizaEnum;
import com.famsa.interfaces.IActualizaBD;

public class ActualizaBDFactory {

	private ActualizaBDFactory() {
		throw new IllegalStateException("ActualizaBDFactory class");
	}

	public static IActualizaBD guarda(ActualizaEnum tipo) {

		if (tipo==ActualizaEnum.ACT_INICIO) {
			return new GuardaInicioCopia();
		} else if (tipo==ActualizaEnum.ACT_CONTENIDO_IMG) {
			return new GuardaContenidoImagen();
		} else if (tipo==ActualizaEnum.ACT_TERMINA_COPIA) {
			return new GuardaTerminaCopia();
		}
		
		return null;
		
	}
	
	
}

