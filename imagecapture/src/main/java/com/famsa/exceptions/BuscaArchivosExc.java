package com.famsa.exceptions;

public class BuscaArchivosExc extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3488780871001042261L;

	public BuscaArchivosExc() {
		super();
	}
	
	public BuscaArchivosExc(String message) {
		super(message);
	}
	
	public BuscaArchivosExc(String message, Throwable cause) {
		super(message, cause);
	}
	
	public BuscaArchivosExc(Throwable cause) {
		super(cause);
	}
}
