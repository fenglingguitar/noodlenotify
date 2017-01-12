package org.fl.noodlenotify.core.connect.net.netty.client;

import org.fl.noodlenotify.core.status.AbstractStatusCheckerFactory;
import org.fl.noodlenotify.core.status.StatusChecker;

public class NettyNetStatusCheckerFactory extends AbstractStatusCheckerFactory {
	
	@Override
	public StatusChecker createStatusChecker(
			long connectId, String ip, int port, String url, 
			int connectTimeout, int readTimeout) {
		return new NettyNetStatusChecker(
				connectId, ip, port, url,
				connectTimeout, readTimeout, encoding);
	}
}
