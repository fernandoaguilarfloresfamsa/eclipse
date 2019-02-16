package com.famsa.exceptions;

public class ProcessFilesRestExc extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5591005788811026096L;

	public ProcessFilesRestExc() {
		super();
	}
	
	public ProcessFilesRestExc(String message) {
		super(message);
	}
	
	public ProcessFilesRestExc(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ProcessFilesRestExc(Throwable cause) {
		super(cause);
	}
	
}
