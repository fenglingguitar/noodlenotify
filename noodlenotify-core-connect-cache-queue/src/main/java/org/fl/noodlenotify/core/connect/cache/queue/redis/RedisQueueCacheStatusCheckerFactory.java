package org.fl.noodlenotify.core.connect.cache.queue.redis;

import org.fl.noodlenotify.core.status.AbstractStatusCheckerFactory;
import org.fl.noodlenotify.core.status.StatusChecker;

public class RedisQueueCacheStatusCheckerFactory extends AbstractStatusCheckerFactory {

	@Override
	public StatusChecker createStatusChecker(long connectId, String ip, int port, String url, int connectTimeout, int readTimeout) {
		return new RedisQueueCacheStatusChecker(connectId, ip, port, null, connectTimeout, readTimeout, encoding);
	}
}
