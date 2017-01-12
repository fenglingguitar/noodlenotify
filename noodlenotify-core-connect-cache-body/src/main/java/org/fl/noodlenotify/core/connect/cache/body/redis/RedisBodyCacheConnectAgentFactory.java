package org.fl.noodlenotify.core.connect.cache.body.redis;

import org.fl.noodle.common.connect.agent.AbstractConnectAgentFactory;
import org.fl.noodle.common.connect.agent.ConnectAgent;
import org.fl.noodlenotify.core.connect.cache.CacheConnectAgentConfParam;
import org.fl.noodlenotify.core.connect.cache.body.BodyCacheConnectAgentConfParam;

public class RedisBodyCacheConnectAgentFactory extends AbstractConnectAgentFactory {
	
	private CacheConnectAgentConfParam cacheConnectAgentConfParam = new CacheConnectAgentConfParam();
	
	private BodyCacheConnectAgentConfParam bodyCacheConnectAgentConfParam = new BodyCacheConnectAgentConfParam();

	@Override
	public ConnectAgent createConnectAgent(
			long connectId, String ip, int port, String url,
			int connectTimeout, int readTimeout) {
		return new RedisBodyCacheConnectAgent(
				connectId, ip, port, null,
				connectTimeout, readTimeout, encoding, invalidLimitNum,
				connectDistinguish, methodInterceptorList, 
				cacheConnectAgentConfParam, bodyCacheConnectAgentConfParam);
	}

	public void setCacheConnectAgentConfParam(CacheConnectAgentConfParam cacheConnectAgentConfParam) {
		this.cacheConnectAgentConfParam = cacheConnectAgentConfParam;
	}

	public void setBodyCacheConnectAgentConfParam(BodyCacheConnectAgentConfParam bodyCacheConnectAgentConfParam) {
		this.bodyCacheConnectAgentConfParam = bodyCacheConnectAgentConfParam;
	}
}
