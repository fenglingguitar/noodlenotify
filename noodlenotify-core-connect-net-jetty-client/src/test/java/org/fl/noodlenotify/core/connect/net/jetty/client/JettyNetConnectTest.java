package org.fl.noodlenotify.core.connect.net.jetty.client;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.fl.noodlenotify.core.connect.net.pojo.Message;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/core/connect/net/jetty/client/noodlenotify-core-connect-net-client-jetty-receive.xml"
})
public class JettyNetConnectTest extends AbstractJUnit4SpringContextTests {

	@Test
	public void testSend() throws Exception {

		JettyNetConnect jettyNetConnect = new JettyNetConnect("127.0.0.1", 20001, "/noodlenotify", 30000);
		jettyNetConnect.connect();
		for (int i=0; i<10; i++) {			
			jettyNetConnect.send(new Message("TestQueue1", "1234567890", "你好"), 3000);
			Thread.sleep(10);
		}
		jettyNetConnect.close();
	}
}
