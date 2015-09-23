package org.fl.noodlenotify.core.connect.cache.trace.redis;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.cache.trace.TraceCacheConnectAgent;
import org.fl.noodlenotify.core.connect.cache.trace.TraceCacheStatusChecker;
import org.fl.noodlenotify.core.connect.cache.trace.constant.TraceConstant;
import org.fl.noodlenotify.core.connect.cache.trace.vo.TraceVo;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/core/connect/cache/trace/redis/noodlenotify-core-connect-cache-trace-redis.xml"
})

public class RedisTraceCacheConnectAgentTest extends AbstractJUnit4SpringContextTests {

	private final static Logger logger = LoggerFactory.getLogger(RedisTraceCacheConnectAgentTest.class);
	
	@Autowired
	RedisTraceCacheConnectAgent redisTraceCacheConnectAgent;
	
	static String uuid = "6f760457a8394f05ba632900bc29f863";
	
	@Test
	public final void testSet() throws Exception {
		TraceCacheConnectAgent traceCacheConnectAgent = redisTraceCacheConnectAgent;
		logger.info("Set...");
		traceCacheConnectAgent.set(new TraceVo(
										uuid, 
										TraceConstant.ACTION_TYPE_EXCHANGE_RECEIVE, 
										System.currentTimeMillis(), 
										TraceConstant.RESULT_TYPE_SUCCESS, 
										TraceConstant.MODULE_TYPE_EXCHANGE,
										1,
										TraceConstant.MODULE_TYPE_EXCHANGE,
										1));
		logger.info("Sleep: " + 1000);
		Thread.sleep(1000);
	}
	
	@Test
	public final void testGet() throws Exception {
		TraceCacheConnectAgent traceCacheConnectAgent = redisTraceCacheConnectAgent;
		logger.info("Get...");
		List<TraceVo> traceVoList = traceCacheConnectAgent.gets(uuid);
		for (TraceVo traceVo : traceVoList) {
			logger.info("TraceVo: " 
						+ traceVo.getUuid() + ", "
						+ traceVo.getAction() + ", "
						+ traceVo.getTimestamp() + ", "
						+ traceVo.getResult() + ", "
						+ traceVo.getTraceModuleType() + ", "
						+ traceVo.getTraceModuleId() + ", "
						+ traceVo.getDealModuleType() + ", "
						+ traceVo.getDealModuleId()
						);
		}
	}
	
	@Test
	public final void testRemove() throws Exception {
		TraceCacheConnectAgent traceCacheConnectAgent = redisTraceCacheConnectAgent;
		logger.info("Remove...");
		traceCacheConnectAgent.remove(uuid);
		logger.info("Sleep: " + 4000);
		Thread.sleep(4000);
		
		logger.info("Size...");
		List<TraceVo> traceVoList = traceCacheConnectAgent.gets(uuid);
		logger.info("TraceVoList Size: " + traceVoList.size());
	}
	
	@Test
	public final void testCheckHealth() throws Exception {
		TraceCacheStatusChecker traceCacheStatusChecker = redisTraceCacheConnectAgent;
		logger.info("CheckHealth...");
		traceCacheStatusChecker.checkHealth();
	}
	
	@Test
	public final void testCheckSize() throws Exception {
		TraceCacheStatusChecker traceCacheStatusChecker = redisTraceCacheConnectAgent;
		logger.info("CheckSize: " + traceCacheStatusChecker.checkSize());
	}
	
	@Test
	public final void testReconnect() throws Exception {
		((ConnectAgent)redisTraceCacheConnectAgent).reconnect();
	}
	
	@Test
	public final void testClose() throws Exception {
		Thread.sleep(1000);
	}
}
