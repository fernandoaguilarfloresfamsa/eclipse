package com.famsa.exceptions;

public class CreateThreadRestExc extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8542660429791022097L;

	public CreateThreadRestExc() {
		super();
	}
	
	public CreateThreadRestExc(String message) {
		super(message);
	}
	
	public CreateThreadRestExc(String message, Throwable cause) {
		super(message, cause);
	}
	
	public CreateThreadRestExc(Throwable cause) {
		super(cause);
	}

}
