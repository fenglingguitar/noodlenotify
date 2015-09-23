package org.fl.noodlenotify.core.connect.net.rmi.client;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.net.NetConnectAgent;
import org.fl.noodlenotify.core.connect.net.NetStatusChecker;
import org.fl.noodlenotify.core.connect.net.rmi.client.RmiNetConnectAgent;
import org.fl.noodlenotify.core.connect.net.pojo.Message;
import org.fl.noodle.common.util.json.JsonTranslator;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/core/connect/net/rmi/client/noodlenotify-core-connect-net-client-rmi.xml"
})

public class RmiNetConnectAgentTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	RmiNetConnectAgent rmiNetConnectAgent;
	
	@Test
	public void testSend() throws Exception {
		
		Message message = new Message();
		message.setUuid("1234567890");
		message.setQueueName("TestQueue1");
		message.setContent(JsonTranslator.toString("Hello"));
		
		NetConnectAgent netConnectAgent = (NetConnectAgent) rmiNetConnectAgent;
		netConnectAgent.send(message);
	}
	
	@Test
	public void testCheckHealth() throws Exception {
		((NetStatusChecker)rmiNetConnectAgent).checkHealth();
	}

	@Test
	public void testReconnect() throws Exception {
		((ConnectAgent)rmiNetConnectAgent).reconnect();
	}

	@Test
	public void testClose() throws Exception {
		((ConnectAgent)rmiNetConnectAgent).close();
	}
}
