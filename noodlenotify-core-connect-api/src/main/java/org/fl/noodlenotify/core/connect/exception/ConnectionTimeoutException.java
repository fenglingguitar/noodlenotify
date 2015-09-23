package org.fl.noodlenotify.core.connect.exception;

public class ConnectionTimeoutException extends Exception {

	private static final long serialVersionUID = 5946369325830722843L;
	
	public ConnectionTimeoutException() {
		super();
	}
	
	public ConnectionTimeoutException(String message) {
		super(message);
	}
}
