package com.famsa.interfaces;

import com.famsa.exceptions.XCaptureExcep;
import com.famsa.xmlmarshall.Archivo;

public interface ISepara {

	public Archivo buildI(Archivo arch) throws XCaptureExcep;
	
}
