package org.fl.noodlenotify.core.connect.cache.body.redis;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.fl.noodle.common.connect.agent.ConnectAgent;
import org.fl.noodle.common.util.json.JsonTranslator;
import org.fl.noodlenotify.common.pojo.db.MessageDb;
import org.fl.noodlenotify.core.connect.cache.body.BodyCacheStatusChecker;
import org.fl.noodlenotify.core.connect.cache.body.redis.RedisBodyCacheConnectAgent;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/core/connect/cache/body/redis/noodlenotify-core-connect-cache-body-redis.xml"
})

public class BodyCacheConnectAgentTest extends AbstractJUnit4SpringContextTests {

	private final static Logger logger = LoggerFactory.getLogger(BodyCacheConnectAgentTest.class);

	@Autowired
	RedisBodyCacheConnectAgent redisBodyCacheConnectAgent;
	
	public static MessageDb messageDb;
	
	@Test
	public final void testSet() throws Exception {
		
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		messageDb = new MessageDb();
		messageDb.setQueueName("testQueue1");
		messageDb.setUuid(uuid);
		messageDb.setContent(JsonTranslator.toByteArray("Hello"));
		
		logger.info("MessageDm uuid: " + uuid);
		redisBodyCacheConnectAgent.set(messageDb);
		logger.info("set...");
		Thread.sleep(100);
		logger.info("Sleep: " + 100);
	}

	@Test
	public final void testGet() throws Exception {
		MessageDb messageDbTemp = new MessageDb();
		messageDbTemp.setUuid(messageDb.getUuid());
		
		messageDbTemp = redisBodyCacheConnectAgent.get(messageDbTemp);
		logger.info("get uuid: " + (messageDbTemp.getContent() != null ? messageDbTemp.getUuid() : "NULL"));
		
		Thread.sleep(1000);
		logger.info("Sleep: " + 1000);
		messageDbTemp = redisBodyCacheConnectAgent.get(messageDbTemp);
		logger.info("get uuid: " + (messageDbTemp.getContent() != null ? messageDbTemp.getUuid() : "NULL"));
		
		Thread.sleep(1001);
		logger.info("Sleep: " + 1001);
		messageDbTemp = redisBodyCacheConnectAgent.get(messageDbTemp);
		logger.info("get uuid: " + (messageDbTemp.getContent() != null ? messageDbTemp.getUuid() : "NULL"));
	}

	@Test
	public final void testRemove() throws Exception {
		redisBodyCacheConnectAgent.set(messageDb);
		logger.info("set...");
		Thread.sleep(100);
		logger.info("Sleep: " + 100);
		
		MessageDb messageDbTemp = new MessageDb();
		messageDbTemp.setUuid(messageDb.getUuid());
		
		messageDbTemp = redisBodyCacheConnectAgent.get(messageDbTemp);
		logger.info("get uuid: " + (messageDbTemp.getContent() != null ? messageDbTemp.getUuid() : "NULL"));
		
		redisBodyCacheConnectAgent.remove(messageDb);
		logger.info("remove...");
		
		messageDbTemp = redisBodyCacheConnectAgent.get(messageDbTemp);
		logger.info("get uuid: " + (messageDbTemp.getContent() != null ? messageDbTemp.getUuid() : "NULL"));
	}
	
	@Test
	public final void testCheckHealth() throws Exception {
		BodyCacheStatusChecker bodyCacheStatusChecker = (BodyCacheStatusChecker) redisBodyCacheConnectAgent;
		bodyCacheStatusChecker.checkHealth();
	}
	
	@Test
	public final void testCheckSize() throws Exception {
		BodyCacheStatusChecker bodyCacheStatusChecker = (BodyCacheStatusChecker) redisBodyCacheConnectAgent;
		logger.info("CheckSize: " + bodyCacheStatusChecker.checkSize());
	}
	
	@Test
	public final void testReconnect() throws Exception {
		((ConnectAgent)redisBodyCacheConnectAgent).reconnect();
	}
	
	@Test
	public final void testClose() throws Exception {
		Thread.sleep(1000);
	}
}
