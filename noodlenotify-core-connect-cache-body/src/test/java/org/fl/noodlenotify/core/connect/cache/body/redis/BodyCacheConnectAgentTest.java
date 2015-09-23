package org.fl.noodlenotify.core.connect.cache.body.redis;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import org.fl.noodle.common.util.json.JsonTranslator;
import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.cache.body.BodyCacheStatusChecker;
import org.fl.noodlenotify.core.connect.cache.body.redis.RedisBodyCacheConnectAgent;
import org.fl.noodlenotify.core.domain.message.MessageDm;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/core/connect/cache/body/redis/noodlenotify-core-connect-cache-body-redis.xml"
})

public class BodyCacheConnectAgentTest extends AbstractJUnit4SpringContextTests {

	private final static Logger logger = LoggerFactory.getLogger(BodyCacheConnectAgentTest.class);

	@Autowired
	RedisBodyCacheConnectAgent redisBodyCacheConnectAgent;
	
	public static MessageDm messageDm;
	
	@Test
	public final void testSet() throws Exception {
		
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		messageDm = new MessageDm();
		messageDm.setQueueName("testQueue1");
		messageDm.setUuid(uuid);
		messageDm.setContent(JsonTranslator.toByteArray("Hello"));
		
		logger.info("MessageDm uuid: " + uuid);
		redisBodyCacheConnectAgent.set(messageDm);
		logger.info("set...");
		Thread.sleep(100);
		logger.info("Sleep: " + 100);
	}

	@Test
	public final void testGet() throws Exception {
		MessageDm messageDmTemp = new MessageDm();
		messageDmTemp.setUuid(messageDm.getUuid());
		
		messageDmTemp = redisBodyCacheConnectAgent.get(messageDmTemp);
		logger.info("get uuid: " + (messageDmTemp.getContent() != null ? messageDmTemp.getUuid() : "NULL"));
		
		Thread.sleep(1000);
		logger.info("Sleep: " + 1000);
		messageDmTemp = redisBodyCacheConnectAgent.get(messageDmTemp);
		logger.info("get uuid: " + (messageDmTemp.getContent() != null ? messageDmTemp.getUuid() : "NULL"));
		
		Thread.sleep(1001);
		logger.info("Sleep: " + 1001);
		messageDmTemp = redisBodyCacheConnectAgent.get(messageDmTemp);
		logger.info("get uuid: " + (messageDmTemp.getContent() != null ? messageDmTemp.getUuid() : "NULL"));
	}

	@Test
	public final void testRemove() throws Exception {
		redisBodyCacheConnectAgent.set(messageDm);
		logger.info("set...");
		Thread.sleep(100);
		logger.info("Sleep: " + 100);
		
		MessageDm messageDmTemp = new MessageDm();
		messageDmTemp.setUuid(messageDm.getUuid());
		
		messageDmTemp = redisBodyCacheConnectAgent.get(messageDmTemp);
		logger.info("get uuid: " + (messageDmTemp.getContent() != null ? messageDmTemp.getUuid() : "NULL"));
		
		redisBodyCacheConnectAgent.remove(messageDm);
		logger.info("remove...");
		
		messageDmTemp = redisBodyCacheConnectAgent.get(messageDmTemp);
		logger.info("get uuid: " + (messageDmTemp.getContent() != null ? messageDmTemp.getUuid() : "NULL"));
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
