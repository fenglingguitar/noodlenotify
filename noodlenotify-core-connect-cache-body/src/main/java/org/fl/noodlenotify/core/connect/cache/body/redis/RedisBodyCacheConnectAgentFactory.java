package org.fl.noodlenotify.core.connect.cache.body.redis;

import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectAgentFactoryAbstract;
import org.fl.noodlenotify.core.connect.cache.CacheConnectAgentConfParam;
import org.fl.noodlenotify.core.connect.cache.body.BodyCacheConnectAgentConfParam;

public class RedisBodyCacheConnectAgentFactory extends ConnectAgentFactoryAbstract {
	
	private CacheConnectAgentConfParam cacheConnectAgentConfParam
				= new CacheConnectAgentConfParam();
	
	private BodyCacheConnectAgentConfParam bodyCacheConnectAgentConfParam
				= new BodyCacheConnectAgentConfParam();
	
	@Override
	public ConnectAgent createConnectAgent(String ip, int port, long connectId) {
		RedisBodyCacheConnectAgent redisBodyCacheConnectAgent = new RedisBodyCacheConnectAgent(ip, port,
				connectId, cacheConnectAgentConfParam,
				bodyCacheConnectAgentConfParam);
		return redisBodyCacheConnectAgent;
	}

	public void setCacheConnectAgentConfParam(CacheConnectAgentConfParam cacheConnectAgentConfParam) {
		this.cacheConnectAgentConfParam = cacheConnectAgentConfParam;
	}

	public void setBodyCacheConnectAgentConfParam(
			BodyCacheConnectAgentConfParam bodyCacheConnectAgentConfParam) {
		this.bodyCacheConnectAgentConfParam = bodyCacheConnectAgentConfParam;
	}
}
