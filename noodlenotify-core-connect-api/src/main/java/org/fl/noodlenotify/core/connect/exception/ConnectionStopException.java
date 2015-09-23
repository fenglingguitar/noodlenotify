package org.fl.noodlenotify.core.connect.exception;

public class ConnectionStopException extends Exception {
	
	private static final long serialVersionUID = -2819701084503593758L;
	
	public ConnectionStopException() {
		super();
	}
	
	public ConnectionStopException(String message) {
		super(message);
	}
}
