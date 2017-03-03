package org.fl.noodlenotify.core.connect.db.mysql;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.fl.noodlenotify.common.pojo.console.MessageVo;
import org.fl.noodlenotify.common.pojo.db.MessageDb;
import org.fl.noodlenotify.core.connect.db.DbStatusChecker;
import org.fl.noodlenotify.core.connect.db.mysql.MysqlDbConnectAgent;
import org.fl.noodlenotify.core.constant.message.MessageConstant;
import org.fl.noodle.common.connect.agent.ConnectAgent;
import org.fl.noodle.common.util.json.JsonTranslator;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/core/connect/db/mysql/noodlenotify-core-connect-db-mysql.xml"
})

public class MysqlDbConnectAgentTest extends AbstractJUnit4SpringContextTests {
	
	private final static Logger logger = LoggerFactory.getLogger(MysqlDbConnectAgentTest.class);
	
	@Autowired
	MysqlDbConnectAgent mysqlDbConnectAgent;
	
	public static MessageDb messageDb1;
	public static MessageDb messageDb2;
	public static MessageDb messageDb3;
	public static MessageDb messageDb4;
	
	@Test
	public final void testCreateTable() throws Exception {
		
		mysqlDbConnectAgent.createTable("TestQueue1");
		logger.info("Create Table...");
		Thread.sleep(10000);
	}
	
	@Test
	public final void testInsert() throws Exception {	
		
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		messageDb1 = new MessageDb();
		messageDb1.setQueueName("TestQueue1");
		messageDb1.setUuid(uuid);
		messageDb1.setContent(JsonTranslator.toByteArray("Hello"));
		messageDb1.setExecuteQueue(10);
		messageDb1.setResultQueue(0);
		messageDb1.setStatus(MessageConstant.MESSAGE_STATUS_NEW);
		logger.info("MessageDm insert 1 uuid: " + uuid);
		mysqlDbConnectAgent.insert(messageDb1);
		logger.info("Insert 1 id:" + messageDb1.getId());
		
		uuid = UUID.randomUUID().toString().replaceAll("-", "");
		messageDb2 = new MessageDb();
		messageDb2.setQueueName("TestQueue1");
		messageDb2.setUuid(uuid);
		messageDb2.setContent(JsonTranslator.toByteArray("Hello"));
		messageDb2.setExecuteQueue(10);
		messageDb2.setResultQueue(0);
		messageDb2.setStatus(MessageConstant.MESSAGE_STATUS_NEW);
		logger.info("MessageDm insert 2 uuid: " + uuid);
		mysqlDbConnectAgent.insert(messageDb2);
		logger.info("Insert 2 id:" + messageDb2.getId());
		Thread.sleep(100);
		
		uuid = UUID.randomUUID().toString().replaceAll("-", "");
		messageDb3 = new MessageDb();
		messageDb3.setQueueName("TestQueue1");
		messageDb3.setUuid(uuid);
		messageDb3.setContent(JsonTranslator.toByteArray("Hello"));
		messageDb3.setExecuteQueue(10);
		messageDb3.setResultQueue(0);
		messageDb3.setStatus(MessageConstant.MESSAGE_STATUS_NEW);
		logger.info("MessageDm insert 3 uuid: " + uuid);
		mysqlDbConnectAgent.insert(messageDb3);
		logger.info("Insert 3 id:" + messageDb3.getId());
		
		uuid = UUID.randomUUID().toString().replaceAll("-", "");
		messageDb4 = new MessageDb();
		messageDb4.setQueueName("TestQueue1");
		messageDb4.setUuid(uuid);
		messageDb4.setContent(JsonTranslator.toByteArray("Hello"));
		messageDb4.setExecuteQueue(10);
		messageDb4.setResultQueue(0);
		messageDb4.setStatus(MessageConstant.MESSAGE_STATUS_NEW);
		logger.info("MessageDm insert 4 uuid: " + uuid);
		mysqlDbConnectAgent.insert(messageDb4);
		logger.info("Insert 4 id:" + messageDb4.getId());
		
		Thread.sleep(100);
	}
	
	@Test
	public final void testUpdate() throws Exception {
		
		messageDb2.setResultQueue(5);
		messageDb2.setStatus(MessageConstant.MESSAGE_STATUS_PORTION);
		mysqlDbConnectAgent.update(messageDb2);
		logger.info("update 2 status:" + messageDb2.getStatus());
		
		messageDb3.setResultQueue(10);
		messageDb3.setStatus(MessageConstant.MESSAGE_STATUS_FINISH);		
		mysqlDbConnectAgent.update(messageDb3);
		logger.info("update 3 status:" + messageDb3.getStatus());
		
		messageDb4.setResultQueue(10);
		messageDb4.setStatus(MessageConstant.MESSAGE_STATUS_FINISH);		
		mysqlDbConnectAgent.update(messageDb4);
		logger.info("update 4 status:" + messageDb4.getStatus());
		
		Thread.sleep(100);
	}
	
	@Test
	public final void testDelete() throws Exception {
		
		mysqlDbConnectAgent.delete(messageDb4);
		logger.info("delete 4 id:" + messageDb4.getId());
		Thread.sleep(100);
	}
	
	@Test
	public final void testSelect() throws Exception {
		
		List<MessageDb> messageDbListNew = mysqlDbConnectAgent.select("TestQueue1", 0, 1000, MessageConstant.MESSAGE_STATUS_NEW);
		for (MessageDb messageDb : messageDbListNew) {
			logger.info("Select MESSAGE_STATUS_NEW:" + messageDb.getId() + "," + messageDb.getUuid());
		}
		List<MessageDb> messageDbListPortion = mysqlDbConnectAgent.select("TestQueue1", 0, 1000, MessageConstant.MESSAGE_STATUS_PORTION);
		for (MessageDb messageDb : messageDbListPortion) {
			logger.info("Select MESSAGE_STATUS_PORTION:" + messageDb.getId() + "," + messageDb.getUuid());
		}
		List<MessageDb> messageDbListFinish = mysqlDbConnectAgent.select("TestQueue1", 0, 1000, MessageConstant.MESSAGE_STATUS_FINISH);
		for (MessageDb messageDb : messageDbListFinish) {
			logger.info("Select MESSAGE_STATUS_FINISH:" + messageDb.getId() + "," + messageDb.getUuid());
		}
	}
	
	@Test
	public final void testSelectTimeout() throws Exception {
		List<MessageDb> messageDbListFinish = mysqlDbConnectAgent.selectTimeout("TestQueue1", 0, 1000, MessageConstant.MESSAGE_STATUS_FINISH, System.currentTimeMillis() - 150);
		for (MessageDb messageDb : messageDbListFinish) {
			logger.info("Select MESSAGE_STATUS_FINISH Timeout:" + messageDb.getId() + "," + messageDb.getUuid());
		}
	}
	
	@Test
	public final void testSelectById() throws Exception {
		
		MessageDb messageDbTemp = mysqlDbConnectAgent.selectById("TestQueue1", messageDb3.getContentId());
		if (messageDbTemp != null) {	
			logger.info("Select by id:" + JsonTranslator.fromByteArray(messageDbTemp.getContent(), Object.class));
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
