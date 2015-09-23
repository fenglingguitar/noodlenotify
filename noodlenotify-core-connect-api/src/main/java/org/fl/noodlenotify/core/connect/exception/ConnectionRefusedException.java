package org.fl.noodlenotify.core.connect.exception;

public class ConnectionRefusedException extends Exception {
	
	private static final long serialVersionUID = -2819701084503593758L;
	
	public ConnectionRefusedException() {
		super();
	}
	
	public ConnectionRefusedException(String message) {
		super(message);
	}
}
