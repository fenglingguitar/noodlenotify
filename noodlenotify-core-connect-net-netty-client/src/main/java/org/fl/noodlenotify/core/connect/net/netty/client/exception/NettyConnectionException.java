package org.fl.noodlenotify.core.connect.net.netty.client.exception;

public class NettyConnectionException extends NettyException {
	
	private static final long serialVersionUID = 1486194108872516527L;

	public NettyConnectionException(String message) {
        super(message);
    }

    public NettyConnectionException(Throwable cause) {
        super(cause);
    }

    public NettyConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
