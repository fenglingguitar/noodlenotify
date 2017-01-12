package org.fl.noodlenotify.core.connect.cache.queue.redis;

import org.fl.noodle.common.connect.agent.AbstractConnectAgentFactory;
import org.fl.noodle.common.connect.agent.ConnectAgent;
import org.fl.noodlenotify.core.connect.cache.CacheConnectAgentConfParam;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheConnectAgentConfParam;

public class RedisQueueCacheConnectAgentFactory extends AbstractConnectAgentFactory {
	
	private CacheConnectAgentConfParam cacheConnectAgentConfParam = new CacheConnectAgentConfParam();
	
	private QueueCacheConnectAgentConfParam queueCacheConnectAgentConfParam = new QueueCacheConnectAgentConfParam();

	@Override
	public ConnectAgent createConnectAgent(
		long connectId, String ip, int port, String url,
		int connectTimeout, int readTimeout) {
	return new RedisQueueCacheConnectAgent(
			connectId, ip, port, null,
			connectTimeout, readTimeout, encoding, invalidLimitNum,
			connectDistinguish, methodInterceptorList, 
			cacheConnectAgentConfParam, queueCacheConnectAgentConfParam);
	}
	
	public void setCacheConnectAgentConfParam(CacheConnectAgentConfParam cacheConnectAgentConfParam) {
		this.cacheConnectAgentConfParam = cacheConnectAgentConfParam;
	}

	public void setQueueCacheConnectAgentConfParam(QueueCacheConnectAgentConfParam queueCacheConnectAgentConfParam) {
		this.queueCacheConnectAgentConfParam = queueCacheConnectAgentConfParam;
	}
}
