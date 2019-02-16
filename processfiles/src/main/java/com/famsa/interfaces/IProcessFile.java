package com.famsa.interfaces;

import com.famsa.bean.Configuracion;
import com.famsa.exceptions.ProcessFileCtrlExc;

public interface IProcessFile {

	public Configuracion findConfiguration() throws ProcessFileCtrlExc;
	public String generaJson(String paramXMLFileName, String paramCreationTime, 
			String paramFilePath,String paramUuid) throws ProcessFileCtrlExc;
}