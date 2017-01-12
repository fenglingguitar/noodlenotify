package org.fl.noodlenotify.core.connect.cache.body.redis;

import org.fl.noodlenotify.core.status.AbstractStatusCheckerFactory;
import org.fl.noodlenotify.core.status.StatusChecker;

public class RedisBodyCacheStatusCheckerFactory extends AbstractStatusCheckerFactory {

	@Override
	public StatusChecker createStatusChecker(long connectId, String ip, int port, String url, int connectTimeout, int readTimeout) {
		return new RedisBodyCacheStatusChecker(connectId, ip, port, null, connectTimeout, readTimeout, encoding);
	}
}
