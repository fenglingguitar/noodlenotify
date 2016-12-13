package org.fl.noodlenotify.core.connect.cache.queue.redis;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheStatusChecker;
import org.fl.noodlenotify.core.connect.cache.queue.redis.RedisQueueCacheConnectAgent;
import org.fl.noodlenotify.core.domain.message.MessageDm;
import org.fl.noodle.common.connect.agent.ConnectAgent;
import org.fl.noodle.common.util.json.JsonTranslator;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/core/connect/cache/queue/redis/noodlenotify-core-connect-cache-queue-factory.xml"
})

public class QueueCacheConnectAgentTest extends AbstractJUnit4SpringContextTests {
	
	private final static Logger logger = LoggerFactory.getLogger(QueueCacheConnectAgentTest.class);
	
	@Autowired
	RedisQueueCacheConnectAgentFactory redisQueueCacheConnectAgentFactory;
	
	public static MessageDm messageDm;
	
	@Test
	public final void testSetActive() throws Exception {
		RedisQueueCacheConnectAgent redisQueueCacheConnectAgent = (RedisQueueCacheConnectAgent) redisQueueCacheConnectAgentFactory.createConnectAgent(1, "127.0.0.1", 2301, null);
		redisQueueCacheConnectAgent.connect();
		redisQueueCacheConnectAgent.setActive("testQueue1", true);
		logger.info("SetActive true...");
		redisQueueCacheConnectAgent.close();
	}
	
	@Test
	public final void testPush() throws Exception {
		RedisQueueCacheConnectAgent redisQueueCacheConnectAgent = (RedisQueueCacheConnectAgent) redisQueueCacheConnectAgentFactory.createConnectAgent(1, "127.0.0.1", 2301, null);
		redisQueueCacheConnectAgent.connect();
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		messageDm = new MessageDm();
		messageDm.setQueueName("testQueue1");
		messageDm.setUuid(uuid);
		messageDm.setContent(JsonTranslator.toByteArray("Hello"));
		logger.info("MessageDm uuid: " + uuid);
		messageDm.setBool(true);
		redisQueueCacheConnectAgent.push(messageDm);
		logger.info("Push New...");
		Thread.sleep(1000);
		logger.info("Sleep: " + 1000);
		messageDm.setBool(false);
		redisQueueCacheConnectAgent.push(messageDm);
		logger.info("Push Portion...");
		Thread.sleep(1000);
		logger.info("Sleep: " + 1000);
		redisQueueCacheConnectAgent.close();
	}

	@Test
	public final void testPop() throws Exception {
		RedisQueueCacheConnectAgent redisQueueCacheConnectAgent = (RedisQueueCacheConnectAgent) redisQueueCacheConnectAgentFactory.createConnectAgent(1, "127.0.0.1", 2301, null);
		redisQueueCacheConnectAgent.connect();
		messageDm = redisQueueCacheConnectAgent.pop("testQueue1", true);
		logger.info("Pop New uuid: " + messageDm.getUuid());
		messageDm = redisQueueCacheConnectAgent.pop("testQueue1", false);
		logger.info("Pop Portion uuid: " + messageDm.getUuid());
		redisQueueCacheConnectAgent.close();
	}

	@Test
	public final void testHavePop() throws Exception {
		RedisQueueCacheConnectAgent redisQueueCacheConnectAgent = (RedisQueueCacheConnectAgent) redisQueueCacheConnectAgentFactory.createConnectAgent(1, "127.0.0.1", 2301, null);
		redisQueueCacheConnectAgent.connect();
		logger.info("HavePop: " + redisQueueCacheConnectAgent.havePop(messageDm));
		Thread.sleep(1000);
		logger.info("Sleep: " + 1000);
		logger.info("HavePop: " + redisQueueCacheConnectAgent.havePop(messageDm));
		Thread.sleep(1001);
		logger.info("Sleep: " + 1001);
		logger.info("HavePop: " + redisQueueCacheConnectAgent.havePop(messageDm));
		redisQueueCacheConnectAgent.close();
	}

	@Test
	public final void testSetPop() throws Exception {
		RedisQueueCacheConnectAgent redisQueueCacheConnectAgent = (RedisQueueCacheConnectAgent) redisQueueCacheConnectAgentFactory.createConnectAgent(1, "127.0.0.1", 2301, null);
		redisQueueCacheConnectAgent.connect();
		redisQueueCacheConnectAgent.setPop(messageDm);
		logger.info("SetPop...");
		logger.info("HavePop: " + redisQueueCacheConnectAgent.havePop(messageDm));
		Thread.sleep(1000);
		logger.info("Sleep: " + 1000);
		logger.info("HavePop: " + redisQueueCacheConnectAgent.havePop(messageDm));
		Thread.sleep(1001);
		logger.info("Sleep: " + 1001);
		logger.info("HavePop: " + redisQueueCacheConnectAgent.havePop(messageDm));
		redisQueueCacheConnectAgent.close();
	}

	@Test
	public final void testRemovePop() throws Exception {
		RedisQueueCacheConnectAgent redisQueueCacheConnectAgent = (RedisQueueCacheConnectAgent) redisQueueCacheConnectAgentFactory.createConnectAgent(1, "127.0.0.1", 2301, null);
		redisQueueCacheConnectAgent.connect();
		redisQueueCacheConnectAgent.setPop(messageDm);
		logger.info("SetPop...");
		logger.info("HavePop: " + redisQueueCacheConnectAgent.havePop(messageDm));
		redisQueueCacheConnectAgent.removePop(messageDm);
		logger.info("RemovePop...");
		logger.info("HavePop: " + redisQueueCacheConnectAgent.havePop(messageDm));
		redisQueueCacheConnectAgent.close();
	}
	
	@Test
	public final void testIsActive() throws Exception {
		RedisQueueCacheConnectAgent redisQueueCacheConnectAgent = (RedisQueueCacheConnectAgent) redisQueueCacheConnectAgentFactory.createConnectAgent(1, "127.0.0.1", 2301, null);
		redisQueueCacheConnectAgent.connect();
		logger.info("IsActive: " + redisQueueCacheConnectAgent.isActive("testQueue1"));
		redisQueueCacheConnectAgent.setActive("testQueue1", false);
		logger.info("SetActive false...");
		logger.info("IsActive: " + redisQueueCacheConnectAgent.isActive("testQueue1"));
		redisQueueCacheConnectAgent.close();
	}
	
	@Test
	public final void testLen() throws Exception {
		RedisQueueCacheConnectAgent redisQueueCacheConnectAgent = (RedisQueueCacheConnectAgent) redisQueueCacheConnectAgentFactory.createConnectAgent(1, "127.0.0.1", 2301, null);
		redisQueueCacheConnectAgent.connect();
		logger.info("New Len: " + redisQueueCacheConnectAgent.len("testQueue1", true));
		logger.info("Portion Len: " + redisQueueCacheConnectAgent.len("testQueue1", false));
		redisQueueCacheConnectAgent.close();
	}
	
	@Test
	public final void testGetDiffTime() throws Exception {
		RedisQueueCacheConnectAgent redisQueueCacheConnectAgent = (RedisQueueCacheConnectAgent) redisQueueCacheConnectAgentFactory.createConnectAgent(1, "127.0.0.1", 2301, null);
		redisQueueCacheConnectAgent.connect();
		logger.info("DiffTime: " + redisQueueCacheConnectAgent.getDiffTime());
		redisQueueCacheConnectAgent.close();
	}
	
	@Test
	public final void testGetAlive() throws Exception {
		RedisQueueCacheConnectAgent redisQueueCacheConnectAgent = (RedisQueueCacheConnectAgent) redisQueueCacheConnectAgentFactory.createConnectAgent(1, "127.0.0.1", 2301, null);
		redisQueueCacheConnectAgent.connect();
		logger.info("GetAlive 1: " + redisQueueCacheConnectAgent.getAlive("testQueue1", 1, 0, 2000));
		redisQueueCacheConnectAgent.close();
	}
	
	@Test
	public final void testKeepAlive() throws Exception {
		RedisQueueCacheConnectAgent redisQueueCacheConnectAgent = (RedisQueueCacheConnectAgent) redisQueueCacheConnectAgentFactory.createConnectAgent(1, "127.0.0.1", 2301, null);
		redisQueueCacheConnectAgent.connect();
		logger.info("Sleep 1000...");
		Thread.sleep(1000);
		logger.info("KeepAlive 1: " + redisQueueCacheConnectAgent.keepAlive("testQueue1", 1, 0, 2000));
		logger.info("Sleep 1000...");
		Thread.sleep(1000);
		logger.info("KeepAlive 2: " + redisQueueCacheConnectAgent.keepAlive("testQueue1", 2, 0, 2000));
		logger.info("GetAlive 2: " + redisQueueCacheConnectAgent.getAlive("testQueue1", 2, 0, 2000));
		logger.info("Sleep 1000...");
		Thread.sleep(1000);
		logger.info("GetAlive 2: " + redisQueueCacheConnectAgent.getAlive("testQueue1", 2, 0, 2000));
		logger.info("Sleep 1000...");
		Thread.sleep(1000);
		logger.info("KeepAlive 1: " + redisQueueCacheConnectAgent.keepAlive("testQueue1", 1, 0, 2000));
		redisQueueCacheConnectAgent.close();
	}
	
	@Test
	public final void testReleaseAlive() throws Exception {
		RedisQueueCacheConnectAgent redisQueueCacheConnectAgent = (RedisQueueCacheConnectAgent) redisQueueCacheConnectAgentFactory.createConnectAgent(1, "127.0.0.1", 2301, null);
		redisQueueCacheConnectAgent.connect();
		logger.info("ReleaseAlive 1");
		redisQueueCacheConnectAgent.releaseAlive("testQueue1", 1);
		logger.info("ReleaseAlive 2");
		redisQueueCacheConnectAgent.releaseAlive("testQueue1", 2);
		redisQueueCacheConnectAgent.close();
	}
	
	@Test
	public final void testCheckHealth() throws Exception {
		RedisQueueCacheConnectAgent redisQueueCacheConnectAgent = (RedisQueueCacheConnectAgent) redisQueueCacheConnectAgentFactory.createConnectAgent(1, "127.0.0.1", 2301, null);
		redisQueueCacheConnectAgent.connect();
		QueueCacheStatusChecker queueCacheStatusChecker = (QueueCacheStatusChecker) redisQueueCacheConnectAgent;
		queueCacheStatusChecker.checkHealth();
		redisQueueCacheConnectAgent.close();
	}
	
	@Test
	public final void testCheckIsActive() throws Exception {
		RedisQueueCacheConnectAgent redisQueueCacheConnectAgent = (RedisQueueCacheConnectAgent) redisQueueCacheConnectAgentFactory.createConnectAgent(1, "127.0.0.1", 2301, null);
		redisQueueCacheConnectAgent.connect();
		redisQueueCacheConnectAgent.setActive("testQueue1", true);
		logger.info("SetActive true...");
		messageDm.setBool(true);
		redisQueueCacheConnectAgent.push(messageDm);
		logger.info("Push New...");
		Thread.sleep(1000);
		logger.info("Sleep: " + 1000);
		messageDm.setBool(false);
		redisQueueCacheConnectAgent.push(messageDm);
		logger.info("Push Portion...");
		Thread.sleep(1000);
		logger.info("Sleep: " + 1000);
		QueueCacheStatusChecker queueCacheStatusChecker = (QueueCacheStatusChecker) redisQueueCacheConnectAgent;
		logger.info("CheckIsActive: " + queueCacheStatusChecker.checkIsActive("testQueue1"));
		redisQueueCacheConnectAgent.close();
	}
	
	@Test
	public final void testCheckNewLen() throws Exception {
		RedisQueueCacheConnectAgent redisQueueCacheConnectAgent = (RedisQueueCacheConnectAgent) redisQueueCacheConnectAgentFactory.createConnectAgent(1, "127.0.0.1", 2301, null);
		redisQueueCacheConnectAgent.connect();
		QueueCacheStatusChecker queueCacheStatusChecker = (QueueCacheStatusChecker) redisQueueCacheConnectAgent;
		logger.info("CheckNewLen: " + queueCacheStatusChecker.checkNewLen("testQueue1"));
		redisQueueCacheConnectAgent.close();
	}
	
	@Test
	public final void testCheckPortionLen() throws Exception {
		RedisQueueCacheConnectAgent redisQueueCacheConnectAgent = (RedisQueueCacheConnectAgent) redisQueueCacheConnectAgentFactory.createConnectAgent(1, "127.0.0.1", 2301, null);
		redisQueueCacheConnectAgent.connect();
		QueueCacheStatusChecker queueCacheStatusChecker = (QueueCacheStatusChecker) redisQueueCacheConnectAgent;
		logger.info("CheckPortionLen: " + queueCacheStatusChecker.checkPortionLen("testQueue1"));
		redisQueueCacheConnectAgent.setActive("testQueue1", false);
		logger.info("SetActive false...");
		redisQueueCacheConnectAgent.close();
	}
	
	@Test
	public final void testRemovePopDelay() throws Exception {
		RedisQueueCacheConnectAgent redisQueueCacheConnectAgent = (RedisQueueCacheConnectAgent) redisQueueCacheConnectAgentFactory.createConnectAgent(1, "127.0.0.1", 2301, null);
		redisQueueCacheConnectAgent.connect();
		redisQueueCacheConnectAgent.setPop(messageDm);
		logger.info("SetPop...");
		logger.info("HavePop: " + redisQueueCacheConnectAgent.havePop(messageDm));
		messageDm.setFinishTime(System.currentTimeMillis());
		messageDm.setDelayTime(5000);
		redisQueueCacheConnectAgent.removePop(messageDm);
		logger.info("RemovePop Delay...");
		logger.info("HavePop: " + redisQueueCacheConnectAgent.havePop(messageDm));
		Thread.sleep(5000);
		logger.info("Sleep: " + 5000);
		logger.info("HavePop: " + redisQueueCacheConnectAgent.havePop(messageDm));
		redisQueueCacheConnectAgent.close();
	}
	
	@Test
	public final void testReconnect() throws Exception {
		RedisQueueCacheConnectAgent redisQueueCacheConnectAgent = (RedisQueueCacheConnectAgent) redisQueueCacheConnectAgentFactory.createConnectAgent(1, "127.0.0.1", 2301, null);
		((ConnectAgent)redisQueueCacheConnectAgent).reconnect();
	}
	
	@Test
	public final void testClose() throws Exception {
		Thread.sleep(1000);
	}
}
