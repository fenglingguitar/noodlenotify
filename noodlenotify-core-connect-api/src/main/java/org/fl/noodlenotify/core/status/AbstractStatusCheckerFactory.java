package org.fl.noodlenotify.core.status;


public abstract class AbstractStatusCheckerFactory implements StatusCheckerFactory {

	protected int connectTimeout = 3000;
	protected int readTimeout = 3000;
	protected String encoding = "utf-8";
	
	@Override
	public StatusChecker createStatusChecker(long connectId, String ip, int port, String url) {
		return createStatusChecker(connectId, ip, port, url, this.connectTimeout, this.readTimeout);
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
}
