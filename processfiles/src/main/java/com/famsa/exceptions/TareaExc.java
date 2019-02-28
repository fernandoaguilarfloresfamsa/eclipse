package com.famsa.exceptions;

public class TareaExc extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7063587520494821711L;

	public TareaExc() {
		super();
	}
	
	public TareaExc(String message) {
		super(message);
	}
	
	public TareaExc(String message, Throwable cause) {
		super(message, cause);
	}
	
	public TareaExc(Throwable cause) {
		super(cause);
	}

}
