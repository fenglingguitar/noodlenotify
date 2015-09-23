package org.fl.noodlenotify.core.connect.cache.queue.redis;

import org.springframework.beans.factory.annotation.Autowired;

import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectAgentFactoryAbstract;
import org.fl.noodlenotify.core.connect.cache.CacheConnectAgentConfParam;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheConnectAgentConfParam;
import org.fl.noodlenotify.monitor.performance.executer.service.impl.OvertimePerformanceExecuterService;
import org.fl.noodlenotify.monitor.performance.executer.service.impl.SuccessPerformanceExecuterService;

public class RedisQueueCacheConnectAgentFactory extends ConnectAgentFactoryAbstract {
	
	private CacheConnectAgentConfParam cacheConnectAgentConfParam
				= new CacheConnectAgentConfParam();
	
	private QueueCacheConnectAgentConfParam queueCacheConnectAgentConfParam 
			 	= new QueueCacheConnectAgentConfParam();
	
	@Autowired(required=false)
	private OvertimePerformanceExecuterService overtimePerformanceExecuterService;
	
	@Autowired(required=false)
	private SuccessPerformanceExecuterService successPerformanceExecuterService;
	
	@Override
	public ConnectAgent createConnectAgent(String ip, int port, long connectId) {
		
		RedisQueueCacheConnectAgent redisQueueCacheConnectAgent = new RedisQueueCacheConnectAgent(ip, port,
				connectId, cacheConnectAgentConfParam,
				queueCacheConnectAgentConfParam);
		redisQueueCacheConnectAgent.setOvertimePerformanceExecuterService(overtimePerformanceExecuterService);
		redisQueueCacheConnectAgent.setSuccessPerformanceExecuterService(successPerformanceExecuterService);
		return redisQueueCacheConnectAgent;
	}

	public void setCacheConnectAgentConfParam(CacheConnectAgentConfParam cacheConnectAgentConfParam) {
		this.cacheConnectAgentConfParam = cacheConnectAgentConfParam;
	}

	public void setQueueCacheConnectAgentConfParam(
			QueueCacheConnectAgentConfParam queueCacheConnectAgentConfParam) {
		this.queueCacheConnectAgentConfParam = queueCacheConnectAgentConfParam;
	}

	public void setOvertimePerformanceExecuterService(
			OvertimePerformanceExecuterService overtimePerformanceExecuterService) {
		this.overtimePerformanceExecuterService = overtimePerformanceExecuterService;
	}
	
	public void setSuccessPerformanceExecuterService(
			SuccessPerformanceExecuterService successPerformanceExecuterService) {
		this.successPerformanceExecuterService = successPerformanceExecuterService;
	}
}
