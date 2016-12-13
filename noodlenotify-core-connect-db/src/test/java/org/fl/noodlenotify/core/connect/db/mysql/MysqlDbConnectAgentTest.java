package org.fl.noodlenotify.core.connect.db.mysql;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.fl.noodlenotify.core.connect.db.DbStatusChecker;
import org.fl.noodlenotify.core.connect.db.mysql.MysqlDbConnectAgent;
import org.fl.noodlenotify.core.constant.message.MessageConstant;
import org.fl.noodlenotify.core.domain.message.MessageDm;
import org.fl.noodlenotify.core.domain.message.MessageVo;
import org.fl.noodle.common.connect.agent.ConnectAgent;
import org.fl.noodle.common.util.json.JsonTranslator;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/core/connect/db/mysql/noodlenotify-core-connect-db-mysql.xml"
})

public class MysqlDbConnectAgentTest extends AbstractJUnit4SpringContextTests {
	
	private final static Logger logger = LoggerFactory.getLogger(MysqlDbConnectAgentTest.class);
	
	@Autowired
	MysqlDbConnectAgent mysqlDbConnectAgent;
	
	public static MessageDm messageDm1;
	public static MessageDm messageDm2;
	public static MessageDm messageDm3;
	public static MessageDm messageDm4;
	
	@Test
	public final void testCreateTable() throws Exception {
		
		mysqlDbConnectAgent.createTable("TestQueue1");
		logger.info("Create Table...");
		Thread.sleep(10000);
	}
	
	@Test
	public final void testInsert() throws Exception {	
		
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		messageDm1 = new MessageDm();
		messageDm1.setQueueName("TestQueue1");
		messageDm1.setUuid(uuid);
		messageDm1.setContent(JsonTranslator.toByteArray("Hello"));
		messageDm1.setExecuteQueue(10);
		messageDm1.setResultQueue(0);
		messageDm1.setStatus(MessageConstant.MESSAGE_STATUS_NEW);
		logger.info("MessageDm insert 1 uuid: " + uuid);
		mysqlDbConnectAgent.insert(messageDm1);
		logger.info("Insert 1 id:" + messageDm1.getId());
		
		uuid = UUID.randomUUID().toString().replaceAll("-", "");
		messageDm2 = new MessageDm();
		messageDm2.setQueueName("TestQueue1");
		messageDm2.setUuid(uuid);
		messageDm2.setContent(JsonTranslator.toByteArray("Hello"));
		messageDm2.setExecuteQueue(10);
		messageDm2.setResultQueue(0);
		messageDm2.setStatus(MessageConstant.MESSAGE_STATUS_NEW);
		logger.info("MessageDm insert 2 uuid: " + uuid);
		mysqlDbConnectAgent.insert(messageDm2);
		logger.info("Insert 2 id:" + messageDm2.getId());
		Thread.sleep(100);
		
		uuid = UUID.randomUUID().toString().replaceAll("-", "");
		messageDm3 = new MessageDm();
		messageDm3.setQueueName("TestQueue1");
		messageDm3.setUuid(uuid);
		messageDm3.setContent(JsonTranslator.toByteArray("Hello"));
		messageDm3.setExecuteQueue(10);
		messageDm3.setResultQueue(0);
		messageDm3.setStatus(MessageConstant.MESSAGE_STATUS_NEW);
		logger.info("MessageDm insert 3 uuid: " + uuid);
		mysqlDbConnectAgent.insert(messageDm3);
		logger.info("Insert 3 id:" + messageDm3.getId());
		
		uuid = UUID.randomUUID().toString().replaceAll("-", "");
		messageDm4 = new MessageDm();
		messageDm4.setQueueName("TestQueue1");
		messageDm4.setUuid(uuid);
		messageDm4.setContent(JsonTranslator.toByteArray("Hello"));
		messageDm4.setExecuteQueue(10);
		messageDm4.setResultQueue(0);
		messageDm4.setStatus(MessageConstant.MESSAGE_STATUS_NEW);
		logger.info("MessageDm insert 4 uuid: " + uuid);
		mysqlDbConnectAgent.insert(messageDm4);
		logger.info("Insert 4 id:" + messageDm4.getId());
		
		Thread.sleep(100);
	}
	
	@Test
	public final void testUpdate() throws Exception {
		
		messageDm2.setResultQueue(5);
		messageDm2.setStatus(MessageConstant.MESSAGE_STATUS_PORTION);
		mysqlDbConnectAgent.update(messageDm2);
		logger.info("update 2 status:" + messageDm2.getStatus());
		
		messageDm3.setResultQueue(10);
		messageDm3.setStatus(MessageConstant.MESSAGE_STATUS_FINISH);		
		mysqlDbConnectAgent.update(messageDm3);
		logger.info("update 3 status:" + messageDm3.getStatus());
		
		messageDm4.setResultQueue(10);
		messageDm4.setStatus(MessageConstant.MESSAGE_STATUS_FINISH);		
		mysqlDbConnectAgent.update(messageDm4);
		logger.info("update 4 status:" + messageDm4.getStatus());
		
		Thread.sleep(100);
	}
	
	@Test
	public final void testDelete() throws Exception {
		
		mysqlDbConnectAgent.delete(messageDm4);
		logger.info("delete 4 id:" + messageDm4.getId());
		Thread.sleep(100);
	}
	
	@Test
	public final void testSelect() throws Exception {
		
		List<MessageDm> messageDmListNew = mysqlDbConnectAgent.select("TestQueue1", 0, 1000, MessageConstant.MESSAGE_STATUS_NEW);
		for (MessageDm messageDm : messageDmListNew) {
			logger.info("Select MESSAGE_STATUS_NEW:" + messageDm.getId() + "," + messageDm.getUuid());
		}
		List<MessageDm> messageDmListPortion = mysqlDbConnectAgent.select("TestQueue1", 0, 1000, MessageConstant.MESSAGE_STATUS_PORTION);
		for (MessageDm messageDm : messageDmListPortion) {
			logger.info("Select MESSAGE_STATUS_PORTION:" + messageDm.getId() + "," + messageDm.getUuid());
		}
		List<MessageDm> messageDmListFinish = mysqlDbConnectAgent.select("TestQueue1", 0, 1000, MessageConstant.MESSAGE_STATUS_FINISH);
		for (MessageDm messageDm : messageDmListFinish) {
			logger.info("Select MESSAGE_STATUS_FINISH:" + messageDm.getId() + "," + messageDm.getUuid());
		}
	}
	
	@Test
	public final void testSelectTimeout() throws Exception {
		List<MessageDm> messageDmListFinish = mysqlDbConnectAgent.selectTimeout("TestQueue1", 0, 1000, MessageConstant.MESSAGE_STATUS_FINISH, System.currentTimeMillis() - 150);
		for (MessageDm messageDm : messageDmListFinish) {
			logger.info("Select MESSAGE_STATUS_FINISH Timeout:" + messageDm.getId() + "," + messageDm.getUuid());
		}
	}
	
	@Test
	public final void testSelectById() throws Exception {
		
		MessageDm messageDmTemp = mysqlDbConnectAgent.selectById("TestQueue1", messageDm3.getContentId());
		if (messageDmTemp != null) {	
			logger.info("Select by id:" + JsonTranslator.fromByteArray(messageDmTemp.getContent(), Object.class));
		}
	}
	
	@Test
	public final void testMaxId() throws Exception {
		logger.info("MaxId: " + mysqlDbConnectAgent.maxId("TestQueue1"));
	}
	
	@Test
	public final void testMaxIdDelay() throws Exception {
		logger.info("MaxId: " + mysqlDbConnectAgent.maxIdDelay("TestQueue1", System.currentTimeMillis() - 240000));
	}
	
	@Test
	public final void testMinId() throws Exception {
		logger.info("MinId: " + mysqlDbConnectAgent.minId("TestQueue1"));
	}
	
	@Test
	public final void testMinFinishId() throws Exception {
		logger.info("MinUnFinishId: " + mysqlDbConnectAgent.minUnFinishId("TestQueue1"));
	}
	
	@Test
	public final void testMinIdByStatus() throws Exception {
		logger.info("MinIdByStatus MESSAGE_STATUS_NEW: " + mysqlDbConnectAgent.minIdByStatus("TestQueue1", MessageConstant.MESSAGE_STATUS_NEW));
		logger.info("MinIdByStatus MESSAGE_STATUS_PORTION: " + mysqlDbConnectAgent.minIdByStatus("TestQueue1", MessageConstant.MESSAGE_STATUS_PORTION));
	}
	
	@Test
	public final void testGetDiffTime() throws Exception {
		logger.info("DiffTime: " + mysqlDbConnectAgent.getDiffTime());
	}
	
	@Test
	public final void testGetAlive() throws Exception {
		logger.info("GetAlive 1: " + mysqlDbConnectAgent.getAlive("TestQueue1", 1, 0, 2000));
	}
	
	@Test
	public final void testKeepAlive() throws Exception {
		logger.info("Sleep 1000...");
		Thread.sleep(1000);
		logger.info("KeepAlive 1: " + mysqlDbConnectAgent.keepAlive("TestQueue1", 1, 0, 2000));
		logger.info("Sleep 1000...");
		Thread.sleep(1000);
		logger.info("KeepAlive 2: " + mysqlDbConnectAgent.keepAlive("TestQueue1", 2, 0, 2000));
		logger.info("GetAlive 2: " + mysqlDbConnectAgent.getAlive("TestQueue1", 2, 0, 2000));
		logger.info("Sleep 1000...");
		Thread.sleep(1000);
		logger.info("GetAlive 2: " + mysqlDbConnectAgent.getAlive("TestQueue1", 2, 0, 2000));
		logger.info("Sleep 1000...");
		Thread.sleep(1000);
		logger.info("KeepAlive 1: " + mysqlDbConnectAgent.keepAlive("TestQueue1", 1, 0, 2000));
	}
	
	@Test
	public final void testReleaseAlive() throws Exception {
		logger.info("ReleaseAlive 1");
		mysqlDbConnectAgent.releaseAlive("TestQueue1", 1);
		logger.info("ReleaseAlive 2");
		mysqlDbConnectAgent.releaseAlive("TestQueue1", 2);
	}
	
	@Test
	public final void testCheckHealth() throws Exception {
		DbStatusChecker dbStatusChecker = (DbStatusChecker) mysqlDbConnectAgent;
		dbStatusChecker.checkHealth();
	}
	
	@Test
	public final void testCheckNewLen() throws Exception {
		DbStatusChecker dbStatusChecker = (DbStatusChecker) mysqlDbConnectAgent;
		logger.info("CheckNewLen: " + dbStatusChecker.checkNewLen("TestQueue1"));
	}
	
	@Test
	public final void testCheckPortionLen() throws Exception {
		DbStatusChecker dbStatusChecker = (DbStatusChecker) mysqlDbConnectAgent;
		logger.info("CheckPortionLen: " + dbStatusChecker.checkPortionLen("TestQueue1"));
	}
	
	@Test
	public final void testQueryPortionMessage() throws Exception {
		DbStatusChecker dbStatusChecker = (DbStatusChecker) mysqlDbConnectAgent;
		List<MessageVo> messageVoList = dbStatusChecker.queryPortionMessage("TestQueue1", null, null, null, 0, 20);
		for (MessageVo messageVoIt : messageVoList) {
			logger.info("Message: " + messageVoIt.getUuid());
		}
	}
	
	@Test
	public final void testReconnect() throws Exception {
		((ConnectAgent)mysqlDbConnectAgent).reconnect();
	}
	
	@Test
	public final void testClose() throws Exception {
		Thread.sleep(1000);
	}
}
