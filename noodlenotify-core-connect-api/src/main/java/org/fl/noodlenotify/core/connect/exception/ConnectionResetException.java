package org.fl.noodlenotify.core.connect.exception;

public class ConnectionResetException extends Exception {

	private static final long serialVersionUID = -831807351280508197L;	
	
	public ConnectionResetException() {
		super();
	}
	
	public ConnectionResetException(String message) {
		super(message);
	}
}
