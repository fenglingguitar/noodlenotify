package org.fl.noodlenotify.core.connect.net.jetty.client;

import org.fl.noodlenotify.core.status.AbstractStatusCheckerFactory;
import org.fl.noodlenotify.core.status.StatusChecker;

public class JettyNetStatusCheckerFactory extends AbstractStatusCheckerFactory {
	
	@Override
	public StatusChecker createStatusChecker(
			long connectId, String ip, int port, String url, 
			int connectTimeout, int readTimeout) {
		return new JettyNetStatusChecker(
				connectId, ip, port, url,
				connectTimeout, readTimeout, encoding);
	}
}
