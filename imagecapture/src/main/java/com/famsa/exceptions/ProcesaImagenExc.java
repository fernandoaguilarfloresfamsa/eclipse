package com.famsa.exceptions;

public class ProcesaImagenExc extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 672265800667428433L;

	public ProcesaImagenExc() {
		super();
	}
	
	public ProcesaImagenExc(String message) {
		super(message);
	}
	
	public ProcesaImagenExc(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ProcesaImagenExc(Throwable cause) {
		super(cause);
	}
}
