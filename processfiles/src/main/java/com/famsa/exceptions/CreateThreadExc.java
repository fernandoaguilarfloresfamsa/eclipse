package com.famsa.exceptions;

public class CreateThreadExc extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7907174149684930651L;

	public CreateThreadExc() {
		super();
	}
	
	public CreateThreadExc(String message) {
		super(message);
	}
	
	public CreateThreadExc(String message, Throwable cause) {
		super(message, cause);
	}
	
	public CreateThreadExc(Throwable cause) {
		super(cause);
	}
	
}
