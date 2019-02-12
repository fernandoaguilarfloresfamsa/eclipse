package com.famsa.interfaces;

import com.famsa.exceptions.XCaptureExcep;
import com.famsa.xmlmarshall.Archivo;

public interface IElimina {

	public Archivo delFileEntrada(Archivo archivo) throws XCaptureExcep;
	public void delFileAnt(String nombreFile) throws XCaptureExcep;
	
}
