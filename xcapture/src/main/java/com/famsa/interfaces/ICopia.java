package com.famsa.interfaces;

import com.famsa.exceptions.XCaptureExcep;
import com.famsa.xmlmarshall.Archivo;

public interface ICopia {

	public Archivo buildI(Archivo archivo) throws XCaptureExcep;
	
}
