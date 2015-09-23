package org.fl.noodlenotify.core.connect.cache.body.redis;

import org.springframework.beans.factory.annotation.Autowired;

import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectAgentFactoryAbstract;
import org.fl.noodlenotify.core.connect.cache.CacheConnectAgentConfParam;
import org.fl.noodlenotify.core.connect.cache.body.BodyCacheConnectAgentConfParam;
import org.fl.noodlenotify.monitor.performance.executer.service.impl.OvertimePerformanceExecuterService;
import org.fl.noodlenotify.monitor.performance.executer.service.impl.SuccessPerformanceExecuterService;

public class RedisBodyCacheConnectAgentFactory extends ConnectAgentFactoryAbstract {
	
	private CacheConnectAgentConfParam cacheConnectAgentConfParam
				= new CacheConnectAgentConfParam();
	
	private BodyCacheConnectAgentConfParam bodyCacheConnectAgentConfParam
				= new BodyCacheConnectAgentConfParam();
	
	@Autowired(required=false)
	private OvertimePerformanceExecuterService overtimePerformanceExecuterService;
	
	@Autowired(required=false)
	private SuccessPerformanceExecuterService successPerformanceExecuterService;
	
	@Override
	public ConnectAgent createConnectAgent(String ip, int port, long connectId) {
		RedisBodyCacheConnectAgent redisBodyCacheConnectAgent = new RedisBodyCacheConnectAgent(ip, port,
				connectId, cacheConnectAgentConfParam,
				bodyCacheConnectAgentConfParam);
		redisBodyCacheConnectAgent.setOvertimePerformanceExecuterService(overtimePerformanceExecuterService);
		redisBodyCacheConnectAgent.setSuccessPerformanceExecuterService(successPerformanceExecuterService);
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
