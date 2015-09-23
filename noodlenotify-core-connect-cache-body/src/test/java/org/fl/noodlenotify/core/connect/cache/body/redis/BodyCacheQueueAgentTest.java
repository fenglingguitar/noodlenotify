package org.fl.noodlenotify.core.connect.cache.body.redis;

//import java.util.ArrayList;
//import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.QueueAgent;
import org.fl.noodlenotify.core.connect.cache.body.BodyCacheQueueAgent;
//import org.fl.noodlenotify.core.connect.cache.queue.redis.RedisQueueCacheConnectAgent;

public class BodyCacheQueueAgentTest {
	
	private final static Logger logger = LoggerFactory.getLogger(BodyCacheQueueAgentTest.class);
	
	public static QueueAgent queueAgent = new BodyCacheQueueAgent("testQueue1");
	public static ConnectAgent connectAgent;

	@Test
	public final void testGetConnectAgent() throws Exception {
		
		/*final List<ConnectAgent> addConnectAgentList = new ArrayList<ConnectAgent>();
		ConnectAgent ConnectAgent1 = new RedisQueueCacheConnectAgent("10.8.210.169", 6382, 1L);
		ConnectAgent ConnectAgent2 = new RedisQueueCacheConnectAgent("10.8.210.169", 6383, 2L);
		ConnectAgent ConnectAgent3 = new RedisQueueCacheConnectAgent("10.8.210.169", 6384, 3L);
		
		ConnectAgent1.connect();
		ConnectAgent2.connect();
		ConnectAgent3.connect();
		
		addConnectAgentList.add(ConnectAgent1);
		addConnectAgentList.add(ConnectAgent2);
		addConnectAgentList.add(ConnectAgent3);
		queueAgent.updateConnectAgents(addConnectAgentList);
		
		connectAgent = queueAgent.getConnectAgent();
		
		logger.info(connectAgent.getConnectId());*/
	}

	@Test
	public final void testGetConnectAgentOther() {
		ConnectAgent connectAgentOther = queueAgent.getConnectAgentOther(connectAgent);
		logger.info(String.valueOf(connectAgentOther.getConnectId()));
	}
}
