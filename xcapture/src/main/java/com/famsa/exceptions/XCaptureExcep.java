package com.famsa.exceptions;

public class XCaptureExcep extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5191512571008822967L;

	public XCaptureExcep() {
		super();
	}
	
	public XCaptureExcep(String message) {
		super(message);
	}
	
	public XCaptureExcep(String message, Throwable cause) {
		super(message, cause);
	}
	
	public XCaptureExcep(Throwable cause) {
		super(cause);
	}

}
