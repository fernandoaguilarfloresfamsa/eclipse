package com.famsa.exceptions;

public class SQLServerConnectionExc extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8330619835270917493L;

	public SQLServerConnectionExc() {
		super();
	}
	
	public SQLServerConnectionExc(String message) {
		super(message);
	}
	
	public SQLServerConnectionExc(String message, Throwable cause) {
		super(message, cause);
	}
	
	public SQLServerConnectionExc(Throwable cause) {
		super(cause);
	}

}
