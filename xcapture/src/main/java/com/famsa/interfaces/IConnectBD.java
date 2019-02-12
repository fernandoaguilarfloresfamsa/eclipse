package com.famsa.interfaces;

import java.sql.Connection;

import com.famsa.exceptions.XCaptureExcep;

public interface IConnectBD {

	public Connection getConnection() throws XCaptureExcep;
	
}
