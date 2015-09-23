package org.fl.noodlenotify.core.connect.cache.trace.redis;

import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectAgentFactoryAbstract;
import org.fl.noodlenotify.core.connect.cache.CacheConnectAgentConfParam;
import org.fl.noodlenotify.core.connect.cache.trace.TraceCacheConnectAgentConfParam;

public class RedisTraceCacheConnectAgentFactory extends ConnectAgentFactoryAbstract {
	
	private CacheConnectAgentConfParam cacheConnectAgentConfParam
				= new CacheConnectAgentConfParam();
	
	private TraceCacheConnectAgentConfParam traceCacheConnectAgentConfParam
				= new TraceCacheConnectAgentConfParam();
	
	@Override
	public ConnectAgent createConnectAgent(String ip, int port, long connectId) {
		return new RedisTraceCacheConnectAgent(ip, port,
				connectId, cacheConnectAgentConfParam,
				traceCacheConnectAgentConfParam);
	}

	public void setCacheConnectAgentConfParam(CacheConnectAgentConfParam cacheConnectAgentConfParam) {
		this.cacheConnectAgentConfParam = cacheConnectAgentConfParam;
	}

	public void setTraceCacheConnectAgentConfParam(
			TraceCacheConnectAgentConfParam traceCacheConnectAgentConfParam) {
		this.traceCacheConnectAgentConfParam = traceCacheConnectAgentConfParam;
	}
}
