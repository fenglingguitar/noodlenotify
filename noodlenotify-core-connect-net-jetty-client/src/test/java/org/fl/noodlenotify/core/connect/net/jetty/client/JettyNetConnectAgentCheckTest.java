package org.fl.noodlenotify.core.connect.net.jetty.client;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/core/connect/net/jetty/client/noodlenotify-core-connect-net-client-jetty-check.xml"
})

public class JettyNetConnectAgentCheckTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	private JettyNetConnectAgent jettyNetConnectAgent;
	
	@Test
	public void testSend() throws Exception {

		jettyNetConnectAgent.checkHealth();
	}
}
