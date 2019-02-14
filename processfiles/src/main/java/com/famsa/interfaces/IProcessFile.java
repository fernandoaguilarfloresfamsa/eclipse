package com.famsa.interfaces;

import com.famsa.bean.Configuracion;
import com.famsa.exceptions.ProcessFileCtrlExc;

public interface IProcessFile {

	public Configuracion findConfiguration() throws ProcessFileCtrlExc;
}
