package com.famsa.fabricas;

import com.famsa.controlador.CreateThreadCtrl;
import com.famsa.interfaces.ICreateThread;

public class CreateThreadFactory {
	
	private CreateThreadFactory() {
		throw new IllegalStateException("CreateThreadFactory class");
	}
	
	public static ICreateThread buscaConfiguracion() {
		return new CreateThreadCtrl();
	}
	
	public static ICreateThread createJsonIds() {
		return new CreateThreadCtrl();
	}

	public static ICreateThread createJsonDetalle() {
		return new CreateThreadCtrl();
	}
	
	public static ICreateThread loadWebServicesDetalle() {
		return new CreateThreadCtrl();
	}

	public static ICreateThread createJsonEnProceso() {
		return new CreateThreadCtrl();
	}
	
	public static ICreateThread loadWebServicesEnProceso() {
		return new CreateThreadCtrl();
	}

}
