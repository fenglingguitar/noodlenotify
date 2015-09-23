package org.fl.noodlenotify.core.connect.net.netty.client;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.net.NetConnectAgent;
import org.fl.noodlenotify.core.connect.net.netty.client.NettyNetConnectAgent;
import org.fl.noodlenotify.core.connect.net.pojo.Message;
import org.fl.noodle.common.util.json.JsonTranslator;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/core/connect/net/netty/client/noodlenotify-core-connect-net-client-netty-receive.xml"
})

public class NettyNetConnectAgentReceiveTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	private NettyNetConnectAgent nettyNetConnectAgent;

	@Test
	public void testSend() throws Exception {
		
		Message message = new Message();
		message.setUuid("1234567890");
		message.setQueueName("TestQueue1");
		message.setContent(JsonTranslator.toString("Hello"));
		
		NetConnectAgent netConnectAgent = (NetConnectAgent) nettyNetConnectAgent;
		netConnectAgent.send(message);
	}

	@Test
	public void testReconnect() throws Exception {
		((ConnectAgent)nettyNetConnectAgent).reconnect();
	}

	@Test
	public void testClose() throws Exception {
		((ConnectAgent)nettyNetConnectAgent).close();
	}
}
