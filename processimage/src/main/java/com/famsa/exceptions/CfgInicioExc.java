package com.famsa.exceptions;

public class CfgInicioExc extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5167530052502593357L;

	public CfgInicioExc() {
		super();
	}
	
	public CfgInicioExc(String message) {
		super(message);
	}
	
	public CfgInicioExc(String message, Throwable cause) {
		super(message, cause);
	}
	
	public CfgInicioExc(Throwable cause) {
		super(cause);
	}

}
