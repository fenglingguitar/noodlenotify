package org.fl.noodlenotify.core.status;

public interface StatusCheckerFactory {
	public StatusChecker createStatusChecker(long connectId, String ip, int port, String url);
	public StatusChecker createStatusChecker(long connectId, String ip, int port, String url, int connectTimeout, int readTimeout);
}