package com.famsa.interfaces;

import com.famsa.bean.Configuracion;
import com.famsa.exceptions.CfgInicioExc;

public interface ICfgInicio {

	public Configuracion findConfiguration() throws CfgInicioExc;
}
