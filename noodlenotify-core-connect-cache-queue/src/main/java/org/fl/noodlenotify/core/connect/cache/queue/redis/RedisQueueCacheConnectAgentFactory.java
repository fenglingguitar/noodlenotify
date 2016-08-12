package org.fl.noodlenotify.core.connect.cache.queue.redis;

import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectAgentFactoryAbstract;
import org.fl.noodlenotify.core.connect.cache.CacheConnectAgentConfParam;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheConnectAgentConfParam;

public class RedisQueueCacheConnectAgentFactory extends ConnectAgentFactoryAbstract {
	
	private CacheConnectAgentConfParam cacheConnectAgentConfParam
				= new CacheConnectAgentConfParam();
	
	private QueueCacheConnectAgentConfParam queueCacheConnectAgentConfParam 
			 	= new QueueCacheConnectAgentConfParam();
	
	@Override
	public ConnectAgent createConnectAgent(String ip, int port, long connectId) {
		
		RedisQueueCacheConnectAgent redisQueueCacheConnectAgent = new RedisQueueCacheConnectAgent(ip, port,
				connectId, cacheConnectAgentConfParam,
				queueCacheConnectAgentConfParam);
		return redisQueueCacheConnectAgent;
	}

	public void setCacheConnectAgentConfParam(CacheConnectAgentConfParam cacheConnectAgentConfParam) {
		this.cacheConnectAgentConfParam = cacheConnectAgentConfParam;
	}

	public void setQueueCacheConnectAgentConfParam(
			QueueCacheConnectAgentConfParam queueCacheConnectAgentConfParam) {
		this.queueCacheConnectAgentConfParam = queueCacheConnectAgentConfParam;
	}
}
