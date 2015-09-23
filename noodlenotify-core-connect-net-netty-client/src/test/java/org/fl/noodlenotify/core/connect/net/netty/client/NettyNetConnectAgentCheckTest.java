package org.fl.noodlenotify.core.connect.net.netty.client;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.net.NetStatusChecker;
import org.fl.noodlenotify.core.connect.net.netty.client.NettyNetConnectAgent;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/core/connect/net/netty/client/noodlenotify-core-connect-net-client-netty-check.xml"
})

public class NettyNetConnectAgentCheckTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	private NettyNetConnectAgent nettyNetConnectAgent;
	
	@Test
	public void testCheckHealth() throws Exception {
		((NetStatusChecker)nettyNetConnectAgent).checkHealth();
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
