package com.famsa.interfaces;

import com.famsa.bean.Configuracion;
import com.famsa.bean.PbProcessFilesHalf;
import com.famsa.exceptions.CreateThreadCtrlExc;

public interface ICreateThread {

	public Configuracion findConfiguration() throws CreateThreadCtrlExc;
	public String generaJsonIds() throws CreateThreadCtrlExc;
	public String generaJsonDetalle(int paramId) throws CreateThreadCtrlExc;
	public PbProcessFilesHalf consumeWebServiceDetalle(int id) throws CreateThreadCtrlExc;
	
}
