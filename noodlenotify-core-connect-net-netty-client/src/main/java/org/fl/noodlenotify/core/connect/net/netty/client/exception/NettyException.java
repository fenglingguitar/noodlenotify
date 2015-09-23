package org.fl.noodlenotify.core.connect.net.netty.client.exception;

public class NettyException extends RuntimeException {
	
	private static final long serialVersionUID = 613631724863117327L;

	public NettyException() {
        super();
    }
	
	public NettyException(String message) {
        super(message);
    }

    public NettyException(Throwable e) {
        super(e);
    }

    public NettyException(String message, Throwable cause) {
        super(message, cause);
    }
}
