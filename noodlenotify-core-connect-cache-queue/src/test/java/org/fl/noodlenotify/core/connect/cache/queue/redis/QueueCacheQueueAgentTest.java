package org.fl.noodlenotify.core.connect.cache.queue.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.QueueAgent;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheQueueAgent;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheConnectAgent;
import org.fl.noodlenotify.core.connect.cache.queue.redis.RedisQueueCacheConnectAgent;
import org.fl.noodlenotify.core.domain.message.MessageDm;
import org.fl.noodle.common.util.json.JsonTranslator;

public class QueueCacheQueueAgentTest {

	private final static Logger logger = LoggerFactory.getLogger(QueueCacheQueueAgentTest.class);
	
	public static QueueAgent queueAgent = new QueueCacheQueueAgent("TestQueue1");
	public static QueueAgent queueAgent1 = new QueueCacheQueueAgent("TestQueue1");
	public static ConnectAgent connectAgent;
	
	@Test
	public final void testGetConnectAgent() throws Exception {
		
		final List<ConnectAgent> addConnectAgentList = new ArrayList<ConnectAgent>();
		ConnectAgent ConnectAgent1 = new RedisQueueCacheConnectAgent("10.8.210.169", 6379, 1L);
		ConnectAgent ConnectAgent2 = new RedisQueueCacheConnectAgent("10.8.210.169", 6380, 2L);
		ConnectAgent ConnectAgent3 = new RedisQueueCacheConnectAgent("10.8.210.169", 6381, 3L);
		
		ConnectAgent1.connect();
		ConnectAgent2.connect();
		ConnectAgent3.connect();
		
		addConnectAgentList.add(ConnectAgent1);
		addConnectAgentList.add(ConnectAgent2);
		addConnectAgentList.add(ConnectAgent3);
		queueAgent.updateConnectAgents(addConnectAgentList);
		
		connectAgent = queueAgent.getConnectAgent();
		
		logger.info("GetConnectAgent -> " + (connectAgent != null ? connectAgent.getConnectId() : "Null"));
	}
	
	@Test
	public final void testGetConnectAgentOther() {
		
		ConnectAgent connectAgentOther = queueAgent.getConnectAgentOther(connectAgent);
		logger.info("GetConnectAgentOther -> " + connectAgentOther.getConnectId());
	}
	
	@Test
	public final void testGetConnectAgentLoop() throws Exception {
		
		Thread.sleep(3000);
		
		final List<ConnectAgent> addConnectAgentList = new ArrayList<ConnectAgent>();
		ConnectAgent ConnectAgent1 = new RedisQueueCacheConnectAgent("10.8.210.169", 6379, 1L);
		ConnectAgent ConnectAgent2 = new RedisQueueCacheConnectAgent("10.8.210.169", 6380, 2L);
		ConnectAgent ConnectAgent3 = new RedisQueueCacheConnectAgent("10.8.210.169", 6381, 3L);

		final List<ConnectAgent> addConnectAgentList1 = new ArrayList<ConnectAgent>();
		ConnectAgent ConnectAgent4 = new RedisQueueCacheConnectAgent("10.8.210.169", 6379, 1L);
		ConnectAgent ConnectAgent5 = new RedisQueueCacheConnectAgent("10.8.210.169", 6380, 2L);
		ConnectAgent ConnectAgent6 = new RedisQueueCacheConnectAgent("10.8.210.169", 6381, 3L);
		
		ConnectAgent1.connect();
		ConnectAgent2.connect();
		ConnectAgent3.connect();
		
		ConnectAgent4.connect();
		ConnectAgent5.connect();
		ConnectAgent6.connect();
		
		addConnectAgentList.add(ConnectAgent1);
		addConnectAgentList.add(ConnectAgent2);
		addConnectAgentList.add(ConnectAgent3);
		queueAgent.updateConnectAgents(addConnectAgentList);
		
		addConnectAgentList1.add(ConnectAgent4);
		addConnectAgentList1.add(ConnectAgent5);
		addConnectAgentList1.add(ConnectAgent6);
		queueAgent1.updateConnectAgents(addConnectAgentList1);
		
		final MessageDm messageDm = new MessageDm();
		messageDm.setQueueName("TestQueue1");
		String uuid = UUID.randomUUID().toString().replaceAll("-", ""); 
		messageDm.setUuid(uuid);
		messageDm.setContent(JsonTranslator.toByteArray("Hello"));
		messageDm.setBool(true);
		
        ExecutorService execService = Executors.newCachedThreadPool();
        
        execService.execute(new Runnable() {

			@Override
			public void run() {
				
				while (true) {
					
					ConnectAgent connectAgent = queueAgent.getConnectAgent();
					if (connectAgent != null) {
						QueueCacheConnectAgent queueCacheConnectAgentMain = (QueueCacheConnectAgent) connectAgent;
						logger.info("Set Thread Get Active -> " + connectAgent.getConnectId());
						try {
							queueCacheConnectAgentMain.setActive("TestQueue1", false);
							logger.info("Set Thread Disable Active -> " + connectAgent.getConnectId());
						} catch (Exception e) {
							logger.error("Set Thread Disable Active -> " + e);
						}
					} else {
						logger.info("Set Thread Disable Active -> Null");
					}
					
					ConnectAgent connectAgentNext = queueAgent.getConnectAgentOther(connectAgent);
					if (connectAgentNext != null) {
						QueueCacheConnectAgent queueCacheConnectAgentNext = (QueueCacheConnectAgent) connectAgentNext;
						logger.info("Set Thread Get Active Next -> " + connectAgentNext.getConnectId());
						try {
							queueCacheConnectAgentNext.setActive("TestQueue1", true);
							logger.info("Set Thread Able Active Next -> " + connectAgentNext.getConnectId());
						} catch (Exception e) {
							logger.error("Set Thread Able Active Next -> " + e);
						}
					} else {
						logger.info("Set Thread Get Active Next ->  null");
					}
					
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						logger.error("Set Thread Sleep -> " + e);
					}
				}
			}
    	});
        
    	for (int i=0; i<10; i++) {
    		
    		final int index = i;
        	
        	execService.execute(new Runnable() {

				@Override
				public void run() {
					
					while (true) {
						
						ConnectAgent connectAgent = queueAgent1.getConnectAgent();
						if (connectAgent != null) {
							logger.info("Push Thread " + index + " Get Active -> " + connectAgent.getConnectId());
							QueueCacheConnectAgent queueCacheConnectAgentMain = (QueueCacheConnectAgent) connectAgent;
							try {
								queueCacheConnectAgentMain.push(messageDm);
							} catch (Exception e) {
								logger.error("Push Thread " + index + " Get Active ->  " + e);
							}
						} else {							
							logger.info("Push Thread " + index + "Get Active -> Null");
						}
						
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							logger.error("Push Thread " + index + " Sleep -> " + e);
						}
					}
				}
        	});
        }
    	
    	Thread.sleep(Long.MAX_VALUE);
	}
}
