package com.famsa.fabricas;

import com.famsa.controlador.ProcessFileCtrl;
import com.famsa.interfaces.IProcessFile;

public class ProcessFileFactory {

	private ProcessFileFactory() {
		throw new IllegalStateException("ProcessFileFactory class");
	}
	
	public static IProcessFile buscaConfiguracion() {
		return new ProcessFileCtrl();
	}
	
	public static IProcessFile createJson() {
		return new ProcessFileCtrl();
	}

	public static IProcessFile loadWebServices() {
		return new ProcessFileCtrl();
	}
}
