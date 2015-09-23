package org.fl.noodlenotify.core.connect.cache.queue.redis;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheStatusChecker;
import org.fl.noodlenotify.core.connect.cache.queue.redis.RedisQueueCacheConnectAgent;
import org.fl.noodlenotify.core.domain.message.MessageDm;
import org.fl.noodle.common.util.json.JsonTranslator;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/core/connect/cache/queue/redis/noodlenotify-core-connect-cache-queue-redis.xml"
})

public class QueueCacheConnectAgentTest extends AbstractJUnit4SpringContextTests {
	
	private final static Logger logger = LoggerFactory.getLogger(QueueCacheConnectAgentTest.class);
	
	@Autowired
	RedisQueueCacheConnectAgent redisQueueCacheConnectAgent;
	
	public static MessageDm messageDm;
	
	@Test
	public final void testSetActive() throws Exception {
		redisQueueCacheConnectAgent.setActive("testQueue1", true);
		logger.info("SetActive true...");
	}
	
	@Test
	public final void testPush() throws Exception {
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
	}

	@Test
	public final void testPop() throws Exception {
		messageDm = redisQueueCacheConnectAgent.pop("testQueue1", true);
		logger.info("Pop New uuid: " + messageDm.getUuid());
		messageDm = redisQueueCacheConnectAgent.pop("testQueue1", false);
		logger.info("Pop Portion uuid: " + messageDm.getUuid());
	}

	@Test
	public final void testHavePop() throws Exception {
		logger.info("HavePop: " + redisQueueCacheConnectAgent.havePop(messageDm));
		Thread.sleep(1000);
		logger.info("Sleep: " + 1000);
		logger.info("HavePop: " + redisQueueCacheConnectAgent.havePop(messageDm));
		Thread.sleep(1001);
		logger.info("Sleep: " + 1001);
		logger.info("HavePop: " + redisQueueCacheConnectAgent.havePop(messageDm));
	}

	@Test
	public final void testSetPop() throws Exception {
		redisQueueCacheConnectAgent.setPop(messageDm);
		logger.info("SetPop...");
		logger.info("HavePop: " + redisQueueCacheConnectAgent.havePop(messageDm));
		Thread.sleep(1000);
		logger.info("Sleep: " + 1000);
		logger.info("HavePop: " + redisQueueCacheConnectAgent.havePop(messageDm));
		Thread.sleep(1001);
		logger.info("Sleep: " + 1001);
		logger.info("HavePop: " + redisQueueCacheConnectAgent.havePop(messageDm));
	}

	@Test
	public final void testRemovePop() throws Exception {
		redisQueueCacheConnectAgent.setPop(messageDm);
		logger.info("SetPop...");
		logger.info("HavePop: " + redisQueueCacheConnectAgent.havePop(messageDm));
		redisQueueCacheConnectAgent.removePop(messageDm);
		logger.info("RemovePop...");
		logger.info("HavePop: " + redisQueueCacheConnectAgent.havePop(messageDm));
	}
	
	@Test
	public final void testIsActive() throws Exception {
		logger.info("IsActive: " + redisQueueCacheConnectAgent.isActive("testQueue1"));
		redisQueueCacheConnectAgent.setActive("testQueue1", false);
		logger.info("SetActive false...");
		logger.info("IsActive: " + redisQueueCacheConnectAgent.isActive("testQueue1"));
	}
	
	@Test
	public final void testLen() throws Exception {
		logger.info("New Len: " + redisQueueCacheConnectAgent.len("testQueue1", true));
		logger.info("Portion Len: " + redisQueueCacheConnectAgent.len("testQueue1", false));
	}
	
	@Test
	public final void testGetDiffTime() throws Exception {
		logger.info("DiffTime: " + redisQueueCacheConnectAgent.getDiffTime());
	}
	
	@Test
	public final void testGetAlive() throws Exception {
		logger.info("GetAlive 1: " + redisQueueCacheConnectAgent.getAlive("testQueue1", 1, 0, 2000));
	}
	
	@Test
	public final void testKeepAlive() throws Exception {
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
	}
	
	@Test
	public final void testReleaseAlive() throws Exception {
		logger.info("ReleaseAlive 1");
		redisQueueCacheConnectAgent.releaseAlive("testQueue1", 1);
		logger.info("ReleaseAlive 2");
		redisQueueCacheConnectAgent.releaseAlive("testQueue1", 2);
	}
	
	@Test
	public final void testCheckHealth() throws Exception {
		QueueCacheStatusChecker queueCacheStatusChecker = (QueueCacheStatusChecker) redisQueueCacheConnectAgent;
		queueCacheStatusChecker.checkHealth();
	}
	
	@Test
	public final void testCheckIsActive() throws Exception {
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
	}
	
	@Test
	public final void testCheckNewLen() throws Exception {
		QueueCacheStatusChecker queueCacheStatusChecker = (QueueCacheStatusChecker) redisQueueCacheConnectAgent;
		logger.info("CheckNewLen: " + queueCacheStatusChecker.checkNewLen("testQueue1"));
	}
	
	@Test
	public final void testCheckPortionLen() throws Exception {
		QueueCacheStatusChecker queueCacheStatusChecker = (QueueCacheStatusChecker) redisQueueCacheConnectAgent;
		logger.info("CheckPortionLen: " + queueCacheStatusChecker.checkPortionLen("testQueue1"));
		redisQueueCacheConnectAgent.setActive("testQueue1", false);
		logger.info("SetActive false...");
	}
	
	@Test
	public final void testRemovePopDelay() throws Exception {
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
	}
	
	@Test
	public final void testReconnect() throws Exception {
		((ConnectAgent)redisQueueCacheConnectAgent).reconnect();
	}
	
	@Test
	public final void testClose() throws Exception {
		Thread.sleep(1000);
	}
}
