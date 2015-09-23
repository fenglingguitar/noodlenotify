package org.fl.noodlenotify.core.connect.net.jetty.client;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import org.fl.noodlenotify.core.connect.net.pojo.Message;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/core/connect/net/jetty/client/noodlenotify-core-connect-net-client-jetty-receive.xml"
})

public class JettyNetConnectAgentReceiveTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	private JettyNetConnectAgent jettyNetConnectAgent;
	
	@Test
	public void testSend() throws Exception {

		for (int i=0; i<10; i++) {			
			jettyNetConnectAgent.send(new Message("TestQueue1", "1234567890", "你好"));
			Thread.sleep(10);
		}
	}
}
